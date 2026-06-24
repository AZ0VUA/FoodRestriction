package com.foodrestriction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import com.foodrestriction.FoodRestrictionPlugin;
import com.foodrestriction.ZoneMarker;
import com.foodrestriction.config.ConfigManager;
import com.foodrestriction.config.ConfigManager.FoodZone;

public class FoodZoneCommand implements CommandExecutor {
    
    private FoodRestrictionPlugin plugin;
    private static final String PREFIX = ChatColor.BLUE + "[FoodZone] " + ChatColor.RESET;
    
    public FoodZoneCommand(FoodRestrictionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (command.getName().equalsIgnoreCase("foodreload")) {
            return handleReload(sender);
        }
        
        if (command.getName().equalsIgnoreCase("foodzone")) {
            if (args.length == 0) {
                sendHelp(sender);
                return true;
            }
            
            String subcommand = args[0].toLowerCase();
            
            switch (subcommand) {
                case "create":
                    return handleCreate(sender, args);
                case "delete":
                    return handleDelete(sender, args);
                case "list":
                    return handleList(sender);
                case "info":
                    return handleInfo(sender);
                default:
                    sendHelp(sender);
                    return true;
            }
        }
        
        return false;
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("foodrestriction.zone.create")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Немаєму прав!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Ця команда працює тільки для гравців!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Використання: /foodzone create <назва>");
            sender.sendMessage(ChatColor.GRAY + "Клікни палицею на 2 точки, потім виконай команду");
            return true;
        }
        
        Player player = (Player) sender;
        String zoneName = args[1];
        
        Location pos1 = ZoneMarker.getFirstPosition(player);
        Location pos2 = ZoneMarker.getSecondPosition(player);
        
        if (pos1 == null || pos2 == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Спочатку позначь дві точки палицею!");
            return true;
        }
        
        ConfigManager configManager = plugin.getConfigManager();
        if (configManager.createZone(zoneName, pos1, pos2)) {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "✓ Зона '" + zoneName + "' успішно створена!");
            int volume = Math.abs(pos1.getBlockX() - pos2.getBlockX()) *
                        Math.abs(pos1.getBlockY() - pos2.getBlockY()) *
                        Math.abs(pos1.getBlockZ() - pos2.getBlockZ());
            sender.sendMessage(ChatColor.GRAY + "Обсяг: " + volume + " блоків");
            ZoneMarker.clearPositions(player);
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "✗ Зона з такою назвою вже існує!");
        }
        
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("foodrestriction.zone.delete")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Немаєму прав!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Використання: /foodzone delete <назва>");
            return true;
        }
        
        String zoneName = args[1];
        ConfigManager configManager = plugin.getConfigManager();
        
        if (configManager.deleteZone(zoneName)) {
            sender.sendMessage(PREFIX + ChatColor.GREEN + "✓ Зона '" + zoneName + "' видалена!");
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "✗ Зона не знайдена!");
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("foodrestriction.zone.list")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Немаєму прав!");
            return true;
        }
        
        ConfigManager configManager = plugin.getConfigManager();
        java.util.Collection<FoodZone> zones = configManager.getAllZones();
        
        sender.sendMessage(PREFIX + ChatColor.BLUE + "════════════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "Дозволені зони для їжи (" + zones.size() + "):");
        sender.sendMessage("");
        
        if (zones.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Немає дозволених зон");
        } else {
            for (FoodZone zone : zones) {
                sender.sendMessage(ChatColor.GREEN + "  • " + ChatColor.WHITE + zone.getName() + 
                                 ChatColor.GRAY + " [" + zone.getWorld() + "]");
            }
        }
        
        sender.sendMessage(ChatColor.BLUE + "════════════════════════════════════");
        return true;
    }
    
    private boolean handleInfo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Ця команда тільки для гравців!");
            return true;
        }
        
        Player player = (Player) sender;
        ConfigManager configManager = plugin.getConfigManager();
        FoodZone zone = configManager.getZoneAtLocation(player.getLocation());
        
        sender.sendMessage(PREFIX + ChatColor.BLUE + "════════════════════════════════════");
        if (zone != null) {
            sender.sendMessage(ChatColor.GREEN + "✓ Ти в дозволеній зоні: " + ChatColor.WHITE + zone.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "✗ Ти НЕ в дозволеній зоні!");
        }
        sender.sendMessage(ChatColor.BLUE + "════════════════════════════════════");
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("foodrestriction.reload")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Немаєму прав!");
            return true;
        }
        
        plugin.getConfigManager().loadConfig();
        sender.sendMessage(PREFIX + ChatColor.GREEN + "✓ Конфіг перезавантажено!");
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "╔════════════════════════════════════╗");
        sender.sendMessage(ChatColor.BLUE + "║   FoodRestriction Команди          ║");
        sender.sendMessage(ChatColor.BLUE + "╠════════════════════════════════════╣");
        sender.sendMessage(ChatColor.YELLOW + "/foodzone create <назва>" + ChatColor.GRAY + " - Створити нову зону");
        sender.sendMessage(ChatColor.YELLOW + "/foodzone delete <назва>" + ChatColor.GRAY + " - Видалити зону");
        sender.sendMessage(ChatColor.YELLOW + "/foodzone list" + ChatColor.GRAY + " - Список всіх зон");
        sender.sendMessage(ChatColor.YELLOW + "/foodzone info" + ChatColor.GRAY + " - Інформація про твоє місце");
        sender.sendMessage(ChatColor.YELLOW + "/foodreload" + ChatColor.GRAY + " - Перезавантажити конфіг");
        sender.sendMessage(ChatColor.BLUE + "╚════════════════════════════════════╝");
    }
}
