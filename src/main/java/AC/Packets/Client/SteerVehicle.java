package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsH;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import org.bukkit.entity.Player;

public class SteerVehicle extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the STEER_VEHICLE packet type
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            handleSteerVehicle(event.getPlayer(), event);
        }
    }

    private void handleSteerVehicle(Player player, PacketReceiveEvent event) {
        // Create an instance of WrapperPlayClientSteerVehicle
        WrapperPlayClientSteerVehicle steerVehicleWrapper = new WrapperPlayClientSteerVehicle(event);
        // Extract steering data from the wrapper
        float forward = steerVehicleWrapper.getForward();
        float sideways = steerVehicleWrapper.getSideways();
        boolean jump = steerVehicleWrapper.isJump();
        boolean unmount = steerVehicleWrapper.isUnmount();
        // Validate the steering inputs using the BadPacketsH utility class
        if (!BadPacketsH.isValidSteerMovement(forward, sideways)) {
            // If the inputs are invalid, kick the player
            KickMessages.kickPlayerForInvalidPacket(player, "H");
        }
    }
}