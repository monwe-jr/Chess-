package com.chess.game;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * A single square on the board. Owns its fixed array coordinates (independent
 * of the board's visual orientation), paints its base color plus at most one
 * highlight overlay, and draws the piece image scaled to whatever size the
 * square currently is -- so resizing the window rescales pieces instead of
 * leaving them a fixed pixel size inside a bigger or smaller square.
 */
class SquarePanel extends JPanel {

    enum Highlight { NONE, SELECTED, LEGAL_MOVE, CAPTURE, LAST_MOVE, CHECK }

    final int arrayX;
    final int arrayY;
    private final Color baseColor;
    private Highlight highlight = Highlight.NONE;
    private Image pieceImage;

    SquarePanel(int arrayX, int arrayY, Color baseColor) {
        this.arrayX = arrayX;
        this.arrayY = arrayY;
        this.baseColor = baseColor;
        setPreferredSize(new Dimension(80, 80));
        setOpaque(false);
    }

    void setPieceImage(Image image) {
        this.pieceImage = image;
        repaint();
    }

    void setHighlight(Highlight highlight) {
        this.highlight = highlight;
        repaint();
    }

    Highlight getHighlight() {
        return highlight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(baseColor);
        g2.fillRect(0, 0, w, h);

        switch (highlight) {
            case LAST_MOVE -> {
                g2.setColor(new Color(246, 234, 122, 140));
                g2.fillRect(0, 0, w, h);
            }
            case CHECK -> {
                g2.setColor(new Color(220, 60, 60, 170));
                g2.fillRect(0, 0, w, h);
            }
            case SELECTED -> {
                g2.setColor(new Color(255, 205, 60, 160));
                g2.fillRect(0, 0, w, h);
            }
            case CAPTURE -> {
                g2.setColor(new Color(200, 55, 55, 190));
                g2.setStroke(new BasicStroke(Math.max(3f, w * 0.07f)));
                int inset = (int) (w * 0.09);
                g2.drawOval(inset, inset, w - 2 * inset, h - 2 * inset);
            }
            case LEGAL_MOVE -> {
                g2.setColor(new Color(40, 40, 40, 90));
                int d = (int) (w * 0.28);
                g2.fillOval((w - d) / 2, (h - d) / 2, d, d);
            }
            case NONE -> {
            }
        }

        if (pieceImage != null) {
            int margin = (int) (w * 0.08);
            g2.drawImage(pieceImage, margin, margin, w - 2 * margin, h - 2 * margin, this);
        }
        g2.dispose();
    }
}
