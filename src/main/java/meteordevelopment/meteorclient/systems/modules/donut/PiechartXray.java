package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.block.entity.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PiechartXray extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<BlockEntityType<?>>> types = sgGeneral.add(new StorageBlockListSetting.Builder()
        .name("block-entities")
        .description("Which block entities to notify about.")
        .defaultValue(
            BlockEntityType.CHEST,
            BlockEntityType.ENDER_CHEST,
            BlockEntityType.TRAPPED_CHEST,
            BlockEntityType.BARREL,
            BlockEntityType.SHULKER_BOX,
            BlockEntityType.BEACON
        )
        .build()
    );

    private final Set<BlockPos> notified = new HashSet<>();

    public PiechartXray() {
        super(Categories.Donut, "piechart-xray", "Notifies in chat when selected block entities are found.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        for (BlockEntity be : Utils.blockEntities()) {
            if (be == null) continue;
            BlockPos pos = be.getPos();
            if (notified.contains(pos)) continue;
            for (BlockEntityType<?> type : types.get()) {
                if (be.getType() == type) {
                    String name = net.minecraft.registry.Registries.BLOCK_ENTITY_TYPE.getId(type).toString();
                    ChatUtils.info("PiechartXray: Found " + name + " at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                    notified.add(pos);
                    break;
                }
            }
        }
    }

    @Override
    public void onDeactivate() {
        notified.clear();
    }
}
