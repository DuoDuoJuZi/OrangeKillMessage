package com.orangekillmessage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerDeath implements Listener {

    private String lastKillerPlayer = null;
    private String lastKilledPlayerName = null;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity(); 
        if (player.getKiller() != null) {
            Entity killerEntity = player.getKiller(); 
            if (killerEntity instanceof Player) {
                lastKillerPlayer = ((Player) killerEntity).getName(); 
            } else {

                lastKillerPlayer = killerEntity.getName();
            }
        } else {
            lastKillerPlayer = OrangeKillMessage.main.getConfig().getString("Lang.defaultKiller");
        }

        lastKilledPlayerName = player.getName();

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (lastKillerPlayer != null) {
            String customMessage = getCustomMessageFromJson(lastKillerPlayer); // 从 JSON 文件中获取自定义消息
            if (customMessage != null) {

                String mainTitle = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', customMessage);
                String subTitle = ChatColor.GOLD + lastKillerPlayer + " 的自定义击杀信息！";

  
                player.sendTitle(mainTitle, subTitle, 10, 70, 20);
            } else {

                String defaultMainTitle = OrangeKillMessage.main.getConfig().getString("Lang.defaultMainTitle");
                String defaultSubTitle = ChatColor.GOLD + lastKillerPlayer + " 的默认击杀信息！";

                player.sendTitle(defaultMainTitle, defaultSubTitle, 10, 70, 20);
            }
            lastKillerPlayer = null;
        }
    }

    private String getCustomMessageFromJson(String playerName) {
        JSONParser parser = new JSONParser();
        File dataFile = new File(OrangeKillMessage.main.getDataFolder(), "data.json");
        try (FileReader reader = new FileReader(dataFile, StandardCharsets.UTF_8)) {
            Object obj = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = (String) jsonObject.get("name");
                String message = (String) jsonObject.get("message");
                if (name != null && name.equals(playerName)) {
                    return message; 
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
