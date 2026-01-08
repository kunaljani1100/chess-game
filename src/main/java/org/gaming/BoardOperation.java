package org.gaming;

import javax.swing.*;
import java.awt.*;

public class BoardOperation {

    /**
     * Function to set up the squares of the chess board.
     * @param board The board where the squares are to be set up.
     * @param jFrame The JFrame to which the squares are added.
     */
    public static void setUpBoard(JButton [][] board, JFrame jFrame) {
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
                board[i][j].addActionListener(listener -> {
                    System.out.println("Button at (" + finalI + ", " + finalJ + ") clicked.");
                    System.out.println(board[finalI][finalJ].getText());
                });
                jFrame.add(board[i][j]);
            }
        }
    }

    /**
     * Function to add chess pieces to the board.
     * @param board The board where the pieces are to be added.
     */
    public static void addPieces(JButton [][] board) {
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
