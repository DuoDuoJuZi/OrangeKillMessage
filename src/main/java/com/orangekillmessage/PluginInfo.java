package com.orangekillmessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;


public class PluginInfo implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1 && strings[0].equals("help")) {
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.pluginname"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.Help.set"));
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.Help.setother"));
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.Help.list"));
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.Help.reload"));
            commandSender.sendMessage(" ");
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.Help.logogram"));

            return true;
        }
        if (strings.length == 1 && strings[0].equals("reload")){
            OrangeKillMessage.main.reloadConfig();
            commandSender.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.reload"));
        }
        return false;
    }
}
