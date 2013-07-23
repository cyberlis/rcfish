package rcfish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;


public class Main extends JavaPlugin{
	public Logger log = Bukkit.getLogger();
	public RCFishConfig config;
	public RCFishListener rcfishl;
	public HashMap<Player, Integer> fishPlayers = new HashMap<Player, Integer>();
	public HashMap<Player, Location> fishPlayersLocations = new HashMap<Player, Location>();
	public Boolean joinStarted = false;
	public Boolean fishingStarted = false;
	public RCFishCommand commandl;
	public BukkitTask mainTask;
	public BukkitTask countdownTask;
	public BukkitTask fishingTask;
	public int countdown = 0;
	
	@Override
	public void onEnable(){
		log.severe("[RCFish] Plugin started.");
		config = new RCFishConfig(this);
		config.loadConfig();
		commandl = new RCFishCommand(this, config);
		getCommand("rcfish").setExecutor(commandl);
		mainTask = new FishingTimesLoop(this, config).runTaskTimer(this, 0L, 1200L);
		rcfishl = new RCFishListener(this, config);
		getServer().getPluginManager().registerEvents(rcfishl, this);
	}
	
	@Override
	public void onDisable(){
		config.saveConfig();
		config = null;
		fishPlayers = null;
		log.severe("[RCFish] Plugin stoped.");
		log = null;
	}
	
	public void addPlayer(Player pl, Integer fcount){
		fishPlayers.put(pl, fcount);
	}
	
	public void removePlayer(Player pl){
		fishPlayers.remove(pl);
	}
	
	public void startJoinFishing(){
		if(!joinStarted && !fishingStarted){
			joinStarted = true;
			countdown = config.countdownMinutes;
			countdownTask = null;
			countdownTask = getServer().getScheduler().runTaskTimer(this, new Runnable() {
				public void run() {
					if(countdown != 0){
						getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Рыбалка начнется через "+String.valueOf(countdown)+" мин. Вы можете присоедениться /rcfish join.");
						countdown--;
					} else {
						countdownTask.cancel();
						startFishing();
					}
				
				}
			}, 0L, 1200L);
		} else {
			log.severe("[RCFish]  JoinFishing already started!");
		}
	}
	
	public void startFishing(){
		if(this.fishPlayers.size()<config.minimumPlayers){
			getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Недостаточно игроков для начала рыбалки.");
			fishingStarted = false;
			joinStarted = false;
			fishPlayers.clear();
		} else {
			Location teleport_loc = config.getWarpLocation();
			for(Player pl: this.fishPlayers.keySet()){
				pl.getInventory().addItem(new ItemStack(346, 1));
				this.fishPlayersLocations.put(pl, pl.getLocation());
				pl.teleport(teleport_loc);
			}
			fishingStarted = true;
			joinStarted = false;
			countdownTask.cancel();
			countdownTask = null;
			if (config.fishingMaxTime!=0)
				fishingTask = getServer().getScheduler().runTaskLater(this, new Runnable(){
	
					@Override
					public void run() {
						stopFishing();
					}
					
				}, config.fishingMaxTime);
			
			getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Рыбалка началась. Закидывайте свои удочки!");
		}
	}
	
	public void stopFishing(){
		for(Player pl: this.fishPlayers.keySet()){
			pl.teleport(this.fishPlayersLocations.get(pl));
			this.fishPlayersLocations.get(pl);
		}
		this.fishPlayersLocations.clear();
		this.fishPlayers.clear();
		fishingStarted = false;
		joinStarted = false;
		fishingTask = null;
	}
	
	public void givePresent(Player pl){
		ItemStack tmp_item = config.getPresentItem();
		if(tmp_item != null)
			pl.getInventory().addItem(tmp_item);
		else
			log.severe("[RCFish] Problem with present item from config.");
	}

}

