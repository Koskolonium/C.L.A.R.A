package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsK;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class LoginStart extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            handleLoginStart(event.getPlayer(), event);
        }
    }

    private void handleLoginStart(Player player, PacketReceiveEvent event) {
        WrapperLoginClientLoginStart loginWrapper = new WrapperLoginClientLoginStart(event);

        String username = loginWrapper.getUsername();
        Optional<UUID> playerUUID = loginWrapper.getPlayerUUID();

        // Convert UUID to String before passing it
        String uuidString = playerUUID.map(UUID::toString).orElse(null);

        boolean isValid = BadPacketsK.isValid(username, uuidString);

        if (!isValid) {
            KickMessages.kickPlayerForInvalidPacket(player, "K");
        }
    }
}