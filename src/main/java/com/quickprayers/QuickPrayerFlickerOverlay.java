package com.quickprayers;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

class QuickPrayerFlickerOverlay extends Overlay {
    private final Client client;
    private final QuickPrayerFlickerConfig config;
    private final QuickPrayerFlickerPlugin plugin;
    private final PanelComponent panelComponent;

    @Inject
    public QuickPrayerFlickerOverlay(Client client, QuickPrayerFlickerConfig config, QuickPrayerFlickerPlugin plugin) {
        setPosition(OverlayPosition.TOP_CENTER);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.panelComponent = new PanelComponent();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setBackgroundColor(new Color(0, 0, 0, 150));
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(TitleComponent.builder().text("Plugin active!").color(Color.WHITE).build());
        panelComponent.render(graphics);



        return panelComponent.getPreferredSize();
    }
}
