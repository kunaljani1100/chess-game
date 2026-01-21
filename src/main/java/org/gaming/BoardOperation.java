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
            if (newX == oldX - 2 && newY == oldY && oldX == 6) {
                if (isPathClear(oldX, oldY, newX, newY)) {
                    isWhiteTurn = false;
                    return true;
                }
            }
            if (newX == oldX - 1 && newY == oldY) {
                // Single square move doesn't need path check, but check destination
                isWhiteTurn = false;
                return true;
            }
            if (newX == oldX - 1 && Math.abs(newY - oldY) == 1 && captureableBlackPieces.contains(board[newX][newY].getText())) {
                // Capture move
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_PAWN)) {
            if (newX == oldX + 2 && newY == oldY && oldX == 1) {
                if (isPathClear(oldX, oldY, newX, newY)) {
                    isWhiteTurn = true;
                    return true;
                }
            }
            if (newX == oldX + 1 && newY == oldY) {
                // Single square move doesn't need path check, but check destination
                isWhiteTurn = true;
                return true;
            }
            if (newX == oldX + 1 && Math.abs(newY - oldY) == 1 && captureableWhitePieces.contains(board[newX][newY].getText())) {
                // Capture move
                isWhiteTurn = true;
                return true;
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_ROOK)) {
            if ((newX == oldX || newY == oldY) && isPathClear(oldX, oldY, newX, newY) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_ROOK) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY) && isPathClear(oldX, oldY, newX, newY)) {
                isWhiteTurn = true;
                return true;
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_BISHOP) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) == Math.abs(newY - oldY) && isPathClear(oldX, oldY, newX, newY)) {
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_BISHOP) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) == Math.abs(newY - oldY) && isPathClear(oldX, oldY, newX, newY)) {
                isWhiteTurn = true;
                return true;
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_QUEEN) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY || Math.abs(newX - oldX) == Math.abs(newY - oldY)) 
                    && isPathClear(oldX, oldY, newX, newY)) {
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_QUEEN) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((newX == oldX || newY == oldY || Math.abs(newX - oldX) == Math.abs(newY - oldY)) 
                    && isPathClear(oldX, oldY, newX, newY)) {
                isWhiteTurn = true;
                return true;
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_KING) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) <= 1 && Math.abs(newY - oldY) <= 1) {
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_KING) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if (Math.abs(newX - oldX) <= 1 && Math.abs(newY - oldY) <= 1) {
                isWhiteTurn = true;
                return true;
            }
        } else if (isWhiteTurn && piece.equals(Constants.WHITE_KNIGHT) && !captureableWhitePieces.contains(board[newX][newY].getText())) {
            if ((Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 1) ||
                   (Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 2)) {
                isWhiteTurn = false;
                return true;
            }
        } else if (!isWhiteTurn && piece.equals(Constants.BLACK_KNIGHT) && !captureableBlackPieces.contains(board[newX][newY].getText())) {
            if ((Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 1) ||
                   (Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 2)) {
                isWhiteTurn = true;
                return true;
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
