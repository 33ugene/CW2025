package com.comp2042;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;


public class ThemeManager {
    public enum Theme {
        CLASSIC, DARK, NEON, PASTEL, LIGHT
    }

    public static class ThemePalette {
        private final String rootBackgroundStyle;
        private final String boardFrameStyle;
        private final String boardGridStyle;
        private final String cardStyle;
        private final String controlsCardStyle;
        private final String miniGridStyle;
        private final String pauseOverlayStyle;
        private final String primaryButtonStyle;
        private final String secondaryButtonStyle;
        private final Color primaryText;
        private final Color secondaryText;
        private final Color accentText;

        public ThemePalette(String rootBackgroundStyle,
                            String boardFrameStyle,
                            String boardGridStyle,
                            String cardStyle,
                            String controlsCardStyle,
                            String miniGridStyle,
                            String pauseOverlayStyle,
                            String primaryButtonStyle,
                            String secondaryButtonStyle,
                            Color primaryText,
                            Color secondaryText,
                            Color accentText) {
            this.rootBackgroundStyle = rootBackgroundStyle;
            this.boardFrameStyle = boardFrameStyle;
            this.boardGridStyle = boardGridStyle;
            this.cardStyle = cardStyle;
            this.controlsCardStyle = controlsCardStyle;
            this.miniGridStyle = miniGridStyle;
            this.pauseOverlayStyle = pauseOverlayStyle;
            this.primaryButtonStyle = primaryButtonStyle;
            this.secondaryButtonStyle = secondaryButtonStyle;
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
            this.accentText = accentText;
        }

        public String getRootBackgroundStyle() {
            return rootBackgroundStyle;
        }

        public String getBoardFrameStyle() {
            return boardFrameStyle;
        }

        public String getBoardGridStyle() {
            return boardGridStyle;
        }

        public String getCardStyle() {
            return cardStyle;
        }

        public String getControlsCardStyle() {
            return controlsCardStyle;
        }

        public String getMiniGridStyle() {
            return miniGridStyle;
        }

        public String getPauseOverlayStyle() {
            return pauseOverlayStyle;
        }

        public String getPrimaryButtonStyle() {
            return primaryButtonStyle;
        }

        public String getSecondaryButtonStyle() {
            return secondaryButtonStyle;
        }

        public Color getPrimaryText() {
            return primaryText;
        }

        public Color getSecondaryText() {
            return secondaryText;
        }

        public Color getAccentText() {
            return accentText;
        }
    }

    private Theme currentTheme = Theme.DARK;
    private final Map<Theme, Map<Integer, Color>> themeColors = new HashMap<>();
    private final Map<Theme, ThemePalette> palettes = new HashMap<>();

    public ThemeManager() {
        initializeThemes();
    }

