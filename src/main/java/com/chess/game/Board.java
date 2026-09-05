package com.chess.game;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BooleanSupplier;

/**
 * 3p71 Term Project: Chess
 *
 * Francis Monwe
 * Jashandeep Pannu
 *
 * The game panel: renders the board and side panel (turn indicator, captured
 * pieces, move log), turns clicks into moves via {@link Piece}, and drives
 * the AI opponent via {@link AI} in Human vs AI mode.
 *
 * Click handling is one small state machine driven by {@link #sideToMove}
 * rather than by which mode is active: a first click on a piece belonging to
 * {@code sideToMove} selects it and highlights its legal destinations
 * (computed via {@link Piece#moveWhitePiece}/{@link Piece#moveBlackPiece} in
 * "check only" mode); a second click either plays the move if it's a
 * highlighted destination, reselects if it's another of the side's own
 * pieces, or is otherwise ignored. This same state machine plays both human
 * moves (from mouse clicks) and AI moves (from {@link #AIMove()}), which is
 * what makes supporting either side being human or AI-controlled
 * straightforward: {@link #humanColor} only gates *whose clicks are
 * accepted*, not how a move is validated or applied.
 */
public class Board extends JPanel implements MouseListener {

    // --- visual palette ---
    private static final Color LIGHT_SQUARE = UiTheme.LIGHT_SQUARE;
    private static final Color DARK_SQUARE = UiTheme.DARK_SQUARE;
    private static final Color PANEL_BG = UiTheme.PANEL_BG;
    private static final Color PANEL_FG = UiTheme.PANEL_FG;
    private static final Color ACCENT = UiTheme.ACCENT;
    private static final Color BOARD_FRAME_BG = UiTheme.BOARD_FRAME_BG;
    private static final Font UI_FONT = UiTheme.UI_FONT;
    private static final Font UI_FONT_BOLD = UiTheme.UI_FONT_BOLD;

    private static final String CARD_GAMEPLAY = "gameplay";
    private static final String CARD_PROMOTION = "promotion";
    private static final String CARD_GAMEOVER = "gameover";

    AI bot;
    public String mode;              // "Human" or "AI"
    public int depth;                // minimax search depth (AI difficulty)
    private final char humanColor;   // side the human plays in "AI" mode; irrelevant in "Human" mode
    private final char aiColor;      // opposite of humanColor, only used in "AI" mode
    private final boolean flipped;   // true when the human plays Black: render Black at the bottom

    char sideToMove = 'w';           // chess always starts with White to move
    private boolean gameOver;
    private boolean input = true;    // false while a promotion choice is pending

    private Point pos1;              // currently selected square, or null
    private List<Point> legalMoves = new ArrayList<>();
    private List<Point> legalCaptures = new ArrayList<>();
    private Point lastMoveFrom;
    private Point lastMoveTo;
    private Point wKing;
    private Point bKing;
    private Point pendingPromotion;

    private final Piece pieces = new Piece();
    final String[][] board = pieces.board;
    final SquarePanel[][] squares = new SquarePanel[8][8];
    private final Random random = new Random();

    private final CardLayout rootLayout = new CardLayout();
    private JLabel turnLabel;
    private JPanel capturedByWhiteRow;
    private JPanel capturedByBlackRow;
    private DefaultListModel<String> moveLogModel;
    private int fullMoveNumber = 1;
    private final List<String> capturedByWhite = new ArrayList<>();
    private final List<String> capturedByBlack = new ArrayList<>();

    private JPanel gameOverPanel;
    private JLabel gameOverMessage;
    private JPanel promotionPanel;
    private JLabel promotionPrompt;

    private Runnable onRestart = () -> {
    };
    private Runnable onExit = () -> System.exit(0);
    private BooleanSupplier restartConfirmationPrompt = this::confirmRestartWithDialog;

    private final ImageIcon pawnW = loadIcon("pawnW.png");
    private final ImageIcon pawnB = loadIcon("pawnB.png");
    private final ImageIcon rookW = loadIcon("rookW.png");
    private final ImageIcon rookB = loadIcon("rookB.png");
    private final ImageIcon knightW = loadIcon("knightW.png");
    private final ImageIcon knightB = loadIcon("knightB.png");
    private final ImageIcon bishopW = loadIcon("bishopW.png");
    private final ImageIcon bishopB = loadIcon("bishopB.png");
    private final ImageIcon queenW = loadIcon("queenW.png");
    private final ImageIcon queenB = loadIcon("queenB.png");
    private final ImageIcon kingW = loadIcon("kingW.png");
    private final ImageIcon kingB = loadIcon("kingB.png");

