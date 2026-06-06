package dev.astra.visuals.theme;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static int withAlpha(int argb, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (argb & 0x00FFFFFF);
    }

    public static int lerp(int first, int second, float amount) {
        float t = Math.max(0.0F, Math.min(1.0F, amount));

        int a = mix((first >>> 24) & 0xFF, (second >>> 24) & 0xFF, t);
        int r = mix((first >>> 16) & 0xFF, (second >>> 16) & 0xFF, t);
        int g = mix((first >>> 8) & 0xFF, (second >>> 8) & 0xFF, t);
        int b = mix(first & 0xFF, second & 0xFF, t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int animatedAccent(Theme theme) {
        double wave = (Math.sin(System.currentTimeMillis() / 520.0D) + 1.0D) * 0.5D;
        return lerp(theme.primary(), theme.secondary(), (float) wave);
    }

    private static int mix(int first, int second, float amount) {
        return Math.round(first + (second - first) * amount);
    }
}
