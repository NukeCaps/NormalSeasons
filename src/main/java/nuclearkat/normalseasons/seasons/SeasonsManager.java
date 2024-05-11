package nuclearkat.normalseasons.seasons;

import nuclearkat.normalseasons.NormalSeasons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class SeasonsManager {

    private final NormalSeasons seasons;
    private SeasonsList currentSeason;
    private final int seasonDurationTicks;
    private BukkitTask scheduleSeasonChangeTask;
    private final SeasonEffects seasonEffects;
    private static SeasonsManager instance;

    public static SeasonsManager getInstance(SeasonEffects seasonEffects, NormalSeasons seasons){
        if (instance == null){
            instance = new SeasonsManager(seasonEffects, seasons);
        }
        return instance;
    }

    private SeasonsManager(SeasonEffects seasonEffects, NormalSeasons seasons){
        this.seasons = seasons;
        this.seasonDurationTicks = seasons.getConfig().getInt("season.season_duration_ticks");
        SeasonsList.initializeBiomeTemperature();
        currentSeason = SeasonsList.SUMMER;
        this.seasonEffects = seasonEffects;
    }

    public void scheduleSeasonChange(){
        scheduleSeasonChangeTask =  new BukkitRunnable(){
            @Override
            public void run(){
                seasonEffects.cancelAndRemoveTasks();
                rotateSeason();
            }
        }.runTaskLaterAsynchronously(seasons, seasonDurationTicks);
    }

    private void rotateSeason(){
        int nextOrdinal = (currentSeason.ordinal() + 1) % SeasonsList.values().length;
        currentSeason = SeasonsList.values()[nextOrdinal];
        applySeasonEffects();
        scheduleSeasonChange();
    }

    private void applySeasonEffects() {
        String seasonChangeMessage = seasons.getConfig().getString("season.season_change_message");

        if (seasonChangeMessage != null) {
            seasonChangeMessage = seasonChangeMessage.replace("%SeasonName%", getCurrentSeason().getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', seasonChangeMessage));

            Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player -> {
                switch (currentSeason) {
                    case WINTER -> seasonEffects.applyWinterEffects(player);
                    case SPRING -> seasonEffects.applySpringEffects(player);
                    case SUMMER -> seasonEffects.cancelAndRemoveTasks();
                    case AUTUMN -> seasonEffects.applyAutumnEffects(player);
                }
            }));
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Season change message is null. Make sure the configuration value is set correctly.");
        }
    }

    public void cancelTasks(){
        if (scheduleSeasonChangeTask != null){
            scheduleSeasonChangeTask.cancel();
            scheduleSeasonChangeTask = null;
        }
    }

    public SeasonsList getCurrentSeason(){
        return currentSeason;
    }
}