    /**
     * Loads a piece image bundled on the classpath under {@code /images/}
     * (packaged into the jar via {@code src/main/resources/images}), rather
     * than a relative file path, so the game runs the same whether launched
     * from an IDE, a random working directory, or a double-clicked jar.
     */
    private static ImageIcon loadIcon(String name) {
        return new ImageIcon(Objects.requireNonNull(Board.class.getResource("/images/" + name),
                "Missing bundled resource: /images/" + name));
    }

    /** Human vs Human: both sides are human-controlled, board is never flipped. */
    public Board(String mode) {
        this(mode, 0, 'w');
    }

    /** Human vs AI: {@code humanColor} is the side the human plays. */
    public Board(String mode, int depth, char humanColor) {
        this.mode = mode;
        this.depth = depth;
        this.humanColor = humanColor;
        this.aiColor = humanColor == 'w' ? 'b' : 'w';
        this.flipped = mode.equals("AI") && humanColor == 'b';
        if (mode.equals("AI")) {
            bot = new AI();
        }

        buildUi();
        updateGui();

        if (mode.equals("AI") && sideToMove == aiColor) {
            AIMove();
        }
    }

    void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    // --- UI construction ---

    private void buildUi() {
        setLayout(rootLayout);
        setBackground(PANEL_BG);

        JPanel gameplay = new JPanel(new BorderLayout(12, 12));
        gameplay.setBackground(PANEL_BG);
        gameplay.setBorder(new EmptyBorder(12, 12, 12, 12));
        gameplay.add(buildBoardWithCoordinates(), BorderLayout.CENTER);
        gameplay.add(buildSidePanel(), BorderLayout.EAST);

        add(gameplay, CARD_GAMEPLAY);
        add(buildPromotionCard(), CARD_PROMOTION);
        add(buildGameOverCard(), CARD_GAMEOVER);
        rootLayout.show(this, CARD_GAMEPLAY);
    }

    private JPanel buildBoardWithCoordinates() {
        JPanel grid = new JPanel(new GridLayout(8, 8));
        grid.setBackground(BOARD_FRAME_BG);

        for (int screenRow = 0; screenRow < 8; screenRow++) {
            for (int screenCol = 0; screenCol < 8; screenCol++) {
                int x = flipped ? 7 - screenCol : screenCol;
                int y = flipped ? screenRow : 7 - screenRow;
                Color base = (x + y) % 2 == 0 ? DARK_SQUARE : LIGHT_SQUARE;
                SquarePanel square = new SquarePanel(x, y, base);
                square.addMouseListener(this);
                squares[x][y] = square;
                grid.add(square);
            }
        }

        JPanel framed = new JPanel(new BorderLayout());
        framed.setBackground(BOARD_FRAME_BG);
        framed.setBorder(new EmptyBorder(6, 6, 6, 6));
        framed.add(fileLabels(), BorderLayout.NORTH);
        framed.add(fileLabels(), BorderLayout.SOUTH);
        framed.add(rankLabels(), BorderLayout.WEST);
        framed.add(rankLabels(), BorderLayout.EAST);
        framed.add(grid, BorderLayout.CENTER);
        return framed;
    }

    private JPanel fileLabels() {
        JPanel row = new JPanel(new GridLayout(1, 8));
        row.setBackground(BOARD_FRAME_BG);
        for (int i = 0; i < 8; i++) {
            int file = flipped ? 7 - i : i;
            row.add(coordinateLabel(String.valueOf((char) ('a' + file))));
        }
        return row;
    }

    private JPanel rankLabels() {
        JPanel col = new JPanel(new GridLayout(8, 1));
        col.setBackground(BOARD_FRAME_BG);
        for (int i = 0; i < 8; i++) {
            int rank = flipped ? i + 1 : 8 - i;
            col.add(coordinateLabel(String.valueOf(rank)));
        }
        return col;
    }

