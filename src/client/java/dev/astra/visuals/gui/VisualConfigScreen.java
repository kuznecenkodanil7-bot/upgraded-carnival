package dev.astra.visuals.gui;

import dev.astra.visuals.AstraVisualsClient;
import dev.astra.visuals.config.ConfigManager;
import dev.astra.visuals.config.VisualConfig;
import dev.astra.visuals.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class VisualConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 154;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final VisualConfig config = AstraVisualsClient.CONFIG;

    public VisualConfigScreen(Screen parent) {
        super(Text.translatable("screen.astravisuals.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int gap = 8;
        int left = width / 2 - BUTTON_WIDTH - gap / 2;
        int right = width / 2 + gap / 2;
        int y = 50;

        addToggle("screen.astravisuals.enabled", () -> config.enabled, value -> config.enabled = value, left, y);
        addToggle("screen.astravisuals.watermark", () -> config.watermark, value -> config.watermark = value, right, y);
        y += 24;

        addToggle("screen.astravisuals.stats", () -> config.stats, value -> config.stats = value, left, y);
        addToggle("screen.astravisuals.keystrokes", () -> config.keystrokes, value -> config.keystrokes = value, right, y);
        y += 24;

        addToggle("screen.astravisuals.armor", () -> config.armor, value -> config.armor = value, left, y);
        addToggle("screen.astravisuals.effects", () -> config.effects, value -> config.effects = value, right, y);
        y += 24;

        addToggle("screen.astravisuals.compass", () -> config.compass, value -> config.compass = value, left, y);
        addToggle("screen.astravisuals.target_hud", () -> config.targetHud, value -> config.targetHud = value, right, y);
        y += 24;

        addToggle("screen.astravisuals.target_indicator", () -> config.targetIndicator, value -> config.targetIndicator = value, left, y);
        addToggle("screen.astravisuals.low_health", () -> config.lowHealthVignette, value -> config.lowHealthVignette = value, right, y);
        y += 28;

        addDrawableChild(ButtonWidget.builder(themeLabel(), button -> {
            Theme next = Theme.fromName(config.theme).next();
            config.theme = next.name();
            button.setMessage(themeLabel());
            ConfigManager.save(config);
        }).dimensions(left, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        addDrawableChild(ButtonWidget.builder(opacityLabel(), button -> {
            config.panelOpacity += 25;
            if (config.panelOpacity > 230) {
                config.panelOpacity = 80;
            }
            button.setMessage(opacityLabel());
            ConfigManager.save(config);
        }).dimensions(right, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        y += 30;
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("screen.astravisuals.done"),
                button -> close()
        ).dimensions(width / 2 - 100, y, 200, BUTTON_HEIGHT).build());
    }

    private void addToggle(String translationKey, BooleanSupplier getter, Consumer<Boolean> setter, int x, int y) {
        addDrawableChild(ButtonWidget.builder(toggleLabel(translationKey, getter.getAsBoolean()), button -> {
            boolean newValue = !getter.getAsBoolean();
            setter.accept(newValue);
            button.setMessage(toggleLabel(translationKey, newValue));
            ConfigManager.save(config);
        }).dimensions(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private Text toggleLabel(String translationKey, boolean enabled) {
        String state = Text.translatable(enabled ? "screen.astravisuals.on" : "screen.astravisuals.off").getString();
        return Text.literal(Text.translatable(translationKey).getString() + ": " + state);
    }

    private Text themeLabel() {
        Theme theme = Theme.fromName(config.theme);
        return Text.literal(Text.translatable("screen.astravisuals.theme").getString() + ": " + theme.displayName());
    }

    private Text opacityLabel() {
        int percent = Math.round(config.panelOpacity / 255.0F * 100.0F);
        return Text.literal(Text.translatable("screen.astravisuals.opacity").getString() + ": " + percent + "%");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("screen.astravisuals.subtitle"),
                width / 2,
                30,
                0xFFB8B8C8
        );
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ConfigManager.save(config);
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
