package nuclearkat.normalseasons.seasons.temperature;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;

public class TemperatureSystem {

    private static final NormalSeasons seasons = NormalSeasons.getPlugin(NormalSeasons.class);

    public static double getBiomeTemperature(Biome biome, SeasonsList.Seasons season) {
        HashMap<Biome, double[]> biomeTemperatureMap = season.getBiomeTemperatureMap();
        double[] temperatureRange = biomeTemperatureMap.get(biome);

        if (temperatureRange != null) {
            return (temperatureRange[0] + temperatureRange[1]) / 2.0;
        } else {
            return getDefaultTemperature(season);
        }
    }

    private static final HashMap<Material, Double> heatSources = new HashMap<>();

    public static void loadHeatSources() {
        ConfigurationSection heatSourcesSection = seasons.getConfig().getConfigurationSection("season.heat_sources");

        if (heatSourcesSection != null) {
            for (String key : heatSourcesSection.getKeys(false)) {
                double heatValue = heatSourcesSection.getDouble(key);
                Material material = Material.matchMaterial(key);
                if (material != null) {
                    heatSources.put(material, heatValue);
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "Invalid material found in heat sources: " + key);
                }
            }
        }
    }

    public static double calculateHeatSourceEffect(Player player) {
        World world = player.getWorld();
        double heatEffect = 0.0;
        int searchRadius = seasons.getConfig().getInt("season.util.heat_detection_radius");

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    Location currentLocation = player.getLocation().add(x, y, z);
                    Block block = world.getBlockAt(currentLocation);
                    Material material = block.getType();
                    heatEffect += heatSources.getOrDefault(material, 0D);
                }
            }
        }
        return heatEffect;
    }

    public static double getDefaultTemperature(SeasonsList.Seasons season) {
        switch (season) {
            case WINTER:
                return seasons.getConfig().getDouble("season.winter.default_temperature");
            case SPRING:
                return seasons.getConfig().getDouble("season.spring.default_temperature");
            case SUMMER:
                return seasons.getConfig().getDouble("season.summer.default_temperature");
            case AUTUMN:
                return seasons.getConfig().getDouble("season.autumn.default_temperature");
            default:
                return 0;
        }
    }
}
