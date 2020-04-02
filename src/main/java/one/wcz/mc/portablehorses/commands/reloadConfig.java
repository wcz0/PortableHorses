package one.wcz.mc.portablehorses.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.wcz.mc.portablehorses.files.FileManager;
import one.wcz.mc.portablehorses.messages.Message;

public class reloadConfig implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("phorse")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.hasPermission("PortableHorses.admin")) {
					p.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.noPermission", "&c你没有权限!")));
					return false;
				}
			}
			if(args.length==0) {
				sender.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color("&3use &a/phorse reload &3to reload config.yml"));
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")) {
				
				FileManager.getInstance().loadFiles();
				FileManager.getInstance().reloadConfig();
				sender.sendMessage(Message.color(FileManager.getInstance().messages.getString("prefix", "&a[&fPortable&bHorses&a]: &7"))+Message.color(FileManager.getInstance().messages.getString("messages.reload", "&a成功加载了配置文件!")));
				return true;
			}
			
		}
		
		return false;
	}
	
}
