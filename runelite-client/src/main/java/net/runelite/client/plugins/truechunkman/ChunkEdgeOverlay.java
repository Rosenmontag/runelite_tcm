package net.runelite.client.plugins.truechunkman;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class ChunkEdgeOverlay extends Overlay {
    private static final int LOCAL_TILE_SIZE = Perspective.LOCAL_TILE_SIZE;
    private static final int CHUNK_SIZE = 8;
    private static final int CULL_CHUNK_BORDERS_RANGE = 32;

    private final Client client;
    private final ChunkLockerConfig config;

    //TODO: Delete
    private HashMap<Integer, Integer> debug_weChunkLines;
    private HashMap<Integer, Integer> debug_snChunkLines;

    @Inject
    private ChunkEdgeOverlay(Client client, ChunkLockerConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(Overlay.PRIORITY_HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.renderChunkEdge()) renderChunkEdge(graphics);

        return null;
    }

    private void renderChunkEdge(Graphics2D graphics) {
        debug_weChunkLines = new HashMap<Integer, Integer>();
        debug_snChunkLines = new HashMap<Integer, Integer>();

        List<String> unlockedChunks = new ArrayList<>(Text.fromCSV(config.unlockedChunks()));

        WorldPoint wp = client.getLocalPlayer().getWorldLocation();

        int startX = (wp.getX() - CULL_CHUNK_BORDERS_RANGE + CHUNK_SIZE - 1) / CHUNK_SIZE * CHUNK_SIZE;
        int startY = (wp.getY() - CULL_CHUNK_BORDERS_RANGE + CHUNK_SIZE - 1) / CHUNK_SIZE * CHUNK_SIZE;
        int endX = (wp.getX() + CULL_CHUNK_BORDERS_RANGE) / CHUNK_SIZE * CHUNK_SIZE;
        int endY = (wp.getY() + CULL_CHUNK_BORDERS_RANGE) / CHUNK_SIZE * CHUNK_SIZE;

        graphics.setStroke(new BasicStroke(config.chunkEdgeWidth()));
        Color c = config.chunkEdgeColor();
        graphics.setColor(c);

        GeneralPath path = new GeneralPath();

        for (int x = startX; x <= endX; x += CHUNK_SIZE)
        {
            LocalPoint lp1 = LocalPoint.fromWorld(client, x, wp.getY() - CULL_CHUNK_BORDERS_RANGE);
            LocalPoint lp2 = LocalPoint.fromWorld(client, x, wp.getY() + CULL_CHUNK_BORDERS_RANGE);
            if (lp1 == null || lp2 == null)
            {
                continue;
            }

            for (int y = startY; y <= endY; y += CHUNK_SIZE) {

                int currChunkId = ((x >> 3) << 16) | (y >> 3);
                int westChunkId = currChunkId - (1 << 16);

                boolean currChunkUnlocked = unlockedChunks.contains(Integer.toString(currChunkId));
                boolean westChunkUnlocked = unlockedChunks.contains(Integer.toString(westChunkId));

                if (currChunkUnlocked ^ westChunkUnlocked) {
                    debug_weChunkLines.put(westChunkId, currChunkId);

                    LocalPoint lp_test1 = LocalPoint.fromWorld(client, x, y);
                    LocalPoint lp_test2 = LocalPoint.fromWorld(client, x, y+8);

                    boolean first = true;
                    for (int tile_y = lp_test1.getY(); tile_y <= lp_test2.getY(); tile_y += LOCAL_TILE_SIZE)
                    {
                        net.runelite.api.Point p = Perspective.localToCanvas(client,
                                new LocalPoint(lp1.getX() - LOCAL_TILE_SIZE / 2, tile_y - LOCAL_TILE_SIZE / 2),
                                client.getPlane());
                        if (p != null)
                        {
                            if (first)
                            {
                                path.moveTo(p.getX(), p.getY());
                                first = false;
                            }
                            else
                            {
                                path.lineTo(p.getX(), p.getY());
                            }
                        }
                    }
                }
            }
        }

        for (int y = startY; y <= endY; y += CHUNK_SIZE)
        {
            LocalPoint lp1 = LocalPoint.fromWorld(client, wp.getX() - CULL_CHUNK_BORDERS_RANGE, y);
            LocalPoint lp2 = LocalPoint.fromWorld(client, wp.getX() + CULL_CHUNK_BORDERS_RANGE, y);
            if (lp1 == null || lp2 == null)
            {
                continue;
            }

            for (int x = startX; x <= endX; x += CHUNK_SIZE) {

                int currChunkId = ((x >> 3) << 16) | (y >> 3);
                int southChunkId = currChunkId - 1;

                boolean currChunkUnlocked = unlockedChunks.contains(Integer.toString(currChunkId));
                boolean southChunkUnlocked = unlockedChunks.contains(Integer.toString(southChunkId));

                if (currChunkUnlocked ^ southChunkUnlocked) {
                    debug_snChunkLines.put(southChunkId, currChunkId);

                    LocalPoint lp_test1 = LocalPoint.fromWorld(client, x, y);
                    LocalPoint lp_test2 = LocalPoint.fromWorld(client, x+8, y);

                    boolean first = true;
                    for (int tile_x = lp_test1.getX(); tile_x <= lp_test2.getX(); tile_x += LOCAL_TILE_SIZE)
                    {
                        net.runelite.api.Point p = Perspective.localToCanvas(client,
                                new LocalPoint(tile_x - LOCAL_TILE_SIZE / 2, lp1.getY() - LOCAL_TILE_SIZE / 2),
                                client.getPlane());
                        if (p != null)
                        {
                            if (first)
                            {
                                path.moveTo(p.getX(), p.getY());
                                first = false;
                            }
                            else
                            {
                                path.lineTo(p.getX(), p.getY());
                            }
                        }
                    }
                }
            }
        }


        graphics.draw(path);
    }

    public void printDebug() {
        System.out.println(debug_weChunkLines);
        System.out.println(debug_snChunkLines);
    }

}
