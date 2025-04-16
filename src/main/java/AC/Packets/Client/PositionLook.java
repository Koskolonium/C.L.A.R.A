package AC.Packets.Client;

import AC.CLARA;
import AC.Checks.Movement.SpeedCheckA;
import AC.Packets.BadPackets.BadPacketsA;
import AC.Utils.CheckUtils.FastMath;
import AC.Utils.CheckUtils.PlayerData;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PositionLook extends PacketListenerAbstract {
    private final ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap;

    public PositionLook(ConcurrentHashMap<UUID, SpeedCheckA> speedCheckMap) {
        this.speedCheckMap = speedCheckMap; // Assign in the constructor
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_POSITION_AND_ROTATION packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            handlePositionLook(event.getPlayer(), event);
        }
    }

    private void handlePositionLook(Player player, PacketReceiveEvent event) {
        UUID playerUUID = player.getUniqueId();
        Vector3d position;
        float yaw, pitch;
        double x, y, z;
        boolean onGround; // Declare variable for onGround state

        try {
            WrapperPlayClientPlayerPositionAndRotation wrapper = new WrapperPlayClientPlayerPositionAndRotation(event);
            position = wrapper.getPosition();
            x = position.getX();
            y = position.getY();
            z = position.getZ();
            yaw = FastMath.normalizeAngle(wrapper.getYaw());
            pitch = wrapper.getPitch();
            onGround = wrapper.isOnGround(); // Retrieve onGround state from wrapper
        } catch (Exception e) {
            e.printStackTrace();
            return; // Exit if packet extraction fails
        }

        // Use the thread pool for further processing, including optional re-validation
        try {
            // Validate packet data using BadPacketsA logic
            if (!BadPacketsA.isValid(position.getX(), position.getY(), position.getZ(), yaw, pitch)) {
                // Kick the player if packet data is deemed invalid
                KickMessages.kickPlayerForInvalidPacket(player, "A");
                return; // Exit early to avoid further processing
            }
            SpeedCheckA speedCheckA = speedCheckMap.get(playerUUID);
            if (speedCheckA != null) {
                speedCheckA.handlePosition(player, x, y, z);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log exception for debugging purposes
        }
        PlayerData playerData = CLARA.getPlayerData(playerUUID);
        playerData.addPosition(x, y, z);
    }
}
