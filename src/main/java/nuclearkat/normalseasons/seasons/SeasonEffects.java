package nuclearkat.normalseasons.seasons;

import nuclearkat.normalseasons.NormalSeasons;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class SeasonEffects {

    private final NormalSeasons seasons;
    private BukkitTask winterTask;
    private BukkitTask springTask;
    private BukkitTask autumnTask;
    private BukkitTask randomParticleTask;
    private BukkitTask randomAutumnParticleTask;
    private final Random random = new Random();
    private final double RADIUS;
    private final double AUTUMN_RADIUS;
    private final int VECTOR_Y_OFFSET;
    private final int PARTICLE_SPAWNS_COUNT;
    private final int WINTER_PARTICLES_TO_SPAWN;
    private final int SPRING_PARTICLES_TO_SPAWN;
    private final int AUTUMN_PARTICLES_TO_SPAWN;

        public SeasonEffects(NormalSeasons seasons){
            this.seasons = seasons;
            this.RADIUS = seasons.getConfig().getDouble("season.util.radius");
            this.AUTUMN_RADIUS = seasons.getConfig().getDouble("season.util.autumn_radius");
            this.VECTOR_Y_OFFSET = seasons.getConfig().getInt("season.util.vector_y_offset");
            this.PARTICLE_SPAWNS_COUNT = seasons.getConfig().getInt("season.util.particle_spawns_count");
            this.WINTER_PARTICLES_TO_SPAWN = seasons.getConfig().getInt("season.winter.particles_to_spawn");
            this.SPRING_PARTICLES_TO_SPAWN = seasons.getConfig().getInt("season.spring.particles_to_spawn");
            this.AUTUMN_PARTICLES_TO_SPAWN = seasons.getConfig().getInt("season.autumn.particles_to_spawn");
        }

    private Vector randomOffset() {
        double x = random.nextDouble() * RADIUS * 2 - RADIUS;
        double z = random.nextDouble() * RADIUS * 2 - RADIUS;
        return new Vector(x, VECTOR_Y_OFFSET, z);
    }

    private Vector randomAutumnOffset(){
        double x = random.nextDouble() * AUTUMN_RADIUS * 2 - AUTUMN_RADIUS;
        double z = random.nextDouble() * AUTUMN_RADIUS * 2 - AUTUMN_RADIUS;
        return new Vector(x, 3, z);
    }

    private final ArrayList<Player> playerToggleVisuals = new ArrayList<>();

    public ArrayList<Player> getPlayerToggleVisuals(){
        return playerToggleVisuals;
    }

    private void spawnRandomizedParticles(Player player, Particle particleEffect) {
        if (playerToggleVisuals.contains(player)) {
            return;
        }
        for (int i = 0; i < PARTICLE_SPAWNS_COUNT; i++) {
            randomParticleTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Vector offset = randomOffset();
                    if (player.getWorld().hasStorm()){
                        setWorldStormFalse(player);
                    }
                    switch (particleEffect) {

                        case FALLING_DRIPSTONE_WATER:
                            player.spawnParticle(particleEffect, player.getLocation().add(offset), SPRING_PARTICLES_TO_SPAWN, 2, -2, 2);
                            break;

                        case SNOWBALL:
                            player.spawnParticle(particleEffect, player.getLocation().add(offset), WINTER_PARTICLES_TO_SPAWN, 2, -4, 2);
                            break;
                    }
                }
            }.runTaskLater(seasons, random.nextInt(11));
        }
    }

    private void spawnRandomizedAutumnParticles(Player player, Particle particleEffect) {
        if (playerToggleVisuals.contains(player)) {
            return;
        }
        setWorldStormFalse(player);
        for (int i = 0; i < PARTICLE_SPAWNS_COUNT; i++) {
            randomAutumnParticleTask = new BukkitRunnable(){
                @Override
                public void run(){
                    Vector autumnOffset = randomAutumnOffset();
                    player.spawnParticle(particleEffect, player.getLocation().add(autumnOffset), AUTUMN_PARTICLES_TO_SPAWN, 2, -4.5, 0);
                }
            }.runTaskLater(seasons, random.nextInt(21));
        }
    }

    private boolean isPlayerInWater(Player player){
        Block feetBlock = player.getLocation().getBlock();
        return feetBlock.getType() == Material.WATER;
    }

    private void setWorldStormFalse(Player player){
        new BukkitRunnable() {
            @Override
            public void run(){
                player.getWorld().setStorm(false);
            }

        }.runTask(seasons);
    }

    public void applyWinterEffects(Player player) {
        winterTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld().hasStorm()){
                    setWorldStormFalse(player);
                }
                if (isPlayerInWater(player)){
                    return;
                }
                spawnRandomizedParticles(player, SeasonsList.WINTER.getParticleEffect());
            }
        }.runTaskTimer(seasons, 0, 10);
    }

    public void applySpringEffects(Player player) {
        springTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld().hasStorm()){
                    setWorldStormFalse(player);
                }
                if (isPlayerInWater(player)){
                    return;
                }
                spawnRandomizedParticles(player, SeasonsList.SPRING.getParticleEffect());
            }
        }.runTaskTimer(seasons, 0, 10);
    }

    public void applyAutumnEffects(Player player) {
        autumnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (playerToggleVisuals.contains(player)){
                    return;
                }
                if (isPlayerInWater(player)){
                    return;
                }
                int playerY = player.getLocation().getBlockY();
                int treeCheckLength = seasons.getConfig().getInt("season.autumn.tree_search_length");

                boolean underTree = false;
                for (int i = 0; i < treeCheckLength; i++) {
                    int checkY = playerY + i;
                    Block blockAbovePlayer = player.getWorld().getBlockAt(player.getLocation().getBlockX(), checkY, player.getLocation().getBlockZ());
                    Material blockType = blockAbovePlayer.getType();
                        if (Tag.LEAVES.isTagged(blockType)) {
                            underTree = true;
                            break;
                        }
                }
                if (underTree) {
                    spawnRandomizedAutumnParticles(player, SeasonsList.AUTUMN.getParticleEffect());
                }
            }
        }.runTaskTimer(seasons, 0, 10);
    }

    public void cancelAndRemoveTasks(){
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
