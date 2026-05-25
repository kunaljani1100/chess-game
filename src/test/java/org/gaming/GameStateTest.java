package org.gaming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        state = new GameState();
    }

    @Test
    void boardStartsEmpty() {
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                assertEquals(Constants.EMPTY_STRING, state.getPiece(row, col));
            }
        }
    }

    @Test
    void initializeBoardSetsUpPiecesCorrectly() {
        state.initializeBoard();

        assertEquals(Constants.BLACK_ROOK, state.getPiece(0, 0));
        assertEquals(Constants.BLACK_KNIGHT, state.getPiece(0, 1));
        assertEquals(Constants.BLACK_BISHOP, state.getPiece(0, 2));
        assertEquals(Constants.BLACK_QUEEN, state.getPiece(0, 3));
        assertEquals(Constants.BLACK_KING, state.getPiece(0, 4));
        assertEquals(Constants.BLACK_BISHOP, state.getPiece(0, 5));
        assertEquals(Constants.BLACK_KNIGHT, state.getPiece(0, 6));
        assertEquals(Constants.BLACK_ROOK, state.getPiece(0, 7));

        for (int col = 0; col < Constants.COLS; col++) {
            assertEquals(Constants.BLACK_PAWN, state.getPiece(1, col));
        }

        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                assertEquals(Constants.EMPTY_STRING, state.getPiece(row, col));
            }
        }

        for (int col = 0; col < Constants.COLS; col++) {
            assertEquals(Constants.WHITE_PAWN, state.getPiece(6, col));
        }

        assertEquals(Constants.WHITE_ROOK, state.getPiece(7, 0));
        assertEquals(Constants.WHITE_KNIGHT, state.getPiece(7, 1));
        assertEquals(Constants.WHITE_BISHOP, state.getPiece(7, 2));
        assertEquals(Constants.WHITE_QUEEN, state.getPiece(7, 3));
        assertEquals(Constants.WHITE_KING, state.getPiece(7, 4));
        assertEquals(Constants.WHITE_BISHOP, state.getPiece(7, 5));
        assertEquals(Constants.WHITE_KNIGHT, state.getPiece(7, 6));
        assertEquals(Constants.WHITE_ROOK, state.getPiece(7, 7));
    }

    @Test
    void startsAsWhiteTurn() {
        assertTrue(state.isWhiteTurn());
    }

    @Test
    void flipTurnSwitchesToBlack() {
        state.flipTurn();
        assertFalse(state.isWhiteTurn());
    }

    @Test
    void flipTurnTwiceReturnsToWhite() {
        state.flipTurn();
        state.flipTurn();
        assertTrue(state.isWhiteTurn());
    }

    @Test
    void selectPieceSetsState() {
        state.setPiece(6, 0, Constants.WHITE_PAWN);
        state.selectPiece(6, 0);

        assertTrue(state.isPieceClicked());
        assertEquals(Constants.WHITE_PAWN, state.getSelectedPiece());
        assertEquals(6, state.getSelectedRow());
        assertEquals(0, state.getSelectedCol());
    }

    @Test
    void clearSelectionResetsState() {
        state.setPiece(6, 0, Constants.WHITE_PAWN);
        state.selectPiece(6, 0);
        state.clearSelection();

        assertFalse(state.isPieceClicked());
        assertEquals(Constants.EMPTY_STRING, state.getSelectedPiece());
        assertEquals(-1, state.getSelectedRow());
        assertEquals(-1, state.getSelectedCol());
    }

    @Test
    void movePieceUpdatesBoard() {
        state.setPiece(6, 0, Constants.WHITE_PAWN);
        state.movePiece(6, 0, 5, 0);

        assertEquals(Constants.EMPTY_STRING, state.getPiece(6, 0));
        assertEquals(Constants.WHITE_PAWN, state.getPiece(5, 0));
    }

    @Test
    void setPieceAndGetPiece() {
        state.setPiece(3, 3, Constants.BLACK_QUEEN);
        assertEquals(Constants.BLACK_QUEEN, state.getPiece(3, 3));
    }

    @Test
    void clearPieceEmptiesSquare() {
        state.setPiece(3, 3, Constants.BLACK_QUEEN);
        state.clearPiece(3, 3);
        assertEquals(Constants.EMPTY_STRING, state.getPiece(3, 3));
    }
}
