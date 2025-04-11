package AC.Utils.PluginUtils;

import AC.CLARA;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Utility class to construct and manage player kick messages.
 */
@UtilityClass
public class KickMessages {

    /**
     * Kicks the player with a dynamically constructed message based on the packet type.
     *
     * @param player     The player to be kicked from the server.
     * @param packetType The type of invalid packet detected (e.g., "A", "B", "C").
     */
    public static void kickPlayerForInvalidPacket(Player player, String packetType) {
        // Construct the kick message dynamically using the packet type
        String kickMessage = ChatColor.RED + "Kicked by " + ChatColor.AQUA + "" + ChatColor.BOLD +
                "[Moderator] C.L.A.R.A: " + ChatColor.RED + "InvalidPacket " + packetType;

        // Schedule the task to kick the player on the main server thread using CLARA.getInstance()
        new BukkitRunnable() {
            @Override
            public void run() {
                player.kickPlayer(kickMessage);
            }
        }.runTask(CLARA.getInstance());
    }

    /**
     * Kicks the player with a dynamically constructed message for speed check violations.
     *
     * @param player The player to be kicked from the server.
     * @param delta  The delta value representing the speed check violation detected.
     */
    public static void kickPlayerForSpeedCheck(Player player, String delta) {
        // Construct the kick message dynamically using the speed check delta value
        String kickMessage = ChatColor.RED + "Kicked by " + ChatColor.AQUA + "" + ChatColor.BOLD +
                "[Moderator] C.L.A.R.A: " + ChatColor.RED + "SpeedCheckA " + delta;

        // Schedule the task to kick the player on the main server thread using CLARA.getInstance()
        new BukkitRunnable() {
            @Override
            public void run() {
                player.kickPlayer(kickMessage);
            }
        }.runTask(CLARA.getInstance());
    }
}