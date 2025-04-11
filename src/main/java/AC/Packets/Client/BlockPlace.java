package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsI;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import org.bukkit.entity.Player;

public class BlockPlace extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_BLOCK_PLACEMENT packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            handleBlockPlace(event.getPlayer(), event);
        }
    }

    private void handleBlockPlace(Player player, PacketReceiveEvent event) {
        // Create an instance of WrapperPlayClientPlayerBlockPlacement
        WrapperPlayClientPlayerBlockPlacement blockPlacementWrapper = new WrapperPlayClientPlayerBlockPlacement(event);

        // Extract block placement data from the wrapper
        int faceId = blockPlacementWrapper.getFaceId();
        BlockFace face = blockPlacementWrapper.getFace();
        Vector3f cursorPosition = blockPlacementWrapper.getCursorPosition();
        boolean insideBlock = blockPlacementWrapper.getInsideBlock().orElse(false);
        int sequence = blockPlacementWrapper.getSequence();
        // Validate the block placement action using the BadPacketsI utility class
        if (!BadPacketsI.isValid(player, faceId, cursorPosition.x, cursorPosition.y, cursorPosition.z, insideBlock, sequence)) {
            // If the block placement is invalid, kick the player
            KickMessages.kickPlayerForInvalidPacket(player, "I");
        }
    }
}