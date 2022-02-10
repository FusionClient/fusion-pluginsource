package net.runelite.client.plugins.tickdebug;

import javax.inject.Inject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "[F] World Ping",
	enabledByDefault = false,
	description = "Displays server cycle time in top-right corner"
)
public class TickDebugPlugin extends Plugin {
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TickDebugOverlay overlay;
	private long last_tick_ns;
	public int last_tick_dur_ms;

	protected void startUp() throws Exception {
		this.overlayManager.add(this.overlay);
	}

	protected void shutDown() throws Exception {
		this.overlayManager.remove(this.overlay);
	}

	@Subscribe
	public void onGameTick(GameTick e) {
		long time = System.nanoTime();
		this.last_tick_dur_ms = (int)((time - this.last_tick_ns) / 1000000L);
		this.last_tick_ns = time;
	}
}
