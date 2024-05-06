package nuclearkat.normalseasons.seasons.util;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsList;
import nuclearkat.normalseasons.seasons.SeasonsManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

public class SeasonEffects {

    private static BukkitTask winterTask;
    private static BukkitTask springTask;
    private static BukkitTask autumnTask;
    private static BukkitTask randomParticleTask;
    private static BukkitTask randomAutumnParticleTask;
    private static final Random random = new Random();
    private static final NormalSeasons seasons = NormalSeasons.getPlugin(NormalSeasons.class);
    private static final double RADIUS = seasons.getConfig().getDouble("season.util.radius");
    private static final double AUTUMN_RADIUS = seasons.getConfig().getDouble("season.util.autumn_radius");
    private static final int VECTOR_Y_OFFSET = seasons.getConfig().getInt("season.util.vector_y_offset");
    private static final int PARTICLE_SPAWNS_COUNT = NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getInt("season.util.particle_spawns_count");
    private static final int WINTER_PARTICLES_TO_SPAWN = NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getInt("season.winter.particles_to_spawn");
    private static final int SPRING_PARTICLES_TO_SPAWN = NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getInt("season.spring.particles_to_spawn");
    private static final int AUTUMN_PARTICLES_TO_SPAWN = NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getInt("season.autumn.particles_to_spawn");

    private static Vector randomOffset() {
        double x = random.nextDouble() * RADIUS * 2 - RADIUS;
        double z = random.nextDouble() * RADIUS * 2 - RADIUS;
        return new Vector(x, VECTOR_Y_OFFSET, z);
    }

    private static Vector randomAutumnOffset(){
        double x = random.nextDouble() * AUTUMN_RADIUS * 2 - AUTUMN_RADIUS;
        double z = random.nextDouble() * AUTUMN_RADIUS * 2 - AUTUMN_RADIUS;
        return new Vector(x, 3, z);
    }

    private static void spawnRandomizedParticles(Player player, Particle particleEffect) {
        for (int i = 0; i < PARTICLE_SPAWNS_COUNT; i++) {
            randomParticleTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Vector offset = randomOffset();
                    World world = player.getWorld();
                    if (world.hasStorm()){
                        world.setStorm(false);
                    }
                    switch (particleEffect){

                        case FALLING_DRIPSTONE_WATER:

                            player.spawnParticle(particleEffect, player.getLocation().add(offset), SPRING_PARTICLES_TO_SPAWN, 32, -2, 32);
                            break;

                        case SNOWBALL:
                            player.spawnParticle(particleEffect, player.getLocation().add(offset), WINTER_PARTICLES_TO_SPAWN, 32, -4, 32);
                            break;

                        case CHERRY_LEAVES:
                            player.spawnParticle(particleEffect, player.getLocation().add(offset), AUTUMN_PARTICLES_TO_SPAWN, 8, -4, 8);
                            break;

                        default:

                            break;
                    }
                }
            }.runTaskLaterAsynchronously(NormalSeasons.getPlugin(NormalSeasons.class), random.nextInt(11));
        }
    }

    private static void spawnRandomizedAutumnParticles(Player player, Particle particleEffect) {
        if (SeasonsManager.getInstance().getPlayerToggleVisuals().contains(player)) {
            return;
        }
        setWorldStormFalse(player);
        for (int i = 0; i < 16; i++) {
            randomAutumnParticleTask = new BukkitRunnable(){
                @Override
                public void run(){
                    Vector autumnOffset = randomAutumnOffset();
                    player.spawnParticle(particleEffect, player.getLocation().add(autumnOffset), AUTUMN_PARTICLES_TO_SPAWN, 8, -4, 8);
                }
            }.runTaskLaterAsynchronously(NormalSeasons.getPlugin(NormalSeasons.class), random.nextInt(11));
        }
    }

    private static boolean isPlayerInWater(Player player){
        Block feetBlock = player.getLocation().getBlock();
        return feetBlock.getType() == Material.WATER;
    }

    private static void setWorldStormFalse(Player player){
        new BukkitRunnable() {
            @Override
            public void run(){
                player.getWorld().setStorm(false);
            }

        }.runTask(seasons);
    }

    public static void applyWinterEffects(Player player) {
        if (SeasonsManager.getInstance().getPlayerToggleVisuals().contains(player)) {
            return;
        }
        winterTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld().hasStorm()){
                    setWorldStormFalse(player);
                }
                if (isPlayerInWater(player)){
                    return;
                }
                spawnRandomizedParticles(player, SeasonsList.Seasons.WINTER.getParticleEffect());
            }
        }.runTaskTimerAsynchronously(NormalSeasons.getPlugin(NormalSeasons.class), 0, 10);
    }

    public static void applySpringEffects(Player player) {
        if (SeasonsManager.getInstance().getPlayerToggleVisuals().contains(player)) {
            return;
        }
        springTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld().hasStorm()){
                    setWorldStormFalse(player);
                }
                if (isPlayerInWater(player)){
                    return;
                }
                spawnRandomizedParticles(player, SeasonsList.Seasons.SPRING.getParticleEffect());
            }
        }.runTaskTimerAsynchronously(NormalSeasons.getPlugin(NormalSeasons.class), 0, 10);
    }

    public static void applyAutumnEffects(Player player) {
        if (SeasonsManager.getInstance().getPlayerToggleVisuals().contains(player)) {
            return;
        }
        autumnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isPlayerInWater(player)){
                    return;
                }
                World world = player.getWorld();
                Location playerLocation = player.getLocation();
                int playerY = playerLocation.getBlockY();
                int treeCheckLength = 8;

                boolean underTree = false;
                for (int i = 0; i < treeCheckLength; i++) {
                    int checkY = playerY + i;
                    Block blockAbovePlayer = world.getBlockAt(playerLocation.getBlockX(), checkY, playerLocation.getBlockZ());
                    Material blockType = blockAbovePlayer.getType();
                        if (Tag.LEAVES.isTagged(blockType)) {
                            underTree = true;
                            break;
                        }
                }
                if (underTree) {
                    spawnRandomizedAutumnParticles(player, SeasonsList.Seasons.AUTUMN.getParticleEffect());
                }
            }
        }.runTaskTimerAsynchronously(NormalSeasons.getPlugin(NormalSeasons.class), 0, 10);
    }

    public static void cancelTasks(){
        if (winterTask != null) {
            winterTask.cancel();
            winterTask = null;
        }
        if (springTask != null) {
            springTask.cancel();
            springTask = null;
        }
        if (autumnTask != null){
            autumnTask.cancel();
            autumnTask = null;
        }
        if (randomParticleTask != null){
            randomParticleTask.cancel();
            randomParticleTask = null;
        }
        if (randomAutumnParticleTask != null){
            randomAutumnParticleTask.cancel();
            randomAutumnParticleTask = null;
        }
    }
}
