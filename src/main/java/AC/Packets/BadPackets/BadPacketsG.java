package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BadPacketsG {
    private static final int MIN_VALID_POSITION = -30000000;
    private static final double MAX_VALID_POSITION = 30000000;
    private static final float MIN_VALID_PITCH = -90.0f;
    private static final float MAX_VALID_PITCH = 90.0f;

    public static boolean isValid(double x, double y, double z, float yaw, float pitch) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) ||
                !Float.isFinite(yaw) || !Float.isFinite(pitch)) {
            System.out.println("Invalid position or orientation: x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch);
            return false;
        }

        if (!isWithinBounds(x, y, z, pitch)) {
            System.out.println("Out of bounds: x=" + x + ", y=" + y + ", z=" + z + ", pitch=" + pitch);
            return false;
        }

        return true;
    }

    private static boolean isWithinBounds(double x, double y, double z, float pitch) {
        return x >= MIN_VALID_POSITION && x <= MAX_VALID_POSITION &&
                y >= MIN_VALID_POSITION && y <= MAX_VALID_POSITION &&
                z >= MIN_VALID_POSITION && z <= MAX_VALID_POSITION &&
                pitch >= MIN_VALID_PITCH && pitch <= MAX_VALID_PITCH;
    }
}