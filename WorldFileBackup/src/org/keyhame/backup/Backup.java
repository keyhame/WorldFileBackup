package org.keyhame.backup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 此类存储静态backup方法
 */
public class Backup {
    private static final String BACKUP_NAME = "Backup" ;
    private static boolean isWarning;
    private static boolean isZip;
    private static File BackupFolder;
    private static int backupLevel;

    /**
     * 此方法用于设置备份等级
     * @param level 备份等级
     */
    public static void setBackupLevel(int level){ backupLevel = level; }

    /**
     * 此方法用于查询备份等级
     * @return 备份等级
     */
    public static int getBackupLevel(){return backupLevel;}

    /**
     * 此方法用于设置备份前是否提醒玩家
     * @param b 是否提醒玩家
     */
    public static void setWarning(boolean b){ isWarning = b; }

    /**
     * 此方法用于查询备份前是否提醒玩家
     * @return 备份前是否提醒玩家
     */
    public static boolean getWarning(){return isWarning;}

    /**
     * 设置是否压缩
     * @param b 设置是否压缩
     */
    public static void setIsZip(boolean b){isZip = b;}

    /**
     * 此方法用于设置备份文件夹
     * @param backupFolder 备份文件夹（必须存在）
     * @return 是否设置成功
     */
    public static boolean setBackupFolder(File backupFolder)  {
        if ( !(backupFolder.exists() && backupFolder.isDirectory()) ) return false;
        BackupFolder = backupFolder;
        return true;
    }

    public static File getBackupFolder(){
        return BackupFolder;
    }

    /**
     * 此方法用于压缩传入的文件到指定备份文件夹，并且写入注释，日期信息
     */

    public synchronized static void backup() throws IOException {

        //如果没有加载插件
        if(!Main.getPlugin().isEnabled()) {
            return;
        }

        //警告
        if(isWarning){
            Bukkit.broadcastMessage(
                Main.getPlugin().getLanguage().getLanguageString("BACKUP.WARN_MESSAGE")
            );
        }

        //创建备份文件夹
        File file;
        int i = 0;
        do {
            i++;
            file = new File(BackupFolder,BACKUP_NAME+ "-" +
                    new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) +
                    "(" + i + ")");
        }while (file.exists());
        file.mkdirs();

        AtomicBoolean saved = new AtomicBoolean(false);
        for (World world : Bukkit.getWorlds()) {
            //保存世界
            saved.set(false);
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                world.save();
                saved.set(true);
            });

            //等待保存
            while( !saved.get() ) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Bukkit.broadcastMessage(ChatColor.RED+"There is something when making the backup of world: "+ world.getName());
                    Main.getPlugin().getLogger().warning("There is something when making the backup of world: "+ world.getName());
                    e.printStackTrace();
                }
            }

            Bukkit.broadcastMessage(ChatColor.YELLOW + Main.getPlugin().getLanguage().getLanguageString("BACKUP.START"));
            //备份
            File worldFolder = world.getWorldFolder();

            //关闭自动世界保存
            if(world.isAutoSave()){
                world.setAutoSave(false);
                org.keyhame.Util.ZipHandle.FileToZip(worldFolder , file , "世界数据备份",true, backupLevel);
                world.setAutoSave(true);
                continue;
            }
            org.keyhame.Util.ZipHandle.FileToZip(worldFolder , file , "世界数据备份",true, backupLevel);
        }

        file = new File(file, "time.txt");

        //时间信息
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        osw.append("压缩日期：\r\n");
        osw.append( DateFormat.getDateInstance().format(new Date()) );
        osw.close();

        if (isWarning){Bukkit.broadcastMessage(Main.getPlugin().getLanguage().getLanguageString("BACKUP.FINISH"));}
    }

    /**
     * 此方法用于删除最旧备份
     */
    public static void deleteOldest(){
        if(BackupFolder.listFiles() == null )return;
        File oldest = null;
        long oldestTime = -1;

        for(File file: Objects.requireNonNull(BackupFolder.listFiles())){
            if(!file.getName().contains(BACKUP_NAME)) continue;
            File timeFile = new File(file,"time.txt");
            if(timeFile.exists() && timeFile.isFile()){
                if (oldest == null) {
                    oldest = file;
                    oldestTime = timeFile.lastModified();
                }else if (timeFile.lastModified() < oldestTime){
                    oldest = file;
                    oldestTime = timeFile.lastModified();
                }
            }
        }

        if (oldest != null) oldest.delete();
    }

    /**
     * 此方法用于获取备份文件夹的大小
     * @return 备份文件夹的大小
     */
    public static long getSize(){ return org.keyhame.Util.FileSize.size(BackupFolder); }

    /**
     * 此方法用于获取备份文件夹内备份文件的个数
     * @return 备份文件的个数
     */
    public static int getNum(){
        int result = 0;
        File[] files = BackupFolder.listFiles();
        if ( files == null || files.length == 0 ) return 0;
        for (File file : files){
            if (file.getName().contains(BACKUP_NAME) && file.isDirectory()) { result++; }
        }
        return result;
    }
}
