package rcfish;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FishingTimesLoop extends BukkitRunnable {

	private Main main;
	private RCFishConfig config;
	
	public FishingTimesLoop(Main main, RCFishConfig config){
		this.main = main;
		this.config = config;
	}
	@Override
	public void run() {
		Date date = null;
		Calendar cur_time = Calendar.getInstance();
		Calendar conf_time = Calendar.getInstance();
		for(String time_str:config.fishingTimes){
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			try {
				date = format.parse(time_str);
				conf_time.setTime(date);
				if((cur_time.get(Calendar.HOUR_OF_DAY) == conf_time.get(Calendar.HOUR_OF_DAY)) && (cur_time.get(Calendar.MINUTE) == conf_time.get(Calendar.MINUTE))){
					this.main.log.severe("[RCFISH] Started time from config " + String.valueOf(conf_time.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(conf_time.get(Calendar.MINUTE)));
					this.main.startJoinFishing();
				}
			} catch (ParseException e) {
				this.main.log.severe("[RCFISH] Bad time format in a config file.");
			}	
			
		}
	}

}
