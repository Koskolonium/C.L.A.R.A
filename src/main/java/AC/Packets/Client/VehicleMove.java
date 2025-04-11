package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsG;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientVehicleMove;
import org.bukkit.entity.Player;

public class VehicleMove extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the VEHICLE_MOVE packet type
        if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
            handleVehicleMove(event.getPlayer(), event);
        }
    }

    private void handleVehicleMove(Player player, PacketReceiveEvent event) {
        // Create an instance of WrapperPlayClientVehicleMove
        WrapperPlayClientVehicleMove vehicleMoveWrapper = new WrapperPlayClientVehicleMove(event);

        // Extract position and rotation data from the wrapper
        Vector3d position = vehicleMoveWrapper.getPosition();
        float yaw = vehicleMoveWrapper.getYaw();
        float pitch = vehicleMoveWrapper.getPitch();
        // Validate the movement using the BadPacketsG utility class
        if (!BadPacketsG.isValid(position.getX(), position.getY(), position.getZ(), yaw, pitch)) {
            // If the movement is invalid, kick the player
            KickMessages.kickPlayerForInvalidPacket(player, "G");
        }
    }
}
