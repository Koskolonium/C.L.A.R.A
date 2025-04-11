package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsF;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import org.bukkit.entity.Player;

public class HeldItemSlot extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the HELD_ITEM_CHANGE packet type
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            handleHeldItemSlot(event.getPlayer(), event);
        }
    }

    private void handleHeldItemSlot(Player player, PacketReceiveEvent event) {
        // Create a WrapperPlayClientHeldItemChange instance
        WrapperPlayClientHeldItemChange heldItemWrapper = new WrapperPlayClientHeldItemChange(event);

        // Extract the slot number the player is attempting to switch to
        int slot = heldItemWrapper.getSlot();

        // Validate the slot number using the BadPacketsF utility class
        if (!BadPacketsF.isValid(slot)) {
            // If the slot is invalid, schedule a task on the main server thread
            // to safely kick the player with an appropriate message
            KickMessages.kickPlayerForInvalidPacket(player, "F");
        }
    }
}
