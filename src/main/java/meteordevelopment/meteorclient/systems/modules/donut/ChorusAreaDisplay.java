package meteordevelopment.meteorclient.systems.modules.wgf;

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
        .name("colore-riempimento")
        .description("Il colore del riempimento dell'area renderizzata.")
        .defaultValue(new SettingColor(255, 0, 255, 75))
        .build()
    );

    private final Setting<SettingColor> outlineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("colore-contorno")
        .description("Il colore del contorno dell'area renderizzata.")
        .defaultValue(new SettingColor(255, 0, 255, 255))
        .build()
    );

    private final Setting<Boolean> showOutline = sgGeneral.add(new BoolSetting.Builder()
        .name("mostra-contorno")
        .description("Mostra il contorno dell'area renderizzata.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> invisible = sgGeneral.add(new BoolSetting.Builder()
        .name("invisibile")
        .description("Rendi invisibile il riquadro dell'area.")
        .defaultValue(false)
        .build()
    );

    public ChorusAreaDisplay() {
        super(Categories.WGFUtility, "chorus-area-display", "Mostra un'area di 16x16x16 quando si tiene in mano un Chorus Fruit.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        // Controlla se il giocatore ha un Chorus Fruit in mano
        if (mc.player.getMainHandStack().getItem() != Items.CHORUS_FRUIT) return;

        // Ottieni la posizione del giocatore
        Vec3d playerPos = mc.player.getPos();

        // Calcola gli angoli dell'area 16x16x16
        double x1 = playerPos.x - 8;
        double y1 = playerPos.y - 8;
        double z1 = playerPos.z - 8;
        double x2 = playerPos.x + 8;
        double y2 = playerPos.y + 8;
        double z2 = playerPos.z + 8;

        // Ottieni i colori di riempimento e contorno
        SettingColor fill = invisible.get() ? new SettingColor(0, 0, 0, 0) : fillColor.get();
        SettingColor outline = showOutline.get() ? outlineColor.get() : new SettingColor(0, 0, 0, 0);

        // Renderizza l'area 16x16x16
        event.renderer.box(
            x1, y1, z1,  // Angolo inferiore sinistro
            x2, y2, z2,  // Angolo superiore destro
            fill,   // Colore del riempimento
            outline, // Colore del contorno
            ShapeMode.Both, // Mostra sia il riempimento che il contorno
            0 // Nessun blocco da escludere
        );
    }
}
