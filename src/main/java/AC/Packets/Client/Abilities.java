package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsE;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;
import org.bukkit.entity.Player;

public class Abilities extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the PLAYER_ABILITIES packet type
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            handleAbilities(event.getPlayer(), event);
        }
    }

    private void handleAbilities(Player player, PacketReceiveEvent event) {
    // Create a WrapperPlayClientPlayerAbilities instance
        WrapperPlayClientPlayerAbilities abilitiesWrapper = new WrapperPlayClientPlayerAbilities(event);
        // Retrieve the player's flying status from the wrapper
        boolean isFlying = abilitiesWrapper.isFlying();
        // Validate flying status using BadPacketsE logic
        if (!BadPacketsE.isValidFlying(player, isFlying)) {
            // Kick the player if flying status is deemed invalid
            KickMessages.kickPlayerForInvalidPacket(player, "E");
        }
    }
}
