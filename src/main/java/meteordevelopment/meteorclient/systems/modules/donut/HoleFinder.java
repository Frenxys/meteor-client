package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

public class HoleFinder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> minDepth = sgGeneral.add(new IntSetting.Builder()
        .name("min-depth")
        .description("Minimum vertical length of the tunnel.")
        .defaultValue(3)
        .min(2)
        .sliderMax(10)
        .build()
    );

    private final Setting<Integer> scanRadius = sgGeneral.add(new IntSetting.Builder()
        .name("scan-radius")
        .description("Horizontal scan radius in blocks.")
        .defaultValue(16)
        .min(4)
        .sliderMax(32)
        .build()
    );

    private final Setting<SettingColor> color = sgGeneral.add(new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
        .name("color")
        .description("ESP box color.")
        .defaultValue(new SettingColor(0, 200, 255, 80))
        .build()
    );

    private final Set<BlockPos> foundHoles = new HashSet<>();

    public HoleFinder() {
        super(Categories.Donut, "hole-finder", "Evidenzia tunnel 1x1 verticali (holes) sottoterra.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        foundHoles.clear();
        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        int radius = scanRadius.get();
        int minY = Math.max(1, playerPos.getY() - 32);
        int maxY = Math.min(255, playerPos.getY() + 32);
        int minLen = minDepth.get();

        for (int x = playerPos.getX() - radius; x <= playerPos.getX() + radius; x++) {
            for (int z = playerPos.getZ() - radius; z <= playerPos.getZ() + radius; z++) {
                for (int y = minY; y <= maxY - minLen; y++) {
                    BlockPos base = new BlockPos(x, y, z);
                    if (isVerticalTunnel(base, minLen)) {
                        foundHoles.add(base);
                        y += minLen - 1; // skip ahead
                    }
                }
            }
        }

        for (BlockPos pos : foundHoles) {
            Box box = new Box(pos);
            event.renderer.box(box, color.get(), color.get(), meteordevelopment.meteorclient.renderer.ShapeMode.Both, 2);
        }
    }

    private boolean isVerticalTunnel(BlockPos start, int minLen) {
        for (int i = 0; i < minLen; i++) {
            BlockPos pos = start.up(i);
            if (!isAir(pos)) return false;
            for (Direction dir : Direction.Type.HORIZONTAL) {
                if (!isSolid(pos.offset(dir))) return false;
            }
        }
        // Check that the block below is solid (entry is on ground)
        return isSolid(start.down());
    }

    private boolean isAir(BlockPos pos) {
        Block block = MinecraftClient.getInstance().world.getBlockState(pos).getBlock();
        return block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR;
    }

    private boolean isSolid(BlockPos pos) {
        Block block = MinecraftClient.getInstance().world.getBlockState(pos).getBlock();
        return block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR;
    }
}
