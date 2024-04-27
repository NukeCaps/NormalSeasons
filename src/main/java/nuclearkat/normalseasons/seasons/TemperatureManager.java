package nuclearkat.normalseasons.seasons;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.util.TemperatureEffects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

import static nuclearkat.normalseasons.seasons.util.TemperatureEffects.*;
import static nuclearkat.normalseasons.seasons.util.TemperatureSystem.*;

public class TemperatureManager {

    private static TemperatureManager instance;

    private TemperatureManager(){
        startTemperatureUpdateTask();
    }

    public static TemperatureManager getInstance(){
        if (instance == null){
            instance = new TemperatureManager();
        }
        return instance;
    }

    private final Map<Player, Double> temperatureCache = new HashMap<>();
    private BukkitTask temperatureUpdateTask;
    private final NormalSeasons seasons = NormalSeasons.getPlugin(NormalSeasons.class);

    public void startTemperatureUpdateTask(){
        if (temperatureUpdateTask != null){
            return;
        }
        temperatureUpdateTask = new BukkitRunnable() {
            @Override
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){
                    updatePlayerTemperature(player, player.getWorld().getBiome(player.getLocation()), SeasonsManager.getInstance().getCurrentSeason());
                }
            }
        }.runTaskTimerAsynchronously(seasons, 5, 20);
    }

    private void updatePlayerTemperature(Player player, Biome biome, SeasonsList.Seasons season){
        double temperature = calculatePlayerTemperature(biome, season, player);
        temperatureCache.put(player, temperature);
        TemperatureEffects.cancelTaskEffects(player);
    }

    private double calculatePlayerTemperature(Biome biome, SeasonsList.Seasons season, Player player) {

        double freezingThreshold = seasons.getConfig().getDouble("season.util.freeze_threshold");
        double coldThreshold = seasons.getConfig().getDouble("season.util.cold_threshold");
        double sweatThreshold = seasons.getConfig().getDouble("season.util.sweat_threshold");
        double fireThreshold = seasons.getConfig().getDouble("season.util.fire_threshold");
        double baseTemperature = getBiomeTemperature(biome, season);
        double temperature = baseTemperature + calculateHeatSourceEffect(player);
        World world = player.getWorld();
        boolean isStorming = world.hasStorm();
        boolean isPlayerInWater = isPlayerInWater(player);

        if (!isStorming && !isPlayerInWater) {
            temperature += 0;
        } else {
            if (isStorming) {
                switch (season) {
                    case SPRING:
                        temperature -= 5;
                        break;
                    case SUMMER:
                        temperature -= 4;
                        break;
                    case AUTUMN:
                        temperature -= 6;
                        break;
                    case WINTER:
                        temperature -= 8;
                        break;
                }
            }

            if (isPlayerInWater) {
                switch (season) {
                    case SPRING:
                        temperature -= 6;
                        break;
                    case SUMMER:
                        temperature -= 5;
                        break;
                    case AUTUMN:
                        temperature -= 7;
                        break;
                    case WINTER:
                        temperature -= 9;
                        break;
                }
            }
        }

        if (temperature < freezingThreshold) {
            applyFreezingEffect(player);
        }

        if (temperature < coldThreshold){
            applyColdEffect(player);
        }

        if (temperature > sweatThreshold){
            applySweatEffect(player);
        }

        if (temperature > fireThreshold) {
            applyFireDamage(player);
        }

        if (temperature > 25 && temperature < 32){
            applyRegenerationEffect(player);
        }

        if (temperature > -1 && temperature < 26){
            cancelTaskEffects(player);
        }
        displayTemperature(player, temperature);
        return temperature;

    }

    private static boolean isPlayerInWater(Player player){
        Block feetBlock = player.getLocation().getBlock();
        return feetBlock.getType() == Material.WATER;
    }

    private double getPlayerTemperature(Player player) {
        return temperatureCache.get(player);
    }

    public void cancelTask(){
        if (temperatureUpdateTask != null){
            temperatureUpdateTask.cancel();
            temperatureUpdateTask = null;
        }
    }
}
