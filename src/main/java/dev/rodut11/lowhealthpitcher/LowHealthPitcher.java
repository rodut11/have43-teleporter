package dev.rodut11.lowhealthpitcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class LowHealthPitcher implements ClientModInitializer {

    public static LowHealthPitcherConfig config = LowHealthPitcherConfig.load();

    private boolean triggered = false;
    private boolean pitchSet = false;
    private long triggerStartTime = 0;

    private KeyBinding triggerKeyBinding;
    public static KeyBinding openAngleMenuKey;

    @Override
    public void onInitializeClient() {
        // Register trigger key (F9)
        triggerKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lowhealthpitcher.trigger",
                GLFW.GLFW_KEY_F9,
                "category.lowhealthpitcher"
        ));

        // Register angle menu key (O)
        openAngleMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lowhealthpitcher.open_angle_menu",
                GLFW.GLFW_KEY_O,
                "category.lowhealthpitcher"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Open angle menu on key press
            while (openAngleMenuKey.wasPressed()) {
                client.setScreen(new AngleMenuScreen(null));
            }

            if (!config.enabled) return;

            ClientPlayerEntity player = client.player;
            boolean lowHealth = player.getHealth() <= config.minHp;
            boolean keyPressed = triggerKeyBinding.wasPressed();
            boolean shouldTrigger = lowHealth || keyPressed;

            if (shouldTrigger && !triggered) {
                triggerStartTime = System.currentTimeMillis();
                triggered = true;
                pitchSet = false;
            }

            if (triggered) {
                long elapsed = System.currentTimeMillis() - triggerStartTime;
                if (elapsed <= 500) {
                    player.setYaw(config.setYaw);
                    player.setPitch(config.setPitch);
                } else if (!pitchSet && elapsed >= 5000) {
                    player.setPitch(-90f);
                    pitchSet = true;
                }
                if (elapsed >= (long)(config.lockSeconds * 1000) && !lowHealth) {
                    triggered = false;
                    pitchSet = false;
                }
            } else {
                pitchSet = false;
            }
        });
    }
}