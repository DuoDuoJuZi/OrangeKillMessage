package com.orangekillmessage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
public final class OrangeKillMessage extends JavaPlugin {

    static OrangeKillMessage main;
    private FileConfiguration config; // 添加配置变量


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("OrangeKillMessage已加载");

        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        config = getConfig();
        getCommand("okm").setExecutor(new PluginInfo());
        getCommand("okmset").setExecutor(new MessageSet(this));
        MessageSet messageSet = new MessageSet(this);
        getCommand("okmset").setExecutor(messageSet);
        getCommand("okmlist").setExecutor(new Messagelist());

        saveDefaultConfig();
        main = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("OrangeKillMessage已卸载");
    }
}
