package meteordevelopment.meteorclient.systems.modules.donut;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPriceTooltip {
    private static final Map<String, Double> ITEM_PRICES = new HashMap<>();
    private static boolean loaded = false;

    public static void init() {
        if (loaded) return;
        loaded = true;
        try {
            InputStreamReader reader = new InputStreamReader(ItemPriceTooltip.class.getResourceAsStream("/meteordevelopment/meteorclient/systems/modules/donut/items.json"));
            Type listType = new TypeToken<List<ItemPriceEntry>>(){}.getType();
            List<ItemPriceEntry> entries = new Gson().fromJson(reader, listType);
            for (ItemPriceEntry entry : entries) {
                ITEM_PRICES.put(entry.itemName, entry.value);
            }
        } catch (Exception e) {
            MeteorClient.LOG.error("Failed to load item prices", e);
        }
    }

    @EventHandler
    public void onTooltip(ItemStackTooltipEvent event) {
        if (!isInventoryContext()) return;
        ItemStack stack = event.itemStack();
        Item item = stack.getItem();
        Identifier id = Registries.ITEM.getId(item);
        String name = id.getPath();
        if (ITEM_PRICES.containsKey(name)) {
            double price = ITEM_PRICES.get(name);
            event.appendEnd(Text.literal("ah price: " + price).formatted(Formatting.GOLD));
        }
    }

    private boolean isInventoryContext() {
        // TODO: Implement context check for inventory only
        return true;
    }

    private static class ItemPriceEntry {
        public int id;
        public String itemName;
        public double value;
    }
}
