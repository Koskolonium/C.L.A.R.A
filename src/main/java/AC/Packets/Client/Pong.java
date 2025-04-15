package AC.Packets.Client;

import AC.CLARA;
import AC.Utils.CheckUtils.PlayerData;
import AC.Utils.PluginUtils.sendPingPacket;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Pong extends PacketListenerAbstract {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PONG) {
            handlePong(event.getPlayer(), event);
        }
    }

    private void handlePong(Player player, PacketReceiveEvent event) {
        long currentTimestamp = System.currentTimeMillis();
        String playerName = player.getName();

        // Retrieve the stored timestamp using the getter method in sendPingPacket
        Long sentTimestamp = sendPingPacket.getPingTimestamp(playerName);

        if (sentTimestamp != null) {
            long playerPing = currentTimestamp - sentTimestamp;
            UUID playerUUID = player.getUniqueId();
            PlayerData playerData = CLARA.getPlayerData(playerUUID);
            if (playerData != null) {
                playerData.setPing(playerPing);
                playerData.setPingTimestamp(currentTimestamp);
            }
            sendPingPacket.triggerPing(player);
        }
    }
}