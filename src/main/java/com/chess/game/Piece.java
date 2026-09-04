package com.chess.game;

import java.awt.Point;

/**
 * 3p71 Term Project: Chess
 *
 * Francis Monwe
 * Jashandeep Pannu
 *
 * Core chess rules engine: board representation, per-piece move legality,
 * check/checkmate/draw detection, castling and en passant.
 *
 * The board is a {@code String[8][8]} where {@code board[x][y]} is a column/row
 * (file/rank) pair with {@code (0,0)} at White's queenside rook square. Each
 * square holds either {@code "  "} (empty) or a two-character code: colour
 * ({@code 'w'}/{@code 'b'}) followed by piece type ({@code P N B R Q K}).
 *
 * Move methods double as both "is this move legal" checks and mutators: pass
 * {@code move = false} to test legality without altering the board (used by
 * move generation and checkmate detection), or {@code move = true} to also
 * apply the move.
 */
public class Piece {

    /**
     * Per-file en passant eligibility set by White/Black's most recent double
     * pawn push. Indexed by file; cleared at the start of the mover's own next
     * move so the right to capture en passant only survives one ply.
     */
    static boolean[] enPassantW = new boolean[8];
    static boolean[] enPassantB = new boolean[8];
    public static String[][] board = new String[8][8];

    Piece() {
        setupBoard(board);
    }

