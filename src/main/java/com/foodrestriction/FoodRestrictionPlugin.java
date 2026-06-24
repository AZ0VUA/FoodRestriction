package com.foodrestriction;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import com.foodrestriction.listeners.FoodEatingListener;
import com.foodrestriction.commands.FoodZoneCommand;
import com.foodrestriction.config.ConfigManager;

public class FoodRestrictionPlugin extends JavaPlugin {
    
    private static FoodRestrictionPlugin instance;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   FoodRestriction 1.0 включений!   ║");
        getLogger().info("║   Розробка: YourName               ║");
        getLogger().info("╚════════════════════════════════════╝");
        
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        Bukkit.getPluginManager().registerEvents(new FoodEatingListener(this), this);
        
        getCommand("foodzone").setExecutor(new FoodZoneCommand(this));
        getCommand("foodreload").setExecutor(new FoodZoneCommand(this));
    }
    
    @Override
    public void onDisable() {
        getLogger().info("FoodRestriction плагін отключен!");
    }
    
    public static FoodRestrictionPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
