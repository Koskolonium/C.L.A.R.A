package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

// This class is a utility for validating player position data.
// It ensures that player coordinates (x, y, z) fall within acceptable boundaries
// and are finite values (not NaN or infinite). This is important for detecting
// potential exploits or corrupted packets in a Minecraft server environment.

@UtilityClass
public class BadPacketsB {

    // Constants defining the valid range for player position coordinates.
    // These boundaries are chosen based on Minecraft's world limits.
    private static final int MIN_VALID_POSITION = -30000000; // Minimum possible position value
    private static final double MAX_VALID_POSITION = 30000000; // Maximum possible position value

    /**
     * Validates player position data based on two criteria:
     * 1. Ensures all values (x, y, z) are finite using Double.isFinite.
     * 2. Ensures values fall within the specified valid boundaries.
     *
     * @param x The player's X coordinate
     * @param y The player's Y coordinate
     * @param z The player's Z coordinate
     * @return True if the position data is valid, false otherwise
     */
    public boolean isValid(double x, double y, double z) {
        // First, check if the values are finite (not NaN or infinite).
        // Second, delegate to the isWithinBounds method to verify range limits.
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z) &&
                isWithinBounds(x, y, z);
    }

    /**
     * Helper method to check whether player position values are within valid ranges.
     * This is used internally by the isValid method for additional boundary checks.
     *
     * @param x The player's X coordinate
     * @param y The player's Y coordinate
     * @param z The player's Z coordinate
     * @return True if all values are within the defined range, false otherwise
     */
    private boolean isWithinBounds(double x, double y, double z) {
        // Validate that each coordinate falls within the specified range.
        return x >= MIN_VALID_POSITION && x <= MAX_VALID_POSITION &&
                y >= MIN_VALID_POSITION && y <= MAX_VALID_POSITION &&
                z >= MIN_VALID_POSITION && z <= MAX_VALID_POSITION;
    }
}
