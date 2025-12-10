package meteordevelopment.meteorclient.systems.modules.donut;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting;import meteordevelopment.meteorclient.systems.modules.Categories;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

public class WeatherFilter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum WeatherType {
        CLEAR, RAIN, THUNDER
    }

    public enum TimeType {
        NONE, DAY, NIGHT
    }


    private final Setting<WeatherType> weather = sgGeneral.add(new EnumSetting.Builder<WeatherType>()
        .name("weather-type")
        .description("Select the weather type.")
        .defaultValue(WeatherType.CLEAR)
        .build()
    );

    private final Setting<TimeType> time = sgGeneral.add(new EnumSetting.Builder<TimeType>()
        .name("time-of-day")
        .description("Set time to day or night.")
        .defaultValue(TimeType.NONE)
        .onChanged(t -> lastTime = TimeType.NONE)
        .build()
    );

    private TimeType lastTime = TimeType.NONE;

    public WeatherFilter() {
        super(Categories.Donut, "weather-filter", "Disables weather and lets you set time and weather effects.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        ClientWorld world = mc.world;

        // Weather is still set every tick for smoothness
        switch (weather.get()) {
            case CLEAR -> {
                world.setRainGradient(0);
                world.setThunderGradient(0);
            }
            case RAIN -> {
                world.setRainGradient(1);
                world.setThunderGradient(0);
            }
            case THUNDER -> {
                world.setRainGradient(1);
                world.setThunderGradient(1);
            }
        }

        // Set time only if changed or module just enabled
        if (time.get() != TimeType.NONE && time.get() != lastTime) {
            long now = world.getTime();
            if (time.get() == TimeType.DAY) world.setTime(now, 1000, false);
            else if (time.get() == TimeType.NIGHT) world.setTime(now, 13000, false);
            lastTime = time.get();
        }
    }

    @Override
    public void onActivate() {
        lastTime = TimeType.NONE; // Force time set on enable
    }


}
