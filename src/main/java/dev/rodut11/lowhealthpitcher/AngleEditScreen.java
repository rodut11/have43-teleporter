package dev.rodut11.lowhealthpitcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AngleEditScreen extends Screen {
    private final Screen parent;
    private final AnglePreset preset;
    private final int presetIndex;
    private TextFieldWidget nameField, yawField, pitchField;

    public AngleEditScreen(Screen parent, AnglePreset preset, int presetIndex) {
        super(Text.literal("Edit Angle Preset"));
        this.parent = parent;
        this.preset = preset;
        this.presetIndex = presetIndex;
    }

    @Override
    protected void init() {
        nameField = new TextFieldWidget(textRenderer, 20, 40, 120, 20, Text.literal("Name"));
        nameField.setText(preset.name);
        addDrawableChild(nameField);

        yawField = new TextFieldWidget(textRenderer, 20, 70, 120, 20, Text.literal("Yaw"));
        yawField.setText(String.valueOf(preset.yaw));
        addDrawableChild(yawField);

        pitchField = new TextFieldWidget(textRenderer, 20, 100, 120, 20, Text.literal("Pitch"));
        pitchField.setText(String.valueOf(preset.pitch));
        addDrawableChild(pitchField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), btn -> {
            preset.name = nameField.getText();
            try { preset.yaw = Float.parseFloat(yawField.getText()); } catch (NumberFormatException ignored) {}
            try { preset.pitch = Float.parseFloat(pitchField.getText()); } catch (NumberFormatException ignored) {}
            LowHealthPitcher.config.anglePresets.set(presetIndex, preset);
            LowHealthPitcher.config.save();
            MinecraftClient.getInstance().setScreen(parent);
        }).position(20, 130).size(80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> {
            MinecraftClient.getInstance().setScreen(parent);
        }).position(110, 130).size(80, 20).build());
    }
}