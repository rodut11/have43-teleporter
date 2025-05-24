package dev.rodut11.lowhealthpitcher;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import java.util.function.Supplier;

public class LowHealthPitcherModMenu implements ModMenuApi {
    @Override
    public Supplier<Screen> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle("LowHealthPitcher Config");

            ConfigCategory general = builder.getOrCreateCategory(builder.getTitle());
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle("Enabled", LowHealthPitcher.config.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> LowHealthPitcher.config.enabled = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField("Yaw", LowHealthPitcher.config.setYaw)
                    .setDefaultValue(-65.19f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.setYaw = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField("Pitch", LowHealthPitcher.config.setPitch)
                    .setDefaultValue(-54.23f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.setPitch = val)
                    .build());

            builder.setSavingRunnable(() -> {/* Optionally save config to file here */});
            return builder.build();
        };
    }
}