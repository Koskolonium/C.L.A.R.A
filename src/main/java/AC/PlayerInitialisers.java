package AC;

import AC.Checks.Movement.SpeedCheckA;
import AC.Utils.CheckUtils.PlayerData;
import AC.Utils.PluginUtils.PlayerOpStorage;
import AC.Utils.PluginUtils.sendPingPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static AC.CLARA.playerDataMap;

/**
 * The PlayerInitialisers class is responsible for handling player-related events
 * (e.g., when players join or quit the server) and managing their associated data.
 */
public class PlayerInitialisers implements Listener {
    private final PlayerOpStorage playerOpStorage; // Stores and manages operator-related data for players
    private final ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap; // Manages SpeedCheckA instances for each player
    private final ExecutorService threadPool; // Thread pool for handling asynchronous tasks

    /**
     * Constructor for initializing PlayerInitialisers.
     *
     * @param playerOpStorage The PlayerOpStorage instance to manage player operator data
     * @param speedCheckMap   The ConcurrentHashMap to manage SpeedCheckA instances for players
     */
    public PlayerInitialisers(PlayerOpStorage playerOpStorage, ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap, ExecutorService threadPool) {
        this.playerOpStorage = playerOpStorage;
        this.speedCheckMap = speedCheckMap;
        this.threadPool = threadPool; // Assign the provided thread pool
    }

    /**
     * Handles the PlayerJoinEvent when a player joins the server.
     * This method initializes the necessary data for the player.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Update the player's operator status in PlayerOpStorage
        playerOpStorage.updatePlayerOperatorStatus(player);

        // Create a new SpeedCheckA instance for the player
        SpeedCheckA speedCheckA = new SpeedCheckA(playerUUID, playerOpStorage, threadPool);

        // Store the SpeedCheckA instance in the speedCheckMap for the player
        speedCheckMap.put(playerUUID, speedCheckA);

        // Initialize PlayerData
        playerDataMap.put(playerUUID, new PlayerData());

        // Call the triggerPing method from sendPingPacket class
        sendPingPacket.triggerPing(player);
    }

    /**
     * Handles the PlayerQuitEvent when a player leaves the server.
     * This method cleans up the player's associated data.
     *
     * @param event The PlayerQuitEvent triggered when a player leaves the server
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Remove the player's SpeedCheckA instance from the speedCheckMap
        speedCheckMap.remove(playerUUID);

        // Remove the player's operator status from PlayerOpStorage
        playerOpStorage.removePlayerOperatorStatus(player);

        // Retrieve and stop the ping logging thread for this player's data
        PlayerData pd = playerDataMap.get(playerUUID);
        if (pd != null) {
            pd.stopPingLogging();
        }

        playerDataMap.remove(playerUUID);

    }
}