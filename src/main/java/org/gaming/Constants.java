package org.gaming;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Constants {

    public static final int ROWS = 8;
    public static final int COLS = 8;
    public static final int SQUARE_SIZE = 80;
    public static final int BOARD_OFFSET = 20;
    public static final int WINDOW_SIZE = 800;
    public static final int PIECE_SIZE = 60;
    public static final Font PIECE_FONT = new Font("Serif", Font.PLAIN, PIECE_SIZE);
    public static final String EMPTY_STRING = "";

    public static final String WHITE_PAWN = "\u2659";
    public static final String BLACK_PAWN = "\u265F";
    public static final String WHITE_ROOK = "\u2656";
    public static final String BLACK_ROOK = "\u265C";
    public static final String WHITE_KNIGHT = "\u2658";
    public static final String BLACK_KNIGHT = "\u265E";
    public static final String WHITE_BISHOP = "\u2657";
    public static final String BLACK_BISHOP = "\u265D";
    public static final String WHITE_QUEEN = "\u2655";
    public static final String BLACK_QUEEN = "\u265B";
    public static final String WHITE_KING = "\u2654";
    public static final String BLACK_KING = "\u265A";

    public static final int KING_START_COL = 4;
    public static final int KINGSIDE_CASTLE_COL = 6;
    public static final int QUEENSIDE_CASTLE_COL = 2;
    public static final int KINGSIDE_ROOK_COL = 7;
    public static final int QUEENSIDE_ROOK_COL = 0;
    public static final int KINGSIDE_ROOK_DEST_COL = 5;
    public static final int QUEENSIDE_ROOK_DEST_COL = 3;
    public static final int WHITE_HOME_ROW = 7;
    public static final int BLACK_HOME_ROW = 0;
    public static final int WHITE_PAWN_START_ROW = 6;
    public static final int BLACK_PAWN_START_ROW = 1;

    public static final List<String> CAPTURABLE_BLACK_PIECES = Collections.unmodifiableList(Arrays.asList(
            BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN
    ));

    public static final List<String> CAPTURABLE_WHITE_PIECES = Collections.unmodifiableList(Arrays.asList(
            WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN
    ));

    public static final String[] WHITE_BACK_RANK = {
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
            WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK
    };

    public static final String[] BLACK_BACK_RANK = {
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
            BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
    };

    private Constants() {}

    public static boolean isWhitePiece(String piece) {
        return CAPTURABLE_WHITE_PIECES.contains(piece) || WHITE_KING.equals(piece);
    }

    public static boolean isPawn(String piece) {
        return WHITE_PAWN.equals(piece) || BLACK_PAWN.equals(piece);
    }

    public static boolean isRook(String piece) {
        return WHITE_ROOK.equals(piece) || BLACK_ROOK.equals(piece);
    }

    public static boolean isBishop(String piece) {
        return WHITE_BISHOP.equals(piece) || BLACK_BISHOP.equals(piece);
    }

    public static boolean isQueen(String piece) {
        return WHITE_QUEEN.equals(piece) || BLACK_QUEEN.equals(piece);
    }

    public static boolean isKing(String piece) {
        return WHITE_KING.equals(piece) || BLACK_KING.equals(piece);
    }

    public static boolean isKnight(String piece) {
        return WHITE_KNIGHT.equals(piece) || BLACK_KNIGHT.equals(piece);
    }

    public static List<String> getFriendlyPieces(boolean isWhite) {
        return isWhite ? CAPTURABLE_WHITE_PIECES : CAPTURABLE_BLACK_PIECES;
    }

    public static List<String> getEnemyPieces(boolean isWhite) {
        return isWhite ? CAPTURABLE_BLACK_PIECES : CAPTURABLE_WHITE_PIECES;
    }
}