    private JLabel coordinateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(ACCENT);
        label.setFont(UI_FONT);
        label.setBorder(new EmptyBorder(2, 4, 2, 4));
        return label;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(PANEL_BG);
        side.setBorder(new EmptyBorder(4, 12, 4, 4));
        side.setPreferredSize(new Dimension(240, 0));

        JButton menuButton = buildQuickMenuButton();
        menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(menuButton);
        side.add(Box.createVerticalStrut(16));

        turnLabel = new JLabel();
        turnLabel.setFont(UI_FONT_BOLD);
        turnLabel.setForeground(PANEL_FG);
        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateTurnLabel();

        JLabel capturedByWhiteTitle = sectionTitle("Captured by White");
        capturedByWhiteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        capturedByWhiteRow.setBackground(PANEL_BG);
        capturedByWhiteRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel capturedByBlackTitle = sectionTitle("Captured by Black");
        capturedByBlackRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        capturedByBlackRow.setBackground(PANEL_BG);
        capturedByBlackRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel moveLogTitle = sectionTitle("Moves");
        moveLogModel = new DefaultListModel<>();
        JList<String> moveLogList = new JList<>(moveLogModel);
        moveLogList.setFont(UI_FONT);
        moveLogList.setBackground(new Color(0x22, 0x22, 0x22));
        moveLogList.setForeground(PANEL_FG);
        JScrollPane moveLogScroll = new JScrollPane(moveLogList);
        moveLogScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        moveLogScroll.setBorder(BorderFactory.createLineBorder(ACCENT.darker()));

