package dev.rodut11.lowhealthpitcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class LowHealthPitcher implements ClientModInitializer {

    public static float setYaw = -65.19f;
    public static float setPitch = -54.23f;

    private boolean triggered = false;
    private boolean pitchSet = false;
    private long lowHealthStartTime = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            if (player.getHealth() <= 8.0f) {
                long now = System.currentTimeMillis();
                if (!triggered) {
                    lowHealthStartTime = now;
                    triggered = true;
                    pitchSet = false;
                }
                long elapsed = now - lowHealthStartTime;
                if (elapsed <= 500) {
                    //hold yaw and pitch for .5 seconds
                    player.setYaw(setYaw);
                    player.setPitch(setPitch);
                } else if (!pitchSet && elapsed >= 5000) {
                    //after 5 seconds set pitch to -90
                    player.setPitch(-90f);
                    pitchSet = true;
                }
            } else {
                triggered = false;
                pitchSet = false;
            }
        });
    }
}