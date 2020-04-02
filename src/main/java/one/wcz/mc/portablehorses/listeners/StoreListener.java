package one.wcz.mc.portablehorses.listeners;

import one.wcz.mc.portablehorses.Main;
import one.wcz.mc.portablehorses.files.FileManager;
import one.wcz.mc.portablehorses.messages.Message;
import one.wcz.mc.portablehorses.util.ItemUtils;
import one.wcz.mc.portablehorses.util.StoreUtils;

import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.entity.TraderLlama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class StoreListener implements Listener {

    private final List<InventoryAction> ALLOWED_ACTIONS = Arrays.asList(
            InventoryAction.PICKUP_ALL
    );

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {

    	//获取点击的玩家
        Player player = (Player) event.getWhoClicked();

        // Check permission
        if (!player.hasPermission("PortableHorses.store")) {
        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.noPermission", "&c你没有权限!")));
            return;
        }

        // Null checks
        if (event.getClickedInventory() == null) {
            return;
        }

        // 检查马是否属于马基类
        // Check that the inventory belongs to a horse
        if (!(event.getClickedInventory().getHolder() instanceof AbstractHorse)) {
            return;
        }

        if (event.getSlot() != 0 && event.getSlot() != 1) {
            return;
        }

        ItemUtils iu = new ItemUtils();
        
        if (event.getCurrentItem().getType() != Material.SADDLE && !(iu.carpets.contains(event.getCurrentItem().getType()))) {
            return;
        }

        // 如果我们忽略shift-click，就不要继续
        // Don't continue if we're ignoring shift-clicks
        if (Main.getInstance().getConfig().getBoolean("store.shiftClickIgnored")) {
            if (!ALLOWED_ACTIONS.contains(event.getAction())) {
                return;
            }
        }

        // 如果马鞍有lore就停止
        // Don't continue if the clicked saddle has lore
        if (event.getCurrentItem().getItemMeta().hasLore()) {
            return;
        }

        AbstractHorse abstractHorse = (AbstractHorse) event.getClickedInventory().getHolder();

        // 获得一个鞍, 然后写入lore
        ItemStack saddle = new ItemStack(event.getCurrentItem().getType(), 1);
        ItemMeta saddleMeta = saddle.getItemMeta();
        List<String> lore = StoreUtils.createAbstractHorseLore(abstractHorse);

        try {
		    if (abstractHorse instanceof Horse) {
		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.horse", "&a你把这匹马封印到了马鞍里!")));
		    } else if(abstractHorse instanceof TraderLlama)	{
		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.traderllama", "&a你把这只行商羊驼封印到了地毯里!")));
		    } else if (abstractHorse instanceof Llama) {
		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.llama", "&a你把这只羊驼封印到了地毯里!")));
		    } else if(abstractHorse instanceof Donkey) {
		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.donkey", "&a你把这只驴封印到了马鞍里!")));
		    } else if(abstractHorse instanceof Mule) {
		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.mule", "&a你把这匹骡封印到了马鞍里!")));
//		    } else if(abstractHorse instanceof SkeletonHorse) {
//		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.skeletonhorse", "&a你把这匹骷髅马封印到了马鞍里!")));
//		    } else if(abstractHorse instanceof ZombieHorse) {
//		    	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.store.zombiehorse", "&a你把这匹僵尸马封印到了马鞍里!")));
		    } 
        }catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("config.yml is error");
        }
    
        if (abstractHorse instanceof Horse) {
            lore.addAll(StoreUtils.createHorseLore((Horse) abstractHorse));
        } else if (abstractHorse instanceof Llama ) {
            lore.addAll(StoreUtils.createLlamaLore((Llama) abstractHorse));
            Material item = event.getCurrentItem().getType();
            lore.add("Carpet: " + item );
        }

        try {
        saddleMeta.setDisplayName(Message.color(FileManager.getInstance().messages.getString("display-name")));
        }catch(Exception e) {
        	e.printStackTrace();
        }
        saddleMeta.setLore(lore);
        saddle.setItemMeta(saddleMeta);

        ItemUtils.addGlow(saddle);

        // 点击的物品变成空气
        event.setCurrentItem(new ItemStack(Material.AIR));

        // 检测是否装箱子, 掉落箱子
        // Drop the horses chest, if carrying
        if (abstractHorse instanceof ChestedHorse) {
            ChestedHorse chestedHorse = (ChestedHorse) abstractHorse;
            if (chestedHorse.isCarryingChest()) {
                ItemStack chestToDrop = new ItemStack(Material.CHEST, 1);
                chestedHorse.getWorld().dropItem(chestedHorse.getLocation(), chestToDrop);
            }
        }

        // 掉落马背包
        // Drop the horses inventory
        abstractHorse.getInventory().forEach(item -> {
            if (item != null) {
                abstractHorse.getWorld().dropItem(abstractHorse.getLocation(), item);
//                item.setType(Material.AIR);
            }
        });
        
        // 删除马实体
        // Remove the horse
        abstractHorse.remove();
        

        // 检测背包是否已满, 掉落或给予玩家
        // Check for full inventory; Drop item or give item to player
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), saddle);
        } else {
            player.getInventory().addItem(saddle);
        }

        event.setCancelled(true);
        

    }

}