        side.add(turnLabel);
        side.add(Box.createVerticalStrut(16));
        side.add(capturedByWhiteTitle);
        side.add(capturedByWhiteRow);
        side.add(Box.createVerticalStrut(10));
        side.add(capturedByBlackTitle);
        side.add(capturedByBlackRow);
        side.add(Box.createVerticalStrut(16));
        side.add(moveLogTitle);
        side.add(moveLogScroll);
        return side;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UI_FONT_BOLD);
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildPromotionCard() {
        promotionPanel = new JPanel();
        promotionPanel.setLayout(new BoxLayout(promotionPanel, BoxLayout.Y_AXIS));
        promotionPanel.setBackground(PANEL_BG);
        promotionPrompt = new JLabel("Choose a promotion", SwingConstants.CENTER);
        promotionPrompt.setFont(UI_FONT_BOLD.deriveFont(20f));
        promotionPrompt.setForeground(PANEL_FG);
        promotionPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        promotionPanel.add(Box.createVerticalGlue());
        promotionPanel.add(promotionPrompt);
        promotionPanel.add(Box.createVerticalStrut(20));
        promotionPanel.add(Box.createVerticalGlue());
        return promotionPanel;
    }

    private JPanel buildGameOverCard() {
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.setBackground(PANEL_BG);

        gameOverMessage = new JLabel("", SwingConstants.CENTER);
        gameOverMessage.setFont(UI_FONT_BOLD.deriveFont(28f));
        gameOverMessage.setForeground(PANEL_FG);
        gameOverMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton resetButton = styledButton("New Game");
        resetButton.addActionListener(e -> onRestart.run());
        JButton exitButton = styledButton("Exit");
        exitButton.addActionListener(e -> onExit.run());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttons.setBackground(PANEL_BG);
        buttons.add(resetButton);
        buttons.add(exitButton);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        gameOverPanel.add(Box.createVerticalGlue());
        gameOverPanel.add(gameOverMessage);
        gameOverPanel.add(Box.createVerticalStrut(24));
        gameOverPanel.add(buttons);
        gameOverPanel.add(Box.createVerticalGlue());
        return gameOverPanel;
    }

    /**
     * A small "Menu" escape hatch, reachable during active gameplay and
     * during a pending promotion choice -- not just from the game-over card.
     * Always available regardless of whose turn it is or what's mid-flight
     * (a selected-but-unplayed square, the AI "thinking", a promotion
     * choice): it asks for confirmation, then defers entirely to the same
     * {@link #onRestart} callback the game-over "New Game" button uses,
     * which tears down this whole Board and returns to the main menu. There
     * is no separate thread or timer in this game to clean up first (the AI
     * search runs synchronously on the event thread), so nothing extra needs
     * to happen before handing off.
     */
    private JButton buildQuickMenuButton() {
        JButton button = new JButton("Menu");
        button.setFont(UI_FONT_BOLD);
        button.setBackground(ACCENT);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(4, 12, 4, 12));
        button.addActionListener(e -> confirmReturnToMenu());
        return button;
    }

    void confirmReturnToMenu() {
        if (restartConfirmationPrompt.getAsBoolean()) {
            onRestart.run();
        }
    }

    private boolean confirmRestartWithDialog() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Restart game and return to the main menu? The current game will be lost.",
                "Restart game?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }

    static JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UI_FONT_BOLD);
        button.setBackground(ACCENT);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 22, 10, 22));
        return button;
    }

    // --- rendering ---

    private void updateGui() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                String piece = board[x][y];
                squares[x][y].setPieceImage(piece.equals("  ") ? null : iconFor(piece).getImage());
                if (piece.equals("wK")) wKing = new Point(x, y);
                if (piece.equals("bK")) bKing = new Point(x, y);
            }
        }
        refreshHighlights();
        updateTurnLabel();
    }

    private ImageIcon iconFor(String code) {
        return switch (code) {
            case "wP" -> pawnW;
            case "bP" -> pawnB;
            case "wR" -> rookW;
            case "bR" -> rookB;
            case "wN" -> knightW;
            case "bN" -> knightB;
            case "wB" -> bishopW;
            case "bB" -> bishopB;
            case "wQ" -> queenW;
            case "bQ" -> queenB;
            case "wK" -> kingW;
            case "bK" -> kingB;
            default -> throw new IllegalArgumentException("Unknown piece code: " + code);
        };
    }

    private void refreshHighlights() {
        for (SquarePanel[] column : squares) {
            for (SquarePanel square : column) {
                square.setHighlight(SquarePanel.Highlight.NONE);
            }
        }
        if (lastMoveFrom != null) squares[lastMoveFrom.x][lastMoveFrom.y].setHighlight(SquarePanel.Highlight.LAST_MOVE);
        if (lastMoveTo != null) squares[lastMoveTo.x][lastMoveTo.y].setHighlight(SquarePanel.Highlight.LAST_MOVE);
        if (pos1 != null) {
            squares[pos1.x][pos1.y].setHighlight(SquarePanel.Highlight.SELECTED);
            for (Point p : legalMoves) squares[p.x][p.y].setHighlight(SquarePanel.Highlight.LEGAL_MOVE);
            for (Point p : legalCaptures) squares[p.x][p.y].setHighlight(SquarePanel.Highlight.CAPTURE);
        }
        if (Piece.check(board, 'w') && wKing != null) squares[wKing.x][wKing.y].setHighlight(SquarePanel.Highlight.CHECK);
        if (Piece.check(board, 'b') && bKing != null) squares[bKing.x][bKing.y].setHighlight(SquarePanel.Highlight.CHECK);
    }

    private void updateTurnLabel() {
        if (turnLabel != null) {
            String side = sideToMove == 'w' ? "White" : "Black";
            turnLabel.setText((gameOver ? "Game over" : side + " to move"));
        }
    }

    // --- move rules glue ---

    private static boolean move(char color, Point from, Point to, String[][] board, boolean apply) {
        return color == 'w' ? Piece.moveWhitePiece(from, to, board, apply) : Piece.moveBlackPiece(from, to, board, apply);
    }

    private ArrayList<Point> validMoves(Point from) {
        ArrayList<Point> destinations = new ArrayList<>();
        char color = board[from.x][from.y].charAt(0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point to = new Point(j, i);
                if (move(color, from, to, board, false)) destinations.add(to);
            }
        }
        return destinations;
    }

    private List<Point> findCaptures(Point from, List<Point> candidateMoves) {
        char color = board[from.x][from.y].charAt(0);
        char enemy = color == 'w' ? 'b' : 'w';
        List<Point> captures = new ArrayList<>();
        for (Point p : candidateMoves) {
            if (board[p.x][p.y].charAt(0) == enemy) captures.add(p);
        }
        return captures;
    }

    // --- click handling ---

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver || !input || !(e.getComponent() instanceof SquarePanel square)) return;
        if (mode.equals("AI") && sideToMove == aiColor) return;

        Point clicked = new Point(square.arrayX, square.arrayY);
        if (pos1 == null) {
            trySelect(clicked);
        } else {
            tryMove(clicked);
        }
    }

    private void trySelect(Point p) {
        if (!board[p.x][p.y].equals("  ") && board[p.x][p.y].charAt(0) == sideToMove) {
            pos1 = p;
            legalMoves = validMoves(pos1);
            legalCaptures = findCaptures(pos1, legalMoves);
            refreshHighlights();
        }
    }

    private void tryMove(Point p) {
        if (legalMoves.contains(p)) {
            Point from = pos1;
            pos1 = null;
            legalMoves = new ArrayList<>();
            legalCaptures = new ArrayList<>();
            performMove(from, p);
        } else if (board[p.x][p.y].charAt(0) == sideToMove) {
            trySelect(p);
        }
    }

    // --- move execution ---

    private void performMove(Point from, Point to) {
        char mover = sideToMove;
        String movingPiece = board[from.x][from.y];
        boolean isCastle = movingPiece.charAt(1) == 'K' && Math.abs(to.x - from.x) == 2;
        boolean destinationWasEmpty = board[to.x][to.y].equals("  ");
        boolean isEnPassant = movingPiece.charAt(1) == 'P' && from.x != to.x && destinationWasEmpty;

        recordCapture(from, to, movingPiece, destinationWasEmpty, isEnPassant);
        move(mover, from, to, board, true);

        lastMoveFrom = from;
        lastMoveTo = to;
        sideToMove = mover == 'w' ? 'b' : 'w';

        boolean promoting = movingPiece.charAt(1) == 'P' && (to.y == 7 || to.y == 0);
        logMove(mover, from, to, movingPiece, !destinationWasEmpty || isEnPassant, isCastle, isEnPassant, promoting);

        updateGui();

        if (promoting) {
            pendingPromotion = to;
            input = false;
            if (mode.equals("Human") || mover == humanColor) {
                showPromotionChoices(to, mover);
            } else {
                autoPromote(to, mover);
            }
        } else {
            finishTurn();
        }
    }

    private void recordCapture(Point from, Point to, String movingPiece, boolean destinationWasEmpty, boolean isEnPassant) {
        String captured;
        if (isEnPassant) {
            captured = board[to.x][from.y];
        } else if (!destinationWasEmpty) {
            captured = board[to.x][to.y];
        } else {
            return;
        }
        if (captured.charAt(1) == 'K') {
            // A king can never legally be captured -- reaching this point means
            // Piece.checkmate() failed to flag mate on the previous ply and let
            // play continue into a position where "capturing" the king looked
            // like a legal move. Refuse the move outright rather than silently
            // removing a king from the board.
            String message = "Refusing illegal king capture: " + movingPiece + " " + squareName(from) + "-" + squareName(to)
                    + " would capture " + captured + ". This indicates a missed checkmate detection.";
            System.err.println(message);
            throw new IllegalStateException(message);
        }
        if (captured.charAt(0) == 'w') capturedByBlack.add(captured);
        else capturedByWhite.add(captured);
        refreshCapturedPanels();
    }

    private void refreshCapturedPanels() {
        capturedByWhiteRow.removeAll();
        for (String code : capturedByBlack) capturedByWhiteRow.add(smallIcon(code)); // pieces White captured are Black's
        capturedByBlackRow.removeAll();
        for (String code : capturedByWhite) capturedByBlackRow.add(smallIcon(code)); // pieces Black captured are White's
        capturedByWhiteRow.revalidate();
        capturedByWhiteRow.repaint();
        capturedByBlackRow.revalidate();
        capturedByBlackRow.repaint();
    }

    private JLabel smallIcon(String code) {
        Image scaled = iconFor(code).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    private String squareName(Point p) {
        return "" + (char) ('a' + p.x) + (p.y + 1);
    }

    private void logMove(char mover, Point from, Point to, String movingPiece, boolean captured, boolean isCastle, boolean isEnPassant, boolean promoting) {
        String entry;
        if (isCastle) {
            entry = to.x == 6 ? "O-O" : "O-O-O";
        } else {
            char type = movingPiece.charAt(1);
            String prefix = type == 'P' ? "" : String.valueOf(type);
            entry = prefix + squareName(from) + (captured ? "x" : "-") + squareName(to);
            if (isEnPassant) entry += " e.p.";
        }
        if (promoting) entry += "=?"; // resolved to the real piece once the choice is made, see updatePromotionLogEntry

        if (mover == 'w') {
            moveLogModel.addElement(fullMoveNumber + ". " + entry);
        } else {
            moveLogModel.addElement(fullMoveNumber + "... " + entry);
            fullMoveNumber++;
        }
    }

    private void updatePromotionLogEntry(char promotedTo) {
        int lastIndex = moveLogModel.size() - 1;
        if (lastIndex >= 0) {
            String entry = moveLogModel.get(lastIndex);
            moveLogModel.set(lastIndex, entry.replace("=?", "=" + promotedTo));
        }
    }

    private void finishTurn() {
        updateGui();
        if (Piece.checkmate(board, 'w') || Piece.checkmate(board, 'b')) {
            endGame(Piece.checkmate(board, 'w') ? "Black wins by checkmate!" : "White wins by checkmate!");
        } else if (Piece.draw(board)) {
            endGame("Draw!");
        } else if (mode.equals("AI") && sideToMove == aiColor) {
            AIMove();
        }
    }

    private void endGame(String message) {
        gameOver = true;
        updateTurnLabel();
        gameOverMessage.setText(message);
        rootLayout.show(this, CARD_GAMEOVER);
    }

    // --- promotion ---

    private void showPromotionChoices(Point p, char color) {
        promotionPanel.removeAll();
        promotionPanel.add(Box.createVerticalGlue());
        promotionPanel.add(promotionPrompt);
        promotionPanel.add(Box.createVerticalStrut(20));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttons.setBackground(PANEL_BG);
        for (char type : new char[]{'Q', 'R', 'B', 'N'}) {
            JButton button = new JButton(iconFor(color + String.valueOf(type)));
            button.setBackground(ACCENT);
            button.setFocusPainted(false);
            button.addActionListener(e -> resolvePromotion(p, color, type));
            buttons.add(button);
        }
        promotionPanel.add(buttons);
        promotionPanel.add(Box.createVerticalStrut(24));
        JButton menuButton = buildQuickMenuButton();
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        promotionPanel.add(menuButton);
        promotionPanel.add(Box.createVerticalGlue());
        promotionPanel.revalidate();
        promotionPanel.repaint();
        rootLayout.show(this, CARD_PROMOTION);
    }

    private void resolvePromotion(Point p, char color, char type) {
        board[p.x][p.y] = color + String.valueOf(type);
        updatePromotionLogEntry(type);
        input = true;
        rootLayout.show(this, CARD_GAMEPLAY);
        finishTurn();
    }

    private void autoPromote(Point p, char color) {
        char type;
        if (depth <= 2) {
            type = random.nextBoolean() ? 'B' : 'N';
        } else if (depth == 3) {
            type = random.nextBoolean() ? 'R' : 'Q';
        } else {
            type = 'Q';
        }
        board[p.x][p.y] = color + String.valueOf(type);
        updatePromotionLogEntry(type);
        input = true;
        finishTurn();
    }

    // --- AI ---

    private void AIMove() {
        if (gameOver) return;
        boolean aiMaximizes = aiColor == 'b';
        int[] result = bot.minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, aiMaximizes);
        Point from = decodeAiSquareIndex(result[0]);
        Point to = decodeAiSquareIndex(result[1]);
        performMove(from, to);
    }

    /**
     * Decodes a square index produced by {@link AI#minimax}. AI.java encodes
     * squares independently of this board's on-screen orientation (it has no
     * notion of {@link #flipped}), so this must always use the same fixed,
     * non-flipped scheme AI.java's own conversion uses, regardless of how the
     * human's side is displayed.
     */
    private Point decodeAiSquareIndex(int index) {
        int row = index / 8;
        int col = index % 8;
        return new Point(col, 7 - row);
    }

    // --- MouseListener plumbing ---

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // --- test-visible accessors ---

    char getHumanColor() {
        return humanColor;
    }

    char getAiColor() {
        return aiColor;
    }

    char getSideToMove() {
        return sideToMove;
    }

    boolean isFlipped() {
        return flipped;
    }

    boolean isGameOver() {
        return gameOver;
    }

    SquarePanel squareAt(int x, int y) {
        return squares[x][y];
    }

    /** Lets tests simulate answering the restart confirmation prompt without popping a real modal dialog. */
    void setRestartConfirmationPrompt(BooleanSupplier restartConfirmationPrompt) {
        this.restartConfirmationPrompt = restartConfirmationPrompt;
    }
}
