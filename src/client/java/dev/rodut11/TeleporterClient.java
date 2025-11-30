package dev.rodut11.teleporter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TeleporterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Teleporter.init();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (Teleporter.openAngleMenuKey.wasPressed()) {
				client.setScreen(new AngleMenuScreen(null));
			}
		});
	}
}