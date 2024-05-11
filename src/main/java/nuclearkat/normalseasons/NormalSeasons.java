package nuclearkat.normalseasons;

import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.commands.NormalSeasonCommand;
import nuclearkat.normalseasons.seasons.events.PlayerJoinListener;
import nuclearkat.normalseasons.seasons.events.PlayerMoveListener;
import nuclearkat.normalseasons.seasons.events.TemperatureEventListener;
import nuclearkat.normalseasons.seasons.SeasonEffects;
import nuclearkat.normalseasons.seasons.temperature.TemperatureEffects;
import nuclearkat.normalseasons.seasons.temperature.TemperatureSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NormalSeasons extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
        loadConfig();
        startMainTasks();
    }

    private void registerCommands(){
        getCommand("NSeasons").setExecutor(new NormalSeasonCommand(seasonsManager, seasonEffects));
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(temperatureEventListener, this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(temperatureSystem, seasonsManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this, temperatureSystem, seasonsManager), this);
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
        getConfig().addDefault("season.seasons_list_message", "There are 4 seasons: &a%Seasons%");
        getConfig().addDefault("season.season_duration_ticks", 1200);

        getConfig().addDefault("season.winter.particles_to_spawn", 96);
        getConfig().addDefault("season.winter.default_temperature", -1);

        getConfig().addDefault("season.spring.particles_to_spawn", 32);
        getConfig().addDefault("season.spring.default_temperature", 20);

        getConfig().addDefault("season.autumn.particles_to_spawn", 10);
        getConfig().addDefault("season.autumn.default_temperature", 10);
        getConfig().addDefault("season.autumn.tree_search_length", 8);

        getConfig().addDefault("season.summer.default_temperature", 30);

        getConfig().addDefault("season.util.radius", 32.0);
        getConfig().addDefault("season.util.autumn_radius", 3);
        getConfig().addDefault("season.util.heat_detection_radius", 6);
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
        temperatureSystem.loadHeatSources();
    }

    private final SeasonEffects seasonEffects = new SeasonEffects(this);
    private final SeasonsManager seasonsManager = SeasonsManager.getInstance(seasonEffects, this);
    private final TemperatureEffects temperatureEffects = new TemperatureEffects(this);
    private final TemperatureSystem temperatureSystem = new TemperatureSystem(this);
    private final TemperatureEventListener temperatureEventListener = new TemperatureEventListener(this, temperatureEffects, seasonsManager, temperatureSystem);

    private void startMainTasks(){
        seasonsManager.scheduleSeasonChange();
        temperatureEventListener.startTemperatureCacheTask();
    }

    private void cancelTasks(){
        seasonEffects.cancelAndRemoveTasks();
        temperatureEffects.cancelAndRemoveTaskEffects();
        temperatureEventListener.cancelTask();
        seasonsManager.cancelTasks();
    }

    @Override
    public void onDisable() {
        cancelTasks();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
