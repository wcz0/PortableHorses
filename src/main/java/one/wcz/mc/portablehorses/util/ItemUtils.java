package one.wcz.mc.portablehorses.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

	// 添加附魔, 让它看起来和普通的鞍有所差别
    public static void addGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
    
    
    public List<Material> carpets = new ArrayList<Material>();
    
    public ItemUtils() {
    	carpets.add(Material.WHITE_CARPET);
    	carpets.add(Material.ORANGE_CARPET);
    	carpets.add(Material.MAGENTA_CARPET);
    	carpets.add(Material.LIGHT_BLUE_CARPET);
    	carpets.add(Material.LIGHT_GRAY_CARPET);
    	carpets.add(Material.YELLOW_CARPET);
    	carpets.add(Material.LIME_CARPET);
    	carpets.add(Material.PINK_CARPET);
    	carpets.add(Material.GRAY_CARPET);
    	carpets.add(Material.CYAN_CARPET);
    	carpets.add(Material.RED_CARPET);
    	carpets.add(Material.BLACK_CARPET);
    	carpets.add(Material.GREEN_CARPET);
    	carpets.add(Material.PURPLE_CARPET);
    	carpets.add(Material.BLUE_CARPET);
    	carpets.add(Material.BROWN_CARPET);
    }
}
