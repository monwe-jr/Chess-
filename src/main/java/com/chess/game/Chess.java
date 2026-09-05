package com.chess.game;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public class Chess {

    private static final String CARD_MENU = "menu";
    private static final String CARD_DIFFICULTY = "difficulty";
    private static final String CARD_COLOR = "color";
    private static final String CARD_GAME = "game";

    private final JFrame frame = new JFrame("Chess");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    private int selectedDepth;
    Board board;

    Chess() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 820);
        frame.setMinimumSize(new Dimension(760, 600));
        frame.setLocationRelativeTo(null);

        root.add(buildMenuCard(), CARD_MENU);
        root.add(buildDifficultyCard(), CARD_DIFFICULTY);
        root.add(buildColorCard(), CARD_COLOR);

        frame.add(root);
        showCard(CARD_MENU);
        frame.setVisible(true);
    }

    void showCard(String name) {
        cardLayout.show(root, name);
    }

    JFrame getFrame() {
        return frame;
    }

    // --- menu screens ---

    private JPanel buildMenuCard() {
        JPanel content = menuContent();
        content.add(title("Chess"));
        content.add(Box.createVerticalStrut(36));

        JButton vsAi = menuButton("Human vs AI");
        vsAi.addActionListener(e -> showCard(CARD_DIFFICULTY));
        JButton vsHuman = menuButton("Human vs Human");
        vsHuman.addActionListener(e -> startGame("Human", 0, 'w'));

        content.add(vsAi);
        content.add(Box.createVerticalStrut(14));
        content.add(vsHuman);
        return centeredCard(content);
    }

    private JPanel buildDifficultyCard() {
        JPanel content = menuContent();
        content.add(title("Choose a difficulty"));
        content.add(Box.createVerticalStrut(36));

        content.add(menuButtonAction("Easy", () -> {
            selectedDepth = 1;
            showCard(CARD_COLOR);
        }));
        content.add(Box.createVerticalStrut(14));
        content.add(menuButtonAction("Normal", () -> {
            selectedDepth = 3;
            showCard(CARD_COLOR);
        }));
        content.add(Box.createVerticalStrut(14));
        content.add(menuButtonAction("Hard", () -> {
            selectedDepth = 4;
            showCard(CARD_COLOR);
        }));
        return centeredCard(content);
    }

    private JPanel buildColorCard() {
        JPanel content = menuContent();
        content.add(title("Play as..."));
        content.add(Box.createVerticalStrut(36));

        content.add(menuButtonAction("White", () -> startGame("AI", selectedDepth, 'w')));
        content.add(Box.createVerticalStrut(14));
        content.add(menuButtonAction("Black", () -> startGame("AI", selectedDepth, 'b')));
        return centeredCard(content);
    }

    void startGame(String mode, int depth, char humanColor) {
        board = new Board(mode, depth, humanColor);
        board.setOnRestart(this::returnToMenu);
        board.setOnExit(() -> System.exit(0));
        root.add(board, CARD_GAME);
        showCard(CARD_GAME);
    }

    private void returnToMenu() {
        if (board != null) {
            root.remove(board);
            board = null;
        }
        showCard(CARD_MENU);
    }

    // --- shared styling helpers ---

    private JPanel menuContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UiTheme.PANEL_BG);
        content.setBorder(new EmptyBorder(60, 60, 60, 60));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        return content;
    }

    /** Wraps menu content in a GridBagLayout so it stays centered as the window resizes. */
    private JPanel centeredCard(JPanel content) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiTheme.PANEL_BG);
        card.add(content);
        return card;
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(UiTheme.TITLE_FONT);
        label.setForeground(UiTheme.ACCENT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton menuButton(String text) {
        JButton button = Board.styledButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(260, 48));
        button.setPreferredSize(new Dimension(260, 48));
        return button;
    }

    private JButton menuButtonAction(String text, Runnable action) {
        JButton button = menuButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Chess::new);
    }
}
