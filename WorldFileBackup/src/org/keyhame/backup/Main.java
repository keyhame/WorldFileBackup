package org.keyhame.backup;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.keyhame.Util.Language;
import org.keyhame.backup.Command.WBackup;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main plugin;
    private Language language;
    private AutoBackup autoBackup;

    public static Main getPlugin() {
        return plugin;
    }

    public Language getLanguage() {
        return language;
    }

    public AutoBackup getAutoBackup(){return autoBackup;}

    @Override
    public void onLoad() {
        plugin = this;

        //配置处理
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        language = new Language(config);
        if (!config.contains("cVersion") || config.getInt("cVersion") < 1){
            getLogger().warning(language.getLanguageString("CONFIG_OLDER"));
        }

        File backupFile = new File(getDataFolder(), String.valueOf(getConfigObject("backup.folder", "backups")));
        if(!backupFile.exists()){
            backupFile.mkdirs();
        }

        Backup.setBackupFolder(backupFile);
        Backup.setBackupLevel(Integer.parseInt(String.valueOf(getConfigObject("backup.level", 4)) ));
        Backup.setWarning(parseBoolean(String.valueOf(getConfigObject("backup.warn", "true"))));
        Backup.setIsZip(parseBoolean(getConfigObject("backup.enable_zip", "true")));
    }

    @Override
    public void onEnable() {
        if(parseBoolean(getConfigObject("auto.enabled", "true"))){
            (autoBackup = new AutoBackup(
                    Long.parseLong(String.valueOf( getConfigObject("auto.time", 7200))),
                    Integer.parseInt(String.valueOf( getConfigObject("auto.num_max",10))),
                    Long.parseLong(String.valueOf( getConfigObject("auto.size_max", 1048576L)))
            )).runTaskAsynchronously(this);
        }

        Objects.requireNonNull(getCommand("wbackup")).setExecutor(new WBackup());
    }

    private Object getConfigObject(String key , Object def){
        if(getConfig().contains(key)){
            return getConfig().get(key);
        }
        getLogger().warning(language.getLanguageString("CONFIG_DELETION") + key);
        return def;
    }

    private boolean parseBoolean(Object s){
        return (s.toString().equalsIgnoreCase("true"));
    }
}
