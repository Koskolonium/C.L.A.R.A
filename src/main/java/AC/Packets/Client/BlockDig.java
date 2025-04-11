package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsJ;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.entity.Player;

public class BlockDig extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_DIGGING packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            handleBlockDig(event.getPlayer(), event);
        }
    }

    private void handleBlockDig(Player player, PacketReceiveEvent event) {
        // Create an instance of WrapperPlayClientPlayerDigging
        WrapperPlayClientPlayerDigging blockDiggingWrapper = new WrapperPlayClientPlayerDigging(event);

        // Extract block digging data from the wrapper
        DiggingAction action = blockDiggingWrapper.getAction();
        Vector3i blockPosition = blockDiggingWrapper.getBlockPosition();
        BlockFace blockFace = blockDiggingWrapper.getBlockFace();
        int sequence = blockDiggingWrapper.getSequence();

        // Retrieve block location coordinates
        int locationX = blockPosition.x;
        int locationY = blockPosition.y;
        int locationZ = blockPosition.z;

        // Validate the block digging action using the BadPacketsJ utility class
        if (!BadPacketsJ.isValid(player, locationX, locationY, locationZ, blockFace, action, action != null ? action.ordinal() : -1)) {
            // Use the KickMessage instance to handle the kick logic
            KickMessages.kickPlayerForInvalidPacket(player, "J");
        }
    }
}