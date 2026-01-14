package nl.openminetopia.modules.police.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BalaclavaNameTagManager {

    private static final String TEAM_PREFIX = "bal_";
    private static BalaclavaNameTagManager instance;

    private final Set<UUID> hiddenPlayers = new HashSet<>();
    private final Scoreboard scoreboard;

    private BalaclavaNameTagManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public static BalaclavaNameTagManager getInstance() {
        if (instance == null) {
            instance = new BalaclavaNameTagManager();
        }
        return instance;
    }

    public void hideNameTag(Player player) {
        if (hiddenPlayers.add(player.getUniqueId())) {
            apply(player);
        }
    }

    public void showNameTag(Player player) {
        if (hiddenPlayers.remove(player.getUniqueId())) {
            remove(player);
        }
    }

    public boolean hasHiddenNameTag(Player player) {
        return hiddenPlayers.contains(player.getUniqueId());
    }

    private void apply(Player player) {
        Team team = getOrCreateTeam(player);
        team.setNameTagVisibility(NameTagVisibility.NEVER);

        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    private void remove(Player player) {
        Team team = scoreboard.getTeam(getTeamName(player));
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister();
        }
    }

    private Team getOrCreateTeam(Player player) {
        String teamName = getTeamName(player);
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        return team;
    }

    private String getTeamName(Player player) {
        // UUID-based → veilig, max 16 chars
        return TEAM_PREFIX + player.getUniqueId().toString().substring(0, 8);
    }

    public void onPlayerQuit(Player player) {
        hiddenPlayers.remove(player.getUniqueId());
        remove(player);
    }
}
