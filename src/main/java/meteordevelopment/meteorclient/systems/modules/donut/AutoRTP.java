package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoRTP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private int timer;

    public AutoRTP() {
        super(Categories.Donut, "Auto-RTP", "Automatically finds bases on the WGF server.");
    }

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay for each RTP in seconds")
        .defaultValue(2)
        .min(0)
        .max(10)
        .build()
    );

    @Override
    public void onActivate() {
        // Initialize the timer
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        // Reset the timer when the module is deactivated
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;

        if (player == null) return;  // Check if the player is null

        if (timer > 0) {
            timer--;  // Decrement the timer if it's greater than 0
            return;
        }

        // Execute the /rtp command
        mc.player.networkHandler.sendChatCommand("rtp");

        // Update the timer
        timer = (delay.get() + 3) * 20;  // 20 ticks per second * (delay + 3) seconds
    }

    public void disabilita() {
        if (isActive()) {
            toggle();  // Disabilita il modulo se è attivo
        }
    }
}
