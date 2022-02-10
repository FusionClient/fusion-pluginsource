package net.runelite.client.plugins.fusionthrallhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

import java.awt.*;

@ConfigGroup("thrallhelper")
public interface ThrallHelperConfig extends Config
{
	@ConfigItem(
		keyName = "shouldNotify",
		name = "Notify when thrall expires",
		description = "Sends a notification once the thrall needs to be summoned"
	)
	default boolean shouldNotify()
	{
		return true;
	}

	@ConfigItem(
		keyName = "thrallTimeout",
		name = "Timeout Thrall Box",
		description = "The duration of time before the thrall box disappears."
	)
	@Units(Units.MINUTES)
	default int thrallTimeout()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "shouldFlash",
		name = "Flash the Reminder Box",
		description = "Makes the reminder box flash."
	)
	default boolean shouldFlash() { return false; }

	@ConfigItem(
			keyName = "dangerColor",
			name = "Thrall Summon Warning Color",
			description = "Color for the overlay when you need to summon a thrall again."
	)
	default Color dangerColor() {return new Color(255, 0 , 0, 150);}
}
