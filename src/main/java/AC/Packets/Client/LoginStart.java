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
        System.out.println("[DEBUG] LoginStart: Constructor initialized");
    }

    // Listens for incoming packets and triggers handling logic when a LOGIN_START packet is detected.
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // The packet type is checked to ensure we only handle relevant login start packets.
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            System.out.println("[DEBUG] onPacketReceive: Detected LOGIN_START packet");
            handleLoginStart(event); // Delegates processing to a specific method.
        }
    }

    private void handleLoginStart(PacketReceiveEvent event) {
        System.out.println("[DEBUG] handleLoginStart: Handling login start");

        // Extracts username and UUID from the packet itself as the player object hasn't been created yet.
        WrapperLoginClientLoginStart loginWrapper = new WrapperLoginClientLoginStart(event);
        String username = loginWrapper.getUsername();
        Optional<UUID> playerUUID = loginWrapper.getPlayerUUID();
        String uuidString = playerUUID.map(UUID::toString).orElse(null);

        System.out.println("[DEBUG] handleLoginStart: Username: " + username);
        System.out.println("[DEBUG] handleLoginStart: Player UUID: " + uuidString);

        // Validates the username and UUID to ensure the request isn't from a bot or malformed packet.
        boolean isValid = BadPacketsK.isValid(username, uuidString);
        System.out.println("[DEBUG] handleLoginStart: Username and UUID validation result: " + isValid);

        if (!isValid) {
            System.out.println("[DEBUG] handleLoginStart: Bot detected");
            event.setCancelled(true); // Blocks the packet as malicious.
            return;
        }

        // Logs a successful validation for monitoring purposes.
        System.out.println("[DEBUG] handleLoginStart: Player " + username + " has successfully validated.");
    }
}