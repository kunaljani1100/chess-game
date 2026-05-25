package org.gaming;

import java.util.List;

public final class MoveValidator {

    private MoveValidator() {}

    /**
     * Determines whether a move is legal for the given piece.
     * @param state the current game state
     * @param piece the piece being moved
     * @param newRow destination row
     * @param newCol destination column
     * @param oldRow source row
     * @param oldCol source column
     * @return true if the move is legal, false otherwise
     */
    public static boolean isLegalMove(GameState state, String piece, int newRow, int newCol, int oldRow, int oldCol) {
        boolean isWhite = Constants.isWhitePiece(piece);
        if (isWhite != state.isWhiteTurn()) {
            return false;
        }

        if (Constants.isPawn(piece)) {
            return isValidPawnMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol);
        } else if (Constants.isRook(piece)) {
            return isValidSlidingMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol,
                    (nr, nc, or, oc) -> nr == or || nc == oc);
        } else if (Constants.isBishop(piece)) {
            return isValidSlidingMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol,
                    (nr, nc, or, oc) -> Math.abs(nr - or) == Math.abs(nc - oc));
        } else if (Constants.isQueen(piece)) {
            return isValidSlidingMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol,
                    (nr, nc, or, oc) -> (nr == or || nc == oc)
                            || Math.abs(nr - or) == Math.abs(nc - oc));
        } else if (Constants.isKing(piece)) {
            return isValidKingMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol);
        } else if (Constants.isKnight(piece)) {
            return isValidKnightMove(state, piece, isWhite, newRow, newCol, oldRow, oldCol);
        }

        return false;
    }

    private static boolean isValidPawnMove(GameState state, String piece, boolean isWhite,
                                           int newRow, int newCol, int oldRow, int oldCol) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? Constants.WHITE_PAWN_START_ROW : Constants.BLACK_PAWN_START_ROW;
        List<String> enemyPieces = Constants.getEnemyPieces(isWhite);

        if (newRow == oldRow + 2 * direction && newCol == oldCol && oldRow == startRow
                && state.getPiece(newRow, newCol).isEmpty()
                && isPathClear(state, oldRow, oldCol, newRow, newCol)
                && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol)) {
            return true;
        }

        if (newRow == oldRow + direction && newCol == oldCol
                && state.getPiece(newRow, newCol).isEmpty()
                && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol)) {
            return true;
        }

        return newRow == oldRow + direction && Math.abs(newCol - oldCol) == 1
                && enemyPieces.contains(state.getPiece(newRow, newCol))
                && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol);
    }

    private static boolean isValidSlidingMove(GameState state, String piece, boolean isWhite,
                                              int newRow, int newCol, int oldRow, int oldCol,
                                              MovementPattern pattern) {
        List<String> friendlyPieces = Constants.getFriendlyPieces(isWhite);
        if (friendlyPieces.contains(state.getPiece(newRow, newCol))) {
            return false;
        }
        return pattern.matches(newRow, newCol, oldRow, oldCol)
                && isPathClear(state, oldRow, oldCol, newRow, newCol)
                && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol);
    }

    private static boolean isValidKnightMove(GameState state, String piece, boolean isWhite,
                                             int newRow, int newCol, int oldRow, int oldCol) {
        List<String> friendlyPieces = Constants.getFriendlyPieces(isWhite);
        if (friendlyPieces.contains(state.getPiece(newRow, newCol))) {
            return false;
        }
        int rowDiff = Math.abs(newRow - oldRow);
        int colDiff = Math.abs(newCol - oldCol);
        return ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))
                && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol);
    }

    private static boolean isValidKingMove(GameState state, String piece, boolean isWhite,
                                           int newRow, int newCol, int oldRow, int oldCol) {
        List<String> friendlyPieces = Constants.getFriendlyPieces(isWhite);
        if (friendlyPieces.contains(state.getPiece(newRow, newCol))) {
            return false;
        }

        if (oldRow == newRow && Math.abs(newCol - oldCol) == 2) {
            return isValidCastling(state, isWhite, oldRow, oldCol, newRow, newCol);
        }

        if (Math.abs(newRow - oldRow) <= 1 && Math.abs(newCol - oldCol) <= 1) {
            return !isSquareUnderAttack(state, newRow, newCol, isWhite)
                    && !wouldMovePutKingInCheck(state, piece, newRow, newCol, oldRow, oldCol);
        }

        return false;
    }

    static boolean isValidCastling(GameState state, boolean isWhite,
                                   int oldRow, int oldCol, int newRow, int newCol) {
        if (oldRow != newRow || Math.abs(newCol - oldCol) != 2) {
            return false;
        }
        if (!state.getPiece(newRow, newCol).isEmpty()) {
            return false;
        }

        int homeRow = isWhite ? Constants.WHITE_HOME_ROW : Constants.BLACK_HOME_ROW;
        String expectedRook = isWhite ? Constants.WHITE_ROOK : Constants.BLACK_ROOK;

        if (oldRow != homeRow || oldCol != Constants.KING_START_COL) {
            return false;
        }
        if (isSquareUnderAttack(state, oldRow, oldCol, isWhite)) {
            return false;
        }

        if (newCol == Constants.KINGSIDE_CASTLE_COL) {
            return isValidKingsideCastling(state, homeRow, expectedRook, isWhite);
        } else if (newCol == Constants.QUEENSIDE_CASTLE_COL) {
            return isValidQueensideCastling(state, homeRow, expectedRook, isWhite);
        }

        return false;
    }

    private static boolean isValidKingsideCastling(GameState state, int homeRow,
                                                   String expectedRook, boolean isWhite) {
        if (!state.getPiece(homeRow, Constants.KINGSIDE_ROOK_COL).equals(expectedRook)) {
            return false;
        }
        if (!state.getPiece(homeRow, Constants.KINGSIDE_ROOK_DEST_COL).isEmpty()
                || !state.getPiece(homeRow, Constants.KINGSIDE_CASTLE_COL).isEmpty()) {
            return false;
        }
        if (isSquareUnderAttack(state, homeRow, Constants.KINGSIDE_ROOK_DEST_COL, isWhite)) {
            return false;
        }
        return !isSquareUnderAttack(state, homeRow, Constants.KINGSIDE_CASTLE_COL, isWhite);
    }

    private static boolean isValidQueensideCastling(GameState state, int homeRow,
                                                    String expectedRook, boolean isWhite) {
        if (!state.getPiece(homeRow, Constants.QUEENSIDE_ROOK_COL).equals(expectedRook)) {
            return false;
        }
        if (!state.getPiece(homeRow, 1).isEmpty()
                || !state.getPiece(homeRow, Constants.QUEENSIDE_CASTLE_COL).isEmpty()
                || !state.getPiece(homeRow, Constants.QUEENSIDE_ROOK_DEST_COL).isEmpty()) {
            return false;
        }
        if (isSquareUnderAttack(state, homeRow, Constants.QUEENSIDE_ROOK_DEST_COL, isWhite)) {
            return false;
        }
        return !isSquareUnderAttack(state, homeRow, Constants.QUEENSIDE_CASTLE_COL, isWhite);
    }

    /**
     * Checks if the path between two squares is clear (no pieces blocking).
     */
    static boolean isPathClear(GameState state, int oldRow, int oldCol, int newRow, int newCol) {
        int deltaRow = Integer.compare(newRow, oldRow);
        int deltaCol = Integer.compare(newCol, oldCol);

        int currentRow = oldRow + deltaRow;
        int currentCol = oldCol + deltaCol;

        while (currentRow != newRow || currentCol != newCol) {
            if (!state.getPiece(currentRow, currentCol).isEmpty()) {
                return false;
            }
            currentRow += deltaRow;
            currentCol += deltaCol;
        }

        return true;
    }

    /**
     * Finds the position of the king for the specified color.
     * @return an array with [row, col] coordinates of the king, or null if not found
     */
    static int[] findKingPosition(GameState state, boolean isWhite) {
        String kingPiece = isWhite ? Constants.WHITE_KING : Constants.BLACK_KING;
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                if (state.getPiece(row, col).equals(kingPiece)) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Checks if a square is under attack by any opponent piece.
     * @param isWhiteKing true if checking attacks against white, false for black
     */
    static boolean isSquareUnderAttack(GameState state, int targetRow, int targetCol, boolean isWhiteKing) {
        List<String> opponentPieces = isWhiteKing
                ? Constants.CAPTURABLE_BLACK_PIECES
                : Constants.CAPTURABLE_WHITE_PIECES;

        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                String piece = state.getPiece(row, col);
                if (opponentPieces.contains(piece)
                        && canPieceAttackSquare(state, piece, row, col, targetRow, targetCol)) {
                    return true;
                }
            }
        }

        String opponentKing = isWhiteKing ? Constants.BLACK_KING : Constants.WHITE_KING;
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                if (state.getPiece(row, col).equals(opponentKing)
                        && Math.abs(targetRow - row) <= 1
                        && Math.abs(targetCol - col) <= 1) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a specific piece can attack a target square (used for check detection).
     */
    static boolean canPieceAttackSquare(GameState state, String piece,
                                        int pieceRow, int pieceCol, int targetRow, int targetCol) {
        if (Constants.isPawn(piece)) {
            int direction = piece.equals(Constants.WHITE_PAWN) ? -1 : 1;
            return targetRow == pieceRow + direction && Math.abs(targetCol - pieceCol) == 1;
        } else if (Constants.isRook(piece)) {
            return (targetRow == pieceRow || targetCol == pieceCol)
                    && isPathClear(state, pieceRow, pieceCol, targetRow, targetCol);
        } else if (Constants.isBishop(piece)) {
            return Math.abs(targetRow - pieceRow) == Math.abs(targetCol - pieceCol)
                    && isPathClear(state, pieceRow, pieceCol, targetRow, targetCol);
        } else if (Constants.isQueen(piece)) {
            boolean isStraight = (targetRow == pieceRow || targetCol == pieceCol);
            boolean isDiagonal = Math.abs(targetRow - pieceRow) == Math.abs(targetCol - pieceCol);
            return (isStraight || isDiagonal)
                    && isPathClear(state, pieceRow, pieceCol, targetRow, targetCol);
        } else if (Constants.isKnight(piece)) {
            int rowDiff = Math.abs(targetRow - pieceRow);
            int colDiff = Math.abs(targetCol - pieceCol);
            return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        }

        return false;
    }

    /**
     * Checks if making a move would leave the moving player's king in check.
     * Simulates the move, checks for check, then restores the board state.
     */
    static boolean wouldMovePutKingInCheck(GameState state, String piece,
                                           int newRow, int newCol, int oldRow, int oldCol) {
        String capturedPiece = state.getPiece(newRow, newCol);
        String originalPiece = state.getPiece(oldRow, oldCol);

        state.setPiece(oldRow, oldCol, Constants.EMPTY_STRING);
        state.setPiece(newRow, newCol, piece);

        boolean isWhite = Constants.isWhitePiece(piece);
        int[] kingPos = findKingPosition(state, isWhite);
        boolean inCheck = kingPos != null && isSquareUnderAttack(state, kingPos[0], kingPos[1], isWhite);

        state.setPiece(oldRow, oldCol, originalPiece);
        state.setPiece(newRow, newCol, capturedPiece);

        return inCheck;
    }

    @FunctionalInterface
    interface MovementPattern {
        boolean matches(int newRow, int newCol, int oldRow, int oldCol);
    }
}
