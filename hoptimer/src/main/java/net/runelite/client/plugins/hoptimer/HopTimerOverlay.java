package net.runelite.client.plugins.hoptimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Singleton
public class HopTimerOverlay extends OverlayPanel {
	private final Client client;
	private final HopTimerPlugin plugin;

	@Inject
	private HopTimerOverlay(Client client, HopTimerPlugin plugin) {
		this.client = client;
		this.plugin = plugin;
		this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.plugin.hopTicks > 0) {
			this.panelComponent.getChildren().clear();
			this.panelComponent.setPreferredSize(new Dimension(50, 0));
			this.panelComponent.getChildren().add(TitleComponent.builder().color(Color.WHITE).text(this.plugin.hopSecondsDisplay).build());
		} else if (this.plugin.hopTicks == 0 && this.plugin.canHop && this.plugin.canHopTicks > 0) {
			this.panelComponent.getChildren().clear();
			this.panelComponent.setPreferredSize(new Dimension(50, 0));
			this.panelComponent.getChildren().add(TitleComponent.builder().color(Color.GREEN).text("HOP").build());
		}

		return super.render(graphics);
	}
}
