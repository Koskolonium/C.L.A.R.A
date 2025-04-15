package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsK;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;

import java.util.Optional;
import java.util.UUID;

public class LoginStart extends PacketListenerAbstract {

    // Constructor: Initializes the listener for login start packets.
    public LoginStart() {
    }

    // Listens for incoming packets and triggers handling logic when a LOGIN_START packet is detected.
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // The packet type is checked to ensure we only handle relevant login start packets.
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            handleLoginStart(event); // Delegates processing to a specific method.
        }
    }

    private void handleLoginStart(PacketReceiveEvent event) {
        // Extracts username and UUID from the packet itself as the player object hasn't been created yet.
        WrapperLoginClientLoginStart loginWrapper = new WrapperLoginClientLoginStart(event);
        String username = loginWrapper.getUsername();
        Optional<UUID> playerUUID = loginWrapper.getPlayerUUID();
        String uuidString = playerUUID.map(UUID::toString).orElse(null);
        // Validates the username and UUID to ensure the request isn't from a bot or malformed packet.
        boolean isValid = BadPacketsK.isValid(username, uuidString);

        if (!isValid) {
            event.setCancelled(true); // Blocks the packet as malicious.
            return;
        }
    }
}