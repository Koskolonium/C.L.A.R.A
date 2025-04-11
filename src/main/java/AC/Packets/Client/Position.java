package AC.Packets.Client;

import AC.Checks.Movement.SpeedCheckA;
import AC.Packets.BadPackets.BadPacketsB;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Position extends PacketListenerAbstract {
    private final ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap;

    public Position(ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap) {
        this.speedCheckMap = speedCheckMap;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_POSITION packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            handlePosition(event.getPlayer(), event);
        }
    }

    private void handlePosition(Player player, PacketReceiveEvent event) {
        UUID playerUUID = player.getUniqueId();
        Vector3d position;
        double x, y, z;
        boolean onGround;
        try {
            WrapperPlayClientPlayerPosition wrapper = new WrapperPlayClientPlayerPosition(event);
            position = wrapper.getPosition();
            x = position.getX();
            y = position.getY();
            z = position.getZ();
            onGround = wrapper.isOnGround();
        } catch (Exception e) {
            e.printStackTrace();
            return; // Exit if packet extraction fails
        }

        // Use the thread pool for further processing, including optional re-validation
        try {
            // Validate packet data using BadPacketsB logic
            if (!BadPacketsB.isValid(x, y, z)) {
                // Kick the player if packet data is deemed invalid
                KickMessages.kickPlayerForInvalidPacket(player, "B");
                return; // Exit early to avoid further processing
            }
            SpeedCheckA speedCheckA = speedCheckMap.get(playerUUID);
            if (speedCheckA != null) {
                speedCheckA.handlePosition(player, x, y, z);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log exception for debugging purposes
        }
    }
}