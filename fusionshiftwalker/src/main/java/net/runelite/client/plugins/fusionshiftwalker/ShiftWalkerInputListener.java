package net.runelite.client.plugins.fusionshiftwalker;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.client.input.KeyListener;

public class ShiftWalkerInputListener implements KeyListener {
	private final Set blockedChars;
	@Inject
	private ShiftWalkerPlugin plugin;
	@Inject
	private ShiftWalkerConfig config;

	public ShiftWalkerInputListener() {
		this.blockedChars = new HashSet();
	}

	public void keyTyped(KeyEvent event) {
		char keyChar = event.getKeyChar();
		if (keyChar != '\uffff' && this.blockedChars.contains(keyChar)) {
			event.consume();
		}

	}

	public void keyPressed(KeyEvent event) {
		if (this.config.pluginHotkey().matches(event)) {
			this.blockedChars.add(event.getKeyChar());
			this.plugin.setHotKeyPressed(true);
		}

	}

	public void keyReleased(KeyEvent event) {
		if (this.config.pluginHotkey().matches(event)) {
			this.blockedChars.remove(event.getKeyChar());
			this.plugin.setHotKeyPressed(false);
		}

	}
}
