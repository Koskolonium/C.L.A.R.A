package AC.Packets.Client;

import AC.CLARA;
import AC.Packets.BadPackets.BadPacketsD;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Chat extends PacketListenerAbstract {
    // Map to store the last action times for players
    private final Map<Player, Long> lastActionTimes = new HashMap<>();

    // Cooldown time in milliseconds (50 ms, equivalent to 1 tick in Minecraft)
    private final long COOLDOWN_TIME = 50L;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Check if the received packet is a chat message
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            // Handle the chat message
            handleChat(event.getPlayer(), event);
        }
    }

    private void handleChat(Player player, PacketReceiveEvent event) {
        // Wrap the incoming chat message packet to extract its content
        WrapperPlayClientChatMessage chatWrapper = new WrapperPlayClientChatMessage(event);

        // Retrieve the chat message from the wrapper
        String message = chatWrapper.getMessage();

        // Validate the message using BadPacketsD logic
        if (!BadPacketsD.isValid(message)) {
            // Cancel the packet if the message is invalid
            event.setCancelled(true);

            // Get the current system time in milliseconds
            long currentTime = System.currentTimeMillis();

            // Retrieve the last recorded action time for this player
            long lastActionTime = lastActionTimes.getOrDefault(player, 0L);

            // Check if enough time has passed since the last warning
            if (currentTime - lastActionTime >= COOLDOWN_TIME) {
                // Schedule a task to send a warning message to the player
                Bukkit.getScheduler().runTask(CLARA.getInstance(), () -> {
                    player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "[Moderator] C.L.A.R.A: " +
                            ChatColor.RED + "Please refrain from using offensive terms.");
                });

                // Update the last action time to enforce the cooldown
                lastActionTimes.put(player, currentTime);
            }
            return; // Stop processing further as the message was invalid
        }

        // Format the message for broadcasting: <playername> message
        String formattedMessage = "<" + player.getName() + "> " + message;

        // Cancel the packet to prevent duplicate broadcasting of the chat message
        event.setCancelled(true);

        // Get the current system time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Retrieve the last recorded action time for this player
        long lastActionTime = lastActionTimes.getOrDefault(player, 0L);

        // Check if enough time has passed since the last broadcast
        if (currentTime - lastActionTime >= COOLDOWN_TIME) {
            // Schedule a task to broadcast the message to all players on the server
            Bukkit.getScheduler().runTask(CLARA.getInstance(), () -> {
                Bukkit.broadcastMessage(formattedMessage);
            });

            // Update the last action time for broadcasting
            lastActionTimes.put(player, currentTime);
        }
    }
}