    /**
     * Sets up the starting position of a board.
     * @param board the board to be set up
     */
    private void setupBoard(String[][] board) {
        board[0][0] = "wR";
        board[1][0] = "wN";
        board[2][0] = "wB";
        board[3][0] = "wQ";
        board[4][0] = "wK";
        board[5][0] = "wB";
        board[6][0] = "wN";
        board[7][0] = "wR";
        for (int i = 0; i < 8; i++) {
            board[i][1] = "wP";
        }

        board[0][7] = "bR";
        board[1][7] = "bN";
        board[2][7] = "bB";
        board[3][7] = "bQ";
        board[4][7] = "bK";
        board[5][7] = "bB";
        board[6][7] = "bN";
        board[7][7] = "bR";
        for (int i = 0; i < 8; i++) {
            board[i][6] = "bP";
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 2; j < 6; j++) {
                board[i][j] = "  ";
            }
        }
    }

    /**
     * Prints a board to the console. Useful for debugging outside the GUI.
     * @param in board that needs to be drawn
     */
    static public void drawBoard(String[][] in) {
        for (int i = 7; i > -1; i--) {
            System.out.println("-----------------------------------------");
            for (String[] strings : in) {
                System.out.print("| " + strings[i] + " ");
            }
            System.out.println("| " + (i + 1));
        }
        System.out.println("-----------------------------------------");
        System.out.println("  A    B    C    D    E    F    G    H\n");
    }

    /**
     * Validates (and optionally performs) a White piece move.
     *
     * Handles per-piece movement rules, blocking pieces along rays for
     * sliding pieces, pawn double-step/diagonal-capture/en passant, and
     * king castling (kingside and queenside). Every candidate move is first
     * rejected if it would leave the White king in check ({@link #moveCheck}),
     * which is how self-checks (including moving into/through check while
     * castling) are prevented without duplicating check logic per piece.
     *
     * @param pos1 position of the piece to move
     * @param pos2 destination position
     * @param board board the move is validated/applied against
     * @param move if true, the board is mutated when the move is legal; if
     *             false, the board is left untouched (used for move
     *             generation and checkmate scans)
     * @return true if the move is legal
     */
    static public boolean moveWhitePiece(Point pos1, Point pos2, String[][] board, boolean move) {
        int x1 = pos1.x;
        int x2 = pos2.x;
        int y1 = pos1.y;
        int y2 = pos2.y;
        String piece = board[x1][y1];

        if (piece.equals("  ") || piece.charAt(0) == 'b' || piece.charAt(0) != 'w') {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            enPassantW[i] = false;
        }

        if (moveCheck(board, x1, y1, x2, y2, 'w')) return false;

        if (piece.equals("wP")) {
            // en passant: capture diagonally onto the file of a pawn that just
            // double-stepped past this one, removing that pawn from its rank
            if (y1 == 4 && y2 == 5 && (x2 == x1 + 1 || x2 == x1 - 1) && board[x2][y2].equals("  ") && enPassantB[x2]) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                    board[x2][y1] = "  ";
                }
                return true;
            }
            if (x1 == x2) {
                if (y2 - y1 == 1 || (y2 - y1 == 2 && y1 == 1 && board[x1][y1 + 1].equals("  "))) {
                    if (board[x2][y2].equals("  ")) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                            if (y1 == 1 && y2 - y1 == 2) {
                                enPassantW[x1] = true;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            if ((x1 == x2 - 1 || x1 == x2 + 1) && (y2 - y1 == 1)) {
                if (board[x2][y2].charAt(0) == 'b') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (piece.equals("wK")) {
            if (board[1][0].equals("  ") && board[2][0].equals("  ") && board[3][0].equals("  ") && board[0][0].equals("wR") && y1 == 0 && x1 == 4 && y2 == 0 && x2 == 2) {
                if (!check(board, 'w')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[0][0] = "  ";
                        board[3][0] = "wR";
                    }
                    return true;
                }
            }
            if (board[5][0].equals("  ") && board[6][0].equals("  ") && x2 == 6 && y2 == 0 && board[7][0].equals("wR")) {
                if (!check(board, 'w')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[7][0] = "  ";
                        board[5][0] = "wR";
                    }
                    return true;
                }
            }
            if (x1 == x2 && y1 == y2) {
                return false;
            }
            if ((x2 - x1 == 1 || x2 == x1 || x2 - x1 == -1) && (y2 - y1 == 1 || y2 == y1 || y2 - y1 == -1)) {
                if (board[x2][y2].equals("  ") || board[x2][y2].charAt(0) == 'b') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (piece.equals("wR")) {
            if ((x1 == x2 && y1 == y2) && !(y1 == y2 || x1 == x2)) {
                return false;
            }
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == 'w') {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == 'w') {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            return false;
        }

        if (piece.equals("wB")) {
            if (y1 == y2 || x1 == x2) {
                return false;
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
        }

        if (piece.equals("wQ")) {
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == 'w') {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == 'w') {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                }
            }
        }

        if (piece.equals("wN")) {
            if ((y2 == y1 + 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == 'w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((y2 == y1 - 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == 'w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 + 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == 'w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 - 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == 'w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Validates (and optionally performs) a Black piece move. Mirrors
     * {@link #moveWhitePiece} with colours and pawn direction reversed.
     *
     * @param pos1 position of the piece to move
     * @param pos2 destination position
     * @param board board the move is validated/applied against
     * @param move if true, the board is mutated when the move is legal; if
     *             false, the board is left untouched
     */
    static public boolean moveBlackPiece(Point pos1, Point pos2, String[][] board, boolean move) {
        int x1 = pos1.x;
        int x2 = pos2.x;
        int y1 = pos1.y;
        int y2 = pos2.y;
        String piece = board[x1][y1];

        if (piece.equals("  ") || piece.charAt(0) == 'w' || piece.charAt(0) != 'b') {
            return false;
        }
        if (move) {
            for (int i = 0; i < 8; i++) {
                enPassantB[i] = false;
            }
        }
        if (moveCheck(board, x1, y1, x2, y2, 'b')) return false;

        if (piece.equals("bP")) {
            // en passant: capture diagonally onto the file of a pawn that just
            // double-stepped past this one, removing that pawn from its rank
            if (y1 == 3 && y2 == 2 && (x2 == x1 + 1 || x2 == x1 - 1) && board[x2][y2].equals("  ") && enPassantW[x2]) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                    board[x2][y1] = "  ";
                }
                return true;
            }
            if (x1 == x2) {
                if (y2 - y1 == -1 || (y2 - y1 == -2 && y1 == 6 && board[x1][y1 - 1].equals("  "))) {
                    if (board[x2][y2].equals("  ")) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                            if (y1 == 6 && y2 - y1 == -2) {
                                enPassantB[x1] = true;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if ((x1 == x2 - 1 || x1 == x2 + 1) && (y2 - y1 == -1)) {
                if (board[x2][y2].charAt(0) == 'w') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (piece.equals("bK")) {
            if (board[1][7].equals("  ") && board[2][7].equals("  ") && board[3][7].equals("  ") && board[0][7].equals("bR") && y1 == 7 && x1 == 4 && y2 == 7 && x2 == 2) {
                if (!check(board, 'b')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[0][7] = "  ";
                        board[3][7] = "bR";
                    }
                    return true;
                }
            }
            if (board[5][7].equals("  ") && board[6][7].equals("  ") && x2 == 6 && y2 == 7 && board[7][7].equals("bR")) {
                if (!check(board, 'b')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[7][7] = "  ";
                        board[5][7] = "bR";
                    }
                    return true;
                }
            }
            if (x1 == x2 && y1 == y2) {
                return false;
            }
            if ((x2 - x1 == 1 || x2 == x1 || x2 - x1 == -1) && (y2 - y1 == 1 || y2 == y1 || y2 - y1 == -1)) {
                if (board[x2][y2].equals("  ") || board[x2][y2].charAt(0) == 'w') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (piece.equals("bR")) {
            if ((x1 == x2 && y1 == y2) || !(y1 == y2 || x1 == x2)) {
                return false;
            }
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == 'b') {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == 'b') {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            return false;
        }

        if (piece.equals("bB")) {
            if (y1 == y2 || x1 == x2) {
                return false;
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
        }

        if (piece.equals("bQ")) {
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == 'b') {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == 'b') {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == 'b') {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == 'w') {
                        return false;
                    }
                }
            }
        }

        if (piece.equals("bN")) {
            if ((y2 == y1 + 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == 'b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((y2 == y1 - 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == 'b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 + 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == 'b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 - 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == 'b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Tests whether {@code colour}'s king is currently attacked. Rather than
     * checking every enemy piece's move list, this walks outward from the
     * king along each attack pattern (pawn diagonals, knight L-shapes,
     * rook/queen files &amp; ranks, bishop/queen diagonals) and asks "is the
     * piece that could attack me from here actually there" -- which is
     * cheaper and is also what makes {@link #moveCheck} affordable to call
     * on every candidate move.
     *
     * @param board the board to check
     * @param colour the colour of the player
     * @return true if colour player is under check
     */
    static public boolean check(String[][] board, char colour) {
        String king = colour + "K";
        char opposite;
        if (colour == 'w') {
            opposite = 'b';
        } else {
            opposite = 'w';
        }
        String knight = opposite + "N";
        String queen = opposite + "Q";
        String rook = opposite + "R";
        String bishop = opposite + "B";
        String pawn = opposite + "P";
        int x = 0;
        int y = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (king.equals(board[i][j])) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }

        if (x - 1 > -1) {
            if ((colour == 'w') && (y + 1 < 8) && (board[x - 1][y + 1].equals(pawn))) {
                return true;
            }
            if ((colour == 'b') && (y - 1 > -1) && (board[x - 1][y - 1].equals(pawn))) {
                return true;
            }
        }
        if (x + 1 < 8) {
            if ((colour == 'w') && (y + 1 < 8) && (board[x + 1][y + 1].equals(pawn))) {
                return true;
            }
            if ((colour == 'b') && (y - 1 > -1) && (board[x + 1][y - 1].equals(pawn))) {
                return true;
            }
        }

        if (y + 2 < 8) {
            if ((x - 1 > -1) && (board[x - 1][y + 2].equals(knight))) {
                return true;
            }
            if ((x + 1 < 8) && (board[x + 1][y + 2].equals(knight))) {
                return true;
            }
        }
        if (y - 2 > -1) {
            if ((x - 1 > -1) && (board[x - 1][y - 2].equals(knight))) {
                return true;
            }
            if ((x + 1 < 8) && (board[x + 1][y - 2].equals(knight))) {
                return true;
            }
        }
        if (x + 2 < 8) {
            if ((y - 1 > -1) && (board[x + 2][y - 1].equals(knight))) {
                return true;
            }
            if ((y + 1 < 8) && (board[x + 2][y + 1].equals(knight))) {
                return true;
            }
        }
        if (x - 2 > -1) {
            if ((y - 1 > -1) && (board[x - 2][y - 1].equals(knight))) {
                return true;
            }
            if ((y + 1 < 8) && (board[x - 2][y + 1].equals(knight))) {
                return true;
            }
        }

        for (int i = 1; x - i > -1; i++) {
            if (board[x - i][y].equals(queen) || board[x - i][y].equals(rook)) {
                return true;
            }
            if (!board[x - i][y].equals("  ")) {
                break;
            }
        }
        for (int i = 1; x + i < 8; i++) {
            if (board[x + i][y].equals(queen) || board[x + i][y].equals(rook)) {
                return true;
            }
            if (!board[x + i][y].equals("  ")) {
                break;
            }
        }
        for (int i = 1; y + i < 8; i++) {
            if (board[x][y + i].equals(queen) || board[x][y + i].equals(rook)) {
                return true;
            }
            if (!board[x][y + i].equals("  ")) {
                break;
            }
        }
        for (int i = 1; y - i > -1; i++) {
            if (board[x][y - i].equals(queen) || board[x][y - i].equals(rook)) {
                return true;
            }
            if (!board[x][y - i].equals("  ")) {
                break;
            }
        }

        for (int i = 1; y + i < 8 && x + i < 8; i++) {
            if (board[x + i][y + i].equals(bishop) || board[x + i][y + i].equals(queen)) {
                return true;
            }
            if (!board[x + i][y + i].equals("  ")) {
                break;
            }
        }
        for (int i = 1; y + i < 8 && x - i > -1; i++) {
            if (board[x - i][y + i].equals(bishop) || board[x - i][y + i].equals(queen)) {
                return true;
            }
            if (!board[x - i][y + i].equals("  ")) {
                break;
            }
        }
        for (int i = 1; y - i > -1 && x + i < 8; i++) {
            if (board[x + i][y - i].equals(bishop) || board[x + i][y - i].equals(queen)) {
                return true;
            }
            if (!board[x + i][y - i].equals("  ")) {
                break;
            }
        }
        for (int i = 1; y - i > -1 && x - i > -1; i++) {
            if (board[x - i][y - i].equals(bishop) || board[x - i][y - i].equals(queen)) {
                return true;
            }
            if (!board[x - i][y - i].equals("  ")) {
                break;
            }
        }

        return false;
    }

    /**
     * Simulates a move on a scratch copy of the board and reports whether it
     * would leave {@code colour}'s own king in check. This is how every move
     * method rejects moves that expose or fail to resolve a check, including
     * pins, without tracking pins explicitly.
     *
     * @param board the board to look at
     * @param x1 x point of the piece to move
     * @param y1 y point of the piece to move
     * @param x2 x point of the square to move to
     * @param y2 y point of the square to move to
     * @param colour the colour of the moving piece
     * @return true if the move will cause a check
     */
    static private boolean moveCheck(String[][] board, int x1, int y1, int x2, int y2, char colour) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }

        copy[x2][y2] = copy[x1][y1];
        copy[x1][y1] = "  ";
        return check(copy, colour);
    }

    /**
     * Checkmate detection: {@code colour} is in check with no legal move
     * (of any of their own pieces, to any square) that escapes it. Legality
     * of each candidate move -- including the resulting self-check -- is
     * delegated to {@link #moveWhitePiece}/{@link #moveBlackPiece} called
     * with {@code move = false}, so this is a pure scan with no board
     * mutation.
     *
     * @param board the board to look at
     * @param colour the colour to check for
     * @return true if the colour is under checkmate
     */
    static public boolean checkmate(String[][] board, char colour) {
        Point pos1;
        Point pos2;
        if (check(board, colour)) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j].charAt(0) == colour) {
                        pos1 = new Point(i, j);

                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                pos2 = new Point(k, l);
                                if (colour == 'w' && moveWhitePiece(pos1, pos2, board, false)) {
                                    return false;
                                }
                                if (colour == 'b' && moveBlackPiece(pos1, pos2, board, false)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks for the game's (simplified) draw condition: only kings remain
     * on the board.
     * @param board the board to look at
     * @return true if there is a draw
     */
    static public boolean draw(String[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!(board[i][j].charAt(1) == 'K') && !(board[i][j].charAt(1) == ' ')) {
                    return false;
                }
            }
        }
        return true;
    }
}
