package AC.Utils.CheckUtils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

@Getter
@Setter
public class Hitbox {
    private static final double STANDING_HEIGHT_PLAYER = 1.8;
    private static final double SNEAKING_HEIGHT_PLAYER = 1.5;
    private static final double WIDTH_PLAYER = 0.6;
    private static final double EPSILON = 1e-8;

    private double height = STANDING_HEIGHT_PLAYER;
    private double width = WIDTH_PLAYER;
    private boolean isSneaking;
    private Vector position = new Vector();

    public void setSneaking(boolean sneaking) {
        this.isSneaking = sneaking;
        this.height = sneaking ? SNEAKING_HEIGHT_PLAYER : STANDING_HEIGHT_PLAYER;
    }

    public void updatePosition(Vector3D playerLocation) {
        this.position = new Vector(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
    }

    public double checkLineIntersection(Vector start, Vector end) {
        double halfWidth = width / 2;
        Vector min = position.clone().add(new Vector(-halfWidth, 0, -halfWidth));
        Vector max = position.clone().add(new Vector(halfWidth, height, halfWidth));
        Double minDistance = null;

        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "front"));
        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "back"));
        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "left"));
        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "right"));
        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "top"));
        minDistance = getClosestIntersection(minDistance, intersectFace(start, end, min, max, "bottom"));
        minDistance = getClosestIntersection(minDistance, intersectCorners(start, end, min, max));

        return (minDistance != null) ? minDistance : -1;
    }

    private Double getClosestIntersection(Double currentMin, Double newDistance) {
        if (newDistance == null || newDistance < 0) return currentMin;
        return (currentMin == null || newDistance < currentMin) ? newDistance : currentMin;
    }

    private Double intersectFace(Vector start, Vector end, Vector min, Vector max, String face) {
        Vector3D start3D = new Vector3D(start.getX(), start.getY(), start.getZ());
        Vector3D end3D = new Vector3D(end.getX(), end.getY(), end.getZ());
        Vector3D min3D = new Vector3D(min.getX(), min.getY(), min.getZ());
        Vector3D max3D = new Vector3D(max.getX(), max.getY(), max.getZ());

        Vector3D normal;
        return switch (face) {
            case "front" -> {
                normal = new Vector3D(0, 0, -1);
                yield calculateIntersection(start3D, end3D, normal, min3D);
            }
            case "back" -> {
                normal = new Vector3D(0, 0, 1);
                yield calculateIntersection(start3D, end3D, normal, max3D);
            }
            case "left" -> {
                normal = new Vector3D(-1, 0, 0);
                yield calculateIntersection(start3D, end3D, normal, min3D);
            }
            case "right" -> {
                normal = new Vector3D(1, 0, 0);
                yield calculateIntersection(start3D, end3D, normal, max3D);
            }
            case "top" -> {
                normal = new Vector3D(0, 1, 0);
                yield calculateIntersection(start3D, end3D, normal, max3D);
            }
            case "bottom" -> {
                normal = new Vector3D(0, -1, 0);
                yield calculateIntersection(start3D, end3D, normal, min3D);
            }
            default -> null;
        };
    }

    private Double intersectCorners(Vector start, Vector end, Vector min, Vector max) {
        Vector[] corners = {
                min.clone(),
                new Vector(max.getX(), min.getY(), min.getZ()),
                new Vector(min.getX(), max.getY(), min.getZ()),
                new Vector(min.getX(), min.getY(), max.getZ()),
                max.clone()
        };

        Double minDistance = null;
        for (Vector corner : corners) {
            double distance = getCornerIntersection(start, end, corner);
            if (distance >= 0 && distance <= end.distance(start)) {
                minDistance = getClosestIntersection(minDistance, distance);
            }
        }
        return minDistance;
    }

    private double calculateIntersection(Vector3D start, Vector3D end, Vector3D normal, Vector3D point) {
        Vector3D lineDir = FastMath.subtract(end, start); // Offload subtraction
        double denom = FastMath.dotProduct(normal, lineDir); // Use FastMath for dot product
        if (FastMath.isZero(denom)) { // Use FastMath for EPSILON comparison
            return -1;
        }
        double t = FastMath.dotProduct(FastMath.subtract(point, start), normal) / denom;
        if (t < 0 || t > 1) {
            return -1;
        }
        Vector3D intersectionPoint = FastMath.add(start, lineDir.scalarMultiply(t)); // Offload addition and scaling
        return FastMath.distance(start, intersectionPoint); // Use FastMath for distance calculation
    }


    private double getCornerIntersection(Vector start, Vector end, Vector corner) {
        Vector3D start3D = new Vector3D(start.getX(), start.getY(), start.getZ());
        Vector3D end3D = new Vector3D(end.getX(), end.getY(), end.getZ());
        Vector3D corner3D = new Vector3D(corner.getX(), corner.getY(), corner.getZ());

        Vector3D lineDir = end3D.subtract(start3D);
        Vector3D toCorner = corner3D.subtract(start3D);
        double t = toCorner.dotProduct(lineDir) / lineDir.getNormSq();

        if (t >= 0 && t <= 1) {
            Vector3D closestPoint = start3D.add(lineDir.scalarMultiply(t));
            return start3D.distance(closestPoint);
        }
        return -1;
    }
}