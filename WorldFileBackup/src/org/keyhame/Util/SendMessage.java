package org.keyhame.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SendMessage {
    private static final String PLUGIN_NAME = "[" + ChatColor.BLUE + "WFB" + ChatColor.WHITE+ "]";

    /**
     * 此方法用于以本插件向所有玩家发布信息
     * @param message 消息
     */
    public static void sendMessageToAllPlayer(String message){
        for (Player player : Bukkit.getOnlinePlayers() ){
            player.sendMessage(PLUGIN_NAME + message);
        }
    }

    /**
     * 此方法用于以本插件向所有玩家发布信息
     * @param messages 消息
     */
    public static void sendMessageToAllPlayer(String[] messages){
        if(messages == null || messages.length == 0)return;

        for(String message: messages){
            sendMessageToAllPlayer(message);
        }

    }

    /**
     * 向op广播信息
     * @param message 信息
     */
    public static void sendMessagesToOps(String message){
        for (OfflinePlayer player : Bukkit.getOperators()){
            if ( player.isOnline() ){
                Objects.requireNonNull(player.getPlayer()).sendMessage(PLUGIN_NAME + message);
            }
        }
    }

    public static void sendMessagesToOps(String[] messages){
        if(messages == null || messages.length == 0)return;

        for(String message: messages){
            sendMessagesToOps(message);
        }
    }
}
