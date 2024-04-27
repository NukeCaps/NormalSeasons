package nuclearkat.normalseasons;

import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.TemperatureManager;
import nuclearkat.normalseasons.seasons.util.SeasonEffects;
import nuclearkat.normalseasons.seasons.commands.NormalSeasonCommand;
import nuclearkat.normalseasons.seasons.util.TemperatureEffects;
import nuclearkat.normalseasons.seasons.util.TemperatureSystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class NormalSeasons extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        loadConfig();
        startMainTasks();
    }

    private void registerCommands(){
        getCommand("NSeasons").setExecutor(new NormalSeasonCommand());
    }

    private void loadConfig(){
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        addDefaultConfigValues();
        reloadConfig();
    }

    private void addDefaultConfigValues(){
        getConfig().addDefault("season.season_change_message", "The season has just changed to&a %SeasonName% &f!");
        getConfig().addDefault("season.current_season_message", "The current season is&a %SeasonName% &f!");
        getConfig().addDefault("season.seasons_list_message", "There are currently 4 seasons: &a%Seasons%");
        getConfig().addDefault("season.season_duration_ticks", 1200);

        getConfig().addDefault("season.winter.particles_to_spawn", 96);
        getConfig().addDefault("season.spring.particles_to_spawn", 32);
        getConfig().addDefault("season.autumn.particles_to_spawn", 10);
        getConfig().addDefault("season.summer.particles_to_spawn", 10);
        getConfig().addDefault("season.summer.summer_chance", 0.3);

        getConfig().addDefault("season.util.radius", 32.0);
        getConfig().addDefault("season.util.autumn_radius", 3);
        getConfig().addDefault("season.util.vector_y_offset", 18);
        getConfig().addDefault("season.util.particle_spawns_count", 32);
        getConfig().addDefault("season.util.freeze_threshold", -25);
        getConfig().addDefault("season.util.cold_threshold", -1);
        getConfig().addDefault("season.util.sweat_threshold", 32);
        getConfig().addDefault("season.util.fire_threshold", 42);

        getConfig().addDefault("season.heat_sources.FIRE", 1.0);
        getConfig().addDefault("season.heat_sources.LAVA", 0.5);
        getConfig().addDefault("season.heat_sources.TORCH", 0.25);
        getConfig().addDefault("season.heat_sources.CAMPFIRE", 0.75);
        getConfig().addDefault("season.heat_sources.FURNACE", 0.5);

        getConfig().options().copyDefaults(true);
        saveConfig();
        TemperatureSystem.loadHeatSources();
    }

    private void startMainTasks(){
        SeasonsManager.getInstance();
        TemperatureManager.getInstance();
    }

    private void cancelTasks(){
        SeasonEffects.cancelTasks();
        TemperatureManager.getInstance().cancelTask();
        TemperatureEffects.cancelTasks();
    }

    @Override
    public void onDisable() {
        cancelTasks();
    }
}
