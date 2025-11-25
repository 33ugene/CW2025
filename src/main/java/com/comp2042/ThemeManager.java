package com.comp2042;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;


public class ThemeManager {
    public enum Theme {
        CLASSIC, DARK, NEON, PASTEL
    }

    private Theme currentTheme = Theme.CLASSIC;
    private Map<Theme, Map<Integer, Color>> themeColors = new HashMap<>();

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

        // DARK THEME
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

        // NEON THEME
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

        // PASTEL THEME
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
    }
    public Color getColor(int brickType, Theme theme) {
        return themeColors.get(theme).get(brickType);
    }

    public Color getCurrentColor(int brickType) {
        return themeColors.get(currentTheme).get(brickType);
    }

    public void setTheme(Theme theme) {
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

