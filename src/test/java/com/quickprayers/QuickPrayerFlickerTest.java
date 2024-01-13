package com.quickprayers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class QuickPrayerFlickerTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(QuickPrayerFlicker.class);
		RuneLite.main(args);
	}
}