    private void initializeThemes() {
        Map<Integer, Color> classic = new HashMap<>();
        classic.put(0, Color.TRANSPARENT);
        classic.put(1, Color.AQUA);      // I-Brick
        classic.put(2, Color.BLUEVIOLET);// Z-Brick
        classic.put(3, Color.DARKGREEN); // T-Brick
        classic.put(4, Color.YELLOW);    // O-Brick
        classic.put(5, Color.RED);       // S-Brick
        classic.put(6, Color.BEIGE);     // L-Brick
        classic.put(7, Color.BURLYWOOD); // J-Brick
        themeColors.put(Theme.CLASSIC, classic);

        Map<Integer, Color> dark = new HashMap<>();
        dark.put(0, Color.TRANSPARENT);
        dark.put(1, Color.CYAN);
        dark.put(2, Color.PURPLE);
        dark.put(3, Color.LIMEGREEN);
        dark.put(4, Color.GOLD);
        dark.put(5, Color.ORANGERED);
        dark.put(6, Color.SILVER);
        dark.put(7, Color.ROYALBLUE);
        themeColors.put(Theme.DARK, dark);

        Map<Integer, Color> neon = new HashMap<>();
        neon.put(0, Color.TRANSPARENT);
        neon.put(1, Color.LIME);
        neon.put(2, Color.MAGENTA);
        neon.put(3, Color.CYAN);
        neon.put(4, Color.YELLOW);
        neon.put(5, Color.ORANGE);
        neon.put(6, Color.PURPLE);
        neon.put(7, Color.RED);
        themeColors.put(Theme.NEON, neon);

        Map<Integer, Color> pastel = new HashMap<>();
        pastel.put(0, Color.TRANSPARENT);
        pastel.put(1, Color.LIGHTBLUE);
        pastel.put(2, Color.LIGHTPINK);
        pastel.put(3, Color.LIGHTGREEN);
        pastel.put(4, Color.LIGHTYELLOW);
        pastel.put(5, Color.LIGHTCORAL);
        pastel.put(6, Color.PLUM);
        pastel.put(7, Color.LIGHTSALMON);
        themeColors.put(Theme.PASTEL, pastel);

        Map<Integer, Color> light = new HashMap<>();
        light.put(0, Color.TRANSPARENT);
        light.put(1, Color.web("#4C4CFF"));
        light.put(2, Color.web("#FF6B6B"));
        light.put(3, Color.web("#4CAF50"));
        light.put(4, Color.web("#FFEB3B"));
        light.put(5, Color.web("#FF9800"));
        light.put(6, Color.web("#795548"));
        light.put(7, Color.web("#2196F3"));
        themeColors.put(Theme.LIGHT, light);

        // Palettes
        palettes.put(Theme.DARK, new ThemePalette(
                "-fx-background-color: radial-gradient(radius 120%, #0f172a, #0b1120); -fx-padding: 30;",
                "-fx-background-color: rgba(15,23,42,0.85); -fx-background-radius: 18; -fx-border-color: rgba(255,255,255,0.12); -fx-border-width: 2; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, rgba(3,8,20,0.8), 25, 0, 0, 8);",
                "-fx-background-color: rgba(0,0,0,0.5);",
                "-fx-padding: 14; -fx-background-color: rgba(15,23,42,0.8); -fx-background-radius: 14; -fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, rgba(3,8,20,0.55), 18, 0.15, 0, 5);",
                "-fx-padding: 14; -fx-background-color: rgba(8,12,24,0.85); -fx-background-radius: 14; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1; -fx-border-radius: 14;",
                "-fx-padding: 6; -fx-background-color: rgba(8,12,24,0.65); -fx-background-radius: 10;",
                "-fx-background-color: rgba(2,6,23,0.88); -fx-padding: 35; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.65), 30, 0.4, 0, 6);",
                "-fx-background-color: linear-gradient(to right, #22d3ee, #3b82f6); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 36;",
                "-fx-background-color: transparent; -fx-border-color: rgba(248,250,252,0.6); -fx-border-width: 2; -fx-border-radius: 12; -fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-padding: 10 36;",
                Color.web("#f8fafc"),
                Color.web("#94a3b8"),
                Color.web("#34d399")
        ));

        palettes.put(Theme.NEON, new ThemePalette(
                "-fx-background-color: linear-gradient(to bottom, #050505, #111111); -fx-padding: 30;",
                "-fx-background-color: rgba(10,10,10,0.95); -fx-background-radius: 18; -fx-border-color: rgba(0,255,170,0.6); -fx-border-width: 2; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,255,255,0.45), 30, 0.35, 0, 0);",
                "-fx-background-color: rgba(0,0,0,0.8);",
                "-fx-padding: 14; -fx-background-color: rgba(5,8,15,0.9); -fx-background-radius: 14; -fx-border-color: rgba(0,255,234,0.4); -fx-border-width: 1.5; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, rgba(255,0,153,0.45), 20, 0.4, 0, 0);",
                "-fx-padding: 14; -fx-background-color: rgba(5,5,5,0.85); -fx-background-radius: 14; -fx-border-color: rgba(0,255,234,0.4); -fx-border-width: 1.5; -fx-border-radius: 14;",
                "-fx-padding: 6; -fx-background-color: rgba(5,5,5,0.9); -fx-background-radius: 10;",
                "-fx-background-color: rgba(5,5,5,0.92); -fx-padding: 35; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,255,170,0.45), 25, 0.5, 0, 0);",
                "-fx-background-color: linear-gradient(to right, #22d3ee, #e11d48); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 10 36; -fx-effect: dropshadow(gaussian, rgba(255,0,153,0.6), 20, 0.3, 0, 0);",
                "-fx-background-color: transparent; -fx-border-color: rgba(0,255,170,0.8); -fx-border-width: 2; -fx-border-radius: 999; -fx-text-fill: #00ffd1; -fx-font-weight: bold; -fx-padding: 10 36;",
                Color.web("#f8fafc"),
                Color.web("#00ffd1"),
                Color.web("#ff61f6")
        ));

        palettes.put(Theme.PASTEL, new ThemePalette(
                "-fx-background-color: linear-gradient(to bottom, #fdf2f8, #e0f2fe); -fx-padding: 30;",
                "-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 22; -fx-border-color: rgba(244,114,182,0.5); -fx-border-width: 2; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, rgba(244,114,182,0.3), 25, 0.3, 0, 10);",
                "-fx-background-color: rgba(255,255,255,0.9);",
                "-fx-padding: 16; -fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 18; -fx-border-color: rgba(236,72,153,0.2); -fx-border-width: 1; -fx-border-radius: 18;",
                "-fx-padding: 16; -fx-background-color: rgba(248,250,252,0.95); -fx-background-radius: 18; -fx-border-color: rgba(14,165,233,0.25); -fx-border-width: 1; -fx-border-radius: 18;",
                "-fx-padding: 6; -fx-background-color: rgba(248,250,252,0.9); -fx-background-radius: 12;",
                "-fx-background-color: rgba(255,255,255,0.92); -fx-padding: 35; -fx-background-radius: 26; -fx-effect: dropshadow(gaussian, rgba(244,114,182,0.35), 25, 0.3, 0, 10);",
                "-fx-background-color: linear-gradient(to right, #fb7185, #f0abfc); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 36;",
                "-fx-background-color: transparent; -fx-border-color: rgba(236,72,153,0.6); -fx-border-width: 2; -fx-border-radius: 18; -fx-text-fill: #ec4899; -fx-font-weight: bold; -fx-padding: 10 36;",
                Color.web("#1f2937"),
                Color.web("#4b5563"),
                Color.web("#ec4899")
        ));

        palettes.put(Theme.LIGHT, new ThemePalette(
                "-fx-background-color: linear-gradient(to bottom, #f5f5f5, #ffffff); -fx-padding: 30;",
                "-fx-background-color: white; -fx-background-radius: 18; -fx-border-color: rgba(15,23,42,0.15); -fx-border-width: 2; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.15), 25, 0.2, 0, 8);",
                "-fx-background-color: rgba(250,250,250,0.95);",
                "-fx-padding: 14; -fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 14; -fx-border-color: rgba(15,23,42,0.08); -fx-border-width: 1; -fx-border-radius: 14;",
                "-fx-padding: 14; -fx-background-color: rgba(248,250,252,0.95); -fx-background-radius: 14; -fx-border-color: rgba(15,23,42,0.05); -fx-border-width: 1; -fx-border-radius: 14;",
                "-fx-padding: 6; -fx-background-color: rgba(248,250,252,0.9); -fx-background-radius: 10;",
                "-fx-background-color: rgba(255,255,255,0.95); -fx-padding: 35; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.15), 25, 0.2, 0, 8);",
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 36;",
                "-fx-background-color: transparent; -fx-border-color: rgba(15,23,42,0.4); -fx-border-width: 2; -fx-border-radius: 10; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-padding: 10 36;",
                Color.web("#111827"),
                Color.web("#4b5563"),
                Color.web("#2563eb")
        ));

        palettes.put(Theme.CLASSIC, palettes.get(Theme.LIGHT));
    }

    public Color getColor(int brickType, Theme theme) {
        return themeColors.get(theme).get(brickType);
    }

    public Color getCurrentColor(int brickType) {
        return themeColors.get(currentTheme).get(brickType);
    }

    public ThemePalette getCurrentPalette() {
        return palettes.get(currentTheme);
    }

    public ThemePalette getPalette(Theme theme) {
        return palettes.get(theme);
    }

    public void setTheme(Theme theme) {
        if (theme == null) {
            return;
        }
        this.currentTheme = theme;
        System.out.println("🎨 Theme changed to: " + theme);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Theme[] getAvailableThemes() {
        return Theme.values();
    }
}

