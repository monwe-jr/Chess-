package com.chess.game;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression coverage for a crash where the AI, searching from a position
 * where Black (its side) had no legal move but wasn't in check (stalemate,
 * not checkmate), fell through {@link AI#minimax}'s terminal check and
 * returned its all-zero-default {@code result} array as if it were a real
 * move -- from square index 0 to square index 0, decoded by
 * {@link Board#decodeAiSquareIndex} as a8-a8, which {@link Board#performMove}
 * then tried to play as the black king capturing itself.
 *
 * {@link Piece#stalemate} fixes the root cause; this confirms minimax itself
 * now recognizes the terminal state (scored as a flat draw) instead of
 * running its move-search loop over zero available moves.
 */
class AITest {

    private static String[][] emptyBoard() {
        String[][] board = new String[8][8];
        for (String[] rank : board) {
            Arrays.fill(rank, "  ");
        }
        return board;
    }

    @Test
    void minimaxScoresAStalematedPositionAsADrawInsteadOfSearchingForAMove() {
        String[][] board = emptyBoard();
        board[0][7] = "bK"; // a8: boxed into the corner, Black (the AI) to move
        board[1][5] = "wK"; // b6
        board[2][6] = "wQ"; // c7: covers a7/b7/b8 but doesn't check a8 itself

        int[] result = new AI().minimax(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        assertEquals(0, result[2], "a stalemate must be scored as a flat draw, not searched as if a move exists");
    }
}
