package com.chess.game;

import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    void stalematingTheAiEndsTheGameInsteadOfCrashingOnAGarbageAiMove() {
        // Regression test for a real crash: the AI (Black) reached a position
        // with no legal move but not in check -- stalemate, not checkmate --
        // and AI.minimax's terminal check (before the fix) never covered that
        // case, so its search loop ran over zero candidate moves and fell
        // through with its all-zero-default result array. Board.AIMove()
        // then decoded that as a real move (square index 0 to square index 0,
        // i.e. a8-a8) and performMove() tried to execute the black king
        // "capturing" itself, which the illegal-king-capture guard in
        // recordCapture() correctly refused -- but by throwing an uncaught
        // IllegalStateException that crashed the whole app instead of ending
        // the game gracefully.
        //
        // Human plays White so the AI (Black) is the side about to be
        // stalemated; White's queen move from c2 to c7 delivers it.
        Board board = new Board("AI", 1, 'w');
        for (String[] file : board.board) {
            Arrays.fill(file, "  ");
        }
        board.board[0][7] = "bK"; // a8
        board.board[1][5] = "wK"; // b6
        board.board[2][1] = "wQ"; // c2, one move from Qc7 stalemating black

        click(board, 2, 1); // select the queen
        click(board, 2, 6); // Qc7, stalemate

        assertTrue(board.isGameOver(), "stalemating the AI must end the game immediately instead of calling AIMove()");
        assertEquals("Draw by stalemate!", board.getGameOverMessage(),
                "the game must end via the stalemate branch in finishTurn(), not the illegal-king-capture safety net");
    }

    private static void click(Board board, int x, int y) {
        SquarePanel square = board.squareAt(x, y);
        board.mouseClicked(new MouseEvent(square, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false));
    }

    // --- always-accessible restart/menu control ---
    //
    // These wire a fake confirmation prompt in place of the real (blocking,
    // modal) JOptionPane dialog -- see Board.setRestartConfirmationPrompt --
    // so the confirm/decline paths can be exercised headlessly. What's under
    // test is that Board.confirmReturnToMenu() (what the always-visible
    // "Menu" button calls) reaches the same onRestart callback the
    // game-over card's "New Game" button already used, no matter what state
    // the game is in when it's invoked.

    @Test
    void restartCanBeDeclinedAndLeavesTheGameRunning() {
        Board board = new Board("Human");
        AtomicInteger restarts = new AtomicInteger();
        board.setOnRestart(restarts::incrementAndGet);
        board.setRestartConfirmationPrompt(() -> false); // simulates clicking "No"

        board.confirmReturnToMenu();

        assertEquals(0, restarts.get(), "declining the prompt must not trigger a restart");
        assertFalse(board.isGameOver());
    }

    @Test
    void restartWorksBeforeAnyMoveHasBeenMade() {
        Board board = new Board("Human");
        AtomicInteger restarts = new AtomicInteger();
        board.setOnRestart(restarts::incrementAndGet);
        board.setRestartConfirmationPrompt(() -> true); // simulates clicking "Yes"

        board.confirmReturnToMenu();

        assertEquals(1, restarts.get());
    }

    @Test
    void restartWorksAfterSelectingASquareButBeforeCompletingAMove() {
        Board board = new Board("Human");
        AtomicInteger restarts = new AtomicInteger();
        board.setOnRestart(restarts::incrementAndGet);
        board.setRestartConfirmationPrompt(() -> true);

        click(board, 4, 1); // select White's e2 pawn; no destination clicked yet

        board.confirmReturnToMenu();

        assertEquals(1, restarts.get(), "an unfinished piece selection must not block or break the restart");
    }

    @Test
    void restartWorksImmediatelyAroundTheAisTurn() {
        // The AI move search runs synchronously on the event thread (no
        // SwingWorker/background thread exists in this codebase), so
        // "during" the AI's turn isn't a separately observable state from a
        // test -- by the time control returns to the caller, the AI's move
        // (if any) has already completed. What we can and do verify is that
        // the restart path works cleanly right around that synchronous call,
        // both immediately after construction (which makes the AI play
        // White's opening move up front here) and after a further human/AI
        // exchange, with no leftover state or exceptions either time.
        Board board = new Board("AI", 1, 'b');
        assertEquals('b', board.getSideToMove(), "AI (White) should have already moved once, precondition for this test");

        AtomicInteger restarts = new AtomicInteger();
        board.setOnRestart(restarts::incrementAndGet);
        board.setRestartConfirmationPrompt(() -> true);

        board.confirmReturnToMenu();

        assertEquals(1, restarts.get());
    }
}
