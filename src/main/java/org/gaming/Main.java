package org.gaming;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Chess");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(null);
        jFrame.setSize(800, 800);
        BoardOperation.setUpBoard(jFrame);
        BoardOperation.addPieces();
        jFrame.setVisible(true);
    }
}