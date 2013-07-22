package rcfish;


import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


class ValueComparator implements Comparator<Player> {

    Map<Player, Integer> base;
    public ValueComparator(Map<Player, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Player a, Player b) {
    	Integer x = base.get(a);
    	Integer y = base.get(b);
        if(x.equals(y)){
        	return a.getName().compareTo(b.getName());
        } else {
        	return x.compareTo(y);
        }
    }
}

public class RCFishCommand implements  CommandExecutor{
	@SuppressWarnings("unused")
	private Main main;
	private RCFishConfig config;
    private ValueComparator bvc; 
    private TreeMap<Player,Integer> sorted_map;
	
	
	RCFishCommand(Main main, RCFishConfig config) {
		this.main = main;
		this.config = config;
		bvc =  new ValueComparator(this.main.fishPlayers);
		sorted_map  = new TreeMap<Player,Integer>(bvc); 
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2,
			String[] args) {
		Player player = null;
		if ((sender instanceof Player)) {
			player = (Player) sender;
		}
		
		//now handle commands
		if (args.length == 0) {
			sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Используйте команд /rcfish help для получения списка комманд");
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
		{

			if((player != null && player.isOp()) || sender instanceof ConsoleCommandSender){
				config.loadConfig();
				sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Конфиг перезагружен");
				return true;
			} else {
				return false;
			}
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("save"))
		{

			if((player != null && player.isOp()) || sender instanceof ConsoleCommandSender){
				config.saveConfig();
				sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Конфиг сохранен");
				return true;
			} else {
				return false;
			}
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("help"))
		{
			displayHelp(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("join"))
		{
			joinFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("leave"))
		{
			leaveFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("stats"))
		{
			listFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("startjoin"))
		{
			startJoinFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("startfishing"))
		{
			startFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("setwarp"))
		{
			setFishingWarp(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("present"))
		{
			giveFishingPresent(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("stopfishing"))
		{
			stopFishing(sender);
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("teleport"))
		{
			testTeleport(sender);
			return true;
		}

		return false;
	}
	
	
	
	
	private void displayHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.AQUA+"/rcfish join "+ChatColor.WHITE+"-"+ChatColor.BLUE+" присоединиться к рыбалке.");
		sender.sendMessage(ChatColor.AQUA+"/rcfish leave "+ChatColor.WHITE+"-"+ChatColor.BLUE+" покинуть рыбалку.");
		sender.sendMessage(ChatColor.AQUA+"/rcfish list"+ChatColor.WHITE+"-"+ChatColor.BLUE+" список игроков.");
		sender.sendMessage(ChatColor.AQUA+"/rcfish stats"+ChatColor.WHITE+"-"+ChatColor.BLUE+" своя статистика.");
	}
	
	private void joinFishing(CommandSender sender)
	{
		this.main.log.severe("Start joining");
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
			this.main.log.severe("is player");
			if(this.main.joinStarted){
				if (this.main.fishPlayers.containsKey(pl)) {
					pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Вы уже участник рыбалки.");
				} else {
					this.main.fishPlayers.put(pl, 0);
					pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Вы присоединились к рыбалке.");
				}
			} else {
				pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Набора на рыбалку пока еще нет.");
			}
			
		}
		else
		{
			sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" В консоле вводить комманды rcfish нельзя.");
		}
	}
	
	private void leaveFishing(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
			if(this.main.joinStarted){
				if (this.main.fishPlayers.containsKey(pl)) {
					this.main.fishPlayers.remove(pl);
					pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Вы покинули рыбалку");
				} else {
					pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Вы не учавствуете в рыбалке.");
				}
			} else {
				pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Набора на рыбалку пока еще нет.");
			}
			
		}
		else
		{
			sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" В консоле вводить комманды rcfish нельзя.");
		}
	}
	
	private void listFishing(CommandSender sender)
	{
		if (sender instanceof Player && this.main.fishPlayers.size()>0)
		{
			Player pl = (Player) sender;
			sorted_map.putAll(this.main.fishPlayers);
			pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Список участников рыбалки:");
			for(Player key: sorted_map.keySet()){
				pl.sendMessage(ChatColor.BLUE+key.getName()+" поймал "+String.valueOf(sorted_map.get(key)));
			}			
			sorted_map.clear();
		}
		else
		{
			sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Нет игроков, учавствующих в рыбалке");
		}
	}
	
	private void startJoinFishing(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp()) || sender instanceof ConsoleCommandSender){
			if(config.fishingWarp.equalsIgnoreCase("0,0,0")){
				sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Не установлен варп для рыбалки");
				return;
			}
			this.main.startJoinFishing();
		}
	}
	
	private void startFishing(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp()) || sender instanceof ConsoleCommandSender){
			if(config.fishingWarp.equalsIgnoreCase("0,0,0")){
				sender.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Не установлен варп для рыбалки");
				return;
			}
			this.main.startFishing();
		}
	}
	
	private void setFishingWarp(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp())){
			Location temp = pl.getLocation();
			String ln_str = String.valueOf(temp.getBlockX())+", "+String.valueOf(temp.getBlockY())+", "+String.valueOf(temp.getBlockZ());
			config.setWarpLocation(temp);
			pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Варп для рыбалки установлен: "+ln_str);
		}
	}
	
	
	private void stopFishing(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp())){
			this.main.stopFishing();
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Рыбалка была прервана администратором.");
		}
	}
	
	private void giveFishingPresent(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp())){
			this.main.givePresent(pl);
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Выдан тестовый подарок.");
		}
	}
	
	private void testTeleport(CommandSender sender)
	{
		Player pl = null;
		if (sender instanceof Player)
		{
			pl = (Player) sender;
		}
		if((pl != null && pl.isOp())){
			pl.teleport(config.getWarpLocation());
		}
	}
	

}

