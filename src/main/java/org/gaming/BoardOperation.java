package org.gaming;

import javax.swing.*;
import java.awt.*;

public class BoardOperation {

    private static boolean pieceClicked = false;
    private static String selectedPiece = Constants.EMPTY_STRING;
    private static JButton [][] board = new JButton[Constants.ROWS][Constants.COLS];

    // true = white to move, false = black to move
    private static boolean whiteTurn = true;

    // castling / moved state
    private static boolean whiteKingMoved = false;
    private static boolean blackKingMoved = false;
    private static boolean whiteRookLeftMoved = false;   // a1
    private static boolean whiteRookRightMoved = false;  // h1
    private static boolean blackRookLeftMoved = false;   // a8
    private static boolean blackRookRightMoved = false;  // h8

    // en-passant tracking: coordinates of pawn that moved two squares on previous turn (row, col)
    private static int[] lastDoublePawn = null;

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
                // Only allow selecting a piece that belongs to the side to move
                if (!canSelect(square.getText())) return;
                selectedPiece = square.getText();
                square.setBackground(Color.YELLOW);
                pieceClicked = true;
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
                // If clicked another piece of the same side, switch selection
                if (!square.getText().isEmpty() && canSelect(square.getText())) {
                    // clear previous highlight
                    for (int x = 0; x < Constants.ROWS; x++) {
                        for (int y = 0; y < Constants.COLS; y++) {
                            if (board[x][y].getBackground().equals(Color.YELLOW)) {
                                if ((x + y) % 2 == 0) {
                                    board[x][y].setBackground(Color.LIGHT_GRAY);
                                } else {
                                    board[x][y].setBackground(Color.DARK_GRAY);
                                }
                            }
                        }
                    }
                    // set new selection
                    selectedPiece = square.getText();
                    square.setBackground(Color.YELLOW);
                    pieceClicked = true;
                    return;
                }

