package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AutoFloat extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public AutoFloat() {
        super(Categories.Donut, "Auto-Float", "Automatically floats on water if you are in water.");
    }

    // TickEvent handler
    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        if (player != null && player.isTouchingWater() && !player.isInLava()) {
            // Keep the player afloat by applying a small upward force
            Vec3d velocity = player.getVelocity();
            if (velocity.y < 0) {
                player.setVelocity(velocity.x, 0.1, velocity.z);  // Set a positive vertical velocity to float
            }
        }
    }
}
