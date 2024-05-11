package nuclearkat.normalseasons.seasons.temperature;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nuclearkat.normalseasons.NormalSeasons;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TemperatureEffects {

    public TemperatureEffects(NormalSeasons seasons){
        this.seasons = seasons;
    }

    private final NormalSeasons seasons;
    private BukkitTask applyFreezingEffect;
    private BukkitTask applyColdEffect;
    private BukkitTask applyFireEffect;
    private BukkitTask applySweatEffect;
    private BukkitTask applyRegenerationEffect;

    public void displayTemperature(Player player, double temperature) {
        String temperatureString = String.format("%.1f°C", temperature);

        TextComponent temperatureComponent = new TextComponent("§7§lTemperature " + "§9" + temperatureString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, temperatureComponent);
    }

    public void applyFreezingEffect(Player player) {
        applyFreezingEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.09f);
                player.spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 5, 0.5, 0.5, 0.5);
            }
        }.runTaskTimer(seasons, 0, 100);
    }

    public void applyColdEffect(Player player) {
        applyColdEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.spawnParticle(Particle.WHITE_SMOKE, player.getLocation().add(0, 1.6, 0), 5, 0, -0.5, 0);
            }
        }.runTaskTimer(seasons, 5, 100);
    }

    public void applyRegenerationEffect(Player player) {
        applyRegenerationEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(40, 1));
                player.spawnParticle(Particle.HEART, player.getLocation(), 1, 0.2, 0.2, 0.2);
            }
        }.runTaskTimer(seasons, 0, 35);
    }

    public void applySweatEffect(Player player) {
        applySweatEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.spawnParticle(Particle.WATER_DROP, player.getLocation().add(0, 1.6, 0), 3, 0.1, 0.2, 0.1);
            }
        }.runTaskTimer(seasons, 5, 100);
    }

    public void applyFireDamage(Player player) {
        applyFireEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.setFireTicks(20);
            }
        }.runTaskTimer(seasons, 0, 60);
    }

    public void cancelAndRemoveTaskEffects() {
        if (applyFreezingEffect != null) {
            applyFreezingEffect.cancel();
            applyFreezingEffect = null;
        }
        if (applyColdEffect != null) {
            applyColdEffect.cancel();
            applyColdEffect = null;
        }
        if (applyFireEffect != null) {
            applyFireEffect.cancel();
            applyFireEffect = null;
        }
        if (applySweatEffect != null) {
            applySweatEffect.cancel();
            applySweatEffect = null;
        }
        if (applyRegenerationEffect != null){
            applyRegenerationEffect.cancel();
            applyRegenerationEffect = null;
        }
    }
}
