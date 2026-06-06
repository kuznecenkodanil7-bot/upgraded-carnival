package dev.astra.visuals.config;

public final class VisualConfig {
    public boolean enabled = true;
    public boolean watermark = true;
    public boolean stats = true;
    public boolean keystrokes = true;
    public boolean armor = true;
    public boolean effects = true;
    public boolean compass = true;
    public boolean targetHud = true;
    public boolean targetIndicator = true;
    public boolean lowHealthVignette = true;

    public String theme = "NEON";
    public int panelOpacity = 155;

    public void normalize() {
        panelOpacity = Math.max(70, Math.min(230, panelOpacity));
        if (theme == null || theme.isBlank()) {
            theme = "NEON";
        }
    }
}
