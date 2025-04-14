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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginStart extends PacketListenerAbstract {

    private static final Map<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_DURATION = 15000; // 15 seconds

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
        String uuidString = playerUUID.map(UUID::toString).orElse(null);
        String playerIP = player.getAddress().getAddress().getHostAddress();

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
            KickMessages.kickPlayerForInvalidPacket(player, "K");
        }

        System.out.println("Player " + username + " has IP: " + playerIP);
    }
}