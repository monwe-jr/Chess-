package com.chess.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sanity-check tests for {@link Piece}'s rules engine: move generation,
 * check/checkmate detection, castling, en passant, and pawn promotion
 * eligibility. Written against the standalone {@code String[][]} move
 * methods rather than the GUI, so they run headless.
 */
class PieceTest {

    @BeforeEach
    void resetEnPassantFlags() {
        for (int i = 0; i < 8; i++) {
            Piece.enPassantW[i] = false;
            Piece.enPassantB[i] = false;
        }
    }

    private static String[][] emptyBoard() {
        String[][] board = new String[8][8];
        for (String[] rank : board) {
            Arrays.fill(rank, "  ");
        }
        return board;
    }

    private static String[][] startingBoard() {
        String[][] board = emptyBoard();
        String[] backRank = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int i = 0; i < 8; i++) {
            board[i][0] = "w" + backRank[i];
            board[i][1] = "wP";
            board[i][6] = "bP";
            board[i][7] = "b" + backRank[i];
        }
        return board;
    }

    // --- legal move generation ---

    @Test
    void whitePawnCanAdvanceOneOrTwoSquaresFromStart() {
        String[][] board = startingBoard();
        assertTrue(Piece.moveWhitePiece(new Point(4, 1), new Point(4, 2), board, false), "e2-e3 should be legal");
        assertTrue(Piece.moveWhitePiece(new Point(4, 1), new Point(4, 3), board, false), "e2-e4 should be legal");
        assertFalse(Piece.moveWhitePiece(new Point(4, 1), new Point(4, 4), board, false), "e2-e5 should be illegal");
    }

    @Test
    void knightCannotJumpOntoItsOwnPiece() {
        String[][] board = startingBoard();
        // b1 knight can hop to a3/c3, but not onto its own d2 pawn
        assertTrue(Piece.moveWhitePiece(new Point(1, 0), new Point(0, 2), board, false));
        assertTrue(Piece.moveWhitePiece(new Point(1, 0), new Point(2, 2), board, false));
        assertFalse(Piece.moveWhitePiece(new Point(1, 0), new Point(3, 1), board, false));
    }

    @Test
    void rookCannotJumpOverBlockingPiece() {
        String[][] board = startingBoard();
        // a1 rook is blocked by its own a2 pawn
        assertFalse(Piece.moveWhitePiece(new Point(0, 0), new Point(0, 3), board, false));
    }

    @Test
    void pinnedPieceCannotMoveOffThePinLine() {
        String[][] board = emptyBoard();
        board[4][0] = "wK";
        board[4][1] = "wR"; // pinned to the king by the rook behind it
        board[4][7] = "bR";

        // legal for a rook in isolation, but exposes the king -> illegal
        assertFalse(Piece.moveWhitePiece(new Point(4, 1), new Point(5, 1), board, false));
        // staying on the e-file keeps the king protected
        assertTrue(Piece.moveWhitePiece(new Point(4, 1), new Point(4, 3), board, false));
    }

    // --- check / checkmate ---

    @Test
    void rookOnOpenFileGivesCheck() {
        String[][] board = emptyBoard();
        board[4][0] = "wK";
        board[4][7] = "bR";
        assertTrue(Piece.check(board, 'w'));
    }

    @Test
    void foolsMateEndsInCheckmate() {
        Piece pieceSetup = new Piece();
        String[][] board = Piece.board;

        assertTrue(Piece.moveWhitePiece(new Point(5, 1), new Point(5, 2), board, true)); // 1. f3
        assertTrue(Piece.moveBlackPiece(new Point(4, 6), new Point(4, 4), board, true)); // 1... e5
        assertTrue(Piece.moveWhitePiece(new Point(6, 1), new Point(6, 3), board, true)); // 2. g4
        assertTrue(Piece.moveBlackPiece(new Point(3, 7), new Point(7, 3), board, true)); // 2... Qh4#

        assertTrue(Piece.check(board, 'w'));
        assertTrue(Piece.checkmate(board, 'w'));
        assertFalse(Piece.checkmate(board, 'b'));
    }

    // --- castling ---

    @Test
    void whiteCanCastleKingsideWhenPathIsClearAndSafe() {
        String[][] board = emptyBoard();
        board[4][0] = "wK";
        board[7][0] = "wR";
        board[4][7] = "bK";

        assertTrue(Piece.moveWhitePiece(new Point(4, 0), new Point(6, 0), board, true));
        assertEquals("wK", board[6][0]);
        assertEquals("wR", board[5][0]);
        assertEquals("  ", board[7][0]);
    }

    @Test
    void blackCanCastleQueensideWhenPathIsClearAndSafe() {
        String[][] board = emptyBoard();
        board[4][7] = "bK";
        board[0][7] = "bR";
        board[4][0] = "wK";

        assertTrue(Piece.moveBlackPiece(new Point(4, 7), new Point(2, 7), board, true));
        assertEquals("bK", board[2][7]);
        assertEquals("bR", board[3][7]);
        assertEquals("  ", board[0][7]);
    }

    @Test
    void cannotCastleOutOfCheck() {
        String[][] board = emptyBoard();
        board[4][0] = "wK";
        board[7][0] = "wR";
        board[4][7] = "bR"; // checks the white king down the e-file

        assertFalse(Piece.moveWhitePiece(new Point(4, 0), new Point(6, 0), board, false));
    }

    // --- en passant ---

    @Test
    void whitePawnCanCaptureEnPassantImmediatelyAfterBlackDoubleSteps() {
        String[][] board = emptyBoard();
        board[4][4] = "wP"; // white pawn on e5
        board[3][6] = "bP"; // black pawn on d7
        board[4][0] = "wK";
        board[4][7] = "bK";

        assertTrue(Piece.moveBlackPiece(new Point(3, 6), new Point(3, 4), board, true)); // d7-d5
        assertTrue(Piece.enPassantB[3]);

        assertTrue(Piece.moveWhitePiece(new Point(4, 4), new Point(3, 5), board, true)); // exd6 e.p.
        assertEquals("wP", board[3][5]);
        assertEquals("  ", board[3][4]); // captured black pawn removed
        assertEquals("  ", board[4][4]); // origin square vacated
    }

    @Test
    void plainForwardPushDoesNotTriggerEnPassant() {
        // Regression test: a straight, non-diagonal pawn push into an empty
        // square must never be treated as an en passant capture, even when
        // an adjacent enemy pawn just double-stepped.
        String[][] board = emptyBoard();
        board[4][4] = "wP"; // white pawn on e5
        board[3][6] = "bP"; // black pawn on d7
        board[4][0] = "wK";
        board[4][7] = "bK";

        assertTrue(Piece.moveBlackPiece(new Point(3, 6), new Point(3, 4), board, true)); // d7-d5

        assertTrue(Piece.moveWhitePiece(new Point(4, 4), new Point(4, 5), board, true)); // e5-e6, plain push
        assertEquals("wP", board[4][5]);
        assertEquals("bP", board[3][4], "adjacent black pawn must survive a non-capturing push");
    }

    @Test
    void enPassantRightExpiresOnceBlackMovesAgain() {
        // The en passant window is exactly White's one reply to Black's
        // double-step; by the time Black has moved again (which, given
        // strict turn alternation, is necessarily after that reply), the
        // right must no longer be usable.
        String[][] board = emptyBoard();
        board[4][4] = "wP";
        board[3][6] = "bP";
        board[0][1] = "wP";
        board[6][7] = "bN";
        board[4][0] = "wK";
        board[4][7] = "bK";

        assertTrue(Piece.moveBlackPiece(new Point(3, 6), new Point(3, 4), board, true)); // d7-d5
        assertTrue(Piece.moveWhitePiece(new Point(0, 1), new Point(0, 2), board, true)); // White declines the capture
        assertTrue(Piece.moveBlackPiece(new Point(6, 7), new Point(5, 5), board, true)); // Black plays on, Ng8-f6
        assertFalse(Piece.moveWhitePiece(new Point(4, 4), new Point(3, 5), board, false), "en passant right should have expired");
    }

    @Test
    void speculativeLegalityScansDoNotEraseAPendingEnPassantRight() {
        // Regression test: Board.checkmate() calls Piece.checkmate() after
        // every move, which scans a side's own legal moves by calling
        // moveWhitePiece/moveBlackPiece with move=false many times. That
        // read-only scanning must never mutate the en passant flags, or a
        // just-created en passant opportunity would be wiped out before the
        // opponent's turn even starts.
        Piece pieceSetup = new Piece();
        String[][] board = Piece.board;

        assertTrue(Piece.moveWhitePiece(new Point(4, 1), new Point(4, 3), board, true)); // e2-e4
        assertTrue(Piece.enPassantW[4]);

        Piece.checkmate(board, 'w'); // simulates Board.checkmate()'s post-move scan
        Piece.checkmate(board, 'b');

        assertTrue(Piece.enPassantW[4], "read-only legality scans must not clear pending en passant flags");
    }

    // --- pawn promotion eligibility ---

    @Test
    void whitePawnCanAdvanceToTheBackRank() {
        String[][] board = emptyBoard();
        board[0][6] = "wP";
        board[4][0] = "wK";
        board[4][7] = "bK";

        assertTrue(Piece.moveWhitePiece(new Point(0, 6), new Point(0, 7), board, true));
        // Piece itself leaves the pawn as "wP" on the back rank; promotion
        // (choosing Q/R/B/N) is a GUI-level concern handled by Board.
        assertEquals("wP", board[0][7]);
    }
}
