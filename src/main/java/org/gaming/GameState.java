package org.gaming;

public class GameState {

    private final String[][] board;
    private boolean pieceClicked;
    private String selectedPiece;
    private int selectedRow;
    private int selectedCol;
    private boolean whiteTurn;

    public GameState() {
        this.board = new String[Constants.ROWS][Constants.COLS];
        this.pieceClicked = false;
        this.selectedPiece = Constants.EMPTY_STRING;
        this.selectedRow = -1;
        this.selectedCol = -1;
        this.whiteTurn = true;
        clearBoard();
    }

    private void clearBoard() {
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                board[row][col] = Constants.EMPTY_STRING;
            }
        }
    }

    public void initializeBoard() {
        clearBoard();
        for (int col = 0; col < Constants.COLS; col++) {
            board[Constants.BLACK_HOME_ROW][col] = Constants.BLACK_BACK_RANK[col];
            board[Constants.BLACK_PAWN_START_ROW][col] = Constants.BLACK_PAWN;
            board[Constants.WHITE_PAWN_START_ROW][col] = Constants.WHITE_PAWN;
            board[Constants.WHITE_HOME_ROW][col] = Constants.WHITE_BACK_RANK[col];
        }
    }

    public String getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, String piece) {
        board[row][col] = piece;
    }

    public void clearPiece(int row, int col) {
        board[row][col] = Constants.EMPTY_STRING;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = Constants.EMPTY_STRING;
    }

    public boolean isPieceClicked() {
        return pieceClicked;
    }

    public String getSelectedPiece() {
        return selectedPiece;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void selectPiece(int row, int col) {
        this.selectedPiece = board[row][col];
        this.selectedRow = row;
        this.selectedCol = col;
        this.pieceClicked = true;
    }

    public void clearSelection() {
        this.selectedPiece = Constants.EMPTY_STRING;
        this.selectedRow = -1;
        this.selectedCol = -1;
        this.pieceClicked = false;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void flipTurn() {
        this.whiteTurn = !this.whiteTurn;
    }
}
