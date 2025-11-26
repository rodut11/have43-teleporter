package dev.rodut11.teleporter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Teleporter implements ClientModInitializer {

    public static TeleporterConfig config = TeleporterConfig.load();

    private boolean triggered = false;
    private long triggerStartTime = 0;
    private float targetYaw = 0f;
    private float targetPitch = 0f;

    private KeyBinding triggerKeyBinding;
    public static KeyBinding openAngleMenuKey;

    // Preset-trigger state
    private static boolean presetTriggered = false;
    private static boolean presetLookedUp = false;
    private static long presetTriggerStartTime = 0;
    private static float presetYaw = 0f;
    private static float presetPitch = 0f;
    private static final long PRESET_LOOKUP_DELAY_MS = 2000; // 5 seconds
    private static int lastCountdownSecond = -1;

    public static void triggerPreset(AnglePreset preset) {
        presetYaw = preset.yaw;
        presetPitch = preset.pitch;
        presetTriggerStartTime = System.currentTimeMillis();
        presetTriggered = true;
        presetLookedUp = false;
        lastCountdownSecond = -1;
    }

    @Override
    public void onInitializeClient() {
        Identifier teleporterId = Identifier.of("teleporter", "category.teleporter");
        KeyBinding.Category teleporterCategory = new KeyBinding.Category(teleporterId);

        triggerKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.teleporter.trigger",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                teleporterCategory
        ));

        openAngleMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.teleporter.open_angle_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                teleporterCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Open angle menu
            while (openAngleMenuKey.wasPressed()) {
                client.setScreen(new AngleMenuScreen(null));
            }

            if (!config.enabled) return;

            ClientPlayerEntity player = client.player;
            boolean lowHealth = player.getHealth() <= config.minHp;
            boolean keyPressed = triggerKeyBinding.wasPressed();
            boolean shouldTrigger = lowHealth || keyPressed;

            // Low HP / manual trigger logic (just lock configured angle)
            if (shouldTrigger && !triggered) {
                triggerStartTime = System.currentTimeMillis();
                triggered = true;
                targetYaw = config.setYaw;
                targetPitch = config.setPitch;
            }

            if (triggered) {
                long elapsed = System.currentTimeMillis() - triggerStartTime;
                if (elapsed <= (long)(config.lockSeconds * 1000)) {
                    player.setYaw(targetYaw);
                    player.setPitch(targetPitch);
                } else {
                    triggered = false;
                }
            }

            // Preset-trigger logic
            if (presetTriggered) {
                long elapsed = System.currentTimeMillis() - presetTriggerStartTime;

                // Hold the preset angle until look-up
                if (!presetLookedUp) {
                    player.setYaw(presetYaw);
                    player.setPitch(presetPitch);

                    long remaining = PRESET_LOOKUP_DELAY_MS - elapsed;
                    if (remaining > 0) {
                        int secondsLeft = (int) Math.ceil((double) remaining / 1000.0);
                        if (secondsLeft != lastCountdownSecond) {
                            lastCountdownSecond = secondsLeft;
                            client.execute(() -> {
                                if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
                                    client.inGameHud.getChatHud().addMessage(
                                            Text.literal("Looking up in " + secondsLeft + "...").formatted(Formatting.YELLOW)
                                    );
                                }
                            });
                        }
                    } else {
                        // Do the look-up
                        presetLookedUp = true;
                        triggerStartTime = System.currentTimeMillis();
                        triggered = true;
                        targetYaw = presetYaw;
                        targetPitch = -90f; // look up
                    }
                }

                // Reset preset after lookup + lockSeconds
                if (presetLookedUp) {
                    long lookupElapsed = System.currentTimeMillis() - triggerStartTime;
                    if (lookupElapsed >= (long)(config.lockSeconds * 1000)) {
                        presetTriggered = false;
                        presetLookedUp = false;
                        lastCountdownSecond = -1;
                    }
                }
            }
        });
    }
}
