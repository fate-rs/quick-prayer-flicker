package com.quickprayers;

import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import net.runelite.api.*;

import javax.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import net.runelite.client.plugins.prayer.PrayerPlugin;

@Slf4j
@PluginDescriptor(
		name = "QuickPrayerFlicker"
)
public class QuickPrayerFlickerPlugin extends Plugin {
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private QuickPrayerFlickerOverlay quickPrayerFlickerOverlay;

	@Inject
	private Client client;

	@Inject
	private QuickPrayerFlickerConfig config;

	@Inject
	private KeyManager keyManager;

	private Canvas canvas;
	private boolean pluginActive = false;
	private boolean prayersActive = false;
	private Instant startOfLastTick = Instant.now();
	private int mouseX = 0;
	private int mouseY = 0;


	private final HotkeyListener hotkeyListener = new HotkeyListener(()->this.config.hotkey()) {
		@Override
		public void hotkeyPressed() {
			pluginActive = !pluginActive;

			if (pluginActive) {
				overlayManager.add(quickPrayerFlickerOverlay);
				setPrayerState(true);
				return;
			}

			setPrayerState(false);
			overlayManager.remove(quickPrayerFlickerOverlay);
		}
	};

	@Override
	protected void startUp() throws Exception {
		log.info("QuickPrayerFlicker plugin enabled");
		canvas = client.getCanvas();
		hotkeyListener.setEnabledOnLoginScreen(false);
		keyManager.registerKeyListener(hotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception {
		keyManager.unregisterKeyListener(hotkeyListener);
		log.info("QuickPrayerFlicker plugin disabled");
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		prayersActive = isAnyPrayerActive();
		startOfLastTick = Instant.now();

		if (pluginActive) {
			System.out.println("TICK. Prayers active: " + prayersActive);
			if (client.getBoostedSkillLevel(Skill.PRAYER) < 1)
				return;

			Widget prayerOrb = client.getWidget(ComponentID.MINIMAP_QUICK_PRAYER_ORB);
			if (prayerOrb == null || prayerOrb.isHidden())
				return;

			Rectangle2D prayerOrbRectangle = prayerOrb.getBounds().getBounds2D();
			if (prayerOrbRectangle.getX() < 0)
				return;

			double prayerOrbHeight = prayerOrbRectangle.getHeight();

			double horizontalDeviance = (1 - Math.sqrt(1 - Math.random())) * prayerOrbHeight;
			double verticalDeviance = (1 - Math.sqrt(1 - Math.random())) * prayerOrbHeight;

			mouseX = (int) (prayerOrbRectangle.getX() + horizontalDeviance);
			mouseY = (int) (prayerOrbRectangle.getY() + verticalDeviance);

			setPrayerState(false);
			setPrayerState(true);
		}
	}

	private void setPrayerState(boolean on) {
		System.out.println("Tasked to switch prayers to " + on + ", prayers currently " + prayersActive);
		if (on && prayersActive)
			return;
		if (!on && !prayersActive)
			return;

		long eventTime = System.currentTimeMillis();
		MouseEvent press = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, eventTime, 0, mouseX, mouseY, 1, false, MouseEvent.BUTTON1);
		canvas.dispatchEvent(press);

		eventTime = System.currentTimeMillis();
		MouseEvent release = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, eventTime, 0, mouseX, mouseY, 1, false, MouseEvent.BUTTON1);
		canvas.dispatchEvent(release);

		eventTime = System.currentTimeMillis();
		MouseEvent click = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, eventTime, 0, mouseX, mouseY, 1, false, MouseEvent.BUTTON1);
		canvas.dispatchEvent(click);
	}

	private boolean isAnyPrayerActive()	{
		for (Prayer pray : Prayer.values())	{
			if (client.isPrayerActive(pray))
				return true;
		}
		return false;
	}

	@Provides
	QuickPrayerFlickerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(QuickPrayerFlickerConfig.class);
	}
}