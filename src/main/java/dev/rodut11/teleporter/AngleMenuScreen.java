package dev.rodut11.teleporter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class AngleMenuScreen extends Screen {
    private final Screen parent;
    private final List<AnglePreset> presets;
    private int scroll = 0;
    private static final int ROW_HEIGHT = 28;
    private static final int VISIBLE_ROWS = 8;

    public AngleMenuScreen(Screen parent) {
        super(Text.literal("Angle Presets"));
        this.parent = parent;
        this.presets = Teleporter.config.anglePresets;
    }

    @Override
    protected void init() {
        this.clearChildren(); // Remove all previous widgets

        int startY = 40;
        int startIdx = scroll;
        int endIdx = Math.min(scroll + VISIBLE_ROWS, presets.size());

        for (int i = startIdx; i < endIdx; i++) {
            AnglePreset preset = presets.get(i);
            int idx = i;
            int y = startY + (i - startIdx) * ROW_HEIGHT;

            // Preset name button: aligns on click
            addDrawableChild(ButtonWidget.builder(Text.literal(preset.name), btn -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    // Trigger the preset countdown/look-up
                    Teleporter.triggerPreset(preset);
                }
            }).position(20, y).size(100, 20).build());

            // Edit button
            addDrawableChild(ButtonWidget.builder(Text.literal("Edit"), btn -> {
                MinecraftClient.getInstance().setScreen(new AngleEditScreen(this, preset, idx));
            }).position(130, y).size(50, 20).build());

            // Remove button
            addDrawableChild(ButtonWidget.builder(Text.literal("Remove"), btn -> {
                presets.remove(idx);
                Teleporter.config.save();
                MinecraftClient.getInstance().setScreen(new AngleMenuScreen(parent));
            }).position(190, y).size(60, 20).build());
        }

        int y = startY + (endIdx - startIdx) * ROW_HEIGHT;
        if (presets.size() < 15) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Add Preset"), btn -> {
                presets.add(new AnglePreset("Preset " + (presets.size() + 1), 0f, 0f));
                Teleporter.config.save();
                MinecraftClient.getInstance().setScreen(new AngleMenuScreen(parent));
            }).position(20, y).size(120, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> {
            Teleporter.config.save();
            MinecraftClient.getInstance().setScreen(parent);
        }).position(150, y).size(80, 20).build());
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, presets.size() - VISIBLE_ROWS);
        if (verticalAmount < 0 && scroll < maxScroll) {
            scroll++;
            this.init();
            return true;
        } else if (verticalAmount > 0 && scroll > 0) {
            scroll--;
            this.init();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}