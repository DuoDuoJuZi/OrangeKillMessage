package com.orangekillmessage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Messagelist implements CommandExecutor {
    private static final int MESSAGES_PER_PAGE = 1;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("okmlist")) {
                File dataFile = new File(OrangeKillMessage.main.getDataFolder(), "data.json");
                if (dataFile.exists()) {
                    try {
                        JSONParser parser = new JSONParser();
                        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(dataFile, StandardCharsets.UTF_8));
                        int totalPages = (int) Math.ceil(jsonArray.size() / (double) MESSAGES_PER_PAGE);

                        int page = 1;
                        if (args.length > 0) {
                            try {
                                page = Integer.parseInt(args[0]);
                                if (page < 1 || page > totalPages) {
                                    player.sendMessage(ChatColor.RED + "无效的页码。");
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "无效的页码。");
                                return true;
                            }
                        }

                        int startIndex = (page - 1) * MESSAGES_PER_PAGE;
                        int endIndex = Math.min(startIndex + MESSAGES_PER_PAGE, jsonArray.size());

                        player.sendMessage(ChatColor.YELLOW + "===== 自定义击杀消息 第 " + page + " 页 / 共 " + totalPages + " 页 =====");
                        for (int i = startIndex; i < endIndex; i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String name = (String) jsonObject.get("name");
                            String message = (String) jsonObject.get("message");
                            String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
                            player.sendMessage(ChatColor.GREEN + "玩家: " + name + " 自定义击杀消息: " + formattedMessage);
                        }

                        if (totalPages > 1) {
                            player.sendMessage(ChatColor.YELLOW + "===== 使用 /okmlist <页码> 查看更多自定义击杀消息 =====");
                        }
                    } catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "无法读取数据文件。");
                        e.printStackTrace();
                    } catch (ParseException e) {
                        player.sendMessage(ChatColor.RED + "数据文件格式错误。");
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "数据文件不存在。");
                }
                return true;
            }
        }
        return false;
    }
}
