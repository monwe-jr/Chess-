package com.chess.game;

import java.awt.Color;
import java.awt.Font;

/** Shared visual constants for the menu screens and the game panel. */
final class UiTheme {
    static final Color LIGHT_SQUARE = new Color(0xF0, 0xD9, 0xB5);
    static final Color DARK_SQUARE = new Color(0xB5, 0x88, 0x63);
    static final Color PANEL_BG = new Color(0x2B, 0x2B, 0x2B);
    static final Color PANEL_FG = new Color(0xEC, 0xEC, 0xEC);
    static final Color ACCENT = new Color(0xC9, 0xA0, 0x6B);
    static final Color BOARD_FRAME_BG = new Color(0x3A, 0x2E, 0x24);
    static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 15);
    static final Font UI_FONT_BOLD = new Font("SansSerif", Font.BOLD, 16);
    static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 30);

    private UiTheme() {
    }
}
