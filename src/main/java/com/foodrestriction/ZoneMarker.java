package com.foodrestriction;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class ZoneMarker {
    
    private static final Map<String, Location> pos1 = new HashMap<>();
    private static final Map<String, Location> pos2 = new HashMap<>();
    
    public static void setFirstPosition(Player player, Location location) {
        pos1.put(player.getName(), location);
        player.sendMessage("§6✓ Позиція 1 встановлена!");
        player.sendMessage("§7X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ());
    }
    
    public static void setSecondPosition(Player player, Location location) {
        pos2.put(player.getName(), location);
        player.sendMessage("§6✓ Позиція 2 встановлена!");
        player.sendMessage("§7X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ());
    }
    
    public static Location getFirstPosition(Player player) {
        return pos1.get(player.getName());
    }
    
    public static Location getSecondPosition(Player player) {
        return pos2.get(player.getName());
    }
    
    public static boolean hasPositions(Player player) {
        return pos1.containsKey(player.getName()) && pos2.containsKey(player.getName());
    }
    
    public static void clearPositions(Player player) {
        pos1.remove(player.getName());
        pos2.remove(player.getName());
    }
    
    public static String getPositionInfo(Player player) {
        StringBuilder info = new StringBuilder();
        
        if (pos1.containsKey(player.getName())) {
            Location p1 = pos1.get(player.getName());
            info.append("§6Позиція 1:§7 X:").append(p1.getBlockX())
                .append(" Y:").append(p1.getBlockY())
                .append(" Z:").append(p1.getBlockZ());
        } else {
            info.append("§7Позиція 1: не встановлена");
        }
        
        info.append("\n");
        
        if (pos2.containsKey(player.getName())) {
            Location p2 = pos2.get(player.getName());
            info.append("§6Позиція 2:§7 X:").append(p2.getBlockX())
                .append(" Y:").append(p2.getBlockY())
                .append(" Z:").append(p2.getBlockZ());
        } else {
            info.append("§7Позиція 2: не встановлена");
        }
        
        return info.toString();
    }
}
