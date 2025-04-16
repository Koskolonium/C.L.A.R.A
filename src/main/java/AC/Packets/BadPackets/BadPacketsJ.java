package AC.Packets.BadPackets;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@UtilityClass
public class BadPacketsJ {

    private static final int MIN_STATUS_VALUE = 0;
    private static final int MAX_STATUS_VALUE = 6;
    private static final int MIN_FACE_VALUE = 0;
    private static final int MAX_FACE_VALUE = 5;
    private static final int MIN_VALID_POSITION = -30000000;
    private static final int MAX_VALID_POSITION = 30000000;
    private static final double MAX_DIG_RADIUS = Math.sqrt(33); // Updated to 5.74456

    public boolean isValid(Player player, int locationX, int locationY, int locationZ, BlockFace blockFace, DiggingAction action, int status) {
        if (status < MIN_STATUS_VALUE || status > MAX_STATUS_VALUE) {
            return false;
        }

        switch (status) {
            case 0: // Start digging
            case 1: // Cancel digging
            case 2: // Finish digging
                if (!isWithinCoordinateBounds(locationX, locationY, locationZ) || !isWithinFaceBounds(blockFace)) {
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
                return isValidExemptAction(locationX, locationY, locationZ, blockFace);

            default:
                return false;
        }
    }

    private boolean isActionExemptFromRadiusCheck(DiggingAction action) {
        return action == DiggingAction.RELEASE_USE_ITEM
                || action == DiggingAction.DROP_ITEM
                || action == DiggingAction.DROP_ITEM_STACK
                || action == DiggingAction.SWAP_ITEM_WITH_OFFHAND;
    }

    private boolean isValidExemptAction(int locationX, int locationY, int locationZ, BlockFace blockFace) {
        return locationX == 0 && locationY == 0 && locationZ == 0 && blockFace == BlockFace.DOWN;
    }

    private boolean isWithinFaceBounds(BlockFace blockFace) {
        int faceValue = blockFace.getFaceValue();
        return faceValue >= MIN_FACE_VALUE && faceValue <= MAX_FACE_VALUE;
    }

    private boolean isWithinCoordinateBounds(int locationX, int locationY, int locationZ) {
        return locationX >= MIN_VALID_POSITION && locationX <= MAX_VALID_POSITION
                && locationY >= MIN_VALID_POSITION && locationY <= MAX_VALID_POSITION
                && locationZ >= MIN_VALID_POSITION && locationZ <= MAX_VALID_POSITION;
    }

    private boolean isWithinDiggingRadius(Player player, int locationX, int locationY, int locationZ) {
        Vector playerEyeLocation = player.getEyeLocation().toVector();
        Vector blockLocation = new Vector(locationX, locationY, locationZ);
        double distanceSquared = playerEyeLocation.distanceSquared(blockLocation);
        return distanceSquared <= MAX_DIG_RADIUS * MAX_DIG_RADIUS;
    }
}