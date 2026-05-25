package org.gaming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveValidatorTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        state = new GameState();
    }

    private void setUpInitialBoard() {
        state.initializeBoard();
    }

    @Nested
    class PawnMoves {

        @Test
        void whitePawnSingleAdvance() {
            setUpInitialBoard();
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 0, 6, 0));
        }

        @Test
        void whitePawnDoubleAdvanceFromStartRow() {
            setUpInitialBoard();
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 4, 0, 6, 0));
        }

        @Test
        void whitePawnCannotDoubleAdvanceFromNonStartRow() {
            state.setPiece(5, 0, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 3, 0, 5, 0));
        }

        @Test
        void whitePawnCannotMoveToOccupiedSquare() {
            setUpInitialBoard();
            state.setPiece(5, 0, Constants.BLACK_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 0, 6, 0));
        }

        @Test
        void whitePawnCaptureDiagonally() {
            setUpInitialBoard();
            state.setPiece(5, 1, Constants.BLACK_PAWN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 1, 6, 0));
        }

        @Test
        void whitePawnCannotCaptureOwnPiece() {
            state.setPiece(6, 0, Constants.WHITE_PAWN);
            state.setPiece(5, 1, Constants.WHITE_KNIGHT);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 1, 6, 0));
        }

        @Test
        void whitePawnCannotMoveBackward() {
            state.setPiece(5, 0, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 6, 0, 5, 0));
        }

        @Test
        void blackPawnSingleAdvance() {
            state.setPiece(1, 0, Constants.BLACK_PAWN);
            state.flipTurn();
            assertTrue(MoveValidator.isLegalMove(state, Constants.BLACK_PAWN, 2, 0, 1, 0));
        }

        @Test
        void blackPawnDoubleAdvanceFromStartRow() {
            state.setPiece(1, 0, Constants.BLACK_PAWN);
            state.flipTurn();
            assertTrue(MoveValidator.isLegalMove(state, Constants.BLACK_PAWN, 3, 0, 1, 0));
        }

        @Test
        void blackPawnCaptureDiagonally() {
            state.setPiece(1, 0, Constants.BLACK_PAWN);
            state.setPiece(2, 1, Constants.WHITE_PAWN);
            state.flipTurn();
            assertTrue(MoveValidator.isLegalMove(state, Constants.BLACK_PAWN, 2, 1, 1, 0));
        }

        @Test
        void whitePawnDoubleAdvanceBlockedByPiece() {
            setUpInitialBoard();
            state.setPiece(5, 0, Constants.BLACK_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 4, 0, 6, 0));
        }
    }

    @Nested
    class RookMoves {

        @Test
        void rookMovesHorizontally() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 4, 5, 4, 0));
        }

        @Test
        void rookMovesVertically() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 0, 0, 4, 0));
        }

        @Test
        void rookCannotMoveDiagonally() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 3, 1, 4, 0));
        }

        @Test
        void rookBlockedByPiece() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            state.setPiece(4, 3, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 4, 5, 4, 0));
        }

        @Test
        void rookCapturesEnemyPiece() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            state.setPiece(4, 5, Constants.BLACK_PAWN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 4, 5, 4, 0));
        }

        @Test
        void rookCannotCaptureOwnPiece() {
            state.setPiece(4, 0, Constants.WHITE_ROOK);
            state.setPiece(4, 5, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 4, 5, 4, 0));
        }
    }

    @Nested
    class BishopMoves {

        @Test
        void bishopMovesDiagonally() {
            state.setPiece(4, 4, Constants.WHITE_BISHOP);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_BISHOP, 2, 6, 4, 4));
        }

        @Test
        void bishopCannotMoveStraight() {
            state.setPiece(4, 4, Constants.WHITE_BISHOP);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_BISHOP, 4, 6, 4, 4));
        }

        @Test
        void bishopBlockedByPiece() {
            state.setPiece(4, 4, Constants.WHITE_BISHOP);
            state.setPiece(3, 5, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_BISHOP, 2, 6, 4, 4));
        }
    }

    @Nested
    class QueenMoves {

        @Test
        void queenMovesDiagonally() {
            state.setPiece(4, 4, Constants.WHITE_QUEEN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_QUEEN, 2, 6, 4, 4));
        }

        @Test
        void queenMovesHorizontally() {
            state.setPiece(4, 4, Constants.WHITE_QUEEN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_QUEEN, 4, 7, 4, 4));
        }

        @Test
        void queenMovesVertically() {
            state.setPiece(4, 4, Constants.WHITE_QUEEN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_QUEEN, 0, 4, 4, 4));
        }

        @Test
        void queenCannotMoveInLShape() {
            state.setPiece(4, 4, Constants.WHITE_QUEEN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_QUEEN, 3, 6, 4, 4));
        }
    }

    @Nested
    class KnightMoves {

        @Test
        void knightMovesInLShape() {
            state.setPiece(4, 4, Constants.WHITE_KNIGHT);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KNIGHT, 2, 5, 4, 4));
        }

        @Test
        void knightMovesInReverseLShape() {
            state.setPiece(4, 4, Constants.WHITE_KNIGHT);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KNIGHT, 3, 2, 4, 4));
        }

        @Test
        void knightCanJumpOverPieces() {
            state.setPiece(4, 4, Constants.WHITE_KNIGHT);
            state.setPiece(3, 4, Constants.WHITE_PAWN);
            state.setPiece(3, 5, Constants.WHITE_PAWN);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KNIGHT, 2, 5, 4, 4));
        }

        @Test
        void knightCannotMoveStraight() {
            state.setPiece(4, 4, Constants.WHITE_KNIGHT);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KNIGHT, 2, 4, 4, 4));
        }
    }

    @Nested
    class KingMoves {

        @Test
        void kingMovesOneSquare() {
            state.setPiece(4, 4, Constants.WHITE_KING);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 3, 4, 4, 4));
        }

        @Test
        void kingMovesOneSquareDiagonally() {
            state.setPiece(4, 4, Constants.WHITE_KING);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 3, 5, 4, 4));
        }

        @Test
        void kingCannotMoveTwoSquaresNonCastle() {
            state.setPiece(4, 4, Constants.WHITE_KING);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 2, 4, 4, 4));
        }

        @Test
        void kingCannotMoveIntoAttackedSquare() {
            state.setPiece(4, 4, Constants.WHITE_KING);
            state.setPiece(2, 5, Constants.BLACK_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 3, 5, 4, 4));
        }
    }

    @Nested
    class Castling {

        @Test
        void whiteKingsideCastling() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(7, 7, Constants.WHITE_ROOK);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 6, 7, 4));
        }

        @Test
        void whiteQueensideCastling() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(7, 0, Constants.WHITE_ROOK);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 2, 7, 4));
        }

        @Test
        void blackKingsideCastling() {
            state.setPiece(0, 4, Constants.BLACK_KING);
            state.setPiece(0, 7, Constants.BLACK_ROOK);
            state.flipTurn();
            assertTrue(MoveValidator.isLegalMove(state, Constants.BLACK_KING, 0, 6, 0, 4));
        }

        @Test
        void blackQueensideCastling() {
            state.setPiece(0, 4, Constants.BLACK_KING);
            state.setPiece(0, 0, Constants.BLACK_ROOK);
            state.flipTurn();
            assertTrue(MoveValidator.isLegalMove(state, Constants.BLACK_KING, 0, 2, 0, 4));
        }

        @Test
        void castlingBlockedByPieceBetween() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(7, 7, Constants.WHITE_ROOK);
            state.setPiece(7, 5, Constants.WHITE_BISHOP);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 6, 7, 4));
        }

        @Test
        void castlingBlockedWhenKingInCheck() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(7, 7, Constants.WHITE_ROOK);
            state.setPiece(0, 4, Constants.BLACK_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 6, 7, 4));
        }

        @Test
        void castlingBlockedWhenKingPassesThroughCheck() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(7, 7, Constants.WHITE_ROOK);
            state.setPiece(0, 5, Constants.BLACK_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 6, 7, 4));
        }

        @Test
        void castlingBlockedWhenNoRook() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 6, 7, 4));
        }

        @Test
        void castlingBlockedWhenKingNotOnStartSquare() {
            state.setPiece(7, 3, Constants.WHITE_KING);
            state.setPiece(7, 7, Constants.WHITE_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 5, 7, 3));
        }
    }

    @Nested
    class TurnEnforcement {

        @Test
        void whitePawnCannotMoveOnBlackTurn() {
            state.setPiece(6, 0, Constants.WHITE_PAWN);
            state.flipTurn();
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 0, 6, 0));
        }

        @Test
        void blackPawnCannotMoveOnWhiteTurn() {
            state.setPiece(1, 0, Constants.BLACK_PAWN);
            assertFalse(MoveValidator.isLegalMove(state, Constants.BLACK_PAWN, 2, 0, 1, 0));
        }
    }

    @Nested
    class CheckDetection {

        @Test
        void cannotMovePieceIfItExposesKingToCheck() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(6, 4, Constants.WHITE_ROOK);
            state.setPiece(0, 4, Constants.BLACK_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_ROOK, 6, 5, 6, 4));
        }

        @Test
        void canMovePieceIfItDoesNotExposeKing() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(6, 0, Constants.WHITE_PAWN);
            state.setPiece(0, 3, Constants.BLACK_ROOK);
            assertTrue(MoveValidator.isLegalMove(state, Constants.WHITE_PAWN, 5, 0, 6, 0));
        }

        @Test
        void kingCannotMoveIntoCheck() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            state.setPiece(0, 3, Constants.BLACK_ROOK);
            assertFalse(MoveValidator.isLegalMove(state, Constants.WHITE_KING, 7, 3, 7, 4));
        }
    }

    @Nested
    class PathClear {

        @Test
        void pathClearOnEmptyBoard() {
            assertTrue(MoveValidator.isPathClear(state, 0, 0, 0, 7));
        }

        @Test
        void pathBlockedByPiece() {
            state.setPiece(0, 3, Constants.WHITE_PAWN);
            assertFalse(MoveValidator.isPathClear(state, 0, 0, 0, 7));
        }

        @Test
        void diagonalPathClear() {
            assertTrue(MoveValidator.isPathClear(state, 0, 0, 7, 7));
        }

        @Test
        void diagonalPathBlocked() {
            state.setPiece(3, 3, Constants.BLACK_PAWN);
            assertFalse(MoveValidator.isPathClear(state, 0, 0, 7, 7));
        }
    }

    @Nested
    class FindKingPosition {

        @Test
        void findsWhiteKing() {
            state.setPiece(7, 4, Constants.WHITE_KING);
            int[] pos = MoveValidator.findKingPosition(state, true);
            assertNotNull(pos);
            assertEquals(7, pos[0]);
            assertEquals(4, pos[1]);
        }

        @Test
        void findsBlackKing() {
            state.setPiece(0, 4, Constants.BLACK_KING);
            int[] pos = MoveValidator.findKingPosition(state, false);
            assertNotNull(pos);
            assertEquals(0, pos[0]);
            assertEquals(4, pos[1]);
        }

        @Test
        void returnsNullWhenKingNotOnBoard() {
            assertNull(MoveValidator.findKingPosition(state, true));
        }
    }
}
