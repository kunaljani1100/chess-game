package org.gaming;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BoardOperation {

    private static boolean pieceClicked = false;
    private static String selectedPiece = Constants.EMPTY_STRING;
    private static JButton [][] board = new JButton[Constants.ROWS][Constants.COLS];
    private static boolean isWhiteTurn = true;
    private static List<String> captureableBlackPieces = Arrays.asList(
            Constants.BLACK_PAWN, Constants.BLACK_ROOK, Constants.BLACK_KNIGHT,
            Constants.BLACK_BISHOP, Constants.BLACK_QUEEN
    );
    private static List<String> captureableWhitePieces = Arrays.asList(
            Constants.WHITE_PAWN, Constants.WHITE_ROOK, Constants.WHITE_KNIGHT,
            Constants.WHITE_BISHOP, Constants.WHITE_QUEEN
    );

    /**
     * Function to set up the squares of the chess board.
     * @param jFrame The JFrame to which the squares are added.
     */
    public static void setUpBoard(JFrame jFrame) {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLS; j++) {
                board[i][j] = new JButton();
                board[i][j].setOpaque(true);
                board[i][j].setBorderPainted(false);
                board[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                board[i][j].setVerticalAlignment(SwingConstants.CENTER);
                board[i][j].setBounds(20 + j * 80, 20 + i * 80, 80, 80);
                if ((i + j) % 2 == 0) {
                    board[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    board[i][j].setBackground(Color.DARK_GRAY);
                }
                int finalI = i;
                int finalJ = j;
                board[i][j].addActionListener(listener -> resetBoardColors(board[finalI][finalJ], finalI, finalJ));
                jFrame.add(board[i][j]);
            }
        }
    }

    private static void resetBoardColors(JButton square, int i, int j) {
        if (!pieceClicked) {
            if (!square.getText().isEmpty()) {
                selectedPiece = square.getText();
                square.setBackground(Color.YELLOW);
                pieceClicked = !pieceClicked;
            }
        } else {
            if (square.getBackground().equals(Color.YELLOW)) {
                selectedPiece = Constants.EMPTY_STRING;
                if ((i + j) % 2 == 0) {
                   square.setBackground(Color.LIGHT_GRAY);
                } else {
                    square.setBackground(Color.DARK_GRAY);
                }
                pieceClicked = !pieceClicked;
            } else {
                if (pieceClicked) {
                    for (int x = 0; x < Constants.ROWS; x++) {
                        for (int y = 0; y < Constants.COLS; y++) {
                            if (board[x][y].getBackground().equals(Color.YELLOW)) {
                                if (isLegalMove(selectedPiece, i, j, x, y)) {
                                    // Move is legal, implement move logic here
                                    if ((x + y) % 2 == 0) {
                                        board[x][y].setBackground(Color.LIGHT_GRAY);
                                    } else {
                                        board[x][y].setBackground(Color.DARK_GRAY);
                                    }
                                    square.setText(selectedPiece);
                                    square.setFont(Constants.PIECE_FONT);
                                    square.setHorizontalAlignment(SwingConstants.CENTER);
                                    square.setVerticalAlignment(SwingConstants.CENTER);
                                    pieceClicked = !pieceClicked;
                                    selectedPiece = Constants.EMPTY_STRING;
                                    board[x][y].setText(Constants.EMPTY_STRING);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds the position of the king for the specified color.
     * @param isWhite true for white king, false for black king
     * @return an array with [x, y] coordinates of the king, or null if not found
     */
    private static int[] findKingPosition(boolean isWhite) {
        String kingPiece = isWhite ? Constants.WHITE_KING : Constants.BLACK_KING;
        for (int x = 0; x < Constants.ROWS; x++) {
            for (int y = 0; y < Constants.COLS; y++) {
                if (board[x][y].getText().equals(kingPiece)) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    /**
     * Checks if a square is under attack by any opponent piece.
     * @param targetX The row position to check
     * @param targetY The column position to check
     * @param isWhiteKing true if checking for white king, false for black king
     * @return true if the square is under attack, false otherwise
     */
    private static boolean isSquareUnderAttack(int targetX, int targetY, boolean isWhiteKing) {
        List<String> opponentPieces = isWhiteKing ? captureableBlackPieces : captureableWhitePieces;
        
        // Check all opponent pieces to see if any can attack the target square
        for (int x = 0; x < Constants.ROWS; x++) {
            for (int y = 0; y < Constants.COLS; y++) {
                String piece = board[x][y].getText();
                if (opponentPieces.contains(piece)) {
                    // Check if this opponent piece can legally move to the target square
                    if (canPieceAttackSquare(piece, x, y, targetX, targetY)) {
                        return true;
                    }
                }
            }
        }
        
        // Also check opponent king
        String opponentKing = isWhiteKing ? Constants.BLACK_KING : Constants.WHITE_KING;
        for (int x = 0; x < Constants.ROWS; x++) {
            for (int y = 0; y < Constants.COLS; y++) {
                if (board[x][y].getText().equals(opponentKing)) {
                    if (Math.abs(targetX - x) <= 1 && Math.abs(targetY - y) <= 1) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Checks if a specific piece can attack a target square (ignoring check rules).
     * This is used for check detection.
     */
    private static boolean canPieceAttackSquare(String piece, int pieceX, int pieceY, int targetX, int targetY) {
        // Check if piece can move to target square (ignoring path blocking temporarily for check detection)
        if (piece.equals(Constants.WHITE_PAWN) || piece.equals(Constants.BLACK_PAWN)) {
            // Pawns attack diagonally
            int direction = piece.equals(Constants.WHITE_PAWN) ? -1 : 1;
            if (targetX == pieceX + direction && Math.abs(targetY - pieceY) == 1) {
                return true;
            }
        } else if (piece.equals(Constants.WHITE_ROOK) || piece.equals(Constants.BLACK_ROOK)) {
            if ((targetX == pieceX || targetY == pieceY) && isPathClear(pieceX, pieceY, targetX, targetY)) {
                return true;
            }
        } else if (piece.equals(Constants.WHITE_BISHOP) || piece.equals(Constants.BLACK_BISHOP)) {
            if (Math.abs(targetX - pieceX) == Math.abs(targetY - pieceY) && isPathClear(pieceX, pieceY, targetX, targetY)) {
                return true;
            }
        } else if (piece.equals(Constants.WHITE_QUEEN) || piece.equals(Constants.BLACK_QUEEN)) {
            if ((targetX == pieceX || targetY == pieceY || Math.abs(targetX - pieceX) == Math.abs(targetY - pieceY))
                    && isPathClear(pieceX, pieceY, targetX, targetY)) {
                return true;
            }
        } else if (piece.equals(Constants.WHITE_KNIGHT) || piece.equals(Constants.BLACK_KNIGHT)) {
            if ((Math.abs(targetX - pieceX) == 2 && Math.abs(targetY - pieceY) == 1) ||
                (Math.abs(targetX - pieceX) == 1 && Math.abs(targetY - pieceY) == 2)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks if making a move would leave the moving player's king in check.
     * @param piece The piece being moved
     * @param newX Destination row
     * @param newY Destination column
     * @param oldX Source row
     * @param oldY Source column
     * @return true if the move would put the king in check (illegal), false otherwise
     */
    private static boolean wouldMovePutKingInCheck(String piece, int newX, int newY, int oldX, int oldY) {
        // Save the state of the destination square
        String capturedPiece = board[newX][newY].getText();
        
        // Simulate the move
        String originalPiece = board[oldX][oldY].getText();
        board[oldX][oldY].setText(Constants.EMPTY_STRING);
        board[newX][newY].setText(piece);
        
        // Determine which king to check (based on the piece being moved)
        boolean isWhiteKing = captureableWhitePieces.contains(piece) || piece.equals(Constants.WHITE_KING);
        
        // Find the king's position after the move
        int[] kingPos = findKingPosition(isWhiteKing);
        boolean inCheck = false;
        
        if (kingPos != null) {
            // Check if the king would be in check
            inCheck = isSquareUnderAttack(kingPos[0], kingPos[1], isWhiteKing);
        }
        
        // Restore the board state
        board[oldX][oldY].setText(originalPiece);
        board[newX][newY].setText(capturedPiece);
        
        return inCheck;
    }

    /**
     * Checks if the path between two squares is clear (no pieces blocking the path).
     * This prevents pieces from jumping over other pieces.
     * @param oldX Starting row position
     * @param oldY Starting column position
     * @param newX Destination row position
     * @param newY Destination column position
     * @return true if the path is clear, false if there are pieces blocking
     */
    private static boolean isPathClear(int oldX, int oldY, int newX, int newY) {
        // Calculate direction of movement
        int deltaX = Integer.compare(newX, oldX);
        int deltaY = Integer.compare(newY, oldY);
        
        // Start checking from the square next to the source (not including source or destination)
        int currentX = oldX + deltaX;
        int currentY = oldY + deltaY;
        
        // Check each square along the path until we reach the destination
        while (currentX != newX || currentY != newY) {
            // If we find a piece on any intermediate square, the path is blocked
            if (!board[currentX][currentY].getText().isEmpty()) {
                return false;
            }
            currentX += deltaX;
            currentY += deltaY;
        }
        
        // Path is clear
        return true;
    }

    public static boolean isLegalMove(String piece, int newX, int newY, int oldX, int oldY) {
        if (isWhiteTurn && piece.equals(Constants.WHITE_PAWN)) {
            if (newX == oldX - 2 && newY == oldY && oldX == 6 && board[newX][newY].getText().isEmpty()) {
                if (isPathClear(oldX, oldY, newX, newY)) {
                    if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                        isWhiteTurn = false;
                        return true;
                    }
                }
            }
            if (newX == oldX - 1 && newY == oldY && board[newX][newY].getText().isEmpty()) {
                // Single square move doesn't need path check, but check destination
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
            if (newX == oldX - 1 && Math.abs(newY - oldY) == 1 && captureableBlackPieces.contains(board[newX][newY].getText())) {
                // Capture move
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_PAWN)) {
            if (newX == oldX + 2 && newY == oldY && oldX == 1 && board[newX][newY].getText().isEmpty()) {
                if (isPathClear(oldX, oldY, newX, newY)) {
                    if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                        isWhiteTurn = true;
                        return true;
                    }
                }
            }
            if (newX == oldX + 1 && newY == oldY && board[newX][newY].getText().isEmpty()) {
                // Single square move doesn't need path check, but check destination
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
            if (newX == oldX + 1 && Math.abs(newY - oldY) == 1 && captureableWhitePieces.contains(board[newX][newY].getText())) {
                // Capture move
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_ROOK)) {
            if ((newX == oldX || newY == oldY) && isPathClear(oldX, oldY, newX, newY) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_ROOK) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY) && isPathClear(oldX, oldY, newX, newY)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_BISHOP) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) == Math.abs(newY - oldY) && isPathClear(oldX, oldY, newX, newY)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_BISHOP) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) == Math.abs(newY - oldY) && isPathClear(oldX, oldY, newX, newY)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_QUEEN) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY || Math.abs(newX - oldX) == Math.abs(newY - oldY)) 
                    && isPathClear(oldX, oldY, newX, newY)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_QUEEN) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY || Math.abs(newX - oldX) == Math.abs(newY - oldY)) 
                    && isPathClear(oldX, oldY, newX, newY)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_KING) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) <= 1 && Math.abs(newY - oldY) <= 1) {
                // For king moves, also check if destination is under attack
                if (!isSquareUnderAttack(newX, newY, true) && !wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_KING) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) <= 1 && Math.abs(newY - oldY) <= 1) {
                // For king moves, also check if destination is under attack
                if (!isSquareUnderAttack(newX, newY, false) && !wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_KNIGHT) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if ((Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 1) ||
                   (Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 2)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_KNIGHT) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 1) ||
                   (Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 2)) {
                if (!wouldMovePutKingInCheck(piece, newX, newY, oldX, oldY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function to add chess pieces to the board.
     */
    public static void addPieces() {
        board[0][0].setText(Constants.BLACK_ROOK);
        board[0][0].setFont(Constants.PIECE_FONT);
        board[0][1].setText(Constants.BLACK_KNIGHT);
        board[0][1].setFont(Constants.PIECE_FONT);
        board[0][2].setText(Constants.BLACK_BISHOP);
        board[0][2].setFont(Constants.PIECE_FONT);
        board[0][3].setText(Constants.BLACK_QUEEN);
        board[0][3].setFont(Constants.PIECE_FONT);
        board[0][4].setText(Constants.BLACK_KING);
        board[0][4].setFont(Constants.PIECE_FONT);
        board[0][5].setText(Constants.BLACK_BISHOP);
        board[0][5].setFont(Constants.PIECE_FONT);
        board[0][6].setText(Constants.BLACK_KNIGHT);
        board[0][6].setFont(Constants.PIECE_FONT);
        board[0][7].setText(Constants.BLACK_ROOK);
        board[0][7].setFont(Constants.PIECE_FONT);
        for (int j = 0; j < Constants.COLS; j++) {
            board[1][j].setText(Constants.BLACK_PAWN);
            board[1][j].setFont(Constants.PIECE_FONT);
        }
        board[7][0].setText(Constants.WHITE_ROOK);
        board[7][0].setFont(Constants.PIECE_FONT);
        board[7][1].setText(Constants.WHITE_KNIGHT);
        board[7][1].setFont(Constants.PIECE_FONT);
        board[7][2].setText(Constants.WHITE_BISHOP);
        board[7][2].setFont(Constants.PIECE_FONT);
        board[7][3].setText(Constants.WHITE_QUEEN);
        board[7][3].setFont(Constants.PIECE_FONT);
        board[7][4].setText(Constants.WHITE_KING);
        board[7][4].setFont(Constants.PIECE_FONT);
        board[7][5].setText(Constants.WHITE_BISHOP);
        board[7][5].setFont(Constants.PIECE_FONT);
        board[7][6].setText(Constants.WHITE_KNIGHT);
        board[7][6].setFont(Constants.PIECE_FONT);
        board[7][7].setText(Constants.WHITE_ROOK);
        board[7][7].setFont(Constants.PIECE_FONT);
        for (int j = 0; j < Constants.COLS; j++) {
            board[6][j].setText(Constants.WHITE_PAWN);
            board[6][j].setFont(Constants.PIECE_FONT);
        }
    }
}
