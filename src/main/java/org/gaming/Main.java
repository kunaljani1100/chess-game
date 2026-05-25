package org.gaming;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);

        GameState gameState = new GameState();
        BoardUI boardUI = new BoardUI(gameState);
        boardUI.setUpBoard(frame);
        boardUI.initializePieces();

        frame.setVisible(true);
    }
}
