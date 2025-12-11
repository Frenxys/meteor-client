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
        private final java.util.Random random = new java.util.Random();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private int timer;

    public enum Region {
        Asia,
        Na_East,
        Na_West,
        Eu_West,
        Eu_Central,
        Oceania,
        Nether,
        End
    }

    private final Setting<Region> region = sgGeneral.add(new meteordevelopment.meteorclient.settings.EnumSetting.Builder<Region>()
        .name("region")
        .description("Select the RTP region.")
        .defaultValue(Region.Na_East)
        .build()
    );

    public AutoRTP() {
        super(Categories.Donut, "Auto-RTP", "Automatically finds bases on the WGF server.");
    }

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay for each RTP in seconds")
        .defaultValue(5)
        .min(14)
        .max(20)
        .build()
    );

    private final Setting<Integer> delayRange = sgGeneral.add(new IntSetting.Builder()
        .name("delayrange")
        .description("Extra random delay (seconds) to bypass anticheat.")
        .defaultValue(3)
        .min(1)
        .max(10)
        .build()
    );

    private String getRegionCommand() {
        switch (region.get()) {
            case Asia: return "asia";
            case Na_East: return "east";
            case Na_West: return "west";
            case Eu_West: return "eu west";
            case Eu_Central: return "eu central";
            case Oceania: return "oceania";
            case Nether: return "nether";
            case End: return "end";
            default: return "east";
        }
    }

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

        // Execute the /rtp <region> command
        String cmd = "rtp " + getRegionCommand();
        mc.player.networkHandler.sendChatCommand(cmd);

        // Update the timer with random delay
        int extra = random.nextInt(delayRange.get() + 1); // 0 to delayRange
        timer = (delay.get() + extra) * 20;  // 20 ticks per second * (delay + extra) seconds
    }

    public void disabilita() {
        if (isActive()) {
            toggle();  // Disabilita il modulo se è attivo
        }
    }
}
