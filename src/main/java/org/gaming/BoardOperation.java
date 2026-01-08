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
                    square.setText(selectedPiece);
                    square.setFont(Constants.PIECE_FONT);
                    square.setHorizontalAlignment(SwingConstants.CENTER);
                    square.setVerticalAlignment(SwingConstants.CENTER);
                    pieceClicked = !pieceClicked;
                    selectedPiece = Constants.EMPTY_STRING;
                    for (int x = 0; x < Constants.ROWS; x++) {
                        for (int y = 0; y < Constants.COLS; y++) {
                            if (board[x][y].getBackground().equals(Color.YELLOW)) {
                                if ((x + y) % 2 == 0) {
                                    board[x][y].setBackground(Color.LIGHT_GRAY);
                                } else {
                                    board[x][y].setBackground(Color.DARK_GRAY);
                                }
                                board[x][y].setText(Constants.EMPTY_STRING);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to add chess pieces to the board.
     */
    public static void addPieces() {
        board[0][0].setText("\u265C");
        board[0][0].setFont(Constants.PIECE_FONT);
        board[0][1].setText("\u265E");
        board[0][1].setFont(Constants.PIECE_FONT);
        board[0][2].setText("\u265D");
        board[0][2].setFont(Constants.PIECE_FONT);
        board[0][3].setText("\u265B");
        board[0][3].setFont(Constants.PIECE_FONT);
        board[0][4].setText("\u265A");
        board[0][4].setFont(Constants.PIECE_FONT);
        board[0][5].setText("\u265D");
        board[0][5].setFont(Constants.PIECE_FONT);
        board[0][6].setText("\u265E");
        board[0][6].setFont(Constants.PIECE_FONT);
        board[0][7].setText("\u265C");
        board[0][7].setFont(Constants.PIECE_FONT);
        for (int j = 0; j < Constants.COLS; j++) {
            board[1][j].setText("\u265F");
            board[1][j].setFont(Constants.PIECE_FONT);
        }
        board[7][0].setText("\u2656");
        board[7][0].setFont(Constants.PIECE_FONT);
        board[7][1].setText("\u2658");
        board[7][1].setFont(Constants.PIECE_FONT);
        board[7][2].setText("\u2657");
        board[7][2].setFont(Constants.PIECE_FONT);
        board[7][3].setText("\u2655");
        board[7][3].setFont(Constants.PIECE_FONT);
        board[7][4].setText("\u2654");
        board[7][4].setFont(Constants.PIECE_FONT);
        board[7][5].setText("\u2657");
        board[7][5].setFont(Constants.PIECE_FONT);
        board[7][6].setText("\u2658");
        board[7][6].setFont(Constants.PIECE_FONT);
        board[7][7].setText("\u2656");
        board[7][7].setFont(Constants.PIECE_FONT);
        for (int j = 0; j < Constants.COLS; j++) {
            board[6][j].setText("\u2659");
            board[6][j].setFont(Constants.PIECE_FONT);
        }
    }
}
