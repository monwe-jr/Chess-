package com.chess.game;

import org.junit.jupiter.api.Test;

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
}
