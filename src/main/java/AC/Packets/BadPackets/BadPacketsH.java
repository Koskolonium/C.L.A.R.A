package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BadPacketsH {
    private static final float MAX_VALID_MOVEMENT = 0.98f;

    public boolean isValidSteerMovement(float forward, float sideways) {
        return Float.isFinite(forward) && Float.isFinite(sideways) &&
                Math.abs(forward) <= MAX_VALID_MOVEMENT && Math.abs(sideways) <= MAX_VALID_MOVEMENT;
    }
}