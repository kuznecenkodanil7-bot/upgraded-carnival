package dev.astra.visuals.theme;

import java.util.Locale;

public enum Theme {
    NEON("Neon", 0xFF9B5CFF, 0xFF25D9FF, 0xFF0C1019),
    AURORA("Aurora", 0xFF55EFC4, 0xFF74B9FF, 0xFF091512),
    CRIMSON("Crimson", 0xFFFF5B72, 0xFFFFA552, 0xFF170A0D),
    MONO("Mono", 0xFFF1F1F1, 0xFF9B9B9B, 0xFF111111);

    private final String displayName;
    private final int primary;
    private final int secondary;
    private final int background;

    Theme(String displayName, int primary, int secondary, int background) {
        this.displayName = displayName;
        this.primary = primary;
        this.secondary = secondary;
        this.background = background;
    }

    public String displayName() {
        return displayName;
    }

    public int primary() {
        return primary;
    }

    public int secondary() {
        return secondary;
    }

    public int background() {
        return background;
    }

    public Theme next() {
        Theme[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static Theme fromName(String name) {
        if (name == null) {
            return NEON;
        }

        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return NEON;
        }
    }
}
