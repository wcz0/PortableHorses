package one.wcz.mc.portablehorses.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import one.wcz.mc.portablehorses.Main;
import one.wcz.mc.portablehorses.files.FileManager;
import one.wcz.mc.portablehorses.messages.Message;
import one.wcz.mc.portablehorses.util.ItemUtils;
import one.wcz.mc.portablehorses.util.PlaceUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlaceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 检测权限
        // Check permission
        if (!player.hasPermission("PortableHorses.place")) {
        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.noPermission", "&c你没有权限!")));
            return;
        }
        
        if (item == null) {
            return;
        }
        
        
        // 判断主副手
        if(event.getHand().equals(EquipmentSlot.HAND)) {
        	return;
        }

        // 确保玩家正在点击一个方块
        // Ensure the player is clicking a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemUtils iu = new ItemUtils();

        // 检测物品是不是鞍或者地毯
        // Check that it's a saddle or carpet
        if (item.getType() != Material.SADDLE && !(iu.carpets.contains(item.getType()))) {
            return;
        }
        

        // 检测物品有没有lore
        // Check that it has a lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        
        // 获取物品的属性
        ItemMeta itemMeta = item.getItemMeta();

        
        // 判断物品的lore是否含有"Name: "
        if (!itemMeta.getLore().stream().anyMatch(lore -> lore.contains("Name: "))) {
            return;
        }

        // 检测物品是否能放在交互方块
        // TODO Check if placing on intractable block
        World world = event.getClickedBlock().getWorld();
        double x = event.getClickedBlock().getX();
        double y = event.getClickedBlock().getY();
        double z = event.getClickedBlock().getZ();
        
        
        if(Main.getInstance().getConfig().getBoolean("place.safeSpawning")) {
        	if(new Location(world, x, y+1, z).getBlock().getType()!=Material.AIR||new Location(world, x, y+2, z).getBlock().getType()!=Material.AIR||new Location(world, x, y+2, z-1).getBlock().getType()!=Material.AIR||new Location(world, x, y+3, z).getBlock().getType()!=Material.AIR||new Location(world, x-1, y+2, z).getBlock().getType()!=Material.AIR||new Location(world, x-1, y+2, z-1).getBlock().getType()!=Material.AIR) {
        		player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.cantSpawnThere", "&d没有足够的空间!")));
        		return;
        	}
        }
            
        Location spawnLocation = new Location(world, x, y+1, z);

        AbstractHorse abstractHorse = createAbstractHorse(itemMeta, spawnLocation, player);
        
        try {
	        if (abstractHorse instanceof Horse) {
	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.horse", "&a你放出了马鞍中的马!")));
	        } else if(abstractHorse instanceof TraderLlama)	{
	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.traderllama", "&a你放出了地毯中的行商羊驼!")));
	        } else if (abstractHorse instanceof Llama) {
	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.llama", "&a你放出了地毯中的羊驼!")));
	        } else if(abstractHorse instanceof Donkey) {
	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.donkey", "&a你放出了马鞍中的驴!")));
	        } else if(abstractHorse instanceof Mule) {
	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.mule", "&a你放出了马鞍中的骡!")));
//	        } else if(abstractHorse instanceof SkeletonHorse) {
//	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.skeletonhorse", "&a你放出了马鞍中的骷髅马!")));
//	        } else if(abstractHorse instanceof ZombieHorse) {
//	        	player.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.place.zombiehorse", "&a你放出了马鞍中的僵尸马!")));
	
	        } 
        }catch(Exception e) {
        	System.out.println(e);
        	System.out.println("messages.yml is error");
        }
        
        // Something went wrong, let's stop
        if (abstractHorse == null) {
            return;
        }

        player.getInventory().removeItem(item);
        
        event.setCancelled(true);

        

    }

    // 创建马匹的方法
    private AbstractHorse createAbstractHorse(ItemMeta itemMeta, Location location, Player spawner) {

        List<String> lore = itemMeta.getLore();
        
        // 检测有效的行/lore, 如果需要抛出异常
        // TODO Check for valid lines/lore first, throw exceptions if neeeded

        // stream() 返回一个集合的流
        // filter() 返回由与此给定谓词匹配的此流的元素组成的流
        // findFirst() 返回描述此流的第一个元素的Optional如果流为空，则返回一个空的Optional 
        // get() 如果 Optional中有一个值，返回值，否则抛出 NoSuchElementException 
        // replace() 替换字符串
        String variant = lore.stream().filter(str -> str.contains("Variant: ")).findFirst().get().replace("Variant: ", "");
        String name = lore.stream().filter(str -> str.contains("Name: ")).findFirst().get().replace("Name: ", "");
        
        String ownerUuid = "";
        try {
            ownerUuid = lore.stream().filter(str -> str.contains("Owner UUID: ")).findFirst().get().replace("Owner UUID: ", "");
        } catch (NoSuchElementException e) {
            ownerUuid = lore.stream().filter(str -> str.contains("UUID: ")).findFirst().get().replace("UUID: ", "");
        }        

        String domestication = lore.stream().filter(str -> str.contains("Domestication: ")).findFirst().get().replace("Domestication: ", "");
        String health = lore.stream().filter(str -> str.contains("Health: ")).findFirst().get().replace("Health: ", "");

        double jump = 0.0D;
        try {
            jump = Double.valueOf(lore.stream().filter(str -> str.contains("Real Jump: ")).findFirst().get().replace("Real Jump: ", ""));
        } catch (NoSuchElementException e) {
            jump = Double.valueOf(lore.stream().filter(str -> str.contains("Jump: ")).findFirst().get().replace("Jump: ", ""));
        }
        
        double speed = 0.0D;
        try {
            speed = Double.valueOf(lore.stream().filter(str -> str.contains("Real Speed: ")).findFirst().get().replace("Real Speed: ", ""));
        } catch (NoSuchElementException e) {
            speed = Double.valueOf(lore.stream().filter(str -> str.contains("Speed: ")).findFirst().get().replace("Speed: ", ""));
        }


        Material m = null;
        try {
           	m = Material.valueOf(lore.stream().filter(str -> str.contains("Carpet: ")).findFirst().get().replace("Carpet: ", ""));
        }catch(NoSuchElementException e) {
        	m = Material.AIR;
        }
        

        AbstractHorse abstractHorse;

        try {
        	// 反射出variant类的实例对象
            Class<?> clazz = Class.forName("org.bukkit.entity." + PlaceUtils.translateOldVariants(variant));
            // asSubclass 获得马基类的一个子类
            Class<? extends AbstractHorse> subclass = clazz.asSubclass(AbstractHorse.class);
            
            abstractHorse = location.getWorld().spawn(location, subclass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if (!name.equals("None")) {
            abstractHorse.setCustomName(name);
        }

        if (!ownerUuid.equals("None")) {
            abstractHorse.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid)));
        } else {
            abstractHorse.setOwner(Bukkit.getOfflinePlayer(spawner.getUniqueId()));
        }

        String[] horseHealth = health.replace("Health: ", "").split("/");
        String[] horseDom = domestication.replace("Domestication: ", "").split("/");

        double horseCurrentHealth = Double.valueOf(horseHealth[0]);
        double horseMaxHealth = Double.valueOf(horseHealth[1]);
        int horseCurrentDom = Integer.valueOf(horseDom[0]);
        int horseMaxDom = Integer.valueOf(horseDom[1]);
        
        // 从lore中读取数值, 创建对应数值的马
        // 设置马的最大血量
        abstractHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(horseMaxHealth);
        //设置马的存储血量
        abstractHorse.setHealth(horseCurrentHealth);
        abstractHorse.setMaxDomestication(horseMaxDom);
        abstractHorse.setDomestication(horseCurrentDom);

        abstractHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(jump);
        abstractHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        if (abstractHorse instanceof Horse) {
            PlaceUtils.createHorse((Horse) abstractHorse, lore);
        } else if (abstractHorse instanceof Llama) {
            PlaceUtils.createLlama((Llama) abstractHorse, lore);
        }

        if (abstractHorse instanceof Horse) {
            PlaceUtils.giveSaddle(abstractHorse);
        } else if (abstractHorse instanceof Llama) {
            PlaceUtils.giveCarpet(abstractHorse, m);
        } else {
            abstractHorse.getInventory().setItem(0, new ItemStack(Material.SADDLE, 1));
        }

        return abstractHorse;
    }

}
