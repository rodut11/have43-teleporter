package dev.rodut11.lowhealthpitcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class LowHealthPitcher implements ClientModInitializer {

    public static float setYaw = -65.19f;
    public static float setPitch = -54.23f;

    private boolean triggered = false;
    private boolean pitchSet = false;
    private long triggerStartTime = 0;

    private KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lowhealthpitcher.trigger",
                GLFW.GLFW_KEY_F9,
                "category.lowhealthpitcher"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            boolean lowHealth = player.getHealth() <= 8.0f;
            boolean keyPressed = keyBinding.wasPressed();
            boolean shouldTrigger = lowHealth || keyPressed;

            if (shouldTrigger && !triggered) {
                triggerStartTime = System.currentTimeMillis();
                triggered = true;
                pitchSet = false;
            }

            if (triggered) {
                long elapsed = System.currentTimeMillis() - triggerStartTime;
                if (elapsed <= 500) {
                    player.setYaw(setYaw);
                    player.setPitch(setPitch);
                } else if (!pitchSet && elapsed >= 2500) {
                    player.setPitch(-90f);
                    pitchSet = true;
                }
                // Reset after sequence completes (e.g., after 3s), unless low health is still true
                if (elapsed >= 3000 && !lowHealth) {
                    triggered = false;
                    pitchSet = false;
                }
            } else {
                pitchSet = false;
            }
        });
    }
}