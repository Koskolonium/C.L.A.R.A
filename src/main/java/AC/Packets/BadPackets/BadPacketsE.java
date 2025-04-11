package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class BadPacketsE {

    /**
     * Validates the flying state of the player.
     * @param player The player being validated.
     * @param isFlying Boolean representing whether the player is flying.
     * @return True if the player is valid; False otherwise.
     */
    public boolean isValidFlying(Player player, boolean isFlying) {
        // Simply return false for now
        return false;
    }
}

