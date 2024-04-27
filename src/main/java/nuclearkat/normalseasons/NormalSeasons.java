package nuclearkat.normalseasons;

import nuclearkat.normalseasons.seasons.util.SeasonEffects;
import nuclearkat.normalseasons.seasons.commands.NormalSeasonCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NormalSeasons extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        loadConfig();
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

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        SeasonEffects.cancelTasks();
    }
}
