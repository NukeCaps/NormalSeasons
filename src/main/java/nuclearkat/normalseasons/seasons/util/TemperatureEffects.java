package nuclearkat.normalseasons.seasons.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nuclearkat.normalseasons.NormalSeasons;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TemperatureEffects {

    private static final NormalSeasons seasons = NormalSeasons.getPlugin(NormalSeasons.class);
    private static BukkitTask applyFreezingEffect;
    private static BukkitTask applyColdEffect;
    private static BukkitTask applyFireEffect;
    private static BukkitTask applySweatEffect;
    private static BukkitTask applyRegenerationEffect;

    public static void applyFreezingEffect(Player player) {
        player.setWalkSpeed(0.2f);
        applyFreezingEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 5, 0.5, 0.5, 0.5);
            }
        }.runTaskAsynchronously(seasons);
    }

    public static void applyColdEffect(Player player) {

        applyColdEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.spawnParticle(Particle.WHITE_SMOKE, player.getEyeLocation().add(0, -0.2, 0), 1, 0.1, -0.5, 0);
            }
        }.runTaskTimerAsynchronously(seasons, 5, 100);
    }

    public static void displayTemperature(Player player, double temperature) {
        String temperatureString = String.format("%.1f°C", temperature);

        TextComponent temperatureComponent = new TextComponent("§7§lTemperature " + "§9" + temperatureString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, temperatureComponent);
    }

    public static void applySweatEffect(Player player) {
        applySweatEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.spawnParticle(Particle.WATER_DROP, player.getEyeLocation(), 3, 0.2, 0.2, 0.2);
            }
        }.runTaskTimerAsynchronously(seasons, 5, 100);
    }

    public static void applyFireDamage(Player player) {

        applyFireEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
                player.setFireTicks(40);
            }
        }.runTaskAsynchronously(seasons);
    }

    public static void applyRegenerationEffect(Player player) {
        applyRegenerationEffect = new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(1, 6));
                player.spawnParticle(Particle.HEART, player.getLocation(), 1, 0.2, 0.2, 0.2);
            }
        }.runTaskTimer(seasons, 5, 100);
    }

    public static void cancelTaskEffects(Player player) {
        if (applyFreezingEffect != null) {
            player.setWalkSpeed(1.0f);
            applyFreezingEffect.cancel();
        }
        if (applyColdEffect != null) {
            applyColdEffect.cancel();
        }
        if (applyFireEffect != null) {
            applyFireEffect.cancel();
        }
        if (applySweatEffect != null) {
            applySweatEffect.cancel();
        }
        if (applyRegenerationEffect != null) {
            applyRegenerationEffect.cancel();
        }
    }
    public static void cancelTasks(){
        applyFreezingEffect = null;
        applyColdEffect = null;
        applySweatEffect = null;
        applyFireEffect = null;
        applyRegenerationEffect = null;
    }
}
