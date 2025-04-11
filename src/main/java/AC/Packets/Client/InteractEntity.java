package AC.Packets.Client;

import AC.Utils.PluginUtils.EntityGetter;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class InteractEntity extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Process only the INTERACT_ENTITY packet type
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            handleLook(event.getPlayer(), event);
        }
    }

    private void handleLook(Player player, PacketReceiveEvent event) {
        // Create an instance of WrapperPlayClientInteractEntity
        WrapperPlayClientInteractEntity interactWrapper = new WrapperPlayClientInteractEntity(event);

        // Extract entity ID and action from the wrapper
        int entityID = interactWrapper.getEntityId();
        WrapperPlayClientInteractEntity.InteractAction action = interactWrapper.getAction();;
        System.out.println("[DEBUG] Extracted Action Type: " + action);
        // Extract interaction hand, target position, and sneaking status
        Optional<Vector3f> target = interactWrapper.getTarget();
        InteractionHand hand = interactWrapper.getHand();
        boolean sneaking = interactWrapper.isSneaking().orElse(false);
        if (action == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT && target.isPresent()) {
            Vector3f targetVec = target.get();
            System.out.println("[DEBUG] Target Position: " + targetVec);
        }
        // Replace the current method of getting entities with EntityGetter
        String entityInfo = EntityGetter.getEntityMap().get(entityID);
        if (entityInfo == null) {
            System.out.println("[DEBUG] No matching entity found for ID: " + entityID);
            return;
        }
        System.out.println("[DEBUG] Found Entity Info: " + entityInfo);

        // Process interaction based on action type
        switch (action) {
            case INTERACT:
                System.out.println("[DEBUG] Player " + player.getName() + " interacted with entity: " + entityInfo);
                break;
            case ATTACK:
                System.out.println("[DEBUG] Player " + player.getName() + " attacked entity: " + entityInfo);
                break;
            case INTERACT_AT:
                if (target.isPresent()) {
                    Vector3f targetVec = target.get();
                    System.out.println("[DEBUG] Player " + player.getName() + " interacted at " + targetVec + " on entity ID: " + entityID);
                } else {
                    System.out.println("[DEBUG] Missing target vector for INTERACT_AT action.");
                }
                break;
            default:
                System.out.println("[DEBUG] Unsupported interaction type (" + action + ")");
        }
        System.out.println("[DEBUG] Completed processing for player: " + player.getName());
    }
}
