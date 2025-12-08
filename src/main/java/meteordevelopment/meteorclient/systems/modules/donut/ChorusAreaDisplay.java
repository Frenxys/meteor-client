package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class ChorusAreaDisplay extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> fillColor = sgGeneral.add(new ColorSetting.Builder()
        .name("fill-color")
        .description("The fill color of the rendered area.")
        .defaultValue(new SettingColor(255, 0, 255, 75))
        .build()
    );

    private final Setting<SettingColor> outlineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("outline-color")
        .description("The outline color of the rendered area.")
        .defaultValue(new SettingColor(255, 0, 255, 255))
        .build()
    );

    private final Setting<Boolean> showOutline = sgGeneral.add(new BoolSetting.Builder()
        .name("show-outline")
        .description("Show the outline of the rendered area.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> invisible = sgGeneral.add(new BoolSetting.Builder()
        .name("invisible")
        .description("Make the area box invisible.")
        .defaultValue(false)
        .build()
    );

    public ChorusAreaDisplay() {
        super(Categories.Donut, "chorus-area-display", "Shows a 16x16x16 area when holding a Chorus Fruit.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.player.getMainHandStack().getItem() != Items.CHORUS_FRUIT) return;

        // Get the player's position as Vec3d
        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        // Calculate the corners of the 16x16x16 area
        double x1 = playerPos.x - 8;
        double y1 = playerPos.y - 8;
        double z1 = playerPos.z - 8;
        double x2 = playerPos.x + 8;
        double y2 = playerPos.y + 8;
        double z2 = playerPos.z + 8;

        SettingColor fill = invisible.get() ? new SettingColor(0, 0, 0, 0) : fillColor.get();
        SettingColor outline = showOutline.get() ? outlineColor.get() : new SettingColor(0, 0, 0, 0);

        // Render the 16x16x16 area
        event.renderer.box(
            x1, y1, z1,  // Bottom left corner
            x2, y2, z2,  // Top right corner
            fill,   // Fill color
            outline, // Outline color
            ShapeMode.Both, // Show both fill and outline
            0 // No blocks to exclude
        );
    }
}
