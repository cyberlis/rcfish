package rcfish;

import java.util.HashSet;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class RCFishConfig {
	@SuppressWarnings("unused")
	private Main main;
	
	public RCFishConfig(Main main) {
		this.main = main;
	}
	
	protected boolean enableRCFish = true;
	protected HashSet<String> fishingTimes = new HashSet<String>();
	protected Integer minimumPlayers = 3;
	protected Integer winFishCount = 3;
	protected String presentItem = "264";
	protected String fishingWarp = "0,0,0";
	protected Integer presentItemAmount = 1;
	protected Integer countdownMinutes = 5;
	
	public void loadConfig(){
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/RCFish/config.yml"));
		enableRCFish = config.getBoolean("RCFish.enable", enableRCFish);
		fishingTimes = new HashSet<String>(config.getStringList("RCFish.fishingTimes"));
		minimumPlayers = config.getInt("RCFish.minimumPlayers", minimumPlayers);
		winFishCount = config.getInt("RCFish.winFishCount", winFishCount);
		countdownMinutes = config.getInt("RCFish.countdownMinutes", countdownMinutes);
		presentItem = config.getString("RCFish.presentItem", presentItem);
		fishingWarp = config.getString("RCFish.fishingWarp", fishingWarp);
		presentItemAmount = config.getInt("RCFish.presentItemAmount", presentItemAmount);
		
		this.saveConfig();
	}
	
	public void saveConfig(){
		FileConfiguration config = new YamlConfiguration();
		config.set("RCFish.enable", enableRCFish);
		config.set("RCFish.fishingTimes", new ArrayList<String>(fishingTimes));
		config.set("RCFish.minimumPlayers", minimumPlayers);
		config.set("RCFish.winFishCount", winFishCount);
		config.set("RCFish.countdownMinutes", countdownMinutes);
		config.set("RCFish.presentItem", presentItem);
		config.set("RCFish.fishingWarp", fishingWarp);
		config.set("RCFish.presentItemAmount", presentItemAmount);
		try{
			config.save(new File("plugins/RCFish/config.yml"));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Location getWarpLocation(){
		String[] warp_coords = fishingWarp.split(",");
		return new Location(this.main.getServer().getWorld("world"),Integer.parseInt(warp_coords[0]), Integer.parseInt(warp_coords[1]), Integer.parseInt(warp_coords[2]));
	}
	
	public void setWarpLocation(Location new_loc){
		fishingWarp = String.valueOf(new_loc.getBlockX())+","+String.valueOf(new_loc.getBlockY())+","+String.valueOf(new_loc.getBlockZ());
	}
	
	public ItemStack getPresentItem(){
		Integer itemId = 0;
		Integer itemData = 0;
		ItemStack tmp_stack = null;
		try{
			if(presentItem.contains(":")){
				String[] pitem = presentItem.split(":");
				if(pitem.length==2){
					itemId = Integer.parseInt(pitem[0]);
					itemData = Integer.parseInt(pitem[1]);
				}
			} else {
				itemId = Integer.parseInt(presentItem);
			}
		}
		catch(NumberFormatException e){
			itemId = 264;
			presentItem = "264";
			this.main.log.severe("[RCFISH] Bad value of presentItem in a config file!");
		}
		if(itemData!=0 && itemId!=0)
			tmp_stack =  new ItemStack(itemId, 1, itemData.byteValue());
		else if(itemData==0 && itemId!=0)
			tmp_stack =  new ItemStack(itemId, 1);
		return tmp_stack;
	}
}
