package AC.Packets.BadPackets;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

@UtilityClass
public class BadPacketsJ {
    private static final Logger LOGGER = Logger.getLogger(BadPacketsJ.class.getName());

    private static final int MIN_STATUS_VALUE = 0;
    private static final int MAX_STATUS_VALUE = 6;
    private static final int MIN_FACE_VALUE = 0;
    private static final int MAX_FACE_VALUE = 5;
    private static final int MIN_VALID_POSITION = -30000000;
    private static final int MAX_VALID_POSITION = 30000000;
    private static final double MAX_DIG_RADIUS = Math.sqrt(33); // Updated to 5.74456

    public boolean isValid(Player player, int locationX, int locationY, int locationZ, BlockFace blockFace, DiggingAction action, int status) {
        LOGGER.info(String.format("Validating action with Status: %d, Location: (%d, %d, %d), BlockFace: %s, Action: %s",
                status, locationX, locationY, locationZ, blockFace, action));

        if (status < MIN_STATUS_VALUE || status > MAX_STATUS_VALUE) {
            LOGGER.warning("Invalid status value!");
            return false;
        }

        switch (status) {
            case 0: // Start digging
            case 1: // Cancel digging
            case 2: // Finish digging
                LOGGER.info("Status corresponds to a digging action.");
                if (!isWithinCoordinateBounds(locationX, locationY, locationZ) || !isWithinFaceBounds(blockFace)) {
                    LOGGER.warning("Invalid location or block face.");
                    return false;
                }
                if (!isActionExemptFromRadiusCheck(action)) {
                    return isWithinDiggingRadius(player, locationX, locationY, locationZ);
                }
                return true;

            case 3: // Drop item stack
            case 4: // Drop item
            case 5: // Shoot arrow / finish eating
            case 6: // Swap item in hand
                LOGGER.info("Status corresponds to an exempt action.");
                return isValidExemptAction(locationX, locationY, locationZ, blockFace);

            default:
                LOGGER.warning("Unexpected status value.");
                return false;
        }
    }

    private boolean isActionExemptFromRadiusCheck(DiggingAction action) {
        LOGGER.info(String.format("Checking if action %s is exempt from radius check.", action));
        boolean result = action == DiggingAction.RELEASE_USE_ITEM
                || action == DiggingAction.DROP_ITEM
                || action == DiggingAction.DROP_ITEM_STACK
                || action == DiggingAction.SWAP_ITEM_WITH_OFFHAND;
        LOGGER.info(String.format("Action exempt from radius check: %s", result));
        return result;
    }

    private boolean isValidExemptAction(int locationX, int locationY, int locationZ, BlockFace blockFace) {
        LOGGER.info(String.format("Checking if action with Location: (%d, %d, %d), BlockFace: %s is valid exempt action.",
                locationX, locationY, locationZ, blockFace));
        boolean result = locationX == 0 && locationY == 0 && locationZ == 0 && blockFace == BlockFace.DOWN;
        LOGGER.info(String.format("Valid exempt action: %s", result));
        return result;
    }

    private boolean isWithinFaceBounds(BlockFace blockFace) {
        LOGGER.info(String.format("Validating block face: %s", blockFace));
        int faceValue = blockFace.getFaceValue();
        boolean result = faceValue >= MIN_FACE_VALUE && faceValue <= MAX_FACE_VALUE;
        LOGGER.info(String.format("Block face within bounds: %s", result));
        return result;
    }

    private boolean isWithinCoordinateBounds(int locationX, int locationY, int locationZ) {
        LOGGER.info(String.format("Validating coordinates: (%d, %d, %d)", locationX, locationY, locationZ));
        boolean result = locationX >= MIN_VALID_POSITION && locationX <= MAX_VALID_POSITION
                && locationY >= MIN_VALID_POSITION && locationY <= MAX_VALID_POSITION
                && locationZ >= MIN_VALID_POSITION && locationZ <= MAX_VALID_POSITION;
        LOGGER.info(String.format("Coordinates within bounds: %s", result));
        return result;
    }

    private boolean isWithinDiggingRadius(Player player, int locationX, int locationY, int locationZ) {
        LOGGER.info(String.format("Checking digging radius for Player: %s and Location: (%d, %d, %d)",
                player.getName(), locationX, locationY, locationZ));
        Vector playerEyeLocation = player.getEyeLocation().toVector();
        Vector blockLocation = new Vector(locationX, locationY, locationZ);
        double distanceSquared = playerEyeLocation.distanceSquared(blockLocation);
        LOGGER.info(String.format("Distance squared: %.2f, Maximum allowed: %.2f",
                distanceSquared, MAX_DIG_RADIUS * MAX_DIG_RADIUS));
        boolean result = distanceSquared <= MAX_DIG_RADIUS * MAX_DIG_RADIUS;
        LOGGER.info(String.format("Within digging radius: %s", result));
        return result;
    }
}