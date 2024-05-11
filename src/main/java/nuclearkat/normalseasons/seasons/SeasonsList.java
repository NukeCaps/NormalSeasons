package nuclearkat.normalseasons.seasons;

import org.bukkit.Particle;
import org.bukkit.block.Biome;

import java.util.HashMap;

public enum SeasonsList {

    SPRING("Spring", Particle.FALLING_DRIPSTONE_WATER),
    SUMMER("Summer", Particle.FLAME),
    AUTUMN("Autumn", Particle.CHERRY_LEAVES),
    WINTER("Winter", Particle.SNOWBALL);

    private final String name;
    private final Particle particleEffect;
    private final HashMap<Biome, double[]> biomeTemperatureMap;

    SeasonsList(String name, Particle particleEffect) {
        this.name = name;
        this.particleEffect = particleEffect;
        this.biomeTemperatureMap = new HashMap<>();
    }

    public static void initializeBiomeTemperature() {
        for (SeasonsList season : values()) {
            season.initializeBiomeTemperatures();
        }
    }

    private void initializeBiomeTemperatures() {
        switch (this) {
            case SPRING -> {
                setBiomeTemperature(Biome.PLAINS, 15, 22);
                setBiomeTemperature(Biome.FOREST, 12, 19);
                setBiomeTemperature(Biome.DESERT, 22, 31);
            }
            case SUMMER -> {
                setBiomeTemperature(Biome.PLAINS, 25, 35);
                setBiomeTemperature(Biome.FOREST, 22, 30);
                setBiomeTemperature(Biome.DESERT, 30, 45);
            }
            case AUTUMN -> {
                setBiomeTemperature(Biome.PLAINS, 10, 20);
                setBiomeTemperature(Biome.FOREST, 8, 16);
                setBiomeTemperature(Biome.DESERT, 14, 24);
            }
            case WINTER -> {
                setBiomeTemperature(Biome.PLAINS, -6, 5);
                setBiomeTemperature(Biome.FOREST, -8, 2);
                setBiomeTemperature(Biome.DESERT, 0, 10);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Particle getParticleEffect() {
        return particleEffect;
    }

    public void setBiomeTemperature(Biome biome, double minTemperature, double maxTemperature) {
        double[] temperatureRange = {minTemperature, maxTemperature};
        biomeTemperatureMap.put(biome, temperatureRange);
    }

    public double[] getTemperatureForBiome(Biome biome) {
        return biomeTemperatureMap.getOrDefault(biome, new double[]{0.0, 0.0});
    }
}
