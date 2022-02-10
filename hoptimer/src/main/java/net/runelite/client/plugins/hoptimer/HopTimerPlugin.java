package net.runelite.client.plugins.hoptimer;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "[F] Hop Timer",
	description = "Shows how long before you are out of combat and can hop worlds",
	enabledByDefault = false
)
public class HopTimerPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private PluginManager pluginManager;
	@Inject
	private HopTimerOverlay overlay;
	public int hopTicks;
	public boolean canHop;
	public int canHopTicks;
	public String hopSecondsDisplay;
	public boolean otherDmg;

	public HopTimerPlugin() {
		this.hopTicks = 0;
		this.canHop = true;
		this.canHopTicks = 0;
		this.hopSecondsDisplay = "";
		this.otherDmg = false;
	}

	private void reset() {
		this.hopTicks = 0;
		this.canHop = true;
		this.canHopTicks = 0;
		this.hopSecondsDisplay = "";
	}

	protected void startUp() throws Exception {
		this.reset();
		this.overlayManager.add(this.overlay);
	}

	protected void shutDown() throws Exception {
		this.reset();
		this.overlayManager.remove(this.overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() != GameState.LOADING && event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.CONNECTION_LOST) {
			this.reset();
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getMessage().equals("Ow! You nearly broke a tooth!") || event.getMessage().equals("The rock cake resists all attempts to eat it.") || event.getMessage().equals("You bite hard into the rock cake to guzzle it down.") || event.getMessage().equals("OW! A terrible shock jars through your skull.") || event.getMessage().contains("You drink some of your divine") || event.getMessage().equals("You drink some of the foul liquid.") || event.getMessage().equals("The locator orb is unstable and hurts you as you use it.")) {
			this.canHop = true;
			this.otherDmg = true;
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor().getName() != null && event.getActor().getName().equals(this.client.getLocalPlayer().getName()) && (event.getActor().getAnimation() == 4409 || event.getActor().getAnimation() == 4411)) {
			this.canHop = true;
			this.otherDmg = true;
		}

	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event) {
		if (event.getActor().getName() != null && event.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
			if (this.otherDmg) {
				this.otherDmg = false;
			} else {
				this.canHop = false;
				this.hopTicks = 16;
				this.canHopTicks = 8;
			}
		}

	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (this.canHop) {
			--this.canHopTicks;
			if (this.canHopTicks == 0) {
				this.canHop = false;
			}
		} else {
			--this.hopTicks;
			this.hopSecondsDisplay = to_mmss(this.hopTicks);
			if (this.hopTicks == 0) {
				this.canHop = true;
				this.canHopTicks = 8;
			}
		}

	}

	public static String to_mmss(int ticks) {
		int m = ticks / 100;
		int s = (ticks - m * 100) * 6 / 10;
		return String.valueOf(s);
	}
}
