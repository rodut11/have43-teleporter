package dev.rodut11.teleporter;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.text.Text;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public class TeleporterModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("LowHealthPitcher Config"));

            ConfigCategory general = builder.getOrCreateCategory(builder.getTitle());
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Low Health Teleporting Enabled"), Teleporter.config.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> Teleporter.config.enabled = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Yaw"), Teleporter.config.setYaw)
                    .setDefaultValue(-65.19f)
                    .setSaveConsumer(val -> Teleporter.config.setYaw = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Pitch"), Teleporter.config.setPitch)
                    .setDefaultValue(-54.23f)
                    .setSaveConsumer(val -> Teleporter.config.setPitch = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Minimum HP"), Teleporter.config.minHp)
                    .setDefaultValue(8.0f)
                    .setMin(1.0f)
                    .setMax(20.0f)
                    .setSaveConsumer(val -> Teleporter.config.minHp = val)
                    .build());

            general.addEntry(entryBuilder.startFloatField(Text.literal("Lock Duration (seconds)"), Teleporter.config.lockSeconds)
                    .setDefaultValue(3.0f)
                    .setMin(0.5f)
                    .setMax(10.0f)
                    .setSaveConsumer(val -> Teleporter.config.lockSeconds = val)
                    .build());

            builder.setSavingRunnable(() -> Teleporter.config.save());
            return builder.build();
        };
    }
}