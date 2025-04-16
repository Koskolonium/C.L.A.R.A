package AC.Checks.Combat;

import AC.CLARA;
import AC.Utils.CheckUtils.Hitbox;
import AC.Utils.CheckUtils.PlayerData;
import AC.Utils.CheckUtils.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Getter
public class ReachCheckA {
    private final Map<UUID, Hitbox> hitboxes = new HashMap<>();
    private static final double MAX_REACH_DISTANCE = 3.0;
    private static final double KICK_REACH_DISTANCE = 6.0;
    private static final Logger logger = Logger.getLogger("ReachCheckA");

    public void updateHitbox(Player player) {
        System.out.println("[ReachCheckA] Updating hitbox for player: " + player.getName());
        Hitbox playerHitbox = hitboxes.computeIfAbsent(player.getUniqueId(), key -> new Hitbox());
        playerHitbox.setSneaking(player.isSneaking());

        Vector3D convertedPosition = new Vector3D(
                player.getLocation().toVector().getX(),
                player.getLocation().toVector().getY(),
                player.getLocation().toVector().getZ()
        );
        playerHitbox.updatePosition(convertedPosition);

        System.out.println("[ReachCheckA] Hitbox updated for player: " + player.getName() + ", Sneaking: " + player.isSneaking());
    }

    public void checkHit(Entity attacker, Entity victim) {
        System.out.println("[ReachCheckA] Checking hit between attacker: " + attacker.getName() + " and victim: " + victim.getName());

        if (attacker instanceof Player playerAttacker) {
            System.out.println("[ReachCheckA] Attacker is a player: " + playerAttacker.getName());
            updateHitbox(playerAttacker);

            if (victim instanceof Player playerVictim) {
                System.out.println("[ReachCheckA] Victim is a player: " + playerVictim.getName());

                Hitbox victimHitbox = hitboxes.computeIfAbsent(playerVictim.getUniqueId(), key -> new Hitbox());
                victimHitbox.setSneaking(playerVictim.isSneaking());
                updateHitbox(playerVictim);

                long attackerPing = (long) CLARA.getPlayerData(playerAttacker.getUniqueId()).getCurrentPing();
                System.out.println("[ReachCheckA] Attacker Ping: " + attackerPing);

                Vector victimPosition = getVictimPositionAtTime(playerVictim.getUniqueId(), attackerPing);
                Vector attackerPosition = getVictimPositionAtTime(playerAttacker.getUniqueId(), attackerPing);

                if (victimPosition == null || attackerPosition == null) {
                    System.out.println("[ReachCheckA] Invalid position(s) retrieved. Skipping reach check.");
                    return;
                }

                System.out.println("[ReachCheckA] Victim position: " + victimPosition);
                System.out.println("[ReachCheckA] Attacker position: " + attackerPosition);

                attackerPosition.setY(attackerPosition.getY() + 1.62);

                Vector direction = playerAttacker.getEyeLocation().getDirection().normalize();
                Vector reachEndPoint = attackerPosition.clone().add(direction.multiply(MAX_REACH_DISTANCE));

                double intersectionDistance = victimHitbox.checkLineIntersection(attackerPosition, reachEndPoint);

                if (intersectionDistance != -1) {
                    if (intersectionDistance > KICK_REACH_DISTANCE) {
                        System.out.println("Your hit was out of reach: " + intersectionDistance + " blocks.");
                        return;
                    }

                    playerAttacker.sendMessage("You hit the target at: " + intersectionDistance + " blocks.");
                    playerVictim.sendMessage("You were hit at: " + intersectionDistance + " blocks.");
                } else {
                    System.out.println("[ReachCheckA] hit to far.");
                }
            } else {
                System.out.println("[ReachCheckA] Victim is not a player, skipping reach check.");
            }
        } else {
            System.out.println("[ReachCheckA] Attacker is not a player, skipping reach check.");
        }
    }

    public Vector getVictimPositionAtTime(UUID victimUUID, long timeAgo) {
        System.out.println("[ReachCheckA] Retrieving position for victim UUID: " + victimUUID + " at time: " + timeAgo + "ms ago");
        PlayerData playerData = CLARA.getPlayerData(victimUUID);
        Position position = playerData.getPositionAtTime(timeAgo);

        if (position == null) {
            System.out.println("[ReachCheckA] No position found for victim UUID: " + victimUUID + " at time: " + timeAgo);
            return null;
        }

        System.out.println("[ReachCheckA] Position for victim UUID: " + victimUUID + " at time " + timeAgo + ": " + position);
        return new Vector(position.getX(), position.getY(), position.getZ());
    }
}
