package org.gaming;

import javax.swing.*;
import java.awt.*;

public class BoardUI {

    private final JButton[][] buttons;
    private final GameState gameState;

    public BoardUI(GameState gameState) {
        this.gameState = gameState;
        this.buttons = new JButton[Constants.ROWS][Constants.COLS];
    }

    /**
     * Sets up the chess board squares and attaches click listeners.
     */
    public void setUpBoard(JFrame frame) {
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                JButton button = createSquareButton(row, col);
                buttons[row][col] = button;
                frame.add(button);
            }
        }
    }

    /**
     * Initializes the game state board and syncs all pieces to the UI.
     */
    public void initializePieces() {
        gameState.initializeBoard();
        syncAllSquares();
    }

    private JButton createSquareButton(int row, int col) {
        JButton button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setBounds(
                Constants.BOARD_OFFSET + col * Constants.SQUARE_SIZE,
                Constants.BOARD_OFFSET + row * Constants.SQUARE_SIZE,
                Constants.SQUARE_SIZE, Constants.SQUARE_SIZE
        );
        button.setBackground(getSquareColor(row, col));
        int finalRow = row;
        int finalCol = col;
        button.addActionListener(listener -> handleSquareClick(finalRow, finalCol));
        return button;
    }

    private void handleSquareClick(int row, int col) {
        if (!gameState.isPieceClicked()) {
            handlePieceSelection(row, col);
        } else if (row == gameState.getSelectedRow() && col == gameState.getSelectedCol()) {
            handlePieceDeselection(row, col);
        } else {
            handleMoveAttempt(row, col);
        }
    }

    private void handlePieceSelection(int row, int col) {
        if (!gameState.getPiece(row, col).isEmpty()) {
            gameState.selectPiece(row, col);
            buttons[row][col].setBackground(Color.YELLOW);
        }
    }

    private void handlePieceDeselection(int row, int col) {
        gameState.clearSelection();
        buttons[row][col].setBackground(getSquareColor(row, col));
    }

    private void handleMoveAttempt(int destRow, int destCol) {
        int srcRow = gameState.getSelectedRow();
        int srcCol = gameState.getSelectedCol();
        String piece = gameState.getSelectedPiece();

        if (MoveValidator.isLegalMove(gameState, piece, destRow, destCol, srcRow, srcCol)) {
            buttons[srcRow][srcCol].setBackground(getSquareColor(srcRow, srcCol));
            executeCastlingRookMove(piece, srcRow, srcCol, destRow, destCol);
            gameState.movePiece(srcRow, srcCol, destRow, destCol);
            syncSquare(destRow, destCol);
            syncSquare(srcRow, srcCol);
            gameState.clearSelection();
            gameState.flipTurn();
        }
    }

    private void executeCastlingRookMove(String piece, int srcRow, int srcCol,
                                         int destRow, int destCol) {
        if (!Constants.isKing(piece) || srcRow != destRow || Math.abs(destCol - srcCol) != 2) {
            return;
        }

        if (destCol == Constants.KINGSIDE_CASTLE_COL) {
            gameState.movePiece(destRow, Constants.KINGSIDE_ROOK_COL,
                    destRow, Constants.KINGSIDE_ROOK_DEST_COL);
            syncSquare(destRow, Constants.KINGSIDE_ROOK_COL);
            syncSquare(destRow, Constants.KINGSIDE_ROOK_DEST_COL);
        } else if (destCol == Constants.QUEENSIDE_CASTLE_COL) {
            gameState.movePiece(destRow, Constants.QUEENSIDE_ROOK_COL,
                    destRow, Constants.QUEENSIDE_ROOK_DEST_COL);
            syncSquare(destRow, Constants.QUEENSIDE_ROOK_COL);
            syncSquare(destRow, Constants.QUEENSIDE_ROOK_DEST_COL);
        }
    }

    private void syncSquare(int row, int col) {
        String piece = gameState.getPiece(row, col);
        buttons[row][col].setText(piece);
        if (!piece.isEmpty()) {
            buttons[row][col].setFont(Constants.PIECE_FONT);
            buttons[row][col].setHorizontalAlignment(SwingConstants.CENTER);
            buttons[row][col].setVerticalAlignment(SwingConstants.CENTER);
        }
    }

    private void syncAllSquares() {
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                syncSquare(row, col);
            }
        }
    }

    private static Color getSquareColor(int row, int col) {
        return (row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
    }
}
