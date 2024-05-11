package nuclearkat.normalseasons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTemperatureChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final double temperature;

    public PlayerTemperatureChangeEvent(Player player, double temperature) {
        this.player = player;
        this.temperature = temperature;
    }

    public Player getPlayer() {
        return player;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

}
