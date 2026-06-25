package com.foodrestriction.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import com.foodrestriction.FoodRestrictionPlugin;
import com.foodrestriction.ZoneMarker;
import com.foodrestriction.config.ConfigManager.FoodZone;

public class FoodEatingListener implements Listener {
    
    private FoodRestrictionPlugin plugin;
    
    public FoodEatingListener(FoodRestrictionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        
        // ════════════════════════════════════════════════════════════
        // МОТИГА ДЛЯ ПОЗНАЧЕННЯ МЕНЬНИЦІ (ТІЛЬКИ ДЛЯ АДМИНІВ)
        // ════════════════════════════════════════════════════════════
        
        if (item != null && item.getType() == Material.NETHERITE_PICKAXE) {
            
            if (!player.isOp() && !player.hasPermission("foodrestriction.zone.create")) {
                player.sendMessage(ChatColor.RED + "✗ Тільки адміни можуть виділяти території!");
                event.setCancelled(true);
                return;
            }
            
            if (action == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (block != null) {
                    ZoneMarker.setFirstPosition(player, block.getLocation());
                    player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    player.sendMessage(ChatColor.GREEN + "✓ Позиція 1 встановлена!");
                    player.sendMessage(ChatColor.GRAY + "X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
                    player.sendMessage(ChatColor.YELLOW + "Тепер клікни ПРАВОЮ мотигою на другу точку");
                    player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    event.setCancelled(true);
                    return;
                }
            }
            
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (block != null) {
                    
                    if (ZoneMarker.getFirstPosition(player) == null) {
                        player.sendMessage(ChatColor.RED + "✗ Спочатку клікни ЛІВОЮ мотигою на першу точку!");
                        event.setCancelled(true);
                        return;
                    }
                    
                    ZoneMarker.setSecondPosition(player, block.getLocation());
                    event.setCancelled(true);
                    
                    player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    player.sendMessage(ChatColor.GREEN + "✓ Позиція 2 встановлена!");
                    player.sendMessage(ChatColor.GRAY + "X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
                    player.sendMessage("");
                    player.sendMessage(ZoneMarker.getPositionInfo(player));
                    player.sendMessage("");
                    player.sendMessage(ChatColor.YELLOW + "Тепер виконай команду:");
                    player.sendMessage(ChatColor.WHITE + "/foodzone create <НазваТеріторії>");
                    player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    return;
                }
            }
        }
        
        // ════════════════════════════════════════════════════════════
        // БЛОКУВАННЯ ЇЖІ - ПЕРЕВІРКА ДОЗВОЛЕНОЇ ЗОНИ
        // ════════════════════════════════════════════════════════════
        
        if (item == null) {
            return;
        }
        
        // ПЕРЕВІРКА ЧИ ЦЕ ЇЖА
        if (!isFoodItem(item.getType())) {
            return;
        }
        
        // ПЕРЕВІРКА ДОЗВОЛЕНОЇ ЗОНИ
        FoodZone zone = plugin.getConfigManager().getZoneAtLocation(player.getLocation());
        
        // ✅ ЯКЩО ГРАВЕЦЬ В ДОЗВОЛЕНІЙ ЗОНІ - МОЖНА ЇСТИ
        if (zone != null) {
            return; // ДОЗВОЛИТИ ЇЖУ
        }
        
        // ⛔ ЯКЩО ГРАВЕЦЬ НЕ В ДОЗВОЛЕНІЙ ЗОНІ - ЗАПРЕТИТИ ЇЖУ
        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_RED + "╔═══════════════════════════════════╗");
        player.sendMessage(ChatColor.DARK_RED + "║ ⛔ ЇЖА ЗАПРЕТЕНА! ⛔              ║");
        player.sendMessage(ChatColor.DARK_RED + "╠═══════════════════════════════════╣");
        player.sendMessage(ChatColor.YELLOW + "║ Ти можеш їсти ТІЛЬКИ в зонах     ║");
        player.sendMessage(ChatColor.WHITE + "║ /foodzone list - список зон      ║");
        player.sendMessage(ChatColor.DARK_RED + "╚═══════════════════════════════════╝");
    }
    
    private boolean isFoodItem(Material material) {
        switch (material) {
            // ФРУКТИ
            case APPLE:
            case GOLDEN_APPLE:
            case ENCHANTED_GOLDEN_APPLE:
            case MELON_SLICE:
            case GLOW_BERRIES:
            case SWEET_BERRIES:
            
            // ОВОЧІ
            case CARROT:
            case GOLDEN_CARROT:
            case POTATO:
            case BAKED_POTATO:
            
            // М'ЯСО
            case BEEF:
            case PORKCHOP:
            case MUTTON:
            case CHICKEN:
            case RABBIT:
            case COOKED_BEEF:
            case COOKED_PORKCHOP:
            case COOKED_MUTTON:
            case COOKED_CHICKEN:
            case COOKED_RABBIT:
            
            // РИБА
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
            case COOKED_COD:
            case COOKED_SALMON:
            
            // ХЛІБОБУЛОЧНІ
            case BREAD:
            case COOKIE:
            
            // ІНШЕ
            case SUSPICIOUS_STEW:
            case MUSHROOM_STEW:
            case BEETROOT:
            case BEETROOT_SOUP:
            case DRIED_KELP:
            case COCOA_BEANS:
            case HONEY_BOTTLE:
            case ROTTEN_FLESH:
            case SPIDER_EYE:
            case PUMPKIN_PIE:
            case CAKE:
                return true;
            default:
                return false;
        }
    }
}
