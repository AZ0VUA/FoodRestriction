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
        
        if (item == null) {
            return;
        }
        
        if (!isFoodItem(item.getType())) {
            return;
        }
        
        if (player.isOp() || player.hasPermission("foodrestriction.bypass")) {
            return;
        }
        
        World world = player.getWorld();
        String worldName = world.getName();
        
        if (worldName.equals("world_nether") || worldName.endsWith("_nether")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "✗ Ти не можеш їсти в Нижнему світу!");
            return;
        }
        
        if (worldName.equals("world_the_end") || worldName.endsWith("_the_end")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "✗ Ти не можеш їсти в Ендерміру!");
            return;
        }
        
        FoodZone zone = plugin.getConfigManager().getZoneAtLocation(player.getLocation());
        
        if (zone != null) {
            return;
        }
        
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "✗ У цьому місці їжа заборонена!");
        player.sendMessage(ChatColor.YELLOW + "📍 Йди в дозволену територію для їжи.");
    }
    
    private boolean isFoodItem(Material material) {
        switch (material) {
            case APPLE:
            case BAKED_POTATO:
            case BEEF:
            case BREAD:
            case CARROT:
            case COD:
            case GOLDEN_APPLE:
            case GOLDEN_CARROT:
            case MELON_SLICE:
            case MUTTON:
            case PORKCHOP:
            case POTATO:
            case PUFFERFISH:
            case RABBIT:
            case SALMON:
            case SUSPICIOUS_STEW:
            case SWEET_BERRIES:
            case TROPICAL_FISH:
            case COCOA_BEANS:
            case COOKIE:
            case DRIED_KELP:
            case GLOW_BERRIES:
            case HONEY_BOTTLE:
            case ROTTEN_FLESH:
            case SPIDER_EYE:
                return true;
            default:
                return false;
        }
    }
}
