package nuclearkat.normalseasons.seasons.events;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsList;
import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.temperature.TemperatureEffects;
import nuclearkat.normalseasons.seasons.temperature.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TemperatureEventListener implements Listener {
    private final ConcurrentHashMap<UUID, Double> temperatureCache = new ConcurrentHashMap<>();
    private BukkitTask temperatureUpdateTask;
    private final NormalSeasons seasons;
    private final TemperatureEffects temperatureEffects;
    private final SeasonsManager seasonsManager;

    public TemperatureEventListener(NormalSeasons seasons, TemperatureEffects temperatureEffects, SeasonsManager seasonsManager){
        this.seasons = seasons;
        this.temperatureEffects = temperatureEffects;
        this.seasonsManager = seasonsManager;
    }

    @EventHandler
    public void onTemperatureChange(PlayerTemperatureChangeEvent event){
        Player player = event.getPlayer();
        double newTemperature = event.getTemperature();
        updatePlayerTemperature(player, newTemperature);
    }

    private void updatePlayerTemperature(Player player, double newTemperature){
        temperatureEffects.cancelTaskEffects();
        player.setWalkSpeed(1.0f);

        if (newTemperature < seasons.getConfig().getDouble("season.util.freeze_threshold")) {
            temperatureEffects.applyFreezingEffect(player);
            player.setWalkSpeed(0.9f);
        } else if (newTemperature < seasons.getConfig().getDouble("season.util.cold_threshold")) {
            temperatureEffects.applyColdEffect(player);
        } else if (newTemperature > seasons.getConfig().getDouble("season.util.sweat_threshold")) {
            temperatureEffects.applySweatEffect(player);
        } else if (newTemperature > seasons.getConfig().getDouble("season.util.fire_threshold")) {
            temperatureEffects.applyFireDamage(player);
        } else if (newTemperature > 25 && newTemperature < 32) {
            temperatureEffects.applyRegenerationEffect(player);
        }

    }

    public void startTemperatureUpdateTask(){
        temperatureUpdateTask = new BukkitRunnable() {
            @Override
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (temperatureCache.get(player.getUniqueId()) == null){
                        temperatureCache.put(player.getUniqueId(), TemperatureSystem.getDefaultTemperature(seasonsManager.getCurrentSeason()));
                    }
                    emitTemperatureChange(player, temperatureCache.getOrDefault(player.getUniqueId(), 0.0), calculatePlayerTemperature(player.getWorld().getBiome(player.getLocation()), seasonsManager.getCurrentSeason(), player));
                }
            }
        }.runTaskTimer(seasons, 5, 100);
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
                temperatureEffects.displayTemperature(player, currentTemp);
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
        double baseTemperature = TemperatureSystem.getBiomeTemperature(biome, season);
        double temperature = baseTemperature + TemperatureSystem.calculateHeatSourceEffect(player);
        boolean isStorming = player.getWorld().hasStorm();
        boolean isPlayerInWater = isPlayerInWater(player);

        if (isStorming) {
            temperature -= switch (season) {
                case SPRING -> isPlayerInWater ? 6 : 5;
                case SUMMER -> isPlayerInWater ? 5 : 4;
                case AUTUMN -> isPlayerInWater ? 7 : 6;
                case WINTER -> isPlayerInWater ? 9 : 8;
            };
        }

        return temperature;
    }

    private boolean isPlayerInWater(Player player){
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
