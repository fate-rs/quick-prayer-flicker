package com.quickprayers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("quickprayerflicker")
public interface QuickPrayerFlickerConfig extends Config
{
	@ConfigItem(
			keyName = "hotkey",
			name = "Toggle hotkey",
			description = "The hotkey which will toggle the prayer flicker on/off"
	)
	default Keybind hotkey() {
		return Keybind.NOT_SET;
	}
}
