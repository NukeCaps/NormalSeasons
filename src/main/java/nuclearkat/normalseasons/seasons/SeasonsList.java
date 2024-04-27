package nuclearkat.normalseasons.seasons;

import org.bukkit.Particle;
import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.Map;

public class SeasonsList {

    public enum Seasons {

        SPRING("Spring", Particle.FALLING_DRIPSTONE_WATER),
        SUMMER("Summer", Particle.FLAME),
        AUTUMN("Autumn", Particle.CHERRY_LEAVES),
        WINTER("Winter", Particle.SNOWBALL);

        private final String name;
        private final Particle particleEffect;
        private final Map<Biome, double[]> biomeTemperatureMap;

        Seasons(String name, Particle particleEffect) {
            this.name = name;
            this.particleEffect = particleEffect;
            this.biomeTemperatureMap = new HashMap<>();
            initializeBiomeTemperatures();
        }

        private void initializeBiomeTemperatures() {
            switch (this) {
                case SPRING:
                    biomeTemperatureMap.put(Biome.PLAINS, new double[]{15, 25});
                    biomeTemperatureMap.put(Biome.FOREST, new double[]{12, 20});
                    break;
                case SUMMER:
                    biomeTemperatureMap.put(Biome.PLAINS, new double[]{25, 35});
                    biomeTemperatureMap.put(Biome.FOREST, new double[]{22, 30});
                    break;
                case AUTUMN:
                    biomeTemperatureMap.put(Biome.PLAINS, new double[]{10, 20});
                    biomeTemperatureMap.put(Biome.FOREST, new double[]{8, 18});
                    break;
                case WINTER:
                    biomeTemperatureMap.put(Biome.PLAINS, new double[]{-5, 5});
                    biomeTemperatureMap.put(Biome.FOREST, new double[]{-8, 2});
                    break;
            }
        }

        public String getName() {
            return name;
        }

        public Particle getParticleEffect() {
            return particleEffect;
        }

        public Map<Biome, double[]> getBiomeTemperatureMap() {
            return biomeTemperatureMap;
        }
    }
}
