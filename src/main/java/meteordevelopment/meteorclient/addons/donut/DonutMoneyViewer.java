package meteordevelopment.meteorclient.addons.donut;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.json.JSONObject;

public class DonutMoneyViewer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> bearerToken = sgGeneral.add(new StringSetting.Builder()
        .name("bearer-token")
        .description("Bearer token for DonutSMP API.(/api in chat to get it)")
        .defaultValue("")
        .build()
    );

    private final Map<String, String> moneyCache = new ConcurrentHashMap<>();

    public DonutMoneyViewer() {
        super(Categories.Donut, "DonutMoneyViewer", "Shows DonutSMP money next to player names.");
    }

    public void onRender() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            String name = player.getName().getString();
            if (!moneyCache.containsKey(name)) {
                fetchMoneyAsync(name);
            }
            String money = moneyCache.get(name);
            if (money != null) {
                String formatted = formatMoney(money);
                player.setCustomName(Text.literal(name + " " + Formatting.GOLD + formatted));
            }
        }
    }

    private void fetchMoneyAsync(String playerName) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL("https://api.donutsmp.net/v1/stats/" + playerName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + bearerToken.get());
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                int status = conn.getResponseCode();
                if (status == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();
                    JSONObject obj = new JSONObject(response.toString());
                    String money = obj.getJSONObject("result").getString("money");
                    moneyCache.put(playerName, money);
                }
            } catch (Exception ignored) {}
        });
    }

    private String formatMoney(String moneyStr) {
        double money;
        try {
            money = Double.parseDouble(moneyStr);
        } catch (Exception e) {
            return moneyStr;
        }
        if (money >= 1e12) return String.format("%.2fT", money / 1e12);
        if (money >= 1e9) return String.format("%.2fB", money / 1e9);
        if (money >= 1e6) return String.format("%.2fM", money / 1e6);
        if (money >= 1e3) return String.format("%.2fk", money / 1e3);
        return String.format("%.0f", money);
    }
}
