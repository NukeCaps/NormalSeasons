package nuclearkat.normalseasons.listeners;

import nuclearkat.normalseasons.SeasonsManager;
import nuclearkat.normalseasons.temperature.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TemperatureSystem temperatureSystem;
    private final SeasonsManager seasonsManager;

    public PlayerJoinListener(TemperatureSystem temperatureSystem, SeasonsManager seasonsManager){
        this.temperatureSystem = temperatureSystem;
        this.seasonsManager = seasonsManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Bukkit.getServer().getPluginManager().callEvent(new PlayerTemperatureChangeEvent(player, temperatureSystem.getDefaultTemperature(seasonsManager.getCurrentSeason())));
    }
}
