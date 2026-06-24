package com.foodrestriction.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    
    private JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;
    private Map<String, FoodZone> zones;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.zones = new HashMap<>();
        
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        configFile = new File(plugin.getDataFolder(), "zones.yml");
    }
    
    public void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        zones.clear();
        
        if (config.contains("zones")) {
            for (String zoneName : config.getConfigurationSection("zones").getKeys(false)) {
                String path = "zones." + zoneName;
                
                String world = config.getString(path + ".world");
                int x1 = config.getInt(path + ".x1");
                int y1 = config.getInt(path + ".y1");
                int z1 = config.getInt(path + ".z1");
                int x2 = config.getInt(path + ".x2");
                int y2 = config.getInt(path + ".y2");
                int z2 = config.getInt(path + ".z2");
                
                zones.put(zoneName, new FoodZone(zoneName, world, x1, y1, z1, x2, y2, z2));
            }
        }
        
        plugin.getLogger().info("Завантажено " + zones.size() + " зон їжі");
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Помилка при збереженні конфіга: " + e.getMessage());
        }
    }
    
    private void createDefaultConfig() {
        try {
            configFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(configFile);
            
            config.set("zones.Example_Zone.world", "world");
            config.set("zones.Example_Zone.x1", 100);
            config.set("zones.Example_Zone.y1", 60);
            config.set("zones.Example_Zone.z1", 100);
            config.set("zones.Example_Zone.x2", 200);
            config.set("zones.Example_Zone.y2", 150);
            config.set("zones.Example_Zone.z2", 200);
            
            config.save(configFile);
            plugin.getLogger().info("Створено стандартну конфігурацію zones.yml");
        } catch (IOException e) {
            plugin.getLogger().severe("Помилка при створенні конфіга: " + e.getMessage());
        }
    }
    
    public boolean createZone(String name, Location loc1, Location loc2) {
        if (zones.containsKey(name)) {
            return false;
        }
        
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        
        String world = loc1.getWorld().getName();
        
        FoodZone zone = new FoodZone(name, world, x1, y1, z1, x2, y2, z2);
        zones.put(name, zone);
        
        String path = "zones." + name;
        config.set(path + ".world", world);
        config.set(path + ".x1", x1);
        config.set(path + ".y1", y1);
        config.set(path + ".z1", z1);
        config.set(path + ".x2", x2);
        config.set(path + ".y2", y2);
        config.set(path + ".z2", z2);
        
        saveConfig();
        return true;
    }
    
    public boolean deleteZone(String name) {
        if (!zones.containsKey(name)) {
            return false;
        }
        
        zones.remove(name);
        config.set("zones." + name, null);
        saveConfig();
        return true;
    }
    
    public FoodZone getZone(String name) {
        return zones.get(name);
    }
    
    public Collection<FoodZone> getAllZones() {
        return zones.values();
    }
    
    public FoodZone getZoneAtLocation(Location location) {
        for (FoodZone zone : zones.values()) {
            if (zone.isInZone(location)) {
                return zone;
            }
        }
        return null;
    }
    
    public static class FoodZone {
        private String name;
        private String world;
        private int x1, y1, z1, x2, y2, z2;
        
        public FoodZone(String name, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.name = name;
            this.world = world;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
        
        public boolean isInZone(Location location) {
            if (!location.getWorld().getName().equals(world)) {
                return false;
            }
            
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            
            return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                   y >= Math.min(y1, y2) && y <= Math.max(y1, y2) &&
                   z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
        }
        
        public String getName() { return name; }
        public String getWorld() { return world; }
    }
}
