package com.chess.game;

import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sanity checks for the player-color setup added to Human vs AI games:
 * whichever color the human picks, the AI must play the other one, the
 * board must only render flipped when the human plays Black, and the AI
 * must make White's opening move immediately when the human plays Black.
 */
class BoardTest {

    @Test
    void humanPlayingWhiteFacesAiAsBlackAndBoardIsNotFlipped() {
        Board board = new Board("AI", 1, 'w');

        assertEquals('w', board.getHumanColor());
        assertEquals('b', board.getAiColor());
        assertFalse(board.isFlipped());
        // White moves first in chess, and the human is White, so it's the
        // human's move: the AI must not have moved yet.
        assertEquals('w', board.getSideToMove());
    }

    @Test
    void humanPlayingBlackFacesAiAsWhiteAndBoardIsFlipped() {
        Board board = new Board("AI", 1, 'b');

        assertEquals('b', board.getHumanColor());
        assertEquals('w', board.getAiColor());
        assertTrue(board.isFlipped());
        // White always moves first; since the AI plays White here, it must
        // have already made the opening move by the time the board is
        // shown, handing the turn to the human (Black).
        assertEquals('b', board.getSideToMove());
    }

    @Test
    void humanVsHumanIsNeverFlippedRegardlessOfHumanColorDefault() {
        Board board = new Board("Human");

        assertFalse(board.isFlipped());
        assertEquals('w', board.getSideToMove());
    }

    @Test
    void deliveringABasicQueenAndKingMateEndsTheGameInsteadOfContinuingPlay() {
        // Regression test for a bug where a real game ended with the black
        // king captured off the board instead of the game ending in
        // checkmate the move before: Piece.checkmate() had a false-negative
        // case (see PieceTest), so Board never called endGame() and the
        // opposing side was allowed to keep playing into the king. Here we
        // drive the actual click-handling path -- not just Piece.checkmate()
        // directly -- to confirm the whole game loop reacts correctly when
        // White plays the mating move.
        Board board = new Board("Human");
        for (String[] file : board.board) {
            Arrays.fill(file, "  ");
        }
        board.board[0][7] = "bK"; // a8
        board.board[1][5] = "wK"; // b6, defends a7
        board.board[0][1] = "wQ"; // a2, one legal move from Qa7#

        click(board, 0, 1); // select the queen
        click(board, 0, 6); // Qa7#

        assertTrue(board.isGameOver(), "Qa7# should end the game immediately instead of allowing further play");
    }

    private static void click(Board board, int x, int y) {
        SquarePanel square = board.squareAt(x, y);
        board.mouseClicked(new MouseEvent(square, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false));
    }
}
