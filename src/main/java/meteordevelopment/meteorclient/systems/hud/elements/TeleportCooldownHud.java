package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class TeleportCooldownHud extends HudElement {
    public static final HudElementInfo<TeleportCooldownHud> INFO = new HudElementInfo<>(Hud.GROUP, "teleport-cooldown", "Shows RTP cooldown timer with ender pearl icon.", TeleportCooldownHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBackground = settings.createGroup("Background");

    private final Setting<Boolean> background = sgBackground.add(new BoolSetting.Builder()
        .name("background")
        .description("Displays background.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> backgroundColor = sgBackground.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color used for the background.")
        .visible(background::get)
        .defaultValue(new SettingColor(25, 25, 25, 50))
        .build()
    );

    private long cooldownEnd = 0;
    private static final long COOLDOWN_MS = 13000; // 13 seconds cooldown

    public TeleportCooldownHud() {
        super(INFO);
        setSize(32, 32);
    }

    public void triggerCooldown() {
        cooldownEnd = System.currentTimeMillis() + COOLDOWN_MS;
    }

    public boolean isOnCooldown() {
        return System.currentTimeMillis() < cooldownEnd;
    }

    public int getSecondsLeft() {
        long left = cooldownEnd - System.currentTimeMillis();
        return (int) Math.max(0, left / 1000);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (background.get()) renderer.quad(x, y, getWidth(), getHeight(), backgroundColor.get());
        String text;
        if (isOnCooldown()) {
            text = String.valueOf(getSecondsLeft());
        } else if (isInEditor()) {
            text = "Teleport Cooldown";
        } else {
            return;
        }
        double textHeight = renderer.textHeight(true);
        double textX = x;
        double textY = y + (getHeight() - textHeight) / 2.0;
        renderer.text(text, textX, textY, Color.WHITE, true);
    }
}
