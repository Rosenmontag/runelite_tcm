package net.runelite.client.plugins.truechunkman;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChunkLockerOverlay extends Overlay {
    private static final Color WHITE_TRANSLUCENT = new Color(255, 255, 255, 127);
    private static final int LABEL_PADDING = 4;
    private static final int REGION_SIZE = 1 << 3;//6;
    // Bitmask to return first coordinate in region
    private static final int REGION_TRUNCATE = ~0x7;//~0x3F;

    private final Client client;
    private final ChunkLockerPlugin chunkLockerPlugin;
    private final ChunkLockerConfig config;

    private int rolledChunk = -1;

    @Inject
    private ChunkLockerOverlay(Client client, ChunkLockerPlugin chunkLockerPlugin, ChunkLockerConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(Overlay.PRIORITY_HIGHEST);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
        this.chunkLockerPlugin = chunkLockerPlugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.drawMapOverlay()) drawRegionOverlay(graphics);

        return null;
    }

    private void drawRegionOverlay(Graphics2D graphics)
    {
        Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

        if (map == null) return;

        WorldMap worldMap = client.getWorldMap();
        float pixelsPerTile = worldMap.getWorldMapZoom();
        Rectangle worldMapRect = map.getBounds();
        graphics.setClip(worldMapRect);

        int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
        int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

        net.runelite.api.Point worldMapPosition = worldMap.getWorldMapPosition();

        // Offset in tiles from anchor sides
        int yTileMin = worldMapPosition.getY() - heightInTiles / 2;
        int xRegionMin = (worldMapPosition.getX() - widthInTiles / 2) & REGION_TRUNCATE;
        int xRegionMax = ((worldMapPosition.getX() + widthInTiles / 2) & REGION_TRUNCATE) + REGION_SIZE;
        int yRegionMin = (yTileMin & REGION_TRUNCATE);
        int yRegionMax = ((worldMapPosition.getY() + heightInTiles / 2) & REGION_TRUNCATE) + REGION_SIZE;
        int regionPixelSize = (int) Math.ceil(REGION_SIZE * pixelsPerTile);

        Point mousePos = client.getMouseCanvasPosition();

        chunkLockerPlugin.setHoveredChunk(-1);
        graphics.setColor(WHITE_TRANSLUCENT);
        for (int x = xRegionMin; x < xRegionMax; x += REGION_SIZE)
        {
            for (int y = yRegionMin; y < yRegionMax; y += REGION_SIZE)
            {
                int yTileOffset = -(yTileMin - y);
                int xTileOffset = x + widthInTiles / 2 - worldMapPosition.getX();

                int xPos = ((int) (xTileOffset * pixelsPerTile)) + (int) worldMapRect.getX();
                int yPos = (worldMapRect.height - (int) (yTileOffset * pixelsPerTile)) + (int) worldMapRect.getY();
                // Offset y-position by a single region to correct for drawRect starting from the top
                yPos -= regionPixelSize;

                //int regionId = ((x >> 6) << 8) | (y >> 6);
                int regionId = ((x >> 3) << 16) | (y >> 3);
                String regionText = String.valueOf(regionId);
                FontMetrics fm = graphics.getFontMetrics();
                Rectangle2D textBounds = fm.getStringBounds(regionText, graphics);
                Rectangle regionRect = new Rectangle(xPos, yPos, regionPixelSize, regionPixelSize);

                RegionTypes regionType = ChunkLocker.getType(regionId);
                boolean containsRegion = (regionType != null) ^ config.invertMapOverlay();
                boolean unlockable = regionType == RegionTypes.UNLOCKABLE;
                boolean blacklisted = regionType == RegionTypes.BLACKLISTED;
                if (containsRegion || unlockable || blacklisted)
                {
                    Color color;
                    if (blacklisted)
                    {
                        color = config.blacklistedOverlayColor();
                    }
                    else if (unlockable)
                    {
                        if (regionId == rolledChunk) {
                            color = config.rolledOverlayColor();
                        }
                        else
                        {
                            color = config.unlockableOverlayColor();
                        }
                    }
                    else
                    {
                        color = config.mapOverlayColor();
                    }
                    if (regionRect.contains(mousePos.getX(), mousePos.getY()))
                        color = color.brighter();
                    graphics.setColor(color);
                    graphics.fillRect(xPos, yPos, regionPixelSize, regionPixelSize);
                }


                if (regionRect.contains(mousePos.getX(), mousePos.getY()))
                    chunkLockerPlugin.setHoveredChunk(regionId);

                graphics.setColor(new Color(0, 19, 36, 127));
                if (config.drawMapGrid()) graphics.drawRect(xPos, yPos, regionPixelSize, regionPixelSize);

                graphics.setColor(WHITE_TRANSLUCENT);
                if (config.drawRegionId())
                    graphics.drawString(regionText, xPos + LABEL_PADDING, yPos + (int) textBounds.getHeight() + LABEL_PADDING);
            }
        }

        int currentId = client.getLocalPlayer().getWorldLocation().getRegionID();
        String regionText = String.valueOf(currentId);
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(regionText, graphics);
        if (config.drawRegionId()) {
            if (chunkLockerPlugin.getHoveredChunk() >= 0)
                graphics.drawString("Hovered chunk: " + chunkLockerPlugin.getHoveredChunk(), (int) worldMapRect.getX() + LABEL_PADDING, (int) (worldMapRect.getY() + worldMapRect.getHeight()) - LABEL_PADDING - (int) textBounds.getHeight());
            graphics.drawString("Player chunk: " + regionText, (int) worldMapRect.getX() + LABEL_PADDING, (int) (worldMapRect.getY() + worldMapRect.getHeight()) - LABEL_PADDING);
        }

    }

    public void rollChunk() {
        Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
        if (map == null) return;

        rolledChunk = ChunkLocker.rollChunk();
    }

}

