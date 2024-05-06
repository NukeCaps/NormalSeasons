package nuclearkat.normalseasons.seasons.events;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsList;
import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.util.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static nuclearkat.normalseasons.seasons.util.TemperatureEffects.*;
import static nuclearkat.normalseasons.seasons.util.TemperatureSystem.calculateHeatSourceEffect;
import static nuclearkat.normalseasons.seasons.util.TemperatureSystem.getBiomeTemperature;

public class TemperatureEventListener implements Listener {
    private final ConcurrentHashMap<UUID, Double> temperatureCache = new ConcurrentHashMap<>();
    private BukkitTask temperatureUpdateTask;
    private final NormalSeasons seasons;

    public TemperatureEventListener(NormalSeasons seasons){
        this.seasons = seasons;
    }

    @EventHandler
    public void onTemperatureChange(PlayerTemperatureChangeEvent event){
        Player player = event.getPlayer();
        double temperature = event.getTemperature();
        double freezingThreshold = seasons.getConfig().getDouble("season.util.freeze_threshold");
        double coldThreshold = seasons.getConfig().getDouble("season.util.cold_threshold");
        double sweatThreshold = seasons.getConfig().getDouble("season.util.sweat_threshold");
        double fireThreshold = seasons.getConfig().getDouble("season.util.fire_threshold");

        cancelTaskEffects(player);

        if (temperature < freezingThreshold) {
            applyFreezingEffect(player);
            return;
        }

        if (temperature < coldThreshold){
            applyColdEffect(player);
            return;
        }

        if (temperature > sweatThreshold){
            applySweatEffect(player);
            return;
        }

        if (temperature > fireThreshold) {
            applyFireDamage(player);
            return;
        }

        if (temperature > 25 && temperature < 32){
            applyRegenerationEffect(player);
            return;
        }
        if (temperature > -1 && temperature < 26){
            cancelTaskEffects(player);
        }
    }

    public void startTemperatureUpdateTask(){

        temperatureUpdateTask = new BukkitRunnable() {
            @Override
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){

                    if (temperatureCache.get(player.getUniqueId()) == null){
                        temperatureCache.put(player.getUniqueId(), TemperatureSystem.getDefaultTemperature(SeasonsManager.getInstance().getCurrentSeason()));
                    }
                    emitTemperatureChange(player, temperatureCache.getOrDefault(player.getUniqueId(), 0.0), calculatePlayerTemperature(player.getWorld().getBiome(player.getLocation()), SeasonsManager.getInstance().getCurrentSeason(), player));
                }
            }
        }.runTaskTimer(seasons, 5, 15);
    }

    public void emitTemperatureChange(Player player, double oldTemp, double newTemp) {
        if (oldTemp != newTemp) {
            double currentTemp = oldTemp;
            while (currentTemp != newTemp) {
                if (currentTemp < newTemp) {
                    currentTemp += 1.0;
                    if (currentTemp > newTemp) {
                        currentTemp = newTemp;
                    }
                } else {
                    currentTemp -= 1.0;
                    if (currentTemp < newTemp) {
                        currentTemp = newTemp;
                    }
                }
                displayTemperature(player, currentTemp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Bukkit.getServer().getPluginManager().callEvent(new PlayerTemperatureChangeEvent(player, currentTemp));
        }
    }

    private double calculatePlayerTemperature(Biome biome, SeasonsList.Seasons season, Player player) {

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
        return temperature;
    }

    private static boolean isPlayerInWater(Player player){
        Block feetBlock = player.getLocation().getBlock();
        return feetBlock.getType() == Material.WATER;
    }

    public void cancelTask(){
        if (temperatureUpdateTask != null){
            temperatureUpdateTask.cancel();
            temperatureUpdateTask = null;
        }
    }
}
