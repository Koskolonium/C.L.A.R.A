package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsK;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;

import java.util.Optional;
import java.util.UUID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginStart extends PacketListenerAbstract {
    // Maps to track rate-limiting and blacklisting of users
    private static final Map<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> blacklistMap = new ConcurrentHashMap<>();
    private static final int RATE_LIMIT_DURATION = 10000; // Time window for rate-limiting in milliseconds (10 seconds)
    private static final int BLACKLIST_DURATION = 60000; // Duration for blacklisting in milliseconds (60 seconds)

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Identify the user sending the packet
        User user = event.getUser();
        // Check if the packet is a "LOGIN_START" type packet
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            // Process the "LOGIN_START" packet
            handleLoginStart(event, user);
        }
    }

    private void handleLoginStart(PacketReceiveEvent event, User user) {
        // Get the user's IP address
        String userIP = user.getAddress().getAddress().getHostAddress();
        // Extract the "network portion" of the IP address (first three segments)
        String playerIP = getNetworkPortion(userIP);

        // Step 1: Check if the user's IP is blacklisted
        if (isBlacklisted(playerIP)) {
            // If blacklisted, cancel the packet and notify the user
            event.setCancelled(true);
            user.sendMessage("You have been blacklisted due to suspicious activity. Please wait and try again later.");
            return; // Stop further processing
        }

        // Step 2: Check if the user's IP is rate-limited
        if (isRateLimited(playerIP)) {
            // If rate-limited, cancel the packet and notify the user
            event.setCancelled(true);
            user.sendMessage("You have been rate-limited. Please try again later.");
            return; // Stop further processing
        }

        // Step 3: Update the rate-limit map to log the user's login attempt
        rateLimitMap.put(playerIP, System.currentTimeMillis());

        // Step 4: Extract and validate the login data
        WrapperLoginClientLoginStart loginWrapper = new WrapperLoginClientLoginStart(event);
        String username = loginWrapper.getUsername(); // Extract username
        Optional<UUID> playerUUID = loginWrapper.getPlayerUUID(); // Extract UUID (if available)
        String uuidString = playerUUID.map(UUID::toString).orElse(null); // Convert UUID to string (if present)

        // Validate the username and UUID using the validation logic
        boolean isValid = BadPacketsK.isValid(username, uuidString);

        // Step 5: Handle invalid data
        if (!isValid) {
            // Blacklist the user's IP for a duration if the data is invalid
            blacklistMap.put(playerIP, System.currentTimeMillis() + BLACKLIST_DURATION);
            event.setCancelled(true); // Cancel the packet
        }
    }

    private String getNetworkPortion(String ip) {
        // Extract the first three segments of an IP address
        String[] parts = ip.split("\\.");
        if (parts.length >= 3) {
            return parts[0] + "." + parts[1] + "." + parts[2];
        }
        return ip; // Return the full IP if it doesn't have at least 3 segments
    }

    private boolean isRateLimited(String playerIP) {
        // Check if the user's IP has made a recent login request within the rate-limit duration
        Long lastRequestTime = rateLimitMap.get(playerIP);
        if (lastRequestTime == null) {
            return false; // No previous login request logged, not rate-limited
        }
        long elapsedTime = System.currentTimeMillis() - lastRequestTime; // Calculate time since last request
        return elapsedTime < RATE_LIMIT_DURATION; // Rate-limited if within the duration
    }

    private boolean isBlacklisted(String playerIP) {
        // Check if the user's IP is currently blacklisted
        Long blacklistExpiry = blacklistMap.get(playerIP);
        if (blacklistExpiry == null) {
            return false; // Not blacklisted
        }
        return System.currentTimeMillis() < blacklistExpiry; // Blacklisted if the current time is before the expiry
    }
}