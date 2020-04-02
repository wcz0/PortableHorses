
package one.wcz.mc.portablehorses;

import one.wcz.mc.portablehorses.files.ConfigHelper;
import one.wcz.mc.portablehorses.listeners.PlaceListener;
import one.wcz.mc.portablehorses.listeners.StoreListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {

	// �ṩ�����ʵ��
	// Provide instance of Main class
	private static Main instance;

	public Main() {
		instance = this;
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		ConfigHelper.loadConfigs();
		
		// ע������¼�
		// Register events
		getServer().getPluginManager().registerEvents(new StoreListener(), this);
		getServer().getPluginManager().registerEvents(new PlaceListener(), this);
		Bukkit.getPluginCommand("phorse").setExecutor(new one.wcz.mc.portablehorses.commands.reloadConfig());
		getServer().getConsoleSender().sendMessage("PortableHorses is now enable");

	}

}
