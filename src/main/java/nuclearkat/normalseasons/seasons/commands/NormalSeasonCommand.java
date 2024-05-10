package nuclearkat.normalseasons.seasons.commands;

import nuclearkat.normalseasons.NormalSeasons;
import nuclearkat.normalseasons.seasons.SeasonsList;
import nuclearkat.normalseasons.seasons.SeasonsManager;
import nuclearkat.normalseasons.seasons.SeasonEffects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class NormalSeasonCommand implements CommandExecutor {

    private final SeasonsManager seasonsManager;
    private final SeasonEffects seasonEffects;

    public NormalSeasonCommand(SeasonsManager seasonsManager, SeasonEffects seasonEffects){
        this.seasonsManager = seasonsManager;
        this.seasonEffects = seasonEffects;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("NSeasons")){
            if (!(commandSender instanceof Player)){
                return false;
            }

            Player player = (Player) commandSender;

            if (args.length == 1){

                String commandArg = args[0];

                switch (commandArg){

                    case "season":

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getString("season.current_season_message").replace("%SeasonName%", seasonsManager.getCurrentSeason().getName())));

                        return true;

                    case "seasons":

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', NormalSeasons.getPlugin(NormalSeasons.class).getConfig().getString("season.seasons_list_message").replace("%Seasons%", Arrays.toString(SeasonsList.values()))));
                        return true;

                    case "toggle":
                        if (seasonEffects.getPlayerToggleVisuals().contains(player)){
                            seasonEffects.getPlayerToggleVisuals().remove(player);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "You have just &atoggled seasons visual effects!"));
                        } else {
                            seasonEffects.getPlayerToggleVisuals().add(player);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "You have just &ctoggled seasons visual effects!"));
                        }
                        return true;

                    default:
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Incorrect usage! Please use /NSeasons <season : seasons>!"));
                        break;

                }

            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Incorrect usage! Please use /NSeasons <season : seasons>!"));
            }

        }
        return false;
    }
}
