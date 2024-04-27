package nuclearkat.normalseasons.seasons.util;

import nuclearkat.normalseasons.NormalSeasons;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TemperatureEffects {

    private static final NormalSeasons seasons = NormalSeasons.getPlugin(NormalSeasons.class);
    private static BukkitTask applyFreezingEffect;
    private static BukkitTask applyColdEffect;
    private static BukkitTask applyFireEffect;
    private static BukkitTask applySweatEffect;


    public static void applyFreezingEffect(Player player) {
        player.setWalkSpeed(0.2f);
        applyFreezingEffect = new BukkitRunnable() {
            @Override
            public void run(){
                player.spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 5, 0.5, 0.5, 0.5);
            }
        }.runTaskAsynchronously(seasons);
    }

    public static void applyColdEffect(Player player){

        applyColdEffect = new BukkitRunnable() {
            @Override
            public void run(){
                player.spawnParticle(Particle.SNOW_SHOVEL, player.getLocation(), 5, 0.5, 0.5, 0.5);
            }
        }.runTaskTimerAsynchronously(seasons, 0, 20);
    }

    public static void applySweatEffect(Player player){
        applySweatEffect = new BukkitRunnable() {
            @Override
            public void run(){
                player.spawnParticle(Particle.WATER_DROP, player.getEyeLocation(), 5, 0.5, 0.5, 0.5);
            }
        }.runTaskTimerAsynchronously(seasons, 0, 20);
    }

    public static void applyFireDamage(Player player) {

        applyFireEffect = new BukkitRunnable() {
            @Override
            public void run(){
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
                player.setFireTicks(10);
            }
        }.runTaskAsynchronously(seasons);


    }


    public static void cancelTasks(){
        if (applyFreezingEffect != null){
            applyFreezingEffect.cancel();
        }
        if (applyFireEffect != null){
            applyFireEffect.cancel();
        }
        if (applyColdEffect != null){
            applyColdEffect.cancel();
        }
    }

}
