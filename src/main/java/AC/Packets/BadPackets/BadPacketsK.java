package AC.Packets.BadPackets;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BadPacketsK {
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String PLAYER_NAME_PATTERN = "^[a-zA-Z0-9_]{1,16}$"; // Minecraft usernames follow this format

    public boolean isValid(String playerName, String uuid) {
        return playerName != null && playerName.matches(PLAYER_NAME_PATTERN) &&
                uuid != null && uuid.matches(UUID_PATTERN);
    }
}