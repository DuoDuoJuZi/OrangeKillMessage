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
        Player player = event.getEntity(); // 获取死亡的玩家对象
        if (player.getKiller() != null) {
            Entity killerEntity = player.getKiller(); // 获取击杀者的实体对象
            if (killerEntity instanceof Player) {
                lastKillerPlayer = ((Player) killerEntity).getName(); // 如果击杀者是玩家，获取玩家的ID作为变量
            } else {
                //lastKillerPlayer = killerEntity.getType().name();
                lastKillerPlayer = killerEntity.getName();
            }
        } else {
            lastKillerPlayer = OrangeKillMessage.main.getConfig().getString("Lang.defaultKiller"); // 没有击杀者时设置默认值
        }

        lastKilledPlayerName = player.getName();
        //String deathMessage = OrangeKillMessage.main.getConfig().getString("Lang.deathed");
        //event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (lastKillerPlayer != null) {
            String customMessage = getCustomMessageFromJson(lastKillerPlayer); // 从 JSON 文件中获取自定义消息
            if (customMessage != null) {
                // 替换标题中的默认消息为自定义消息
                String mainTitle = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', customMessage);
                String subTitle = ChatColor.GOLD + lastKillerPlayer + " 的自定义击杀信息！";

                // 发送标题给重生的玩家
                player.sendTitle(mainTitle, subTitle, 10, 70, 20);
            } else {
                // 发送默认标题消息
                String defaultMainTitle = OrangeKillMessage.main.getConfig().getString("Lang.defaultMainTitle");
                String defaultSubTitle = ChatColor.GOLD + lastKillerPlayer + " 的默认击杀信息！";

                // 发送标题给重生的玩家
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
                    return message; // 返回自定义消息
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}