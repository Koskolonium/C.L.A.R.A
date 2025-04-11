package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

// Utility class for validating player position and orientation data in network packets.
// This class ensures that position values (x, y, z) and orientation values (yaw, pitch)
// are within acceptable ranges and finite. Such validation is crucial for detecting
// corrupted or malicious packets that could disrupt gameplay or lead to exploits.

@UtilityClass
public class BadPacketsA {

    // Constants defining the valid ranges for player positions and orientations.
    // These boundaries correspond to Minecraft's world limits for coordinates
    // and realistic angles for orientation.
    private static final int MIN_VALID_POSITION = -30000000; // Minimum possible position value
    private static final double MAX_VALID_POSITION = 30000000; // Maximum possible position value
    private static final float MIN_VALID_PITCH = -90.0f; // Minimum possible pitch (looking up)
    private static final float MAX_VALID_PITCH = 90.0f;  // Maximum possible pitch (looking down)
    private static final int Min_Valid_Yaw = 0;          // Minimum possible yaw (north direction)
    private static final int Max_Valid_Yaw = 360;        // Maximum possible yaw (full rotation)

    /**
     * Validates player packet data for position and orientation.
     * Ensures all values are finite (not NaN or infinite) and fall within valid bounds.
     *
     * @param x Player's X coordinate
     * @param y Player's Y coordinate
     * @param z Player's Z coordinate
     * @param yaw Player's yaw (horizontal rotation)
     * @param pitch Player's pitch (vertical rotation)
     * @return True if the data is valid, false otherwise
     */
    public static boolean isValid(double x, double y, double z, float yaw, float pitch) {
        // Ensure position and orientation values are finite and within bounds
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) &&
                isWithinBounds(x, y, z, yaw, pitch);
    }

    /**
     * Checks whether player position and orientation values fall within acceptable ranges.
     * Used internally by the isValid method to verify bounds for all values.
     *
     * @param x Player's X coordinate
     * @param y Player's Y coordinate
     * @param z Player's Z coordinate
     * @param yaw Player's yaw (horizontal rotation)
     * @param pitch Player's pitch (vertical rotation)
     * @return True if all values are within valid ranges, false otherwise
     */
    private static boolean isWithinBounds(double x, double y, double z, float yaw, float pitch) {
        // Validate position coordinates and orientation angles
        return x >= MIN_VALID_POSITION && x <= MAX_VALID_POSITION &&
                y >= MIN_VALID_POSITION && y <= MAX_VALID_POSITION &&
                z >= MIN_VALID_POSITION && z <= MAX_VALID_POSITION &&
                Float.isFinite(yaw) && Float.isFinite(pitch) &&
                pitch >= MIN_VALID_PITCH && pitch <= MAX_VALID_PITCH &&
                yaw >= Min_Valid_Yaw && yaw <= Max_Valid_Yaw;
    }
}
