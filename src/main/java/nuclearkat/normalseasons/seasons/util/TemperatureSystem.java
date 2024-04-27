package nuclearkat.normalseasons.seasons.util;

import nuclearkat.normalseasons.seasons.SeasonsList;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

import static nuclearkat.normalseasons.seasons.util.TemperatureEffects.applyFireDamage;
import static nuclearkat.normalseasons.seasons.util.TemperatureEffects.applyFreezingEffect;

public class TemperatureSystem {

    private static final double FREEZING_THRESHOLD = -25;
    private static final double COLD_THRESHOLD = -1;
    private static final double SWEAT_THRESHOLD = 32;
    private static final double FIRE_DAMAGE_THRESHOLD = 42;

    public static double calculateTemperature(Biome biome, SeasonsList.Seasons season, Player player) {

        double baseTemperature = getBiomeTemperature(biome, season);
        double temperature = baseTemperature + calculateHeatSourceEffect(player);

        if (temperature < FREEZING_THRESHOLD) {
            applyFreezingEffect(player);
        }

        if (temperature > FIRE_DAMAGE_THRESHOLD) {
            applyFireDamage(player);
        }

        return temperature;
    }

    private static double getBiomeTemperature(Biome biome, SeasonsList.Seasons season) {
        Map<Biome, double[]> biomeTemperatureMap = season.getBiomeTemperatureMap();
        double[] temperatureRange = biomeTemperatureMap.get(biome);

        if (temperatureRange != null) {
            return (temperatureRange[0] + temperatureRange[1]) / 2.0;
        } else {
            return getDefaultTemperature(season);
        }
    }

    private static double calculateHeatSourceEffect(Player player) {
        World world = player.getWorld();
        double heatEffect = 0.0;
        int searchRadius = 6;

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    Location currentLocation = player.getLocation().add(x, y, z);
                    Block block = world.getBlockAt(currentLocation);
                    Material material = block.getType();

                    if (material == Material.FIRE) {
                        double distance = player.getLocation().distance(currentLocation);
                        heatEffect += 1.0 / (distance * distance);
                    } else if (material == Material.LAVA) {
                        heatEffect += 0.5;
                    } else if (material == Material.TORCH) {
                        heatEffect += 0.25;
                    }
                }
            }
        }

        return heatEffect;
    }


    private static double[] getTemperatureRange(Biome biome, SeasonsList.Seasons season) {
        double[] range = season.getBiomeTemperatureMap().get(biome);
        if (range == null) {
            System.out.println("Temperature range not defined for biome " + biome + " in season " + season.getName());
        }
        return range;
    }

    private static double getDefaultTemperature(SeasonsList.Seasons season) {
        switch (season) {
            case WINTER:
                return -10;
            case SPRING:
                return 15;
            case SUMMER:
                return 30;
            case AUTUMN:
                return 20;
            default:
                return 0;
        }
    }
}
