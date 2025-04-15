package AC.Utils.CheckUtils;

import lombok.experimental.UtilityClass;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

/**
 * FastMath is a utility class for optimized mathematical operations.
 * This class provides reusable and efficient methods for common mathematical
 * calculations, such as vector operations and angle normalization.
 */
@UtilityClass
public class FastMath {

    // A small value used for zero comparisons to avoid floating-point precision errors
    public static final double EPSILON = 1e-5;

    /**
     * Subtracts one vector from another.
     *
     * @param a The vector to subtract from
     * @param b The vector to subtract
     * @return A new vector representing the result of a - b
     */
    public Vector3D subtract(Vector3D a, Vector3D b) {
        return new Vector3D(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
    }

    /**
     * Calculates the dot product of two vectors.
     *
     * @param a The first vector
     * @param b The second vector
     * @return The dot product of a and b
     */
    public double dotProduct(Vector3D a, Vector3D b) {
        return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
    }

    /**
     * Adds two vectors together.
     *
     * @param a The first vector
     * @param b The second vector
     * @return A new vector representing the result of a + b
     */
    public Vector3D add(Vector3D a, Vector3D b) {
        return new Vector3D(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
    }

    /**
     * Calculates the Euclidean distance between two vectors.
     *
     * @param a The first vector
     * @param b The second vector
     * @return The distance between a and b
     */
    public double distance(Vector3D a, Vector3D b) {
        return Math.sqrt(
                Math.pow(a.getX() - b.getX(), 2) +
                        Math.pow(a.getY() - b.getY(), 2) +
                        Math.pow(a.getZ() - b.getZ(), 2)
        );
    }

    public boolean isZero(double value) {
        return Math.abs(value) < EPSILON;
    }


    /**
     * Normalizes an angle (e.g., yaw) to fall within the range [0, 360).
     *
     * @param angle The angle to normalize
     * @return The normalized angle in the range [0, 360)
     */
    public float normalizeAngle(float angle) {
        // Normalize using a single modulo operation and addition
        return (angle % 360 + 360) % 360;
    }
        /**
         * Calculates the delta (absolute difference) between two vectors.
         *
         * @param a The first vector
         * @param b The second vector
         * @return A new vector representing the absolute delta
         */
        public Vector3D calculateDelta(Vector3D a, Vector3D b) {
            // Directly use Math.abs() on the components
            double deltaX = Math.abs(a.getX() - b.getX());
            double deltaY = Math.abs(a.getY() - b.getY());
            double deltaZ = Math.abs(a.getZ() - b.getZ());

            // Return a new Vector3D with the calculated deltas
            return new Vector3D(deltaX, deltaY, deltaZ);
        }

        /**
         * Efficiently checks if two vectors are approximately equal using EPSILON.
         *
         * @param a The first vector
         * @param b The second vector
         * @return True if vectors are approximately equal, false otherwise
         */
        public boolean areVectorsApproximatelyEqual(Vector3D a, Vector3D b) {
            Vector3D delta = calculateDelta(a, b);
            return delta.getNorm() < EPSILON;
        }

    /**
     * Calculates the speed percentage relative to a given threshold.
     *
     * @param maxDelta The maximum delta value (highest delta among X, Y, Z).
     * @param threshold The threshold value for the speed check.
     * @return The calculated speed percentage.
     */
    public double calculateSpeedPercentage(double maxDelta, double threshold) {
        return (maxDelta / threshold) * 100;
    }

    public double getAverage(List<Long> pings) {
        if (pings.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (long ping : pings) {
            sum += ping;
        }
        return (double) sum / pings.size(); // Calculate average
    }
}


