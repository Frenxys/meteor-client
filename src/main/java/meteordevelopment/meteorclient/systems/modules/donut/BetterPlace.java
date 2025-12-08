package meteordevelopment.meteorclient.systems.modules.wgf;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class BetterPlace extends Module {
    private final SettingGroup sgGenerale = settings.getDefaultGroup();
    private final SettingGroup sgRaggio = settings.createGroup("Raggio");

    // Generale

    private final Setting<Boolean> render = sgGenerale.add(new BoolSetting.Builder()
        .name("render")
        .description("Rende un'overlay del blocco dove il blocco sarà posizionato.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgGenerale.add(new EnumSetting.Builder<ShapeMode>()
        .name("modalità-forma")
        .description("Come vengono renderizzate le forme.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgGenerale.add(new ColorSetting.Builder()
        .name("colore-lato")
        .description("Il colore dei lati dei blocchi renderizzati.")
        .defaultValue(new SettingColor(146, 188, 98, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGenerale.add(new ColorSetting.Builder()
        .name("colore-linea")
        .description("Il colore delle linee dei blocchi renderizzati.")
        .defaultValue(new SettingColor(146, 188, 98, 255))
        .build()
    );

    // Raggio

    private final Setting<Boolean> customRange = sgRaggio.add(new BoolSetting.Builder()
        .name("raggio-personalizzato")
        .description("Usa un raggio personalizzato per il miglior posizionamento.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> range = sgRaggio.add(new DoubleSetting.Builder()
        .name("raggio")
        .description("Raggio personalizzato per posizionare.")
        .visible(customRange::get)
        .defaultValue(5)
        .min(0)
        .sliderMax(6)
        .build()
    );

    private HitResult hitResult;

    public BetterPlace() {
        super(Categories.WGFUtility, "BetterPlace", "Ti aiuta a posizionare blocchi dove normalmente non puoi");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        setHitResult();
        if (hitResult instanceof BlockHitResult && mc.player.getMainHandStack().getItem() instanceof BlockItem && mc.options.useKey.isPressed()) {
            BlockUtils.place(((BlockHitResult) hitResult).getBlockPos(), Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, true, false);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!(hitResult instanceof BlockHitResult)
            || !mc.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).isReplaceable()
            || !(mc.player.getMainHandStack().getItem() instanceof BlockItem)
            || !render.get()) return;

        event.renderer.box(((BlockHitResult) hitResult).getBlockPos(), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    private void setHitResult() {
        final double r = customRange.get() ? range.get() : 4.5;
        for (int i = (int) r; i > 0; i -= 1D) {
            hitResult = mc.getCameraEntity().raycast(Math.min(r, i), 0, false);
            if (hitResult instanceof BlockHitResult && isValid(((BlockHitResult) hitResult).getBlockPos())) return;
        }
        hitResult = null;
    }

    private boolean isValid(BlockPos pos) {
        return !pos.equals(mc.player.getBlockPos()) && BlockUtils.getPlaceSide(pos) != null;
    }
}
