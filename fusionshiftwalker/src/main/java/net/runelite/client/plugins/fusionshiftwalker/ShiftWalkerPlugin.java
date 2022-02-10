package net.runelite.client.plugins.fusionshiftwalker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
	name = "[F] Shift Click Walk Under",
	description = "Because niggers like two of everything.",
	tags = {"npcs", "items", "objects"},
	enabledByDefault = false,
	conflicts = "[F] Hotkey Walk Here"
)
public class ShiftWalkerPlugin extends Plugin {
	private static final String WALK_HERE = "WALK HERE";
	private static final String CANCEL = "CANCEL";
	@Inject
	private Client client;
	@Inject
	private ShiftWalkerConfig config;
	@Inject
	private ShiftWalkerInputListener inputListener;
	@Inject
	private ConfigManager configManager;
	@Inject
	private KeyManager keyManager;
	private boolean hotKeyPressed;

	public ShiftWalkerPlugin() {
		this.hotKeyPressed = false;
	}

	@Provides
	ShiftWalkerConfig provideConfig(ConfigManager configManager) {
		return (ShiftWalkerConfig)configManager.getConfig(ShiftWalkerConfig.class);
	}

	public void startUp() {
		this.keyManager.registerKeyListener(this.inputListener);
	}

	public void shutDown() {
		this.keyManager.unregisterKeyListener(this.inputListener);
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused()) {
			this.hotKeyPressed = false;
		}

	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (this.client.getGameState() == GameState.LOGGED_IN && this.hotKeyPressed) {
			String pOptionToReplace = Text.removeTags(event.getOption()).toUpperCase();
			if (!pOptionToReplace.equals("CANCEL") && !pOptionToReplace.equals("WALK HERE")) {
				String target = Text.removeTags(event.getTarget().toUpperCase());
				if (this.config.shiftWalkEverything()) {
					this.stripEntries();
				} else if (this.config.shiftWalkBoxTraps() && ShiftWalkerGroups.BOX_TRAP_TARGETS.contains(target) && ShiftWalkerGroups.BOX_TRAP_KEYWORDS.contains(pOptionToReplace) && event.getType() != 1004) {
					this.stripEntries();
				} else if (this.config.shiftWalkAttackOption() && ShiftWalkerGroups.ATTACK_OPTIONS_KEYWORDS.contains(pOptionToReplace) && event.getType() != 1004) {
					this.stripEntries();
				}
			}
		}

	}

	private void delete(MenuEntry entry, MenuEntry[] newEntries) {
		for (int i = newEntries.length - 1; i >= 0; --i) {
			if (newEntries[i].equals(entry)) {
				newEntries = (MenuEntry[])ArrayUtils.remove((Object[])newEntries, i);
			}
		}

		this.client.setMenuEntries(newEntries);
	}

	private void stripEntries() {
		MenuEntry walkkHereEntry = null;
		MenuEntry[] newEntries = this.client.getMenuEntries();
		int var3 = newEntries.length;
		int var4 = 0;

		MenuEntry entry;
		while (var4 < var3) {
			entry = newEntries[var4];
			String var5 = entry.getOption();
			byte var6 = -1;
			switch(var5.hashCode()) {
			case -918617145:
				if (var5.equals("Walk here")) {
					var6 = 0;
				}
			default:
				switch(var6) {
				case 0:
					walkkHereEntry = entry;
				default:
					++var4;
				}
			}
		}

		if (walkkHereEntry != null) {
			if (this.config.shiftWalkAttackOption() && !this.config.shiftWalkEverything()) {
				newEntries = this.client.getMenuEntries();
				var3 = newEntries.length;

				for (var4 = 0; var4 < var3; ++var4) {
					entry = newEntries[var4];
					if (entry.getOption().toLowerCase().contains("attack") || entry.getOption().toLowerCase().contains("pickpocket") || entry.getOption().toLowerCase().contains("talk") || entry.getOption().toLowerCase().contains("knock")) {
						this.delete(entry, this.client.getMenuEntries());
					}
				}
			} else {
				newEntries = new MenuEntry[]{walkkHereEntry};
				this.client.setMenuEntries(newEntries);
			}
		}

	}

	private void swap(String pOptionToReplace) {
		MenuEntry[] entries = this.client.getMenuEntries();
		Integer walkHereEntry = this.searchIndex(entries, "WALK HERE");
		Integer entryToReplace = this.searchIndex(entries, pOptionToReplace);
		if (walkHereEntry != null && entryToReplace != null) {
			MenuEntry walkHereMenuEntry = entries[walkHereEntry];
			entries[walkHereEntry] = entries[entryToReplace];
			entries[entryToReplace] = walkHereMenuEntry;
			this.client.setMenuEntries(entries);
		}

	}

	private Integer searchIndex(MenuEntry[] pMenuEntries, String pMenuEntryToSearchFor) {
		Integer indexLocation = 0;
		MenuEntry[] var4 = pMenuEntries;
		int var5 = pMenuEntries.length;

		for (int var6 = 0; var6 < var5; ++var6) {
			MenuEntry menuEntry = var4[var6];
			String entryOption = Text.removeTags(menuEntry.getOption()).toUpperCase();
			if (entryOption.equals(pMenuEntryToSearchFor)) {
				return indexLocation;
			}

			indexLocation = indexLocation + 1;
		}

		return null;
	}

	public void setHotKeyPressed(boolean hotKeyPressed) {
		this.hotKeyPressed = hotKeyPressed;
	}
}
