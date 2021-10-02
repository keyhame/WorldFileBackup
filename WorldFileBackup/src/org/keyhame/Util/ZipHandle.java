package org.keyhame.Util;

import org.keyhame.backup.Main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHandle {
    private static final int BYTES_SIZE = 1024;

    /**
     *用于zip压缩一个File与目录下文件到指定目录
     * @param sourceFile 源文件（待压缩文件）
     * @param purposePath 压缩文件路径，压缩后的文件将会存储到此
     * @param isCover 是否覆盖压缩文件路径下重名文件，true为覆盖
     * @param comment 压缩文件下的注释
     * @param level 压缩等级
     * @throws IOException 抛出IOException异常
     */

    public static void FileToZip(@Nonnull File sourceFile, @Nonnull File purposePath, @Nullable String comment, boolean isCover, int level) throws IOException {
        if (!purposePath.exists()) {throw new IOException("压缩目标路径不存在");}
        File purposeFile = new File(purposePath,sourceFile.getName() + ".zip");
        if (purposeFile.exists()) {
            if (isCover) {
                purposeFile.delete();
            }else { return; }
        }
        purposeFile.createNewFile();

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(purposeFile));
        zos.setLevel(level);
        if (comment != null) { zos.setComment(comment); }
        if ( !sourceFile.exists() ) {throw new IOException("压缩原文件没有找到"); }
        WriteFileToZip(sourceFile,zos,sourceFile.getName());
        zos.close();
    }

    private synchronized static void WriteFileToZip(@Nonnull File sourceFile, @Nonnull ZipOutputStream zos ,String path)
            throws IOException
    {
        byte[] bytes ;
        InputStream ips;

        if ( sourceFile.isFile() && sourceFile.canRead()) {
            int len;
            bytes = new byte[BYTES_SIZE];
            ips = new FileInputStream(sourceFile);
            zos.putNextEntry(new ZipEntry(path));

            while (( len = ips.read(bytes) ) != -1) {
                zos.write(bytes, 0, len);
            }

            zos.closeEntry();
            ips.close();
        }

        if ( sourceFile.isDirectory() ){
            File[] files = sourceFile.listFiles();
            if ( files == null || files.length == 0){return;}

            for(File file : files){
                try {
                    WriteFileToZip(file , zos , path + '/' + file.getName() );
                }catch (IOException e){
                    Main.getPlugin().getLogger().warning("Can't zip file: " + path + '/' + file.getName());
                    return;
                }
            }
        }
    }
}
