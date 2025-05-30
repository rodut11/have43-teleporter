// LowHealthPitcherClient.java
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class LowHealthPitcherClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LowHealthPitcher.init();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (LowHealthPitcher.openAngleMenuKey.wasPressed()) {
				client.setScreen(new AngleMenuScreen(null));
			}
		});
	}
}