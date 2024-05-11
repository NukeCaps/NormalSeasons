package nuclearkat.normalseasons.seasons.events;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.temperature.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMoveListener implements Listener {

    private final NormalSeasons seasons;
    private final TemperatureSystem temperatureSystem;
    private final SeasonsManager seasonsManager;

    public PlayerMoveListener(NormalSeasons seasons, TemperatureSystem temperatureSystem, SeasonsManager seasonsManager){
        this.seasons = seasons;
        this.temperatureSystem = temperatureSystem;
        this.seasonsManager = seasonsManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Block blockFrom = event.getFrom().getBlock();
        Block blockTo = event.getTo().getBlock();
        Player player = event.getPlayer();

        if (blockFrom == blockTo) {
            return;
        }

        if (blockFrom.getBiome() != blockTo.getBiome()) {
            triggerTemperatureChangeEvent(player, temperatureSystem.calculatePlayerTemperature(blockTo.getBiome(), seasonsManager.getCurrentSeason(), player));
            return;
        }

        if (blockFrom.getType() != Material.WATER && blockTo.getType() == Material.WATER ||
                blockFrom.getType() == Material.WATER && blockTo.getType() != Material.WATER) {
            triggerTemperatureChangeEvent(player, temperatureSystem.calculatePlayerTemperature(blockTo.getBiome(), seasonsManager.getCurrentSeason(), player));
        }
    }

    private void triggerTemperatureChangeEvent(Player player, double temperature) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new PlayerTemperatureChangeEvent(player, temperature));
            }
        }.runTask(seasons);
    }
}
