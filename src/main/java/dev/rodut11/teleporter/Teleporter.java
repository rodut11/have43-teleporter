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

    private static boolean isActionActive = false;
    private static boolean isDelayed = false;
    private static long actionStartTime = 0;
    private static float initialYaw = 0f;
    private static float initialPitch = 0f;
    private static final long LOOKUP_DELAY_MS = 2000;
    private static int lastCountdownSecond = -1;

    // Key Bindings
    private KeyBinding triggerKeyBinding;
    public static KeyBinding openAngleMenuKey;

    public static void triggerTeleportAction(float targetYaw, float targetPitch, String presetName) {
        // Only start if no action is currently running
        if (!isActionActive) {
            initialYaw = targetYaw;
            initialPitch = targetPitch;
            actionStartTime = System.currentTimeMillis();
            isActionActive = true;
            isDelayed = true;
            lastCountdownSecond = -1;

            // Announce the action
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
                client.execute(() -> {
                    client.inGameHud.getChatHud().addMessage(
                            Text.literal(String.format("§bTeleporting to %s!", presetName)).formatted(Formatting.AQUA)
                    );
                });
            }
        }
    }

    public static void triggerPreset(AnglePreset preset) {
        triggerTeleportAction(preset.yaw, preset.pitch, preset.name);
    }

    @Override
    public void onInitializeClient() {
        Identifier teleporterId = Identifier.of("teleporter", "teleporter");
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
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            // Open angle menu
            while (openAngleMenuKey.wasPressed()) {
                client.setScreen(new AngleMenuScreen(null));
            }

            // Check for key press OR low health
            boolean lowHealth = player.getHealth() <= config.minHp;
            boolean keyPressed = triggerKeyBinding.wasPressed();
            boolean shouldTrigger = (lowHealth && config.enabled) || keyPressed;

            if (shouldTrigger && !isActionActive) {
                // Key press or low health triggers the action with the config angle
                triggerTeleportAction(config.setYaw, config.setPitch, "Configured Angles");
            }

            if (isActionActive) {
                long elapsed = System.currentTimeMillis() - actionStartTime;

                if (isDelayed) {
                    // Look at the initial angle
                    player.setYaw(initialYaw);
                    player.setPitch(initialPitch);

                    long remaining = LOOKUP_DELAY_MS - elapsed;
                    if (remaining > 0) {
                        // Countdown logic
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
                        // Start look up
                        isDelayed = false;
                        actionStartTime = System.currentTimeMillis(); // Restart timer for the lock duration
                        lastCountdownSecond = -1; // Reset countdown
                        
                        // Send message
                        client.execute(() -> {
                            if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
                                client.inGameHud.getChatHud().addMessage(
                                        Text.literal("Looking Up!").formatted(Formatting.RED)
                                );
                            }
                        });
                    }
                } else {
                    // Look up for lock seconds
                    player.setYaw(initialYaw);
                    player.setPitch(-90f); 

                    if (elapsed >= (long)(config.lockSeconds * 1000)) {
                        // Lock duration is over
                        isActionActive = false;
                        lastCountdownSecond = -1;
                        client.execute(() -> {
                            if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
                                client.inGameHud.getChatHud().addMessage(
                                        Text.literal("Teleportation Complete!").formatted(Formatting.GREEN)
                                );
                            }
                        });
                    }
                }
            }
        });
    }
}