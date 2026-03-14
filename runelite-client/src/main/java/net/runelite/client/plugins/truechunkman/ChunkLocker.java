package net.runelite.client.plugins.truechunkman;

import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.Text;

import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkLocker {
    private final ChunkLockerConfig config;
    private final ConfigManager configManager;

    @Getter
    private final static Map<String, RegionTypes> chunks = new HashMap<>();

    public static boolean renderLockedChunks;
    public static Color grayColor = new Color(0, 31, 77, 204);
    public static int grayAmount;
    public static boolean hardBorder;
    public static boolean invertShader;
    private static boolean unlockNonMainlandChunks;


    ChunkLocker(ChunkLockerConfig config, ConfigManager configManager) {
        this.config = config;
        this.configManager = configManager;
        readConfig();
    }

    private java.util.List<String> StringToList(String s)
    {
        List<String> regs;
        if (s.isEmpty())
            regs = new ArrayList<>();
        else
            regs = new ArrayList<>(Text.fromCSV(s));
        return regs;
    }

    public void readConfig() {
        renderLockedChunks = config.renderLockedChunks();
        unlockNonMainlandChunks = config.unlockNonMainlandChunks();
        grayColor = config.shaderGrayColor();
        grayAmount = config.shaderGrayAmount().getAlpha();
        hardBorder = config.hardBorder();
        invertShader = config.invertShader();

        chunks.clear();

        String unlockedString = config.unlockedChunks();
        List<String> unlockedChunks = StringToList(unlockedString);
        setChunks(unlockedChunks, RegionTypes.UNLOCKED);

        String unlockableString = config.unlockableChunks();
        List<String> unlockableChunks = StringToList(unlockableString);
        setChunks(unlockableChunks, RegionTypes.UNLOCKABLE);

        String blacklistedString = config.blacklistedChunks();
        List<String> blacklistedChunks = StringToList(blacklistedString);
        setChunks(blacklistedChunks, RegionTypes.BLACKLISTED);

    }

    private void setChunks(List<String> chunkList, RegionTypes regionType) {
        for (String id : chunkList) {
            chunks.put(id, regionType);
        }
    }

    private void setConfig() {
        List<String> unlockedChunks = new ArrayList<>();
        List<String> unlockableChunks = new ArrayList<>();
        List<String> blockedChunks = new ArrayList<>();

        chunks.forEach((key, value) -> {
            if (value == RegionTypes.UNLOCKED) unlockedChunks.add(key);
            if (value == RegionTypes.UNLOCKABLE) unlockableChunks.add(key);
            if (value == RegionTypes.BLACKLISTED) blockedChunks.add(key);
        });

        String csv = Text.toCSV(unlockedChunks);
        configManager.setConfiguration(ChunkLockerPlugin.CONFIG_KEY, "unlockedChunks", csv);

        csv = Text.toCSV(unlockableChunks);
        configManager.setConfiguration(ChunkLockerPlugin.CONFIG_KEY, "unlockableChunks", csv);

        csv = Text.toCSV(blockedChunks);
        configManager.setConfiguration(ChunkLockerPlugin.CONFIG_KEY, "blacklistedChunks", csv);
    }

    public void addChunk(int chunkId) {
        String id = Integer.toString(chunkId);
        RegionTypes type = chunks.get(id);
        if (type == null)
            chunks.put(id, RegionTypes.UNLOCKABLE);
        else if (type == RegionTypes.UNLOCKABLE)
            chunks.put(id, RegionTypes.UNLOCKED);
        else
            chunks.remove(id);
        setConfig();
    }

    public void blockChunk(int chunkId) {
        String id = Integer.toString(chunkId);
        RegionTypes type = chunks.get(id);
        if (type != RegionTypes.BLACKLISTED)
            if (type == null) chunks.put(id, RegionTypes.BLACKLISTED);
            else chunks.replace(id, RegionTypes.BLACKLISTED);
        else
            chunks.remove(id);
        setConfig();
    }

    public static RegionTypes getType(int chunkId) {
        String id = Integer.toString(chunkId);
        return chunks.get(id);
    }

    public static boolean hasChunk(int chunkId) {
        RegionTypes type = getType(chunkId);
        if (type == null) return false;
        return type == RegionTypes.UNLOCKED;
    }
}
