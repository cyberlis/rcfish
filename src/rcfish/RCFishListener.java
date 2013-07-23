package rcfish;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class RCFishListener implements Listener{
	private Main main;
	private RCFishConfig config;
	
	RCFishListener(Main main, RCFishConfig config){
		this.main = main;
		this.config = config;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onFishEvent(PlayerFishEvent event){
		Integer winCount = config.winFishCount;
		if(this.main.fishingStarted){
			Player pl = event.getPlayer();
			if( (event.getState() == State.CAUGHT_FISH) && this.main.fishPlayers.containsKey(pl)){
				Integer prev_count = this.main.fishPlayers.get(pl);
				if((prev_count+1) >= winCount){
					this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" В рыбалке победил "+pl.getName());
					this.main.givePresent(pl);
					this.main.stopFishing();
				} else {
					this.main.fishPlayers.put(pl, prev_count+1);
					this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish] "+ChatColor.BLUE+pl.getName()+" поймал рыбу. Теперь у него "+String.valueOf(prev_count+1)+" шт.");
				}
			}
		} else {
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onFishOtherCommands(PlayerCommandPreprocessEvent event){
		if(this.main.fishingStarted){
			Player pl = event.getPlayer();
			if(! event.getMessage().equalsIgnoreCase("/rcfish stats") && this.main.fishingStarted && this.main.fishPlayers.containsKey(pl) && !pl.isOp()){
				event.setCancelled(true);
				pl.sendMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Во время рыбалки можно использовать только /rcfish stats");
			}
		} else {
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onFishQuitServer(PlayerQuitEvent event){
		Player pl = event.getPlayer();
		if(this.main.fishPlayers.containsKey(pl)  && this.main.fishingStarted){
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Из рыбалки выбыл "+pl.getName());
			this.main.fishPlayers.remove(pl);
			if((this.main.fishPlayers.size())<=1 && this.main.fishingStarted){
				this.main.stopFishing();
				this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Недостаточно игроков для продолжения рыбалки.");
			}
		} else if(this.main.fishPlayers.containsKey(pl)  && this.main.joinStarted){
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Из рыбалки выбыл "+pl.getName());
			this.main.fishPlayers.remove(pl);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onFishKickServer(PlayerKickEvent event){
		Player pl = event.getPlayer();
		if(this.main.fishPlayers.containsKey(pl)  && this.main.fishingStarted ){
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Из рыбалки выбыл "+pl.getName());
			this.main.fishPlayers.remove(pl);
			if((this.main.fishPlayers.size())<=1 && this.main.fishingStarted){
				this.main.stopFishing();
				this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Недостаточно игроков для продолжения рыбалки.");
			}
		} else if(this.main.fishPlayers.containsKey(pl)  && this.main.joinStarted){
			this.main.getServer().broadcastMessage(ChatColor.AQUA+"[RCFish]"+ChatColor.BLUE+" Из рыбалки выбыл "+pl.getName());
			this.main.fishPlayers.remove(pl);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFishPlayerRespawn(PlayerRespawnEvent event){
		final Player pl = event.getPlayer();
		if(this.main.fishPlayers.containsKey(pl) && this.main.fishingStarted){
			final Location teleport_loc = config.getWarpLocation();
			pl.getInventory().addItem(new ItemStack(346, 1));
			event.setRespawnLocation(teleport_loc);
			this.main.getServer().getScheduler().runTaskLater(this.main, new Runnable(){
				public void run(){
					pl.teleport(teleport_loc);
				}
			}, 2L);
		}
	}
}
