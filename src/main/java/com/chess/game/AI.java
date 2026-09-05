package com.chess.game;

import java.awt.Point;
import java.util.ArrayList;

/**
 * 3p71 Term Project: Chess
 *
 * Francis Monwe
 * Jashandeep Pannu
 *
 * The computer opponent: a minimax search with alpha-beta pruning over
 * {@link Piece}'s move rules, guided by a material + piece-square-table
 * heuristic. Black is always the maximizing player (the AI plays Black);
 * White is the minimizing player.
 */
public class AI {
    Piece pieces = new Piece();
    String[][] board = pieces.board;

    // Piece-square tables bias the heuristic toward good squares for each
    // piece type (e.g. knights near the centre, king tucked away early on)
    // on top of raw material value. Indexed [file][rank] from White's
    // perspective; Black's score mirrors the table (see heuristic()).
    static int[][] pawnTable = {
            {900, 900, 900, 900, 900, 900, 900, 900},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    static int[][] knightTable = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, 30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };
    static int[][] bishopTable = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    static int[][] rookTable = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };
    static int[][] queenTable = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {-5, 0, 5, 5, 5, 5, 0, 0},
            {-10, 0, 5, 5, 5, 5, 5, -10},
            {-10, 0, 0, 0, 0, 5, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
    };
    static int[][] kingTable = {
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}
    };

    AI() {
    }

    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }

    /**
     * Converts array coordinates to board (GUI grid) coordinates.
     *
     * @param p array coordinate
     * @return board coordinate
     */
    private int convertArrayToBoard(Point p) {
        int k = 0;
        int x = 0;

        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[i].length; j++) {
                if (p.getX() == j && p.getY() == i) {
                    x = k;
                }

                k++;
            }
        }

        return x;
    }

    /**
     * Static evaluation of a board position: sum of (material value + piece-
     * square-table bonus) for every piece, positive favouring Black and
     * negative favouring White -- matching minimax's convention of Black as
     * the maximizer. Each piece-square table is defined from White's point of
     * view, so Black's lookup mirrors both axes ({@code table[7-j][7-i]} vs
     * {@code table[j][i]}) to read the equivalent square from Black's side of
     * the board.
     *
     * @param board the position to evaluate
     * @return the heuristic score, positive favours Black, negative favours White
     */
    static public int heuristic(String[][] board) {
        int value = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if (piece.equals("wP")) value -= 100 + pawnTable[7 - j][7 - i];
                else if (piece.equals("wN")) value -= 320 + knightTable[7 - j][7 - i];
                else if (piece.equals("wB")) value -= 330 + bishopTable[7 - j][7 - i];
                else if (piece.equals("wR")) value -= 500 + rookTable[7 - j][7 - i];
                else if (piece.equals("wQ")) value -= 900 + queenTable[7 - j][7 - i];
                else if (piece.equals("wK")) value -= 20000 + kingTable[7 - j][7 - i];
                else if (piece.equals("bP")) value += 100 + pawnTable[j][i];
                else if (piece.equals("bN")) value += 320 + knightTable[j][i];
                else if (piece.equals("bB")) value += 330 + bishopTable[j][i];
                else if (piece.equals("bR")) value += 500 + rookTable[j][i];
                else if (piece.equals("bQ")) value += 900 + queenTable[j][i];
                else if (piece.equals("bK")) value += 20000 + kingTable[j][i];
            }
        }
        return value;
    }

    private ArrayList<Point> getMoves(String[][] arr, char c) {
        ArrayList<Point> temp = new ArrayList<>();

        for (int i = arr.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr.length; j++) {
                if (board[j][i].charAt(0) == c) {
                    temp.add(new Point(j, i));
                }

            }
        }

        return temp;
    }

    /**
     * Chooses the AI's (Black's) move by minimax search with alpha-beta
     * pruning over {@code d} plies.
     *
     * The search alternates {@code maximizing} between plies: Black picks
     * the move maximizing {@link #heuristic}, White (simulated one ply
     * ahead) picks the move minimizing it, and so on down to depth 0 or a
     * terminal position (checkmate/draw). Terminal checkmates are scored as
     * {@code Integer.MAX_VALUE}/{@code MIN_VALUE} rather than the material
     * heuristic so a forced mate always outranks any positional score.
     *
     * Alpha-beta pruning cuts branches that can't affect the final choice:
     * {@code alpha} is the best score the maximizer can already guarantee,
     * {@code beta} the best the minimizer can already guarantee. Once
     * {@code beta <= alpha} the rest of the current move's sibling branches
     * are skipped, since the opponent would never let play reach a position
     * that lets this branch improve on what they can already force
     * elsewhere -- this prunes the tree without changing the final result.
     *
     * @param arr the position to search from
     * @param d remaining search depth in plies (the "difficulty")
     * @param alpha best score the maximizing player can currently guarantee
     * @param beta best score the minimizing player can currently guarantee
     * @param maximizing true when it is Black's (the AI's) turn to move
     * @return a 3-element array: [from-square, to-square, score], where the
     *         squares are in GUI board-index form (see
     *         {@link #convertArrayToBoard})
     */
    public int[] minimax(String[][] arr, int d, int alpha, int beta, boolean maximizing) {
        int[] result = new int[3];
        String[][] temp;
        int value;
        int score;

        if (d == 0 || Piece.checkmate(arr, 'w') || Piece.checkmate(arr, 'b') || Piece.draw(arr)) {
            result[2] = heuristic(arr);
            if (Piece.checkmate(arr, 'w')) result[2] = Integer.MAX_VALUE;
            if (Piece.checkmate(arr, 'b')) result[2] = Integer.MIN_VALUE;
            return result;
        }

        // Stalemate: the side to move has no legal move but also isn't in
        // check, so it's neither of the checkmate branches above nor
        // Piece.draw()'s "only kings remain" case. Without this check, the
        // maximizing/minimizing loop below would iterate over zero moves and
        // fall through with result still at its all-zero default -- a
        // "move" from square index 0 to square index 0, silently executed by
        // Board.AIMove() as if it were real. Scored as a flat draw (0),
        // matching how a stalemate is valued regardless of material on the
        // board.
        if (Piece.stalemate(arr, 'w') || Piece.stalemate(arr, 'b')) {
            result[2] = 0;
            return result;
        }

        if (maximizing) {
            value = Integer.MIN_VALUE;
            ArrayList<Point> moves = getMoves(arr, 'b');
            for (Point move : moves) {
                for (Point valid : validMoves(move, arr)) {

                    temp = copyOf(arr);
                    Piece.moveBlackPiece(move, valid, temp, true);
                    score = minimax(temp, d - 1, alpha, beta, false)[2];

                    if (score > value) {
                        value = score;
                        result[0] = convertArrayToBoard(move);
                        result[1] = convertArrayToBoard(valid);
                        result[2] = value;
                    }

                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break;
                    }
                }

            }

            return result;

        } else {
            value = Integer.MAX_VALUE;
            ArrayList<Point> moves = getMoves(arr, 'w');
            for (Point move : moves) {
                for (Point valid : validMoves(move, arr)) {

                    temp = copyOf(arr);
                    Piece.moveWhitePiece(move, valid, temp, true);
                    score = minimax(temp, d - 1, alpha, beta, true)[2];

                    if (score < value) {
                        value = score;
                        result[0] = convertArrayToBoard(move);
                        result[1] = convertArrayToBoard(valid);
                        result[2] = value;
                    }

                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }

            }

            return result;
        }
    }

    /**
     * Returns all legal destination squares for the piece at {@code pos1}.
     *
     * @param pos1 initial position
     * @param arr 2D array representation of the board
     */
    private Point[] validMoves(Point pos1, String[][] arr) {
        ArrayList<Point> moves = new ArrayList<>();
        Point[] returns;

        if ((arr[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    Point pos2 = new Point(j, i);

                    if (Piece.moveWhitePiece(pos1, pos2, arr, false)) {
                        moves.add(pos2);

                    }
                }
            }
        } else if (arr[pos1.x][pos1.y].charAt(0) == 'b') {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    Point pos2 = new Point(j, i);

                    if (Piece.moveBlackPiece(pos1, pos2, arr, false)) {
                        moves.add(pos2);

                    }
                }
            }
        }

        returns = new Point[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            returns[i] = moves.get(i);

        }

        return returns;
    }
}
