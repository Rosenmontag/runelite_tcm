package net.runelite.client.plugins.truechunkman;

import net.runelite.client.config.*;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.*;

@ConfigGroup("chunklocker")
public interface ChunkLockerConfig extends Config{

    @ConfigSection(
            name = "Chunks",
            description = "Settings relating to chunks that you can unlock",
            position = 3
    )
    String chunkSettings = "chunkSettings";

    @ConfigSection(
            name = "Environment Looks",
            description = "Settings relating to locked regions look",
            position = 1
    )
    String environmentSettings = "environmentSettings";

    @ConfigSection(
            name = "Map Settings",
            description = "Settings relating to the map overlay",
            position = 2
    )
    String mapSettings = "mapSettings";


    @ConfigItem(
            keyName = "unlockUnderground",
            name = "Unlock underground",
            description = "Unlock all underground chunks (and any non-mainland chunks)",
            section = chunkSettings
    )
    default boolean unlockNonMainlandChunks()
    {
        return true;
    }

    @ConfigItem(
            keyName = "unlockedChunks",
            name = "Unlocked chunks",
            description = "List of unlocked chunks seperated by a ',' symbol",
            section = chunkSettings
    )
    default String unlockedChunks()
    {
        return "";
    }

    @ConfigItem(
            keyName = "unlockableChunks",
            name = "Unlockable chunks",
            description = "List of unlockable chunks seperated by a ',' symbol",
            section = chunkSettings
    )
    default String unlockableChunks()
    {
        return "";
    }

    @ConfigItem(
            keyName = "blacklistedChunks",
            name = "Blacklisted chunks",
            description = "List of blacklisted chunks seperated by a ',' symbol",
            section = chunkSettings
    )
    default String blacklistedChunks()
    {
        return "";
    }

    @ConfigItem(
            keyName = "renderLockedChunks",
            name = "Locked chunk shader",
            description = "Adds graphical change to all chunks that are locked",
            section = environmentSettings,
            hidden = true
    )
    default boolean renderLockedChunks()
    {
        return true;
    }

    @ConfigItem(
            keyName = "renderChunkBorders",
            name = "Draw chunk border lines",
            description = "Draw the chunk borders in the environment marked by lines",
            section = environmentSettings,
            position = 4
    )
    default boolean renderChunkBorders()
    {
        return true;
    }

    @ConfigItem(
            keyName = "chunkBorderWidth",
            name = "Chunk border width",
            description = "How wide the chunk border will be",
            section = environmentSettings,
            position = 7
    )
    default int chunkBorderWidth()
    {
        return 1;
    }

    @ConfigItem(
            keyName = "renderChunkEdges",
            name = "Draw unlocked chunks edge",
            description = "Draw the outer border line between unlocked and locked chunks",
            section = environmentSettings,
            position = 5
    )
    default boolean renderChunkEdge()
    {
        return true;
    }

    @ConfigItem(
            keyName = "chunkEdgeWidth",
            name = "Chunk edge width",
            description = "How wide the chunk edge will be",
            section = environmentSettings,
            position = 9
    )
    default int chunkEdgeWidth()
    {
        return 1;
    }

    @Alpha
    @ConfigItem(
            keyName = "chunkEdgeColor",
            name = "Chunk edge color",
            description = "The color of the chunk edges",
            section = environmentSettings,
            position = 8
    )
    default Color chunkEdgeColor()
    {
        return new Color(200,0,83, 200);
    }

    @Alpha
    @ConfigItem(
            keyName = "chunkBorderColor",
            name = "Chunk border color",
            description = "The color of the chunk borders",
            section = environmentSettings,
            position = 6
    )
    default Color chunkBorderColor()
    {
        return new Color(0, 200, 83, 200);
    }

    @Alpha
    @ConfigItem(
            keyName = "shaderGrayColor",
            name = "Chunk shader color",
            description = "The color of the locked chunks in the shader",
            section = environmentSettings,
            hidden = true
    )
    default Color shaderGrayColor()
    {
        return new Color(0, 31, 77, 204);
    }

    @Alpha
    @ConfigItem(
            keyName = "shaderGrayAmount",
            name = "Chunk shader opacity",
            description = "The amount of gray scale that is applied to a locked chunk in the shader (alpha only)",
            section = environmentSettings,
            hidden = true
    )
    default Color shaderGrayAmount()
    {
        return new Color(0, 0, 0, 204);
    }

    @ConfigItem(
            keyName = "hardBorder",
            name = "Hard chunk border",
            description = "True = hard border cutoff, False = chunk border gradient",
            section = environmentSettings,
            hidden = true
    )
    default boolean hardBorder()
    {
        return true;
    }


    @ConfigItem(
            keyName = "drawMapOverlay",
            name = "Draw chunks on map",
            description = "Draw a color overlay for each locked/unlocked chunk",
            section = mapSettings,
            position = 10
    )
    default boolean drawMapOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "invertMapOverlay",
            name = "Invert map overlay",
            description = "Switches which chunks the map will draw the color overlay for (true = locked, false = unlocked)",
            section = mapSettings,
            position = 13
    )
    default boolean invertMapOverlay()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "mapOverlayColor",
            name = "Map overlay color",
            description = "The color the map overlay will draw the chunks in",
            section = mapSettings,
            position = 17
    )
    default Color mapOverlayColor()
    {
        return new Color(200, 16, 0, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "unlockableOverlayColor",
            name = "Unlockable overlay color",
            description = "The color the map overlay will draw the unlockable chunks in",
            section = mapSettings,
            position = 18
    )
    default Color unlockableOverlayColor()
    {
        return new Color(60, 200, 160, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "blacklistedOverlayColor",
            name = "Blacklisted overlay color",
            description = "The color the map overlay will draw the blacklisted chunks in",
            section = mapSettings,
            position = 19
    )
    default Color blacklistedOverlayColor()
    {
        return new Color(0, 0, 0, 200);
    }

    @ConfigItem(
            keyName = "drawMapGrid",
            name = "Draw map grid",
            description = "Draw the grid of chunks on the map",
            section = mapSettings,
            position = 11
    )
    default boolean drawMapGrid()
    {
        return true;
    }

    @ConfigItem(
            keyName = "drawRegionId",
            name = "Draw region IDs",
            description = "Draw the chunk ID for each chunk on the map",
            section = mapSettings,
            position = 12
    )
    default boolean drawRegionId()
    {
        return true;
    }

    @ConfigItem(
            keyName = "unlockKey",
            name = "Unlock Key",
            description = "When you hold this key you can click on the map to unlock a chunk",
            section = mapSettings,
            position = 14
    )
    default Keybind unlockKey(){return Keybind.SHIFT;}

    @ConfigItem(
            keyName = "blockKey",
            name = "Block Key",
            description = "When you hold this key you can click on the map to blacklist a chunk",
            section = mapSettings,
            position = 15
    )
    default Keybind blockKey(){return Keybind.CTRL;}

    @ConfigItem(
            keyName = "rollKey",
            name = "Roll Key",
            description = "Press and release this key once to roll a random chunk out of all unlockable ones.",
            section = mapSettings,
            position = 16
    )
    default Keybind rollKey(){return Keybind.NOT_SET;}

    @ConfigItem(
            keyName = "invertShader",
            name = "Invert chunk shader",
            description = "True = unlocked chunks are shaded",
            section = environmentSettings,
            hidden = true
    )
    default boolean invertShader() { return false; }
}
