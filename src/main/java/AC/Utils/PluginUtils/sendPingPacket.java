package AC.Utils.PluginUtils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class sendPingPacket {
    // A map to store player ping timestamps
    private static final Map<String, Long> playerPingTimestamps = new HashMap<>();

    // Method to send a ping packet
    private static void sendPingPacket(Player player) {
        WrapperPlayServerPing pingPacket = new WrapperPlayServerPing(0);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, pingPacket);
        long timestamp = System.currentTimeMillis();
        playerPingTimestamps.put(player.getName(), timestamp);
    }

    // Method to trigger the ping packet from anywhere
    public static void triggerPing(Player player) {
        sendPingPacket(player);
    }

    // Public method to retrieve the timestamp for a specific player
    public static Long getPingTimestamp(String playerName) {
        return playerPingTimestamps.get(playerName);
    }
}