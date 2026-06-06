package dev.astra.visuals;

import dev.astra.visuals.config.ConfigManager;
import dev.astra.visuals.config.VisualConfig;
import dev.astra.visuals.gui.VisualConfigScreen;
import dev.astra.visuals.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AstraVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "astravisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static VisualConfig CONFIG;

    private static KeyBinding openMenuKey;

    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {
        CONFIG = ConfigManager.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.astravisuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new VisualConfigScreen(client.currentScreen));
            }
        });

        HudRenderCallback.EVENT.register(HudRenderer::render);
        LOGGER.info("Astra Visuals loaded.");
    }
}
