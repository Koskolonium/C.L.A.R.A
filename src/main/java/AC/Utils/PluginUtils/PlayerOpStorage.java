package AC.Utils.PluginUtils;

import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The PlayerOpStorage class is responsible for managing the operator (op) status of players.
 * It stores this information in a thread-safe manner using a ConcurrentHashMap.
 */
public class PlayerOpStorage {
    // Thread-safe map to store operator status for players
    // The key is the player's UUID (as a String), and the value is a Boolean indicating op status
    private final ConcurrentHashMap<String, Boolean> playerOpStatus = new ConcurrentHashMap<>();

    /**
     * Checks whether the given player is an operator.
     *
     * @param player The player whose operator status is being checked
     * @return True if the player is an operator, false otherwise
     */
    public boolean isPlayerOperator(Player player) {
        // Retrieve the op status from the map, defaulting to false if not found
        return playerOpStatus.getOrDefault(player.getUniqueId().toString(), false);
    }

    /**
     * Updates the operator status of a player.
     * This is typically called when a player joins the server or when their op status changes.
     *
     * @param player The player whose operator status is being updated
     */
    public void updatePlayerOperatorStatus(Player player) {
        // Store the player's UUID and their current op status in the map
        playerOpStatus.put(player.getUniqueId().toString(), player.isOp());
    }

    /**
     * Removes a player's data from the map.
     * This is typically called when a player leaves the server.
     *
     * @param player The player whose data is being removed
     */
    public void removePlayerOperatorStatus(Player player) {
        // Remove the player's UUID and their associated op status from the map
        playerOpStatus.remove(player.getUniqueId().toString());
    }
}