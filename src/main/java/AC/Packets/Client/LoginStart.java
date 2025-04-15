package AC.Packets.Client;

import AC.Packets.BadPackets.BadPacketsK;
import AC.Utils.PluginUtils.KickMessages;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class LoginStart extends PacketListenerAbstract {

    private static final Map<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> blacklistedIPs = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_DURATION = 15000; // 15 seconds
    private static final long BLACKLIST_DURATION = 60000; // 1 minute
    private final ExecutorService executorService;


    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            handleLoginStart(event.getPlayer(), event);
        }
    }

    private void handleLoginStart(Player player, PacketReceiveEvent event) {
        executorService.submit(() -> {
            String playerIPFull = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
            String playerIP = getNetworkPortion(playerIPFull);

            long blacklistTime = blacklistedIPs.getOrDefault(playerIP, 0L);
            if (System.currentTimeMillis() - blacklistTime < BLACKLIST_DURATION) {
                event.setCancelled(true);
                return;
            }

            WrapperLoginClientLoginStart loginWrapper = new WrapperLoginClientLoginStart(event);
            String username = loginWrapper.getUsername();
            Optional<UUID> playerUUID = loginWrapper.getPlayerUUID();
            String uuidString = playerUUID.map(UUID::toString).orElse(null);

            long currentTime = System.currentTimeMillis();
            long lastRequestTime = rateLimitMap.getOrDefault(playerIP, 0L);

            if (currentTime - lastRequestTime < RATE_LIMIT_DURATION) {
                player.sendMessage("You are rate-limited. Please wait before retrying.");
                event.setCancelled(true);
                return;
            }

            rateLimitMap.put(playerIP, currentTime);

            boolean isValid = BadPacketsK.isValid(username, uuidString);

            if (!isValid) {
                blacklistedIPs.put(playerIP, System.currentTimeMillis());
                event.setCancelled(true);
                player.sendMessage("Bot Detected");
                return;
            }

            System.out.println("Player " + username + " has IP: " + playerIP);
        });
    }

    private String getNetworkPortion(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length >= 3) {
            return parts[0] + "." + parts[1] + "." + parts[2];
        }
        return ip;
    }

    public LoginStart(ExecutorService executorService) {
        this.executorService = executorService;
    }
}