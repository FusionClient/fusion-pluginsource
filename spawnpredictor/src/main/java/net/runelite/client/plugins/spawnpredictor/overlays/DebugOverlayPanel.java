package net.runelite.client.plugins.spawnpredictor.overlays;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorConfig;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorPlugin;
import net.runelite.client.plugins.spawnpredictor.util.StartLocations;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.apache.commons.lang3.tuple.Pair;

public class DebugOverlayPanel extends OverlayPanel {
    private final SpawnPredictorPlugin plugin;

    private final SpawnPredictorConfig config;

    @Inject
    private DebugOverlayPanel(Client client, SpawnPredictorPlugin plugin, SpawnPredictorConfig config) {
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.config.debug())
            return null;
        this.panelComponent.setPreferredSize(new Dimension(75, 0));
        if (!this.plugin.isFightCavesActive() && this.plugin.isLocatedAtTzhaars()) {
            int rotationCol = this.plugin.getRotationCol();
            this.panelComponent.getChildren().add(LineComponent.builder().left("RCol:").leftColor(Color.WHITE).right(Integer.toString(rotationCol)).rightColor(Color.GREEN).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("RTrans:").leftColor(Color.WHITE).right(Integer.toString(StartLocations.translateRotation(rotationCol))).rightColor(Color.GREEN).build());
        }
        if (this.plugin.isFightCavesActive() && !this.plugin.isLocatedAtTzhaars()) {
            int rotation = this.plugin.getCurrentRotation();
            this.panelComponent.getChildren().add(LineComponent.builder().left("Wave:").leftColor(Color.WHITE).right(Integer.toString(SpawnPredictorPlugin.getCurrentWave())).rightColor(Color.GREEN).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Rotation:").leftColor(Color.WHITE).right(Integer.toString(rotation)).rightColor(Color.GREEN).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("rsV:").leftColor(Color.WHITE).right(Integer.toString(SpawnPredictorPlugin.getRsVal())).rightColor(Color.ORANGE).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("rsF:").leftColor(Color.WHITE).right(Integer.toString(((Integer)((Pair)StartLocations.getLookupMap().get(Integer.valueOf(rotation))).getRight()).intValue())).rightColor(Color.ORANGE).build());
        }
        return super.render(graphics);
    }
}
