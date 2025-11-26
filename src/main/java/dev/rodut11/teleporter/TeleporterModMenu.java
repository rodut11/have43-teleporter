package dev.rodut11.lowhealthpitcher;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.text.Text;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public class LowHealthPitcherModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("LowHealthPitcher Config"));

            ConfigCategory general = builder.getOrCreateCategory(builder.getTitle());
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), LowHealthPitcher.config.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> LowHealthPitcher.config.enabled = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Yaw"), LowHealthPitcher.config.setYaw)
                    .setDefaultValue(-65.19f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.setYaw = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Pitch"), LowHealthPitcher.config.setPitch)
                    .setDefaultValue(-54.23f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.setPitch = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Minimum HP"), LowHealthPitcher.config.minHp)
                    .setDefaultValue(8.0f)
                    .setMin(1.0f)
                    .setMax(20.0f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.minHp = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Lock Duration (seconds)"), LowHealthPitcher.config.lockSeconds)
                    .setDefaultValue(3.0f)
                    .setMin(0.5f)
                    .setMax(10.0f)
                    .setSaveConsumer(val -> LowHealthPitcher.config.lockSeconds = val)
                    .build());

            builder.setSavingRunnable(() -> LowHealthPitcher.config.save());
            return builder.build();
        };
    }
}