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
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                String playerName = player.getName(); 
                String message = strings[1];


                if (!player.hasPermission("orangekillmessage.set")) {
                    player.sendMessage(ChatColor.RED + "你没有执行该命令的权限！");
                    return true;
                }


                JSONArray customMessages = loadCustomMessages();


                removeExistingMessage(customMessages, playerName);

              
                JSONObject playerMessage = new JSONObject();
                playerMessage.put("name", playerName);
                playerMessage.put("message", message);

               
                customMessages.add(playerMessage);

          
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
