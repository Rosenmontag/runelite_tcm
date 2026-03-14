package net.runelite.client.plugins.truechunkman;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = ChunkLockerPlugin.PLUGIN_NAME,
        description = ChunkLockerPlugin.CONFIG_KEY
)
public class ChunkLockerPlugin extends Plugin
{
    static final String PLUGIN_NAME = "AAA True Chunk Man";
    static final String CONFIG_KEY = "chunklocker";

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ChunkLockerConfig config;

    @Inject
    private ChunkLockerOverlay chunkLockerOverlay;

    @Inject
    private ChunkBorderOverlay chunkBorderOverlay;

    @Inject
    private ChunkEdgeOverlay chunkEdgeOverlay;

    @Inject
    private ChunkLockerInput inputListener;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ConfigManager configManager;

    @Setter(AccessLevel.PACKAGE)
    private boolean unlockKeyPressed = false;

    @Setter(AccessLevel.PACKAGE)
    private boolean blockKeyPressed = false;

    @Setter(AccessLevel.PACKAGE)
    @Getter
    private int hoveredChunk = -1;

    private ChunkLocker chunkLocker;

    @Provides
    ChunkLockerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChunkLockerConfig.class);
    }

    @Override
    protected void startUp() {
        chunkLocker = new ChunkLocker(config, configManager);
        overlayManager.add(chunkLockerOverlay);
        overlayManager.add(chunkBorderOverlay);
        overlayManager.add(chunkEdgeOverlay);
        keyManager.registerKeyListener(inputListener);
        setKeys();
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(chunkLockerOverlay);
        overlayManager.remove(chunkBorderOverlay);
        overlayManager.remove(chunkEdgeOverlay);
        keyManager.unregisterKeyListener(inputListener);
        ChunkLocker.renderLockedChunks = false;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(ChunkLockerPlugin.CONFIG_KEY)) {
            return;
        }

        setKeys();
        chunkLocker.readConfig();
    }

    @Subscribe
    public void onFocusChanged(FocusChanged focusChanged) {
        if (!focusChanged.isFocused()) {
            unlockKeyPressed = false;
            blockKeyPressed = false;
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

        if (!(unlockKeyPressed || blockKeyPressed) || hoveredChunk == -1) {
            return;
        }

        if (unlockKeyPressed) chunkLocker.addChunk(hoveredChunk);
        if (blockKeyPressed) chunkLocker.blockChunk(hoveredChunk);

    }


    private void setKeys() {
        ChunkLockerInput.UNLOCK_KEY = config.unlockKey();
        ChunkLockerInput.BLOCK_KEY = config.blockKey();
        ChunkLockerInput.ROLL_KEY = config.rollKey();
    }

    public void onRollKeyReleased() {
        chunkLockerOverlay.rollChunk();
    }
}
