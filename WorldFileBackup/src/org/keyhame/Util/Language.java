package org.keyhame.Util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.keyhame.backup.Main;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Language {
    private final Map<String,String> value;

    public Language(FileConfiguration fileConfiguration){
        value = new HashMap<>();
        //检查文件完整性
        FileConfiguration sourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(Main.getPlugin().getResource("plugin.yml"))));
        for(String key: sourceConfig.getKeys(true)){
            if(key.startsWith("language.")){
                if(!fileConfiguration.contains(key)){
                    Main.getPlugin().getLogger().warning("Can't find the language entry :" +key);
                    fileConfiguration.set(key, sourceConfig.get(key));
                }
            }
        }
        //读取文件
        for(Object o: Objects.requireNonNull(fileConfiguration.getConfigurationSection("language")).getKeys(true).toArray()){
            value.put(o.toString(), ChatColor.translateAlternateColorCodes('%', Objects.requireNonNull(fileConfiguration.getString("language." + o))));
        }
    }

    public String getLanguageString(String name){
        return ((name.equals("FIRST_NAME"))?"": value.getOrDefault("FIRST_NAME", "Error when get language "+ "FIRST_NAME"))
                +value.getOrDefault(name, "Error when get language "+ name);
    }
}
