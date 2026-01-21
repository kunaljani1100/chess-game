package org.gaming;

import javax.swing.*;
import java.awt.*;

public class BoardOperation {

    private static boolean pieceClicked = false;
    private static String selectedPiece = Constants.EMPTY_STRING;
    private static JButton [][] board = new JButton[Constants.ROWS][Constants.COLS];

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

    public static boolean isLegalMove(String piece, int newX, int newY, int oldX, int oldY) {
        if (piece.equals(Constants.WHITE_PAWN)) {
            if (newX == oldX - 2 && newY == oldY && oldX == 6) {
                return true;
            }
            return newX == oldX - 1 && newY == oldY;
        } else if (piece.equals(Constants.BLACK_PAWN)) {
            if (newX == oldX + 2 && newY == oldY && oldX == 1) {
                return true;
            }
            return newX == oldX + 1 && newY == oldY;
        } else if (piece.equals(Constants.WHITE_ROOK)) {
            return newX == oldX || newY == oldY;
        } else if (piece.equals(Constants.BLACK_ROOK)) {
            return newX == oldX || newY == oldY;
        }
        return true;
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
