package net.runelite.client.plugins.externals.oneclickthieving;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
	name = "[F] One Click Pickpocket",
	description = "QOL for pickpocketing",
	tags = {"Sundar", "Pickpocket", "Skilling", "Thieving"},
	enabledByDefault = false
)
public class OneClickThievingPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private ItemManager itemManager;
	@Inject
	private OneClickThievingConfig config;
	@Inject
	private Notifier notifier;
	@Inject
	private ChatMessageManager chatMessageManager;
	@Inject
	private ConfigManager configManager;
	@Inject
	private OverlayManager overlayManager;
	Set foodMenuOption;
	Set prayerPotionIDs;

	Set foodBlacklist;
	Set coinPouches;
	private boolean shouldHeal;
	private int prayerTimeOut;
	private static final int DODGY_NECKLACE_ID = 21143;
	// $FF: synthetic field
	static final boolean $assertionsDisabled;

	public OneClickThievingPlugin() {
		this.foodMenuOption = Set.of("Drink", "Eat");
		this.prayerPotionIDs = Set.of(139, 141, 143, 2434, 3024, 3026, 3028, 3030, 189, 191, 193, 2450, 26346, 26344, 26342, 26340);
		this.foodBlacklist = Set.of(139, 141, 143, 2434, 3024, 3026, 3028, 3030, 24774, 189, 191, 193, 2450);
		this.coinPouches = Set.of(22521, 22522, 22523, 22524, 22525, 22526, 22527, 22528, 22529, 22530, 22531, 22532, 22533, 22534, 22535, 22536, 22537, 22538, 24703);
		this.shouldHeal = false;
		this.prayerTimeOut = 0;
	}

	@Provides
	OneClickThievingConfig provideConfig(ConfigManager configManager) {
		return (OneClickThievingConfig)configManager.getConfig(OneClickThievingConfig.class);
	}

	protected void startUp() {
	}

	protected void shutDown() {
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getMessage().contains("You have run out of prayer points")) {
			this.prayerTimeOut = 0;
		}

	}

	@Subscribe
	public void onStatChanged(StatChanged event) {
		if (event.getSkill() == Skill.PRAYER && event.getBoostedLevel() == 0 && this.prayerTimeOut == 0) {
			this.prayerTimeOut = 10;
		}

	}

	@Subscribe
	private void onClientTick(ClientTick event) {
		if (this.config.clickOverride() && this.client.getLocalPlayer() != null && this.client.getGameState() == GameState.LOGGED_IN) {
			this.client.insertMenuItem("Pickpocket", "", MenuAction.UNKNOWN.getId(), 0, 0, 0, true);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (this.config.clickOverride() && event.getMenuOption().equals("Pickpocket")) {
			NPC npc = (NPC)(new NPCQuery()).idEquals(new int[]{this.config.npcID()}).result(this.client).nearestTo(this.client.getLocalPlayer());
			if (npc == null) {
				this.sendGameMessage("Npc not found please change the id");
				event.consume();
				return;
			}

			event.setMenuOption("Pickpocket");
			event.setMenuTarget(npc.getName());
			event.setId(npc.getIndex());
			event.setMenuAction(MenuAction.NPC_THIRD_OPTION);
			event.setParam0(0);
			event.setParam1(0);


		//	event.setMenuEntry(new MenuEntry("Pickpocket", npc.getName(), npc.getIndex(), MenuAction.NPC_THIRD_OPTION.getId(), 0, 0, false);


			switch(this.getActions(npc).indexOf("Pickpocket")) {
			case 0:
				event.setMenuAction(MenuAction.NPC_FIRST_OPTION);
				break;
			case 1:
				event.setMenuAction(MenuAction.NPC_SECOND_OPTION);
				break;
			case 2:
				event.setMenuAction(MenuAction.NPC_THIRD_OPTION);
				break;
			case 3:
				event.setMenuAction(MenuAction.NPC_FOURTH_OPTION);
				break;
			case 4:
				event.setMenuAction(MenuAction.NPC_FIFTH_OPTION);
				break;
			default:
				this.sendGameMessage("Did not find pickpocket option on npc, check configs");
				event.consume();
				return;
			}
		}

		this.changeMenuAction(event);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (this.prayerTimeOut > 0) {
			--this.prayerTimeOut;
		}

		if (this.client.getBoostedSkillLevel(Skill.HITPOINTS) >= Math.min(this.client.getRealSkillLevel(Skill.HITPOINTS), this.config.HPTopThreshold())) {
			this.shouldHeal = false;
		} else if (this.client.getBoostedSkillLevel(Skill.HITPOINTS) <= Math.max(5, this.config.HPBottomThreshold())) {
			this.shouldHeal = true;
		}

	}

	private List getActions(NPC npc) {
		return (List)Arrays.stream(npc.getComposition().getActions()).map((o) -> {
			return o == null ? null : Text.removeTags(o);
		}).collect(Collectors.toList());
	}

	private void changeMenuAction(MenuOptionClicked event) {
		if (this.config.disableWalk() && event.getMenuOption().equals("Walk here")) {
			event.consume();
		} else if (event.getMenuOption().equals("Pickpocket")) {
			WidgetItem coinpouch = this.getWidgetItem(this.coinPouches);
			WidgetItem prayerPotion;
			if (this.config.enableHeal() && this.shouldHeal) {
				prayerPotion = this.getItemMenu(this.foodMenuOption, this.foodBlacklist);
				if (this.config.haltOnLowFood() && prayerPotion == null) {
					event.consume();
					this.notifier.notify("You are out of food");
					this.sendGameMessage("You are out of food");
					return;
				}

				if (prayerPotion != null) {
					String[] foodMenuOptions = this.itemManager.getItemComposition(prayerPotion.getId()).getInventoryActions();

				//	event.setMenuEntry(new MenuEntry(foodMenuOptions[0], foodMenuOptions[0], prayerPotion.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), prayerPotion.getIndex(), WidgetInfo.INVENTORY.getId(), false));


					event.setMenuOption(foodMenuOptions[0]);
					event.setMenuTarget(foodMenuOptions[0]);
					event.setId(prayerPotion.getId()); //check if setId is correct
					event.setMenuAction(MenuAction.ITEM_FIRST_OPTION);
					event.setParam0(prayerPotion.getIndex());
					event.setParam1(WidgetInfo.INVENTORY.getId());
					return;
				}
			}

			if (this.config.enableCoinPouch() && coinpouch != null && coinpouch.getQuantity() == 28) {
				event.setMenuOption("Open-all");
				event.setMenuTarget("Coin Pouch");
				event.setId(coinpouch.getId()); //check if setId is correct
				event.setMenuAction(MenuAction.ITEM_FIRST_OPTION);
				event.setParam0(coinpouch.getIndex());
				event.setParam1(WidgetInfo.INVENTORY.getId());

				//event.setMenuEntry(new MenuEntry("Open-all", "Coin Pouch", coinpouch.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), coinpouch.getIndex(), WidgetInfo.INVENTORY.getId(), false));
			} else if (this.config.enableNecklace() && this.getWidgetItem(21143) != null && !this.isItemEquipped(List.of(21143))) {
				event.setMenuOption("Wear");
				event.setMenuTarget("Necklace");
				event.setId(21143); //check if setId is correct
				event.setMenuAction(MenuAction.ITEM_SECOND_OPTION);
				event.setParam0(this.getWidgetItem(21143).getIndex());
				event.setParam1(WidgetInfo.INVENTORY.getId());

			//	event.setMenuEntry(new MenuEntry("Wear", "Necklace", 21143, MenuAction.ITEM_SECOND_OPTION.getId(), this.getWidgetItem(21143).getIndex(), WidgetInfo.INVENTORY.getId(), false));
			} else if (this.config.enableSpell() && this.client.getVarbitValue(12414) == 0) {
				if (this.client.getVarbitValue(4070) != 3) {
					event.consume();
					this.notifier.notify("You are on the wrong spellbook");
					this.sendGameMessage("You are on the wrong spellbook");
				} else {

					event.setMenuOption("Cast");
					event.setMenuTarget("Shadow Veil");
					event.setId(1); //check if setId is correct
					event.setMenuAction(MenuAction.CC_OP);
					event.setParam0(-1);
					event.setParam1(WidgetInfo.SPELL_SHADOW_VEIL.getId());

				//	event.setMenuEntry(new MenuEntry("Cast", "Shadow Veil", 1, MenuAction.CC_OP.getId(), -1, WidgetInfo.SPELL_SHADOW_VEIL.getId(), false));
				}
			} else if (this.config.enablePray()) {
				if (this.client.getBoostedSkillLevel(Skill.PRAYER) == 0 && this.prayerTimeOut == 0) {
					prayerPotion = this.getWidgetItem(this.prayerPotionIDs);
					if (prayerPotion != null) {

						event.setMenuOption("Drink");
						event.setMenuTarget("Prayer");
						event.setId(prayerPotion.getId()); //check if setId is correct
						event.setMenuAction(MenuAction.ITEM_FIRST_OPTION);
						event.setParam0(prayerPotion.getIndex());
						event.setParam1(WidgetInfo.INVENTORY.getId());

					//	event.setMenuEntry(new MenuEntry("Drink", "Prayer", prayerPotion.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), prayerPotion.getIndex(), WidgetInfo.INVENTORY.getId(), false));
					} else if (this.config.haltOnLowFood()) {
						event.consume();
						this.notifier.notify("You are out of prayer potions");
						this.sendGameMessage("You are out of prayer potions");
					}
				} else if (this.client.getVarbitValue(Varbits.PRAYER_REDEMPTION.getId()) == 0 && this.client.getBoostedSkillLevel(Skill.PRAYER) > 0 && (this.config.prayMethod() == PrayMethod.REACTIVE_PRAY && this.shouldPray() || this.config.prayMethod() == PrayMethod.LAZY_PRAY)) {

					event.setMenuOption("Activate");
					event.setMenuTarget("Redemption");
					event.setId(1); //check if setId is correct
					event.setMenuAction(MenuAction.CC_OP);
					event.setParam0(-1);
					event.setParam1(WidgetInfo.PRAYER_REDEMPTION.getId());

				//	event.setMenuEntry(new MenuEntry("Activate", "Redemption", 1, MenuAction.CC_OP.getId(), -1, WidgetInfo.PRAYER_REDEMPTION.getId(), false));
				}
			}

		}
	}

	private boolean shouldPray() {
		return this.client.getBoostedSkillLevel(Skill.HITPOINTS) < 11;
	}

	public boolean isItemEquipped(Collection itemIds) {
		if (!$assertionsDisabled && !this.client.isClientThread()) {
			throw new AssertionError();
		} else {
			ItemContainer equipmentContainer = this.client.getItemContainer(InventoryID.EQUIPMENT);
			if (equipmentContainer != null) {
				Item[] items = equipmentContainer.getItems();
				Item[] var4 = items;
				int var5 = items.length;

				for (int var6 = 0; var6 < var5; ++var6) {
					Item item = var4[var6];
					if (itemIds.contains(item.getId())) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public WidgetItem getWidgetItem(Collection ids) {
		Widget inventoryWidget = this.client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget != null) {
			Collection items = inventoryWidget.getWidgetItems();
			Iterator var4 = items.iterator();

			while (var4.hasNext()) {
				WidgetItem item = (WidgetItem)var4.next();
				if (ids.contains(item.getId())) {
					return item;
				}
			}
		}

		return null;
	}

	private WidgetItem getWidgetItem(int id) {
		return this.getWidgetItem(Collections.singletonList(id));
	}

	private WidgetItem getItemMenu(Collection menuOptions, Collection ignoreIDs) {
		Widget inventoryWidget = this.client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget != null) {
			Collection items = inventoryWidget.getWidgetItems();
			Iterator var5 = items.iterator();

			while (true) {
				WidgetItem item;
				do {
					if (!var5.hasNext()) {
						return null;
					}

					item = (WidgetItem)var5.next();
				} while(ignoreIDs.contains(item.getId()));

				String[] menuActions = this.itemManager.getItemComposition(item.getId()).getInventoryActions();
				String[] var8 = menuActions;
				int var9 = menuActions.length;

				for (int var10 = 0; var10 < var9; ++var10) {
					String action = var8[var10];
					if (action != null && menuOptions.contains(action)) {
						return item;
					}
				}
			}
		} else {
			return null;
		}
	}

	private void sendGameMessage(String message) {
		String chatMessage = (new ChatMessageBuilder()).append(ChatColorType.HIGHLIGHT).append(message).build();
		this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
	}

	static {
		$assertionsDisabled = !OneClickThievingPlugin.class.desiredAssertionStatus();
		log = LoggerFactory.getLogger(OneClickThievingPlugin.class);
	}
}
