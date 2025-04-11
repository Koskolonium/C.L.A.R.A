package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsC;
import AC.Utils.CheckUtils.FastMath;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import org.bukkit.entity.Player;

public class Look extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_ROTATION packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            handleLook(event.getPlayer(), event);
        }
    }

    private void handleLook(Player player, PacketReceiveEvent event) {
        // Create the wrapper using the PacketReceiveEvent
        WrapperPlayClientPlayerRotation wrapper =
                new WrapperPlayClientPlayerRotation(event);

        // Extract orientation data from the wrapper
        float yaw = wrapper.getYaw();
        float pitch = wrapper.getPitch();
        boolean onGround = wrapper.isOnGround(); // Note: Not currently used but extracted for reference

        // Normalize yaw using FastMath utility class to ensure it's within valid bounds
        yaw = FastMath.normalizeAngle(yaw);
        wrapper.setYaw(yaw);
        float finalYaw = yaw;
        // Validate packet orientation data using BadPacketsC logic
        if (!BadPacketsC.isValid(finalYaw, pitch)) {
            // Kick the player if packet data is deemed invalid
            KickMessages.kickPlayerForInvalidPacket(player, "C");
            return; // Exit early to avoid further processing
        }
    }
}