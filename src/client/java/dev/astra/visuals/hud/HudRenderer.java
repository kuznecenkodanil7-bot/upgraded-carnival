package dev.astra.visuals.hud;

import dev.astra.visuals.AstraVisualsClient;
import dev.astra.visuals.config.VisualConfig;
import dev.astra.visuals.theme.ColorUtil;
import dev.astra.visuals.theme.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public final class HudRenderer {
    private static final int TEXT = 0xFFF4F4F7;
    private static final int MUTED_TEXT = 0xFFB8B8C8;
    private static final int SHADOW = 0x50000000;

    private HudRenderer() {
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        VisualConfig config = AstraVisualsClient.CONFIG;

        if (config == null || !config.enabled || client.player == null || client.options.hudHidden) {
            return;
        }

        Theme theme = Theme.fromName(config.theme);
        int accent = ColorUtil.animatedAccent(theme);
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        if (config.watermark) {
            drawWatermark(context, client, theme, accent);
        }
        if (config.stats) {
            drawStats(context, client, theme, accent);
        }
        if (config.compass) {
            drawCompass(context, client, theme, accent, width);
        }
        if (config.keystrokes) {
            drawKeystrokes(context, client, theme, accent, height);
        }
        if (config.armor) {
            drawArmor(context, client, theme, accent, width, height);
        }
        if (config.effects) {
            drawEffects(context, client, theme, accent, width);
        }
        if (config.targetHud) {
            drawTargetHud(context, client, theme, accent, width, height);
        }
        if (config.targetIndicator) {
            drawTargetIndicator(context, client, accent, width, height);
        }
        if (config.lowHealthVignette) {
            drawLowHealthVignette(context, client, width, height);
        }
    }

    private static void drawWatermark(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent
    ) {
        String title = "ASTRA";
        String version = "Visuals 1.0";
        int titleWidth = client.textRenderer.getWidth(title);
        int versionWidth = client.textRenderer.getWidth(version);
        int width = titleWidth + versionWidth + 23;

        panel(context, 8, 8, width, 22, theme, accent);
        context.drawTextWithShadow(client.textRenderer, title, 16, 15, accent);
        context.drawTextWithShadow(client.textRenderer, version, 21 + titleWidth, 15, TEXT);
    }

    private static void drawStats(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent
    ) {
        int ping = getPing(client);
        Vec3d velocity = client.player.getVelocity();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z) * 20.0D * 3.6D;

        String[] lines = {
                "FPS  " + client.getCurrentFps(),
                "PING " + (ping < 0 ? "—" : ping + " ms"),
                String.format(Locale.ROOT, "XYZ  %d  %d  %d",
                        client.player.getBlockX(),
                        client.player.getBlockY(),
                        client.player.getBlockZ()),
                String.format(Locale.ROOT, "SPD  %.1f km/h", speed)
        };

        int width = 0;
        for (String line : lines) {
            width = Math.max(width, client.textRenderer.getWidth(line));
        }
        width += 18;

        int x = 8;
        int y = 36;
        panel(context, x, y, width, 49, theme, accent);

        for (int index = 0; index < lines.length; index++) {
            int color = index == 0 ? TEXT : MUTED_TEXT;
            context.drawTextWithShadow(client.textRenderer, lines[index], x + 9, y + 7 + index * 10, color);
        }
    }

    private static void drawCompass(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent,
            int screenWidth
    ) {
        float yaw = wrapDegrees(client.player.getYaw());
        String direction = cardinal(yaw);
        String line = direction + "  " + Math.round(yaw) + "°";
        int width = Math.max(84, client.textRenderer.getWidth(line) + 24);
        int x = screenWidth / 2 - width / 2;
        int y = 8;

        panel(context, x, y, width, 22, theme, accent);
        context.drawCenteredTextWithShadow(client.textRenderer, line, screenWidth / 2, y + 7, TEXT);
    }

    private static void drawKeystrokes(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent,
            int screenHeight
    ) {
        int x = 8;
        int y = screenHeight - 88;
        int key = 22;
        int gap = 3;

        drawKey(context, client, "W", x + key + gap, y, key, key, client.options.forwardKey.isPressed(), theme, accent);
        drawKey(context, client, "A", x, y + key + gap, key, key, client.options.leftKey.isPressed(), theme, accent);
        drawKey(context, client, "S", x + key + gap, y + key + gap, key, key, client.options.backKey.isPressed(), theme, accent);
        drawKey(context, client, "D", x + (key + gap) * 2, y + key + gap, key, key, client.options.rightKey.isPressed(), theme, accent);

        drawKey(context, client, "SPACE", x, y + (key + gap) * 2, key * 2 + gap, 17,
                client.options.jumpKey.isPressed(), theme, accent);
        drawKey(context, client, "SHIFT", x + key * 2 + gap * 2, y + (key + gap) * 2, key * 2 + gap, 17,
                client.options.sneakKey.isPressed(), theme, accent);

        drawKey(context, client, "LMB", x + (key + gap) * 3, y, 33, key,
                client.options.attackKey.isPressed(), theme, accent);
        drawKey(context, client, "RMB", x + (key + gap) * 3, y + key + gap, 33, key,
                client.options.useKey.isPressed(), theme, accent);
    }

    private static void drawKey(
            DrawContext context,
            MinecraftClient client,
            String label,
            int x,
            int y,
            int width,
            int height,
            boolean pressed,
            Theme theme,
            int accent
    ) {
        int background = pressed
                ? ColorUtil.withAlpha(accent, 205)
                : ColorUtil.withAlpha(theme.background(), AstraVisualsClient.CONFIG.panelOpacity);

        context.fill(x + 1, y + 2, x + width + 1, y + height + 2, SHADOW);
        context.fill(x, y, x + width, y + height, background);
        context.fill(x, y, x + width, y + 1, pressed ? TEXT : ColorUtil.withAlpha(accent, 170));
        context.drawCenteredTextWithShadow(
                client.textRenderer,
                label,
                x + width / 2,
                y + (height - 8) / 2,
                pressed ? 0xFF101014 : TEXT
        );
    }

    private static void drawArmor(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent,
            int screenWidth,
            int screenHeight
    ) {
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        int itemSize = 20;
        int width = slots.length * itemSize + 12;
        int x = screenWidth - width - 8;
        int y = screenHeight - 35;

        panel(context, x, y, width, 27, theme, accent);

        for (int index = 0; index < slots.length; index++) {
            ItemStack stack = client.player.getEquippedStack(slots[index]);
            int itemX = x + 7 + index * itemSize;

            if (!stack.isEmpty()) {
                context.drawItem(stack, itemX, y + 5);
                context.drawStackOverlay(client.textRenderer, stack, itemX, y + 5);

                if (stack.isDamageable()) {
                    int remaining = stack.getMaxDamage() - stack.getDamage();
                    int percent = Math.round(remaining / (float) stack.getMaxDamage() * 100.0F);
                    int barWidth = Math.max(1, Math.round(16.0F * percent / 100.0F));
                    int barColor = percent > 55 ? 0xFF5FE18A : percent > 25 ? 0xFFFFC857 : 0xFFFF5B72;
                    context.fill(itemX, y + 23, itemX + 16, y + 25, 0x90000000);
                    context.fill(itemX, y + 23, itemX + barWidth, y + 25, barColor);
                }
            }
        }
    }

    private static void drawEffects(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent,
            int screenWidth
    ) {
        if (client.player.getStatusEffects().isEmpty()) {
            return;
        }

        int widest = 0;
        int count = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (count++ >= 6) {
                break;
            }
            widest = Math.max(widest, client.textRenderer.getWidth(effectLine(effect)));
        }

        int width = widest + 18;
        int height = 12 + Math.min(6, client.player.getStatusEffects().size()) * 11;
        int x = screenWidth - width - 8;
        int y = 40;

        panel(context, x, y, width, height, theme, accent);

        count = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (count >= 6) {
                break;
            }
            context.drawTextWithShadow(
                    client.textRenderer,
                    effectLine(effect),
                    x + 9,
                    y + 7 + count * 11,
                    count == 0 ? TEXT : MUTED_TEXT
            );
            count++;
        }
    }

    private static void drawTargetHud(
            DrawContext context,
            MinecraftClient client,
            Theme theme,
            int accent,
            int screenWidth,
            int screenHeight
    ) {
        if (!(client.targetedEntity instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        String name = target.getName().getString();
        String details = String.format(Locale.ROOT, "%.1f / %.1f HP   %.1f m",
                target.getHealth(),
                target.getMaxHealth(),
                client.player.distanceTo(target));

        int width = Math.max(146, Math.max(
                client.textRenderer.getWidth(name),
                client.textRenderer.getWidth(details)
        ) + 24);
        int height = 43;
        int x = screenWidth / 2 - width / 2;
        int y = screenHeight / 2 + 28;

        panel(context, x, y, width, height, theme, accent);
        context.drawTextWithShadow(client.textRenderer, name, x + 9, y + 7, TEXT);
        context.drawTextWithShadow(client.textRenderer, details, x + 9, y + 18, MUTED_TEXT);

        float healthRatio = target.getMaxHealth() <= 0.0F
                ? 0.0F
                : Math.max(0.0F, Math.min(1.0F, target.getHealth() / target.getMaxHealth()));

        context.fill(x + 9, y + 32, x + width - 9, y + 36, 0x90000000);
        context.fill(x + 9, y + 32, x + 9 + Math.round((width - 18) * healthRatio), y + 36, accent);
    }

    private static void drawTargetIndicator(
            DrawContext context,
            MinecraftClient client,
            int accent,
            int screenWidth,
            int screenHeight
    ) {
        if (client.targetedEntity == null) {
            return;
        }

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int color = ColorUtil.withAlpha(accent, 225);

        context.fill(centerX - 8, centerY - 1, centerX - 4, centerY + 1, color);
        context.fill(centerX + 4, centerY - 1, centerX + 8, centerY + 1, color);
        context.fill(centerX - 1, centerY - 8, centerX + 1, centerY - 4, color);
        context.fill(centerX - 1, centerY + 4, centerX + 1, centerY + 8, color);
    }

    private static void drawLowHealthVignette(
            DrawContext context,
            MinecraftClient client,
            int width,
            int height
    ) {
        float max = client.player.getMaxHealth();
        if (max <= 0.0F) {
            return;
        }

        float ratio = client.player.getHealth() / max;
        if (ratio >= 0.35F) {
            return;
        }

        float severity = 1.0F - ratio / 0.35F;
        int alpha = Math.min(125, 35 + Math.round(severity * 90.0F));
        int color = alpha << 24 | 0x00D82035;
        int thickness = 4 + Math.round(severity * 7.0F);

        context.fill(0, 0, width, thickness, color);
        context.fill(0, height - thickness, width, height, color);
        context.fill(0, thickness, thickness, height - thickness, color);
        context.fill(width - thickness, thickness, width, height - thickness, color);
    }

    private static void panel(
            DrawContext context,
            int x,
            int y,
            int width,
            int height,
            Theme theme,
            int accent
    ) {
        int opacity = AstraVisualsClient.CONFIG.panelOpacity;
        int background = ColorUtil.withAlpha(theme.background(), opacity);

        context.fill(x + 2, y + 3, x + width + 2, y + height + 3, SHADOW);
        context.fill(x, y, x + width, y + height, background);
        context.fill(x, y, x + 2, y + height, ColorUtil.withAlpha(accent, 235));
        context.fill(x + 2, y, x + width, y + 1, ColorUtil.withAlpha(accent, 95));
    }

    private static int getPing(MinecraftClient client) {
        if (client.getNetworkHandler() == null || client.player == null) {
            return -1;
        }

        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        return entry == null ? -1 : entry.getLatency();
    }

    private static String effectLine(StatusEffectInstance effect) {
        String name = Text.translatable(effect.getTranslationKey()).getString();
        int amplifier = effect.getAmplifier() + 1;
        String level = amplifier > 1 ? " " + amplifier : "";
        return name + level + "  " + formatDuration(effect.getDuration());
    }

    private static String formatDuration(int ticks) {
        if (ticks < 0) {
            return "∞";
        }

        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
    }

    private static float wrapDegrees(float degrees) {
        float wrapped = degrees % 360.0F;
        if (wrapped < 0.0F) {
            wrapped += 360.0F;
        }
        return wrapped;
    }

    private static String cardinal(float yaw) {
        int index = Math.round(yaw / 45.0F) & 7;
        return switch (index) {
            case 0 -> "S";
            case 1 -> "SW";
            case 2 -> "W";
            case 3 -> "NW";
            case 4 -> "N";
            case 5 -> "NE";
            case 6 -> "E";
            default -> "SE";
        };
    }
}
