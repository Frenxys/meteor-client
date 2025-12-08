package meteordevelopment.meteorclient.systems.modules.wgf;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoRTP extends Module {
    private final SettingGroup sgGenerale = settings.getDefaultGroup();
    private int timer;

    public AutoRTP() {
        super(Categories.WGFGrief, "Auto-RTP", "Trova automaticamente le basi sul server WGF.");
    }

    private final Setting<Integer> ritardo = sgGenerale.add(new IntSetting.Builder()
        .name("ritardo")
        .description("Ritardo per ogni RTP in secondi")
        .defaultValue(2)
        .min(0)
        .max(10)
        .build()
    );

    @Override
    public void onActivate() {
        // Inizializza il timer
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        // Resetta il timer alla disattivazione del modulo
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity giocatore = mc.player;

        if (giocatore == null) return;  // Controlla se il giocatore è nullo

        if (timer > 0) {
            timer--;  // Decrementa il timer se è maggiore di 0
            return;
        }

        // Esegui il comando /rtp
        mc.player.networkHandler.sendChatCommand("rtp");

        // Aggiorna il timer
        timer = (ritardo.get() + 3) * 20;  // 20 tick al secondo * (ritardo + 3) secondi
    }

    public void disabilita() {
        if (isActive()) {
            toggle();  // Disabilita il modulo se è attivo
        }
    }
}
