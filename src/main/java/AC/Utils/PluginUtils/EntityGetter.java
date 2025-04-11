package AC.Utils.PluginUtils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class EntityGetter {

    // ConcurrentHashMap to store entities and additional information
    @Getter
    private static final ConcurrentHashMap<Integer, String> entityMap = new ConcurrentHashMap<>();

    // Method to retrieve the main plugin instance dynamically
    private static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("CLARA");
    }

    // Method to start the task to update the entity map every 10 ticks
    public static void startEntityUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            entityMap.clear(); // Clear the map before updating
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    // Use entity ID as the key
                    if (entity.getType() == EntityType.PLAYER) {
                        // If the entity is a player, store its name
                        Player player = (Player) entity; // Cast entity to Player
                        entityMap.put(player.getEntityId(), "Player: " + player.getName());

                    } else {
                        // Otherwise, store the entity type information
                        entityMap.put(entity.getEntityId(), entity.getType().toString());
                    }
                }
            }
        }, 0L, 10L); // 10 ticks interval
    }
}
