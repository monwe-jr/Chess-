package com.chess.game;

import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression coverage for a bug where maximizing the window (a single large
 * resize, as opposed to a gradual manual drag) left a visible gap between the
 * board and the side panel. {@link Board}'s gameplay screen uses a custom
 * {@code GameplayLayout} that always recomputes bounds from the container's
 * *current* {@code getWidth()}/{@code getHeight()} rather than a cached
 * value, so it must stay gap-free at every size a resize (including a jump
 * straight to a maximized, multi-monitor-scale size) can land on.
 */
class ResizeLayoutTest {

    private static final int MARGIN = 12;
    private static final int GAP = 12;

    @Test
    void boardAndSidePanelStayFlushAcrossOrdinaryAndMaximizeScaleSizes() {
        Board board = new Board("Human");
        Container gameplay = (Container) board.getComponent(0);
        Component framedBoard = gameplay.getComponent(0);
        Component side = gameplay.getComponent(1);

        int[][] sizesToCheck = {
                {1000, 820},   // the app's default startup size
                {1920, 1080},  // common single-monitor maximize
                {3840, 2160},  // 4K maximize
                {7680, 4320},  // 8K maximize
                {5120, 1440},  // ultrawide maximize
                {800, 3000},   // very tall and narrow
                {1921, 1081},  // odd dimensions, to catch integer-rounding gaps
        };

        for (int[] size : sizesToCheck) {
            int width = size[0];
            int height = size[1];
            board.setSize(width, height);
            gameplay.setSize(width, height);
            gameplay.doLayout();

            Rectangle boardBounds = framedBoard.getBounds();
            Rectangle sideBounds = side.getBounds();
            String at = "at " + width + "x" + height;

            assertEquals(boardBounds.width, boardBounds.height, "board must stay square " + at);
            assertEquals(boardBounds.x + boardBounds.width + GAP, sideBounds.x,
                    "side panel must be flush against the board with no gap " + at);
            assertEquals(width - MARGIN, sideBounds.x + sideBounds.width,
                    "side panel must absorb all remaining width up to the margin " + at);
            assertTrue(sideBounds.width > 0, "side panel must have positive width " + at);
        }
    }
}