                if (pieceClicked) {
                    int[] src = findSelectedSquare();
                    if (src == null) {
                        // no source found - reset
                        selectedPiece = Constants.EMPTY_STRING;
                        pieceClicked = false;
                        return;
                    }
                    int fromI = src[0], fromJ = src[1];

                    // first check movement pattern (including en-passant & castling)
                    if (isLegalMove(selectedPiece, fromI, fromJ, i, j)) {
                        // ensure move does not leave own king in check (simulate)
                        boolean sideIsWhite = isWhitePiece(selectedPiece.codePointAt(0));
                        if (!wouldLeaveKingInCheck(selectedPiece, fromI, fromJ, i, j)) {
                            // perform the move (handle en-passant, castling, promotion and moved flags)
                            performMove(selectedPiece, fromI, fromJ, i, j);
                            // toggle turn after successful move
                            whiteTurn = !whiteTurn;
                        }
                    }

                    // clear any selected highlights and reset selection state
                    for (int x = 0; x < Constants.ROWS; x++) {
                        for (int y = 0; y < Constants.COLS; y++) {
                            if (board[x][y].getBackground().equals(Color.YELLOW)) {
                                if ((x + y) % 2 == 0) {
                                    board[x][y].setBackground(Color.LIGHT_GRAY);
                                } else {
                                    board[x][y].setBackground(Color.DARK_GRAY);
                                }
                            }
                        }
                    }
                    pieceClicked = false;
                    selectedPiece = Constants.EMPTY_STRING;
                }
            }
        }
    }

    /**
     * Find the coordinates of the currently selected (yellow) square.
     */
    private static int[] findSelectedSquare() {
        for (int x = 0; x < Constants.ROWS; x++) {
            for (int y = 0; y < Constants.COLS; y++) {
                if (board[x][y].getBackground().equals(Color.YELLOW)) {
                    return new int[] { x, y };
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the given piece string belongs to the side to move.
     */
    private static boolean canSelect(String pieceStr) {
        if (pieceStr == null || pieceStr.isEmpty()) return false;
        int code = pieceStr.codePointAt(0);
        return whiteTurn ? isWhitePiece(code) : isBlackPiece(code);
    }

    /**
     * Basic legality checker for moves.
     * Handles regular moves, captures, pawn double move, en-passant and castling.
     * Does not consider check here (check is enforced separately via simulation).
     */
    private static boolean isLegalMove(String pieceStr, int fromI, int fromJ, int toI, int toJ) {
        if (pieceStr == null || pieceStr.isEmpty()) return false;
        if (fromI == toI && fromJ == toJ) return false;

        String destText = board[toI][toJ].getText();
        // cannot capture own piece
        if (!destText.isEmpty() && sameColor(pieceStr, destText)) return false;

        int dx = toI - fromI;
        int dy = toJ - fromJ;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        int code = pieceStr.codePointAt(0);

        // Pawn logic (including en-passant and promotion)
        if (isPawn(code)) {
            boolean white = isWhitePiece(code);
            int dir = white ? -1 : 1;
            int startRow = white ? 6 : 1;

            // forward one
            if (dy == 0 && dx == dir && destText.isEmpty()) return true;

            // double move from start
            if (dy == 0 && dx == 2 * dir && fromI == startRow && destText.isEmpty()) {
                // intermediate must be empty
                if (board[fromI + dir][fromJ].getText().isEmpty()) return true;
            }

            // capture normal
            if (adx == 1 && ady == 1) {
                if (!destText.isEmpty() && sameColorDifferent(pieceStr, destText)) return true;
                // en-passant: destination is empty but lastDoublePawn is capturable
                if (destText.isEmpty() && lastDoublePawn != null && lastDoublePawn[0] == fromI && lastDoublePawn[1] == toJ) {
                    return true;
                }
            }
            return false;
        }

        // Knight
        if (isKnight(code)) {
            return (adx == 1 && ady == 2) || (adx == 2 && ady == 1);
        }

        // Bishop
        if (isBishop(code)) {
            if (adx == ady) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }

        // Rook
        if (isRook(code)) {
            if (fromI == toI || fromJ == toJ) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }

        // Queen
        if (isQueen(code)) {
            if (fromI == toI || fromJ == toJ || adx == ady) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }

        // King (including castling)
        if (isKing(code)) {
            // normal single-square king move
            if (Math.max(adx, ady) == 1) return true;
            // castling attempt: two-square horizontal move
            if (adx == 0 && ady == 2) {
                return canCastle(fromI, fromJ, toI, toJ);
            }
            return false;
        }

        return false;
    }

    private static boolean canCastle(int fromI, int fromJ, int toI, int toJ) {
        // king must not currently be in check
        boolean white = isWhitePiece(board[fromI][fromJ].getText().codePointAt(0));
        if (isInCheck(white)) return false;

        // must be same row, move two columns
        if (fromI != toI) return false;
        int dir = (toJ > fromJ) ? 1 : -1; // +1 king-side, -1 queen-side

        // determine rook column and moved flags
        int rookCol = (dir == 1) ? Constants.COLS - 1 : 0;
        String rookText = board[fromI][rookCol].getText();
        if (rookText.isEmpty()) return false;
        if (sameColor(board[fromI][fromJ].getText(), rookText) == false) return false; // rook must be same color

        // check moved flags
        if (white) {
            if (whiteKingMoved) return false;
            if (dir == 1 && whiteRookRightMoved) return false;
            if (dir == -1 && whiteRookLeftMoved) return false;
        } else {
            if (blackKingMoved) return false;
            if (dir == 1 && blackRookRightMoved) return false;
            if (dir == -1 && blackRookLeftMoved) return false;
        }

        // path between king and rook must be clear
        int step = dir;
        int x = fromJ + step;
        while (x != rookCol) {
            if (!board[fromI][x].getText().isEmpty()) return false;
            x += step;
        }

        // squares the king passes through (including destination) must not be attacked
        // check the two squares: fromJ + dir (passing) and fromJ + 2*dir (destination)
        int passCol = fromJ + dir;
        int destCol = fromJ + 2 * dir;
        if (isSquareAttacked(fromI, passCol, !white)) return false;
        if (isSquareAttacked(fromI, destCol, !white)) return false;

        return true;
    }

    private static boolean wouldLeaveKingInCheck(String pieceStr, int fromI, int fromJ, int toI, int toJ) {
        boolean isWhite = isWhitePiece(pieceStr.codePointAt(0));
        // store originals
        String origFrom = board[fromI][fromJ].getText();
        String origTo = board[toI][toJ].getText();
        int[] origLastDouble = (lastDoublePawn == null) ? null : new int[] { lastDoublePawn[0], lastDoublePawn[1] };

        // perform simulated move
        board[toI][toJ].setText(origFrom);
        board[fromI][fromJ].setText(Constants.EMPTY_STRING);

        // handle en-passant capture simulation: if pawn capturing en-passant, remove captured pawn
        boolean enPassant = false;
        if (isPawn(pieceStr.codePointAt(0))) {
            if (origTo.isEmpty() && Math.abs(toJ - fromJ) == 1 && toI - fromI == (isWhite ? -1 : 1)) {
                // empty dest with diagonal pawn move => possible en-passant
                if (origLastDouble != null && origLastDouble[0] == fromI && origLastDouble[1] == toJ) {
                    // remove the pawn that was double-moved
                    board[origLastDouble[0]][origLastDouble[1]].setText(Constants.EMPTY_STRING);
                    enPassant = true;
                }
            }
        }

        // handle castling simulation (move rook)
        boolean castled = false;
        if (isKing(pieceStr.codePointAt(0)) && Math.abs(toJ - fromJ) == 2) {
            int dir = (toJ > fromJ) ? 1 : -1;
            int rookFromCol = (dir == 1) ? Constants.COLS - 1 : 0;
            int rookToCol = fromJ + dir;
            String rookText = board[fromI][rookFromCol].getText();
            board[fromI][rookToCol].setText(rookText);
            board[fromI][rookFromCol].setText(Constants.EMPTY_STRING);
            castled = true;
        }

        boolean inCheck = isInCheck(isWhite);

        // restore originals
        board[fromI][fromJ].setText(origFrom);
        board[toI][toJ].setText(origTo);
        if (enPassant && origLastDouble != null) {
            // restore captured pawn
            board[origLastDouble[0]][origLastDouble[1]].setText(isWhite ? "\u265F" : "\u2659");
            // Note: this assumes the captured pawn is the correct color glyph; it's only used for simulation
        }
        if (castled) {
            // restore rook movement
            int dir = (toJ > fromJ) ? 1 : -1;
            int rookFromCol = (dir == 1) ? Constants.COLS - 1 : 0;
            int rookToCol = fromJ + dir;
            String rookText = board[fromI][rookToCol].getText();
            board[fromI][rookFromCol].setText(rookText);
            board[fromI][rookToCol].setText(Constants.EMPTY_STRING);
        }
        lastDoublePawn = (origLastDouble == null) ? null : new int[] { origLastDouble[0], origLastDouble[1] };

        return inCheck;
    }

    private static void performMove(String pieceStr, int fromI, int fromJ, int toI, int toJ) {
        int code = pieceStr.codePointAt(0);
        boolean white = isWhitePiece(code);

        // handle en-passant capture removal
        if (isPawn(code)) {
            if (board[toI][toJ].getText().isEmpty() && Math.abs(toJ - fromJ) == 1 && toI - fromI == (white ? -1 : 1)) {
                if (lastDoublePawn != null && lastDoublePawn[0] == fromI && lastDoublePawn[1] == toJ) {
                    // remove the pawn that was double-moved (captured en-passant)
                    board[lastDoublePawn[0]][lastDoublePawn[1]].setText(Constants.EMPTY_STRING);
                }
            }
        }

        // handle castling move (also move rook and set moved flags)
        if (isKing(code) && Math.abs(toJ - fromJ) == 2) {
            int dir = (toJ > fromJ) ? 1 : -1;
            int rookFromCol = (dir == 1) ? Constants.COLS - 1 : 0;
            int rookToCol = fromJ + dir;
            String rookText = board[fromI][rookFromCol].getText();
            // move king
            board[toI][toJ].setText(pieceStr);
            board[toI][toJ].setFont(Constants.PIECE_FONT);
            board[fromI][fromJ].setText(Constants.EMPTY_STRING);
            // move rook
            board[fromI][rookFromCol].setText(Constants.EMPTY_STRING);
            board[fromI][rookToCol].setText(rookText);
            board[fromI][rookToCol].setFont(Constants.PIECE_FONT);

            // update moved flags
            if (white) {
                whiteKingMoved = true;
                if (rookFromCol == 0) whiteRookLeftMoved = true;
                else whiteRookRightMoved = true;
            } else {
                blackKingMoved = true;
                if (rookFromCol == 0) blackRookLeftMoved = true;
                else blackRookRightMoved = true;
            }

            // castling clears en-passant eligibility
            lastDoublePawn = null;
            return;
        }

        // normal move (including captures)
        board[toI][toJ].setText(pieceStr);
        board[toI][toJ].setFont(Constants.PIECE_FONT);
        board[toI][toJ].setHorizontalAlignment(SwingConstants.CENTER);
        board[toI][toJ].setVerticalAlignment(SwingConstants.CENTER);
        board[fromI][fromJ].setText(Constants.EMPTY_STRING);

        // update moved flags for rooks/kings
        if (isKing(code)) {
            if (white) whiteKingMoved = true;
            else blackKingMoved = true;
        }
        if (isRook(code)) {
            if (white) {
                if (fromI == 7 && fromJ == 0) whiteRookLeftMoved = true;
                if (fromI == 7 && fromJ == 7) whiteRookRightMoved = true;
            } else {
                if (fromI == 0 && fromJ == 0) blackRookLeftMoved = true;
                if (fromI == 0 && fromJ == 7) blackRookRightMoved = true;
            }
        }

        // handle pawn double move tracking for en-passant
        if (isPawn(code)) {
            if (Math.abs(toI - fromI) == 2) {
                lastDoublePawn = new int[] { toI, toJ };
            } else {
                // if pawn moved normally (including promotion/capture), clear lastDoublePawn because en-passant only immediate
                lastDoublePawn = null;
            }

            // promotion: if pawn reaches last rank
            if ((white && toI == 0) || (!white && toI == Constants.ROWS - 1)) {
                // promote to queen by default
                board[toI][toJ].setText(white ? "\u2655" : "\u265B"); // White queen / Black queen
                board[toI][toJ].setFont(Constants.PIECE_FONT);
            }
        } else {
            // any non-pawn move clears en-passant eligibility
            lastDoublePawn = null;
        }
    }

    /**
     * Find if the given side's king is in check.
     */
    private static boolean isInCheck(boolean white) {
        // find king position
        int kingI = -1, kingJ = -1;
        String whiteKing = "\u2654";
        String blackKing = "\u265A";
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLS; j++) {
                String txt = board[i][j].getText();
                if (white && txt.equals(whiteKing)) {
                    kingI = i; kingJ = j; break;
                }
                if (!white && txt.equals(blackKing)) {
                    kingI = i; kingJ = j; break;
                }
            }
            if (kingI != -1) break;
        }
        if (kingI == -1) return false; // king not found (should not happen)

        return isSquareAttacked(kingI, kingJ, !white);
    }

    /**
     * Returns true if the square at (targetI, targetJ) is attacked by side 'byWhite'.
     */
    private static boolean isSquareAttacked(int targetI, int targetJ, boolean byWhite) {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLS; j++) {
                String txt = board[i][j].getText();
                if (txt.isEmpty()) continue;
                int code = txt.codePointAt(0);
                if (byWhite == isWhitePiece(code)) {
                    if (canAttack(txt, i, j, targetI, targetJ)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Like isLegalMove but used for attack detection: ignores capturing color at destination,
     * and treats target square occupancy appropriately for pawns (pawn attacks are diagonals).
     */
    private static boolean canAttack(String pieceStr, int fromI, int fromJ, int toI, int toJ) {
        if (pieceStr == null || pieceStr.isEmpty()) return false;
        if (fromI == toI && fromJ == toJ) return false;

        int dx = toI - fromI;
        int dy = toJ - fromJ;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        int code = pieceStr.codePointAt(0);

        if (isPawn(code)) {
            boolean white = isWhitePiece(code);
            int dir = white ? -1 : 1;
            // pawn attacks diagonally forward by one
            return dx == dir && ady == 1;
        }
        if (isKnight(code)) {
            return (adx == 1 && ady == 2) || (adx == 2 && ady == 1);
        }
        if (isBishop(code)) {
            if (adx == ady) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }
        if (isRook(code)) {
            if (fromI == toI || fromJ == toJ) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }
        if (isQueen(code)) {
            if (fromI == toI || fromJ == toJ || adx == ady) return pathClear(fromI, fromJ, toI, toJ);
            return false;
        }
        if (isKing(code)) {
            return Math.max(adx, ady) == 1;
        }
        return false;
    }

    private static boolean pathClear(int fromI, int fromJ, int toI, int toJ) {
        int dx = Integer.compare(toI, fromI);
        int dy = Integer.compare(toJ, fromJ);
        int x = fromI + dx;
        int y = fromJ + dy;
        while (x != toI || y != toJ) {
            if (!board[x][y].getText().isEmpty()) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    private static boolean sameColor(String a, String b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return false;
        return (isWhitePiece(a.codePointAt(0)) && isWhitePiece(b.codePointAt(0)))
                || (isBlackPiece(a.codePointAt(0)) && isBlackPiece(b.codePointAt(0)));
    }

    private static boolean sameColorDifferent(String a, String b) {
        return !sameColor(a, b) && !b.isEmpty();
    }

    private static boolean isWhitePiece(int code) {
        return code >= 0x2654 && code <= 0x2659;
    }

    private static boolean isBlackPiece(int code) {
        return code >= 0x265A && code <= 0x265F;
    }

    private static boolean isPawn(int code) {
        return code == 0x2659 || code == 0x265F;
    }

    private static boolean isRook(int code) {
        return code == 0x2656 || code == 0x265C;
    }

    private static boolean isKnight(int code) {
        return code == 0x2658 || code == 0x265E;
    }

    private static boolean isBishop(int code) {
        return code == 0x2657 || code == 0x265D;
    }

    private static boolean isQueen(int code) {
        return code == 0x2655 || code == 0x265B;
    }

    private static boolean isKing(int code) {
        return code == 0x2654 || code == 0x265A;
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

        // reset state tracking
        whiteTurn = true;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookLeftMoved = false;
        whiteRookRightMoved = false;
        blackRookLeftMoved = false;
        blackRookRightMoved = false;
        lastDoublePawn = null;
    }
}
