package org.keyhame.backup.Command;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.keyhame.Util.Language;
import org.keyhame.Util.SendMessage;
import org.keyhame.backup.Backup;
import org.keyhame.backup.Main;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public  class WBackup implements TabExecutor {
    private final Language language;

    public WBackup(){
        language = Main.getPlugin().getLanguage();
    }

    @ParametersAreNonnullByDefault
    //用于返回错误解析的函数
    private void commandError(CommandSender sender, @Nullable String reason) {
        if (reason == null) {
            reason = Main.getPlugin().getLanguage().getLanguageString("FIRST_NAME");
        }
        if (sender instanceof Server) {
            Main.getPlugin().getLogger().warning(reason);
            return;
        }
        sender.sendMessage(reason);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            //检测权限
            if (sender instanceof Player && !sender.hasPermission("worldfilebackup")) {
                this.commandError(sender, Main.getPlugin().getLanguage().getLanguageString("COMMAND.PERMISSION"));
                return true;
            }

            switch (args[0]) {
                case "backup": {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                Backup.backup();
                            } catch (IOException e) {
                                e.printStackTrace();
                                commandError(sender, language.getLanguageString("BACKUP.ERROR"));
                            }
                        }
                    }.runTaskAsynchronously(Main.getPlugin());
                    return true;
                }

                case "help": {
                    sender.sendMessage(language.getLanguageString("HELP.DESCRIPTION"));
                    sender.sendMessage(language.getLanguageString("HELP.AUTHOR"));
                    sender.sendMessage(language.getLanguageString("HELP.COMMAND.USAGE"));
                    sender.sendMessage(language.getLanguageString("HELP.COMMAND.INFO"));
                    sender.sendMessage(language.getLanguageString("HELP.COMMAND.HELP"));
                    sender.sendMessage(language.getLanguageString("HELP.COMMAND.AUTO_BACKUP"));
                    sender.sendMessage(language.getLanguageString("HELP.COMMAND.BACKUP"));
                    return true;
                }

                case "info": {
                    String outSize;
                    if (Backup.getSize() > 1024 * 1024 * 1024) outSize = String.format("%.2f",Backup.getSize() / (1024 * 1024 * 1024.00)) + "GB";
                    else if (Backup.getSize() > 1024 * 1024) outSize = String.format("%.2f",Backup.getSize() / (1024 * 1024.00)) + "MB";
                    else if (Backup.getSize() > 1024) outSize = String.format("%.2f",Backup.getSize() / (1024.00)) + "KB";
                    else outSize = Backup.getSize() + "BYTE";

                    String outWaitTime;
                    if (Main.getPlugin().getAutoBackup().getWAIT_TIME() > 86400) outWaitTime = String.format("%.2f",(Main.getPlugin().getAutoBackup().getWAIT_TIME() / 86400.00)) + "d";
                    else if (Main.getPlugin().getAutoBackup().getWAIT_TIME() > 3600) outWaitTime = String.format("%.2f",(Main.getPlugin().getAutoBackup().getWAIT_TIME() / 3600.00)) + "h";
                    else if (Main.getPlugin().getAutoBackup().getWAIT_TIME() > 60) outWaitTime = String.format("%.2f",(Main.getPlugin().getAutoBackup().getWAIT_TIME() / 60.00)) + "min";
                    else outWaitTime = Main.getPlugin().getAutoBackup().getWAIT_TIME() + "s";

                    sender.sendMessage(language.getLanguageString("HELP.DESCRIPTION"));
                    sender.sendMessage(language.getLanguageString("HELP.AUTHOR"));
                    sender.sendMessage(language.getLanguageString("INFO.START"));
                    sender.sendMessage((Main.getPlugin().getAutoBackup() == null || Main.getPlugin().getAutoBackup().isCancelled())
                            ?language.getLanguageString("INFO.CLOSE"):language.getLanguageString("INFO.OPEN"));
                    sender.sendMessage(language.getLanguageString("INFO.TIME") + outWaitTime);
                    sender.sendMessage(language.getLanguageString("INFO.WARN") + Backup.getWarning());
                    sender.sendMessage(language.getLanguageString("INFO.LEVEL") + Backup.getBackupLevel());
                    sender.sendMessage(language.getLanguageString("INFO.SIZE") + outSize);
                    sender.sendMessage(language.getLanguageString("INFO.NUM") + Backup.getNum());
                    return true;
                }

                case "autobackup": {
                    //是否有附加参数
                    if(args.length >= 2 ){
                        if(args[1].equals("true")){
                            if(Main.getPlugin().getAutoBackup().isCancelled()) Main.getPlugin().getAutoBackup().runTaskAsynchronously(Main.getPlugin());
                            sender.sendMessage(language.getLanguageString("INFO.OPEN"));
                            return true;
                        }

                        if(args[1].equals("false")){
                            if (!Main.getPlugin().getAutoBackup().isCancelled()) Main.getPlugin().getAutoBackup().cancel();
                            sender.sendMessage(language.getLanguageString("INFO.CLOSE"));
                            return true;
                        }

                        commandError(sender,null);
                    }
                    return true;
                }
                default: {
                    commandError(sender, null);
                }
            }
            return true;
        }

        sender.sendMessage(language.getLanguageString("HELP.DESCRIPTION"));
        sender.sendMessage(language.getLanguageString("HELP.AUTHOR"));
        sender.sendMessage(language.getLanguageString("COMMAND.LEADING_HELP"));
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        //检测权限
        if (sender instanceof Player && !sender.hasPermission("worldfilebackup")) return null;

        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 0:
            case 1: {
                list.add("help");
                list.add("backup");
                list.add("info");
                list.add("autobackup");
                break;
            }
            case 2:{
                if(args[0].equals("autobackup")){
                    list.add("true");
                    list.add("false");
                }
            }
        }
        return list;
    }
}
