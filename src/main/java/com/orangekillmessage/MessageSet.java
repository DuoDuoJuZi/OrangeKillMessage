package com.orangekillmessage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageSet implements CommandExecutor {

    private final String filePath;

    private final JSONParser parser = new JSONParser();

    public MessageSet(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.filePath = new File(dataFolder, "data.json").getAbsolutePath();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("okmset") && strings.length > 1 && strings[0].equalsIgnoreCase("set")) {
            // 执行 "okm set xxx" 命令的逻辑
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                String playerName = player.getName(); // 获取玩家名字
                String message = strings[1]; // 获取玩家输入的内容

                // 检查权限
                if (!player.hasPermission("orangekillmessage.set")) {
                    player.sendMessage(ChatColor.RED + "你没有执行该命令的权限！");
                    return true;
                }

                // 读取玩家的自定义击杀信息
                JSONArray customMessages = loadCustomMessages();

                // 移除重复的自定义消息
                removeExistingMessage(customMessages, playerName);

                // 创建玩家的自定义消息对象
                JSONObject playerMessage = new JSONObject();
                playerMessage.put("name", playerName);
                playerMessage.put("message", message);

                // 添加新的自定义击杀信息
                customMessages.add(playerMessage);

                // 将更新后的自定义击杀信息保存到文件中
                saveCustomMessages(customMessages);

                player.sendMessage(OrangeKillMessage.main.getConfig().getString("Lang.saveCustomMessages"));
            }
        }

        return false;
    }

    private JSONArray loadCustomMessages() {
        try (FileReader fileReader = new FileReader(filePath, StandardCharsets.UTF_8)) {
            Object obj = parser.parse(fileReader);
            if (obj instanceof JSONArray) {
                return (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private void removeExistingMessage(JSONArray customMessages, String playerName) {
        for (int i = 0; i < customMessages.size(); i++) {
            JSONObject message = (JSONObject) customMessages.get(i);
            String name = (String) message.get("name");
            if (name.equals(playerName)) {
                customMessages.remove(i);
                break;
            }
        }
    }

    private void saveCustomMessages(JSONArray customMessages) {
        try (FileWriter file = new FileWriter(filePath, StandardCharsets.UTF_8, false)) {
            file.write(customMessages.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
