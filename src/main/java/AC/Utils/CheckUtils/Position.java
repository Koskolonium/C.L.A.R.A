package AC.Utils.CheckUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class Position {
    private final double x;
    private final double y;
    private final double z;
    private final long timestamp;

    // Method to calculate the distance to a Bukkit Location
    public double distanceTo(Location location) {
        double deltaX = this.x - location.getX();
        double deltaY = this.y - location.getY();
        double deltaZ = this.z - location.getZ();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
}