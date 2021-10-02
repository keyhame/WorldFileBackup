package org.keyhame.Util;

import java.io.File;

public class FileSize {
    /**
     * 此方法用于计算文件（目录）大小
     * @param file 待计算的文件
     * @return 文件大小，未找到文件为-1
     */
    public static long size(File file) {
        long l = 0;
        if(!file.exists()) return -1;
        if(file.isFile()) return file.length();
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return 0;

        for (File file1 : files) {
            l += size(file1);
        }

        return l;
    }
}
