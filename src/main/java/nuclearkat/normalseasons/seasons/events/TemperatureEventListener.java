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

import java.util.HashMap;
import java.util.UUID;

public class TemperatureEventListener implements Listener {
    private final HashMap<UUID, Double> temperatureCache = new HashMap<>();
    private BukkitTask temperatureUpdateTask;
    private final NormalSeasons seasons;
    private final TemperatureEffects temperatureEffects;
    private final SeasonsManager seasonsManager;
    private final TemperatureSystem temperatureSystem;

    public TemperatureEventListener(NormalSeasons seasons, TemperatureEffects temperatureEffects, SeasonsManager seasonsManager, TemperatureSystem temperatureSystem){
        this.seasons = seasons;
        this.temperatureEffects = temperatureEffects;
        this.seasonsManager = seasonsManager;
        this.temperatureSystem = temperatureSystem;
    }

    @EventHandler
    public void onTemperatureChange(PlayerTemperatureChangeEvent event){
        Player player = event.getPlayer();
        double newTemperature = event.getTemperature();
        updatePlayerTemperature(player, newTemperature);
        player.setWalkSpeed(1.0f);
        System.out.println("Temperature Change Event triggered");
    }

    private void updatePlayerTemperature(Player player, double newTemperature){
        temperatureEffects.cancelTaskEffects();

        if (newTemperature < seasons.getConfig().getDouble("season.util.freeze_threshold")) {
            temperatureEffects.applyFreezingEffect(player);
            player.setWalkSpeed(1.0f);
            System.out.println("Freezing effect applied!");
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
                        temperatureCache.put(player.getUniqueId(), temperatureSystem.getDefaultTemperature(seasonsManager.getCurrentSeason()));
                    }
                    emitTemperatureChange(player, temperatureCache.getOrDefault(player.getUniqueId(), 0.0), calculatePlayerTemperature(player.getWorld().getBiome(player.getLocation()), seasonsManager.getCurrentSeason(), player));

                }
            }
        }.runTaskTimer(seasons, 5, 100);
    }

    private void emitTemperatureChange(Player player, double oldTemp, double tempTarget) {
        final double currentTemp = oldTemp;
        new BukkitRunnable() {
            @Override
            public void run() {
                double updatedTemp = currentTemp;
                double increment = (tempTarget > currentTemp) ? 1.0 : -1.0;
                temperatureEffects.displayTemperature(player, updatedTemp);

                if ((increment > 0 && updatedTemp >= tempTarget) || (increment < 0 && updatedTemp <= tempTarget)) {
                    temperatureEffects.displayTemperature(player, updatedTemp);

                    Bukkit.getServer().getPluginManager().callEvent(new PlayerTemperatureChangeEvent(player, tempTarget));

                    this.cancel();
                } else {
                    updatedTemp += increment;
                    temperatureEffects.displayTemperature(player, updatedTemp);
                }
            }
        }.runTaskTimer(seasons, 0, 20);
    }

    private double calculatePlayerTemperature(Biome biome, SeasonsList season, Player player) {
        double baseTemperature = temperatureSystem.getBiomeTemperature(biome, season);
        double temperature = baseTemperature + temperatureSystem.calculateHeatSourceEffect(player);
        boolean isStorming = player.getWorld().hasStorm();
        boolean isPlayerInWater = isPlayerInWater(player);

        if (isStorming || isPlayerInWater) {
            temperature -= switch (season) {
                case SPRING -> isPlayerInWater ? 5 : 8;
                case SUMMER -> isPlayerInWater ? 4 : 7;
                case AUTUMN -> isPlayerInWater ? 6 : 9;
                case WINTER -> isPlayerInWater ? 9 : 11;
            };
            Bukkit.getPluginManager().callEvent(new PlayerTemperatureChangeEvent(player, temperature));
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
