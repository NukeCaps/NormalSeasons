package nuclearkat.normalseasons.listeners;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.SeasonsManager;
import nuclearkat.normalseasons.temperature.TemperatureEffects;
import nuclearkat.normalseasons.temperature.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TemperatureEventListener implements Listener {
    private final HashMap<UUID, Double> temperatureCache = new HashMap<>();
    private BukkitTask temperatureCacheTask;
    private BukkitTask emitTemperatureChangeTask;
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
        handleTemperatureUpdate(player, newTemperature);
    }

    private void handleTemperatureUpdate(Player player, double newTemperature){
        temperatureEffects.cancelAndRemoveTaskEffects();
        emitTemperatureChange(player, temperatureCache.getOrDefault(player.getUniqueId(), 0.0), newTemperature);
        if (newTemperature < seasons.getConfig().getDouble("season.util.freeze_threshold")) {
            temperatureEffects.applyFreezingEffect(player);
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

    public void startTemperatureCacheTask(){
        temperatureCacheTask = new BukkitRunnable() {
            @Override
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){
                    temperatureCache.computeIfAbsent(player.getUniqueId(), uuid -> {
                        double defaultTemp = temperatureSystem.getDefaultTemperature(seasonsManager.getCurrentSeason());
                        emitTemperatureChange(player, defaultTemp, defaultTemp);
                        return defaultTemp;
                    });

                    double currentTemp = temperatureSystem.calculatePlayerTemperature(player.getWorld().getBiome(player.getLocation()), seasonsManager.getCurrentSeason(), player);
                    temperatureCache.put(player.getUniqueId(), currentTemp);
                }
            }
        }.runTaskTimer(seasons, 5, 100);
    }

    private void emitTemperatureChange(Player player, double oldTemp, double tempTarget) {
        if (emitTemperatureChangeTask != null){
            emitTemperatureChangeTask.cancel();
        }
        emitTemperatureChangeTask = new BukkitRunnable() {
            double updatedTemp = oldTemp;
            final double increment = (tempTarget > oldTemp) ? 1.0 : -1.0;

            @Override
            public void run() {
                temperatureEffects.displayTemperature(player, updatedTemp);
                updatedTemp += increment;

                if (increment > 0 && updatedTemp >= tempTarget || increment < 0 && updatedTemp <= tempTarget) {
                    temperatureEffects.displayTemperature(player, tempTarget);
                    this.cancel();
                }
            }
        }.runTaskTimer(seasons, 0, 10);
    }

    public void cancelTask(){
        if (temperatureCacheTask != null){
            temperatureCacheTask.cancel();
            temperatureCacheTask = null;
            temperatureCache.clear();
        }
        if (emitTemperatureChangeTask != null){
            emitTemperatureChangeTask.cancel();
            emitTemperatureChangeTask = null;
        }
    }
}
