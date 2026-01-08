package org.gaming;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Chess");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(null);
        jFrame.setSize(800, 800);
        JButton [][] board = new JButton[8][8];
        BoardOperation.setUpBoard(board, jFrame);
        BoardOperation.addPieces(board);
        jFrame.setVisible(true);
    }
}