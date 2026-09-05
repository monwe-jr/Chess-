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

    /**
     * The handful of things that differ between White's and Black's move
     * rules: which way pawns advance, which ranks they start/castle on, and
     * which of the two en passant flag arrays is "mine" versus "theirs".
     * Pulling these into one small config per colour is what lets
     * {@link #movePiece} implement both colours' rules once instead of as
     * two near-identical ~435-line copies.
     */
    private static final class ColourRules {
        final char self;
        final char enemy;
        final int direction;            // pawn's forward step: +1 for White, -1 for Black
        final int pawnStartRank;        // rank index pawns begin on
        final int enPassantRank;        // rank index a pawn must be on to capture en passant
        final int backRank;             // king/rook home rank, for castling
        final boolean[] ownEnPassant;   // flags this colour sets on its own double-step
        final boolean[] enemyEnPassant; // flags this colour reads to capture the opponent's double-step

        ColourRules(char self, char enemy, int direction, int pawnStartRank, int enPassantRank, int backRank,
                    boolean[] ownEnPassant, boolean[] enemyEnPassant) {
            this.self = self;
            this.enemy = enemy;
            this.direction = direction;
            this.pawnStartRank = pawnStartRank;
            this.enPassantRank = enPassantRank;
            this.backRank = backRank;
            this.ownEnPassant = ownEnPassant;
            this.enemyEnPassant = enemyEnPassant;
        }
    }

    private static final ColourRules WHITE_RULES = new ColourRules('w', 'b', 1, 1, 4, 0, enPassantW, enPassantB);
    private static final ColourRules BLACK_RULES = new ColourRules('b', 'w', -1, 6, 3, 7, enPassantB, enPassantW);

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
     * Validates (and optionally performs) a White piece move. See
     * {@link #movePiece} for the shared rules implementation.
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
        return movePiece(pos1, pos2, board, move, 'w');
    }

    /**
     * Validates (and optionally performs) a Black piece move. See
     * {@link #movePiece} for the shared rules implementation.
     *
     * @param pos1 position of the piece to move
     * @param pos2 destination position
     * @param board board the move is validated/applied against
     * @param move if true, the board is mutated when the move is legal; if
     *             false, the board is left untouched
     */
    static public boolean moveBlackPiece(Point pos1, Point pos2, String[][] board, boolean move) {
        return movePiece(pos1, pos2, board, move, 'b');
    }

    /**
     * Validates (and optionally performs) a move for {@code colour}.
     *
     * Handles per-piece movement rules, blocking pieces along rays for
     * sliding pieces, pawn double-step/diagonal-capture/en passant, and
     * king castling (kingside and queenside). Every candidate move is first
     * rejected if it would leave {@code colour}'s king in check
     * ({@link #moveCheck}), which is how self-checks (including moving
     * into/through check while castling) are prevented without duplicating
     * check logic per piece.
     *
     * White and Black differ only in pawn direction, starting/castling
     * ranks, and which en passant flag array is "mine" versus "theirs" --
     * see {@link ColourRules}, which captures exactly those differences so
     * this method implements both colours' rules once.
     *
     * @param pos1 position of the piece to move
     * @param pos2 destination position
     * @param board board the move is validated/applied against
     * @param move if true, the board is mutated when the move is legal; if
     *             false, the board is left untouched (used for move
     *             generation and checkmate scans)
     * @param colour the colour of the piece being moved ('w' or 'b')
     * @return true if the move is legal
     */
    static private boolean movePiece(Point pos1, Point pos2, String[][] board, boolean move, char colour) {
        ColourRules c = colour == 'w' ? WHITE_RULES : BLACK_RULES;
        int x1 = pos1.x;
        int x2 = pos2.x;
        int y1 = pos1.y;
        int y2 = pos2.y;
        String piece = board[x1][y1];

        if (piece.equals("  ") || piece.charAt(0) == c.enemy || piece.charAt(0) != c.self) {
            return false;
        }

        if (move) {
            for (int i = 0; i < 8; i++) {
                c.ownEnPassant[i] = false;
            }
        }

        if (moveCheck(board, x1, y1, x2, y2, c.self)) return false;

        if (piece.charAt(1) == 'P') {
            // en passant: capture diagonally onto the file of a pawn that just
            // double-stepped past this one, removing that pawn from its rank
            if (y1 == c.enPassantRank && y2 == c.enPassantRank + c.direction
                    && (x2 == x1 + 1 || x2 == x1 - 1) && board[x2][y2].equals("  ") && c.enemyEnPassant[x2]) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                    board[x2][y1] = "  ";
                }
                return true;
            }
            if (x1 == x2) {
                if (y2 - y1 == c.direction || (y2 - y1 == 2 * c.direction && y1 == c.pawnStartRank && board[x1][y1 + c.direction].equals("  "))) {
                    if (board[x2][y2].equals("  ")) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                            if (y1 == c.pawnStartRank && y2 - y1 == 2 * c.direction) {
                                c.ownEnPassant[x1] = true;
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

            if ((x1 == x2 - 1 || x1 == x2 + 1) && (y2 - y1 == c.direction)) {
                if (board[x2][y2].charAt(0) == c.enemy) {
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

        if (piece.charAt(1) == 'K') {
            if (board[1][c.backRank].equals("  ") && board[2][c.backRank].equals("  ") && board[3][c.backRank].equals("  ")
                    && board[0][c.backRank].equals(c.self + "R") && y1 == c.backRank && x1 == 4 && y2 == c.backRank && x2 == 2) {
                if (!check(board, c.self)) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[0][c.backRank] = "  ";
                        board[3][c.backRank] = c.self + "R";
                    }
                    return true;
                }
            }
            if (board[5][c.backRank].equals("  ") && board[6][c.backRank].equals("  ") && x2 == 6 && y2 == c.backRank
                    && board[7][c.backRank].equals(c.self + "R")) {
                if (!check(board, c.self)) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[7][c.backRank] = "  ";
                        board[5][c.backRank] = c.self + "R";
                    }
                    return true;
                }
            }
            if (x1 == x2 && y1 == y2) {
                return false;
            }
            if ((x2 - x1 == 1 || x2 == x1 || x2 - x1 == -1) && (y2 - y1 == 1 || y2 == y1 || y2 - y1 == -1)) {
                if (board[x2][y2].equals("  ") || board[x2][y2].charAt(0) == c.enemy) {
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

        if (piece.charAt(1) == 'R') {
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == c.self) {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == c.self) {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            return false;
        }

        if (piece.charAt(1) == 'B') {
            if (y1 == y2 || x1 == x2) {
                return false;
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
        }

        if (piece.charAt(1) == 'Q') {
            if (y2 - y1 > 0 && x1 == x2) {
                for (int i = 1; i + y1 < 8; i++) {
                    if (board[x1][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x1 == x2) {
                for (int i = 1; y1 - i > -1; i++) {
                    if (board[x1][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (x2 - x1 < 0 && y1 == y2) {
                for (int i = 1; x1 - i > -1; i++) {
                    if (board[x1 - i][y1].charAt(0) == c.self) {
                        return false;
                    }
                    if (x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (x2 - x1 > 0 && y1 == y2) {
                for (int i = 1; x1 + i < 8; i++) {
                    if (board[x1 + i][y1].charAt(0) == c.self) {
                        return false;
                    }
                    if (x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 > 0) {
                for (int i = 1; (i + y1 < 8 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 > 0 && x2 - x1 < 0) {
                for (int i = 1; (i + y1 < 8 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 + i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 + i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 + i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 > 0) {
                for (int i = 1; (y1 - i > -1 && i + x1 < 8); i++) {
                    if (board[x1 + i][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2 && x1 + i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 + i][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
            if (y2 - y1 < 0 && x2 - x1 < 0) {
                for (int i = 1; (y1 - i > -1 && x1 - i > -1); i++) {
                    if (board[x1 - i][y1 - i].charAt(0) == c.self) {
                        return false;
                    }
                    if (y1 - i == y2 && x1 - i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    if (board[x1 - i][y1 - i].charAt(0) == c.enemy) {
                        return false;
                    }
                }
            }
        }

        if (piece.charAt(1) == 'N') {
            if ((y2 == y1 + 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == c.self)) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((y2 == y1 - 2 && (x1 == x2 - 1 || x1 == x2 + 1)) && !(board[x2][y2].charAt(0) == c.self)) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 + 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == c.self)) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            if ((x2 == x1 - 2 && (y1 == y2 - 1 || y1 == y2 + 1)) && !(board[x2][y2].charAt(0) == c.self)) {
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
        String enemyKing = opposite + "K";
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

        // Kings may never stand adjacent to each other: moving into (or
        // staying in) a square next to the enemy king is itself check. This
        // matters most in king-and-queen-type endgames, where an escape
        // square is often unguarded by any piece but is controlled solely by
        // the defending king's own king -- without this, checkmate() can
        // miss mates whose only "escape" is capturing onto or stepping next
        // to a square the enemy king itself covers.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx > -1 && nx < 8 && ny > -1 && ny < 8 && board[nx][ny].equals(enemyKing)) {
                    return true;
                }
            }
        }

        // An enemy pawn attacks diagonally toward its own forward direction,
        // so a pawn threatening this king sits one rank further along that
        // same direction from the king (White's pawns attack upward, so a
        // black pawn threatening a white king is one rank above it, and
        // vice versa) -- one rank check shared by both colours instead of a
        // colour-branched pair of checks in each x-direction.
        int pawnAttackRank = colour == 'w' ? y + 1 : y - 1;
        if (pawnAttackRank > -1 && pawnAttackRank < 8) {
            if (x - 1 > -1 && board[x - 1][pawnAttackRank].equals(pawn)) {
                return true;
            }
            if (x + 1 < 8 && board[x + 1][pawnAttackRank].equals(pawn)) {
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
     * Whether {@code colour} has at least one legal move available, across
     * every one of their pieces. Legality of each candidate move -- including
     * the resulting self-check -- is delegated to {@link #movePiece} called
     * with {@code move = false}, so this is a pure scan with no board
     * mutation. Shared by {@link #checkmate} and {@link #stalemate}, which
     * differ only in whether {@code colour} must also currently be in check.
     *
     * @param board the board to look at
     * @param colour the colour to scan for a legal move
     * @return true if colour has at least one legal move
     */
    static private boolean hasLegalMove(String[][] board, char colour) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].charAt(0) == colour) {
                    Point pos1 = new Point(i, j);
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (movePiece(pos1, new Point(k, l), board, false, colour)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checkmate detection: {@code colour} is in check with no legal move
     * that escapes it.
     *
     * @param board the board to look at
     * @param colour the colour to check for
     * @return true if the colour is under checkmate
     */
    static public boolean checkmate(String[][] board, char colour) {
        return check(board, colour) && !hasLegalMove(board, colour);
    }

    /**
     * Stalemate detection: {@code colour} is NOT in check but has no legal
     * move available -- the other "no legal moves" terminal state besides
     * checkmate, and a draw rather than a loss.
     *
     * Missing this case is what let {@link AI#minimax} fall through to a
     * garbage default move (the AI's side had no legal move but also wasn't
     * in check, so minimax's terminal check never fired and the search loop
     * simply never ran) -- see {@code AITest} and {@code BoardTest} for the
     * regression coverage this came from.
     *
     * @param board the board to look at
     * @param colour the colour to check for
     * @return true if the colour is stalemated
     */
    static public boolean stalemate(String[][] board, char colour) {
        return !check(board, colour) && !hasLegalMove(board, colour);
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
