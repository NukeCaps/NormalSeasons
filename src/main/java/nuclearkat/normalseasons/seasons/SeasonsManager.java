package nuclearkat.normalseasons.seasons;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.util.SeasonEffects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SeasonsManager {

    private final NormalSeasons seasonsInstance = NormalSeasons.getPlugin(NormalSeasons.class);
    private SeasonsList.Seasons currentSeason;
    private final int seasonDurationTicks;

    private static SeasonsManager instance;

    private SeasonsManager(){
        this.seasonDurationTicks = NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getInt("season.season_duration_ticks");
        currentSeason = SeasonsList.Seasons.SPRING;
        scheduleSeasonChange();
    }

    public static SeasonsManager getInstance(){
        if (instance == null){
            instance = new SeasonsManager();
        }
        return instance;
    }

    public void scheduleSeasonChange(){
        new BukkitRunnable(){
            @Override
            public void run(){
                SeasonEffects.cancelTasks();
                rotateSeason();
            }
        }.runTaskLaterAsynchronously(seasonsInstance, seasonDurationTicks);

    }

    private void rotateSeason(){
        int nextOrdinal = (currentSeason.ordinal() + 1) % SeasonsList.Seasons.values().length;
        currentSeason = SeasonsList.Seasons.values()[nextOrdinal];
        applySeasonEffects();
        scheduleSeasonChange();
    }

    private final List<Player> playerToggleVisuals = new ArrayList<>();

    public List<Player> getPlayerToggleVisuals(){
        return playerToggleVisuals;
    }

    private void applySeasonEffects() {
        String seasonChangeMessage = seasonsInstance.getConfig().getString("season.season_change_message");

        if (seasonChangeMessage != null) {
            seasonChangeMessage = seasonChangeMessage.replace("%SeasonName%", getCurrentSeason().getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', seasonChangeMessage));

            Bukkit.getWorlds().forEach(world -> {
                world.getPlayers().forEach(player -> {
                    switch (currentSeason) {
                        case WINTER:
                            SeasonEffects.applyWinterEffects(player);
                            break;
                        case SUMMER:
                            SeasonEffects.applySummerEffects(player);
                            break;
                        case SPRING:
                            SeasonEffects.applySpringEffects(player);
                            break;
                        case AUTUMN:
                            SeasonEffects.applyAutumnEffects(player);
                            break;
                    }
                });
            });
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Season change message is null. Make sure the configuration value is set correctly.");
        }
    }

    public SeasonsList.Seasons getCurrentSeason(){
        return currentSeason;
    }

}
