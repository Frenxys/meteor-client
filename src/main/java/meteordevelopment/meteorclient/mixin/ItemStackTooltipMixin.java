package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void meteor$injectTooltip(CallbackInfoReturnable<List<Text>> cir) {
        BetterTooltips module = Modules.get().get(BetterTooltips.class);
        if (module == null || !module.isActive()) return;
        List<Text> tooltip = cir.getReturnValue();
        ItemStack stack = (ItemStack)(Object)this;
        String itemKey = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString();
        if (BetterTooltips.ITEM_PRICES != null && BetterTooltips.ITEM_PRICES.containsKey(itemKey)) {
            double price = BetterTooltips.ITEM_PRICES.get(itemKey);
            tooltip.add(Text.literal("Prezzo: " + price + " coins").formatted(net.minecraft.util.Formatting.GOLD));
        }
        // Puoi aggiungere qui altre info, come la descrizione custom
        // Esempio: tooltip.add(Text.literal("Descrizione: ...").formatted(net.minecraft.util.Formatting.GRAY));
        cir.setReturnValue(tooltip);
    }
}
