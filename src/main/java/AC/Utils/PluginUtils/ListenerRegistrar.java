package AC.Utils.PluginUtils;

import AC.Checks.Movement.SpeedCheckA;
import AC.Packets.Client.*;
import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ListenerRegistrar class is responsible for registering both packet listeners
 * (to handle network-level events) and event listeners (to handle Bukkit events) in the plugin.
 */
public final class ListenerRegistrar {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This ensures the class is used only for its static methods.
     */
    private ListenerRegistrar() {
    }

    /**
     * Registers packet listeners with the PacketEvents API to handle client-side packet events.
     * Each listener corresponds to a specific type of network packet sent by players.
     *
     * @param speedCheckMap A ConcurrentHashMap managing SpeedCheckA instances for players.
     *                      This is specifically used by the Position packet listener.
     */
    public static void registerPacketListeners(ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap) {
        // Register a packet listener for handling abilities-related packets
        PacketEvents.getAPI().getEventManager().registerListener(new Abilities());

        // Register a packet listener for block digging actions
        PacketEvents.getAPI().getEventManager().registerListener(new BlockDig());

        // Register a packet listener for block placement actions
        PacketEvents.getAPI().getEventManager().registerListener(new BlockPlace());

        // Register a packet listener for player chat messages
        PacketEvents.getAPI().getEventManager().registerListener(new Chat());

        // Register a packet listener for held item slot changes
        PacketEvents.getAPI().getEventManager().registerListener(new HeldItemSlot());

        // Register a packet listener for interactions with entities
        PacketEvents.getAPI().getEventManager().registerListener(new InteractEntity());

        // Register a packet listener for player look direction updates
        PacketEvents.getAPI().getEventManager().registerListener(new Look());

        // Register a packet listener for player movement (position changes)
        // This listener requires the speedCheckMap to track speed-related checks per player
        PacketEvents.getAPI().getEventManager().registerListener(new Position(speedCheckMap));

        // Register a packet listener for combined position and look updates
        // This listener requires the speedCheckMap to track speed-related checks per player
        PacketEvents.getAPI().getEventManager().registerListener(new PositionLook(speedCheckMap));

        // Register a packet listener for player steering vehicle actions
        PacketEvents.getAPI().getEventManager().registerListener(new SteerVehicle());

        // Register a packet listener for vehicle movement updates
        PacketEvents.getAPI().getEventManager().registerListener(new VehicleMove());

        // Register a packet listener for vehicle movement updates
        PacketEvents.getAPI().getEventManager().registerListener(new LoginStart());
    }

    /**
     * Registers event listeners with the Bukkit plugin manager.
     * These listeners respond to server-side events such as player interactions.
     *
     * @param plugin    The main plugin instance required for registering events.
     * @param listeners An array of Bukkit event listeners to be registered.
     */
    public static void registerEventListeners(Plugin plugin, org.bukkit.event.Listener... listeners) {
        for (org.bukkit.event.Listener listener : listeners) {
            // Register each listener with the server's plugin manager
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}