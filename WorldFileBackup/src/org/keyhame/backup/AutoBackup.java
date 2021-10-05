package org.keyhame.backup;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class AutoBackup extends BukkitRunnable {
    private boolean isCancel = false;
    private final long ONCE_WAIT_TIME = 1000;
    private final long WAIT_TIME;
    private final int BACKUP_NUM;
    private final long BACKUP_SIZE;

    public AutoBackup(long waitTime, int backupNum, long backupSize){
        this.WAIT_TIME = waitTime/1000;
        this.BACKUP_NUM = backupNum;
        this.BACKUP_SIZE = backupSize*1024;
    }

    public long getWAIT_TIME() { return WAIT_TIME/1000; }

    @Override
    public void run() {
        Main.getPlugin().getLogger().info(Main.getPlugin().getLanguage().getLanguageString("AUTO.START"));

        while (true) {
            if ( BACKUP_SIZE != -1 && Backup.getSize() >= BACKUP_SIZE){
                Main.getPlugin().getLogger().warning(Main.getPlugin().getLanguage().getLanguageString("AUTO.FULL"));
                this.cancel();
            }

            if(Backup.getNum() >= BACKUP_NUM && BACKUP_NUM != -1){
                Backup.deleteOldest();
            }

            try {
                for(long l = 0; l<WAIT_TIME; l+=ONCE_WAIT_TIME){
                    Thread.sleep(ONCE_WAIT_TIME);
                    if(isCancel){
                        return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Main.getPlugin().getLogger().severe(
                        Main.getPlugin().getLanguage().getLanguageString("AUTO.ERROR"));
                return;
            }

            try {
                Backup.backup();
            } catch (IOException e) {
                e.printStackTrace();
                Main.getPlugin().getLogger().warning(
                        Main.getPlugin().getLanguage().getLanguageString("AUTO.ERROR"));
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        isCancel = true;
        try {
            Thread.sleep(ONCE_WAIT_TIME);
        } catch (InterruptedException ignored) {
        }
        Main.getPlugin().getLogger().info(Main.getPlugin().getLanguage().getLanguageString("AUTO.STOP"));
    }
}
