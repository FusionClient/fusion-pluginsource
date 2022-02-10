package net.runelite.client.plugins.tickdebug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TickDebugOverlay extends Overlay {
	private static final int Y_OFFSET = 22;
	private static final int X_OFFSET = 1;
	private final Client client;
	private final TickDebugPlugin tickDebugPlugin;

	@Inject
	private TickDebugOverlay(Client client, TickDebugPlugin worldHopperPlugin) {
		this.client = client;
		this.tickDebugPlugin = worldHopperPlugin;
		this.setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.setPriority(OverlayPriority.HIGH);
		this.setPosition(OverlayPosition.DYNAMIC);
	}


	public Dimension render(Graphics2D graphics) {
		int delay = this.tickDebugPlugin.last_tick_dur_ms;
		String text = delay + " ms";
		int textWidth = graphics.getFontMetrics().stringWidth(text);
		int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
		Widget logoutButton = this.client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_LOGOUT_BUTTON);
		int xOffset = 1;
		if (logoutButton != null && !logoutButton.isHidden()) {
			xOffset += logoutButton.getWidth();
		}

		int width = (int)this.client.getRealDimensions().getWidth();
		Point point = new Point(width - textWidth - xOffset, textHeight + 22);
		Color c;
		if (delay < 800 && delay > 400) {
			if (delay < 700 && delay > 500) {
				c = Color.YELLOW;
			} else {
				c = Color.ORANGE;
			}
		} else {
			c = Color.RED;
		}

		OverlayUtil.renderTextLocation(graphics, point, text, c);
		return null;
	}
}
