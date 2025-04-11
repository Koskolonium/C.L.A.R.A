package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BadPacketsF {
    private static final int MIN_INVENTORY_SLOT = 0;
    private static final int MAX_INVENTORY_SLOT = 8;

    public boolean isValid(int slot) {
        return slot >= MIN_INVENTORY_SLOT && slot <= MAX_INVENTORY_SLOT;
    }
}