import java.awt.*;

/**
 * 3p71 Term Project: Chess
 *
 * Francis Monwe
 * 6724355
 *
 * Jashandeep Pannu
 * 6505861
 *
 * This class implements the majority of the chess game's logic
 */

public class Piece {

    //these arrays hold the conditions for en passant
    static boolean[] enPassantW = new boolean[8];
    static boolean[] enPassantB = new boolean[8];
    public static String[][] board = new String[8][8];
    //these booleans hold the conditions for castling

    Piece (){
        setupBoard(board);
        //drawBoard(board);
    }

    /**
     * Sets up the starting state of a board
     * @param board the board to be set up
     */
    private void setupBoard(String[][] board) {

        //Setting up white pieces
        board[0][0] = "wR";
        board[1][0] = "wN";
        board[2][0] = "wB";
        board[3][0] = "wQ";
        board[4][0] = "wK";
        board[5][0] = "wB";
        board[6][0] = "wN";
        board[7][0] = "wR";
        for (int i=0;i<8;i++) {
            board[i][1] = "wP";
        }
        //Setting up black pieces
        board[0][7] = "bR";
        board[1][7] = "bN";
        board[2][7] = "bB";
        board[3][7] = "bQ";
        board[4][7] = "bK";
        board[5][7] = "bB";
        board[6][7] = "bN";
        board[7][7] = "bR";
        for (int i=0;i<8;i++) {
            board[i][6] = "bP";
        }
        //createEmptySpaces
        for (int i=0; i<8; i ++) {
            for (int j=2; j<6; j++ ) {
                board[i][j] = "  ";
            }
        }
    }

    /**
     * Draws a board in console
     * @param in board that needs to be drawn
     */
    static public  void drawBoard(String[][]in) {
        //Draw updated board for players
        for (int i=7; i>-1;i--){
            System.out.println("-----------------------------------------");
            for (String[] strings : in) {
                System.out.print("| " + strings[i] + " ");
            }
            System.out.println("| "+(i+1));
        }
        System.out.println("-----------------------------------------");
        System.out.println("  A    B    C    D    E    F    G    H\n");
    }

    /**
     * Performs white piece movement
     * @param pos1 position of pieces that needs to be moved
     * @param pos2 position
     * @param board board where movement takes place
     * @param move whether or not the piece should actually be removed on the given board
     */
    static public boolean moveWhitePiece(Point pos1, Point pos2, String[][]board,boolean move) {
        int x1 = pos1.x;
        int x2 = pos2.x;
        int y1 = pos1.y;
        int y2 = pos2.y;
        String piece = board[x1][y1];

        if (piece.equals("  ") || piece.charAt(0)=='b' || piece.charAt(0)!='w' ) {
            //System.out.println("SELECTION ERROR: White piece was not chosen");
            return false;
        }

        //set en passant conditions to false
        for (int i = 0; i<8; i++) {
            enPassantW[i]=false;
        }
        //Look for self-check
        if (moveCheck(board,x1,y1,x2,y2,'w')) return false;

        //move a Pawn
        if (piece.equals("wP")) {
            //en passant
            if (y1==4 && y2==5 && x1==x2 && board[x2][y2].equals("  ") && (enPassantB[x1+1] || enPassantB[x1-1])) {
                if(move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                    if (enPassantB[x1 + 1]) {
                        board[x1 + 1][y1] = "  ";
                    } else {
                        board[x1 - 1][y1] = "  ";
                    }
                }
                return true;
            }
            //if the movement is within same column
            if ( x1 == x2) {
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == 1 || (y2-y1 == 2 && y1 == 1 && board[x1][y1 + 1].equals("  "))) {
                    //if the space is empty
                    if (board[x2][y2].equals("  ")) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                            //set value for en passant true on a double move
                            if (y1 == 1 && y2 - y1 == 2) {
                                enPassantW[x1] = true;
                            }
                        }
                        return true;
                    } else {
                        //System.out.println("ERROR 100: Invalid movement");
                        return false;
                    }
                } else {
                    //System.out.println("ERROR 101: Invalid movement");
                    return false;
                }
            }

            //if the movement is a diagonal take
            if ((x1 == x2-1 || x1 == x2+1) && (y2-y1==1)) {
                if (board[x2][y2].charAt(0)=='b'){
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    //System.out.println("ERROR 102: Invalid movement");
                    return false;
                }
            }

        }

        //move the King
        if (piece.equals("wK")) {
            //castling
            if (board[1][0].equals("  ") && board[2][0].equals("  ") && board[3][0].equals("  ") && board[0][0].equals("wR") && y1 == 0 && x1 == 4 && y2 == 0 && x2 == 2) {
                if (!check(board,'w')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[0][0] = "  ";
                        board[3][0] = "wR";
                    }
                    return true;
                }
            }
            if (board[5][0].equals("  ") && board[6][0].equals("  ") && x2==6 && y2==0 && board[7][0].equals("wR")) {
                if (!check(board,'w')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[7][0] = "  ";
                        board[5][0] = "wR";
                    }
                    return true;
                }
            }
            //check if movement is to same spot
            if (x1==x2 && y1==y2) {
                //System.out.println("ERROR 105: Invalid movement");
                return false;
            }
            //check range of movement
            if ((x2-x1==1 || x2==x1 || x2-x1==-1)&&(y2-y1==1 || y2==y1 || y2-y1==-1)) {
                //check if space is empty or if there's a black piece
                if (board[x2][y2].equals("  ") || board[x2][y2].charAt(0)=='b') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    //System.out.println("ERROR 106: Invalid movement");
                    return false;
                }
            } else {
                //System.out.println("ERROR 107: Invalid movement");
                return false;
            }
        }

        //Rook
        if (piece.equals("wR")) {
            //check if movement is in same spot and if it is either exclusively vertical or horizontal
            if ((x1==x2 && y1==y2)&&!(y1==y2 || x1==x2)) {
                //System.out.println("ERROR 108: Invalid movement");
                return false;
            }
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is downwards
            if (y2-y1 < 0 && x1==x2) {
                //loop up each tile
                for (int i=1; y1-i>-1;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is left
            if (x2-x1 < 0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1-i>-1;i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is right
            if (x2-x1 >  0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1+i<8;i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //System.out.println("ERROR 111: Invalid movement");
            return false;
        }

        //Bishop
        if (piece.equals("wB")) {
            //Error if movement is within same column or row
            if (y1==y2 || x1==x2) {
                //System.out.println("ERROR 112: Invalid movement");
                return false;
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 113: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 114: Invalid movement");
                        return false;
                    }
                }
            }
            //up-left diagonal
            if (y2-y1 > 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && x1-i>-1);i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 115: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 116: Invalid movement");
                        return false;
                    }
                }
            }
            //down-right diagonal
            if (y2-y1 < 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && i+x1<8);i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 117: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 118: Invalid movement");
                        return false;
                    }
                }
            }
            //down-left diagonal
            if (y2-y1 < 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && x1-i>-1);i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 119: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 120: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Queen
        if (piece.equals("wQ")) {
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 121: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 122: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is downwards
            if (y2-y1 < 0 && x1==x2) {
                //loop up each tile
                for (int i=1; y1-i>-1;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 123: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 124: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is left
            if (x2-x1 < 0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1-i>-1;i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 125: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 126: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is right
            if (x2-x1 >  0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1+i<8;i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 127: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 128: Invalid movement");
                        return false;
                    }
                }
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 129: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 130: Invalid movement");
                        return false;
                    }
                }
            }
            //up-left diagonal
            if (y2-y1 > 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && x1-i>-1);i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 131: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 132: Invalid movement");
                        return false;
                    }
                }
            }
            //down-right diagonal
            if (y2-y1 < 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && i+x1<8);i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 133: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 134: Invalid movement");
                        return false;
                    }
                }
            }
            //down-left diagonal
            if (y2-y1 < 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && x1-i>-1);i++) {
                    //check for a blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 135: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 136: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Knight
        if (piece.equals("wN")) {
            //upwards
            if ((y2==y1+2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //downwards
            if ((y2==y1-2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //right
            if ((x2==x1+2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //left
            if ((x2==x1-2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //System.out.println("ERROR 137: Invalid movement");
            return false;
        }

        //System.out.println("Unknown Error: ???");
        return false;
    }

    /**
     * Performs black piece movement
     * @param pos1 position of pieces that needs to be moved
     * @param pos2 position
     * @param board board where movement takes place
     * @param move whether or not the piece should actually be removed on the given board
     */
    static public boolean moveBlackPiece(Point pos1, Point pos2, String[][]board, boolean move) {
        int x1 = pos1.x;
        int x2 = pos2.x;
        int y1 = pos1.y;
        int y2 = pos2.y;
        String piece = board[x1][y1];

        if (piece.equals("  ") || piece.charAt(0)=='w' || piece.charAt(0)!='b' ) {
            //System.out.println("SELECTION ERROR: Black piece was not chosen");
            return false;
        }
        if (move) {
            //set en passant conditions to false
            for (int i = 0; i < 8; i++) {
                enPassantB[i] = false;
            }
        }
        //If this move would cause a self-check
        if (moveCheck(board,x1,y1,x2,y2,'b')) return false;

        //move a Pawn
        if (piece.equals("bP")) {
            //if the movement is within same column
            if ( x1 == x2) {
                //en passant
                if (y1==3 && y2==2 && x1==x2 && board[x2][y2].equals("  ") && (enPassantW[x1+1] || enPassantW[x1-1])) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        if (enPassantW[x1 + 1]) {
                            board[x1 + 1][y1] = "  ";
                        } else {
                            board[x1 - 1][y1] = "  ";
                        }
                    }
                    return true;
                }
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == -1 || (y2-y1 == -2 && y1 == 6  && board[x1][y1 - 1].equals("  "))) {
                    //if the space is empty
                    if (board[x2][y2].equals("  ")) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                            if (y1 == 6 && y2 - y1 == -2) {
                                enPassantB[x1] = true;
                            }
                        }
                        return true;
                    } else {
                        //System.out.println("ERROR 100: Invalid movement");
                        return false;
                    }
                } else {
                    //System.out.println("ERROR 101: Invalid movement");
                    return false;
                }
            }
            //if the movement is a diagonal take
            if ((x1 == x2-1 || x1 == x2+1) && (y2-y1==-1)) {
                if (board[x2][y2].charAt(0)=='w'){
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    //System.out.println("ERROR 102: Invalid movement");
                    return false;
                }
            }
        }

        //move the King
        if (piece.equals("bK")) {
            //castling
            if (board[1][7].equals("  ") && board[2][7].equals("  ") && board[3][7].equals("  ") && board[0][7].equals("bR") && y1 == 7 && x1 == 4 && y2 == 7 && x2 == 2) {
                if (!check(board,'b')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[0][7] = "  ";
                        board[3][7] = "bR";
                    }
                    return true;
                }
            }
            if (board[5][7].equals("  ") && board[6][7].equals("  ") && x2==6 && y2==7 && board[7][7].equals("bR")) {
                if (!check(board,'b')) {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                        board[7][7] = "  ";
                        board[5][7] = "bR";
                    }
                    return true;
                }
            }
            //check if movement is to same spot
            if (x1==x2 && y1==y2) {
                return false;
            }
            //check range of movement
            if ((x2-x1==1 || x2==x1 || x2-x1==-1)&&(y2-y1==1 || y2==y1 || y2-y1==-1)) {
                //check if space is empty or if there's a black piece
                if (board[x2][y2].equals("  ") || board[x2][y2].charAt(0)=='w') {
                    if (move) {
                        board[x2][y2] = piece;
                        board[x1][y1] = "  ";
                    }
                    return true;
                } else {
                    //System.out.println("ERROR 106: Invalid movement");
                    return false;
                }
            } else {
                //System.out.println("ERROR 107: Invalid movement");
                return false;
            }
        }

        //Rook
        if (piece.equals("bR")) {
            //check if movement is in same spot and if it is either exclusively vertical or horizontal
            if ((x1==x2 && y1==y2)||!(y1==y2 || x1==x2)) {
                //System.out.println("ERROR 108: Invalid movement");
                return false;
            }
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is downwards
            if (y2-y1 < 0 && x1==x2) {
                //loop up each tile
                for (int i=1; y1-i>-1;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is left
            if (x2-x1 < 0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1-i>-1;i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is right
            if (x2-x1 >  0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1+i<8;i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            //System.out.println("ERROR 111: Invalid movement");
            return false;
        }

        //Bishop
        if (piece.equals("bB")) {
            //Error if movement is within same column or row
            if (y1==y2 || x1==x2) {
                //System.out.println("ERROR 112: Invalid movement");
                return false;
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 113: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 114: Invalid movement");
                        return false;
                    }
                }
            }
            //up-left diagonal
            if (y2-y1 > 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && x1-i>-1);i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 115: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 116: Invalid movement");
                        return false;
                    }
                }
            }
            //down-right diagonal
            if (y2-y1 < 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && i+x1<8);i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 117: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 118: Invalid movement");
                        return false;
                    }
                }
            }
            //down-left diagonal
            if (y2-y1 < 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && x1-i>-1);i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 119: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 120: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Queen
        if (piece.equals("bQ")) {
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 121: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 122: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is downwards
            if (y2-y1 < 0 && x1==x2) {
                //loop up each tile
                for (int i=1; y1-i>-1;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 123: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 124: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is left
            if (x2-x1 < 0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1-i>-1;i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 125: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 126: Invalid movement");
                        return false;
                    }
                }
            }
            //if movement is right
            if (x2-x1 >  0 && y1==y2) {
                //loop up each tile
                for (int i=1; x1+i<8;i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        //System.out.println("ERROR 127: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        //System.out.println("ERROR 128: Invalid movement");
                        return false;
                    }
                }
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 129: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 130: Invalid movement");
                        return false;
                    }
                }
            }
            //up-left diagonal
            if (y2-y1 > 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && x1-i>-1);i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        //System.out.println("ERROR 131: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        //System.out.println("ERROR 132: Invalid movement");
                        return false;
                    }
                }
            }
            //down-right diagonal
            if (y2-y1 < 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && i+x1<8);i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 133: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 134: Invalid movement");
                        return false;
                    }
                }
            }
            //down-left diagonal
            if (y2-y1 < 0 && x2-x1 < 0) {
                //loop up each tile
                for (int i=1; (y1-i>-1 && x1-i>-1);i++) {
                    //check for a blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        //System.out.println("ERROR 135: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        if (move) {
                            board[x2][y2] = piece;
                            board[x1][y1] = "  ";
                        }
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        //System.out.println("ERROR 136: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Knight
        if (piece.equals("bN")) {
            //upwards
            if ((y2==y1+2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //downwards
            if ((y2==y1-2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //right
            if ((x2==x1+2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //left
            if ((x2==x1-2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                if (move) {
                    board[x2][y2] = piece;
                    board[x1][y1] = "  ";
                }
                return true;
            }
            //System.out.println("ERROR 137: Invalid movement");
            return false;
        }

        //System.out.println("Unknown Error: ???");
        return false;
    }

    /**
     * Checks to see a player is under check
     * @param board the board to check
     * @param colour the colour of the player
     * @return true if colour player is under check
     */
    static public boolean check(String[][] board, char colour) {
        //Setting up string of enemy pieces, based on received colour paramater
        String king = colour + "K";
        char opposite;
        if (colour=='w') {
            opposite = 'b';
        } else {
            opposite = 'w';
        }
        String knight = opposite + "N";
        String queen = opposite + "Q";
        String rook = opposite + "R";
        String bishop = opposite + "B";
        String pawn = opposite + "P";
        //Setting up x and y which will hold king's location. -1 set for later break condition.
        int x=0;
        int y=0;

        //look for king, place coordinates in x and y
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (king.equals(board[i][j])) {
                    x=i;
                    y=j;
                    break;
                }
            }
        }

        //This block looks for checks from enemy pawns
        //left
        if (x-1>-1) {
            if ((colour=='w')&&(y+1<8)&&(board[x-1][y+1].equals(pawn)) ) {
                return true;
            }
            if ((colour=='b')&&(y-1>-1)&&(board[x-1][y-1].equals(pawn)) ) {
                return true;
            }
        }
        //right
        if (x+1<8) {
            if ((colour=='w')&&(y+1<8)&&(board[x+1][y+1].equals(pawn)) ) {
                return true;
            }
            if ((colour=='b')&&(y-1>-1)&&(board[x+1][y-1].equals(pawn)) ) {
                return true;
            }
        }

        //This block looks for checks from enemy knights
        //check upwards for knights
        if (y+2<8) {
            //check up-left
            if ((x-1>-1)&&(board[x-1][y+2].equals(knight))) {
                return true;
            }
            //check up-right
            if ((x+1<8)&&(board[x+1][y+2].equals(knight))) {
                return true;
            }
        }
        //check downwards for knights
        if (y-2>-1) {
            //check up-left
            if ((x-1>-1)&&(board[x-1][y-2].equals(knight))) {
                return true;
            }
            //check up-right
            if ((x+1<8)&&(board[x+1][y-2].equals(knight))) {
                return true;
            }
        }
        //check left for knights
        if (x+2<8) {
            //check left-up
            if ((y-1>-1)&&(board[x+2][y-1].equals(knight))) {
                return true;
            }
            //checks left-down
            if ((y+1<8)&&(board[x+2][y+1].equals(knight))) {
                return true;
            }
        }
        //check right for knights
        if (x-2>-1) {
            //check left-up
            if ((y-1>-1)&&(board[x-2][y-1].equals(knight))) {
                return true;
            }
            //checks left-down
            if ((y+1<8)&&(board[x-2][y+1].equals(knight))) {
                return true;
            }
        }

        //Horizontal checks for queen and rooks
        //Left
        for (int i=1; x-i>-1; i++) {
            if (board[x-i][y].equals(queen) || board[x-i][y].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (!board[x - i][y].equals("  ")) {
                break;
            }
        }
        //Right
        for (int i=1; x+i<8; i++) {
            if (board[x+i][y].equals(queen) || board[x+i][y].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (!board[x + i][y].equals("  ")) {
                break;
            }
        }
        //Upwards
        for (int i=1; y+i<8; i++) {
            if (board[x][y+i].equals(queen) || board[x][y+i].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (!board[x][y + i].equals("  ")) {
                break;
            }
        }
        //Downwards
        for (int i=1; y-i>-1; i++) {
            if (board[x][y-i].equals(queen) || board[x][y-i].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (!board[x][y - i].equals("  ")) {
                break;
            }
        }

        //Diagonal checks for bishops and queen
        //Up-right
        for (int i=1; y+i<8 && x+i<8; i++) {
            if (board[x+i][y+i].equals(bishop) || board[x+i][y+i].equals(queen)) {
                return true;
            }
            if (!board[x + i][y + i].equals("  ")) {
                break;
            }
        }
        //Up-left
        for (int i=1; y+i<8 && x-i>-1; i++) {
            if (board[x-i][y+i].equals(bishop) || board[x-i][y+i].equals(queen)) {
                return true;
            }
            if (!board[x - i][y + i].equals("  ")) {
                break;
            }
        }
        //Down-right
        for (int i=1; y-i>-1 && x+i<8; i++) {
            if (board[x+i][y-i].equals(bishop) || board[x+i][y-i].equals(queen)) {
                return true;
            }
            if (!board[x + i][y - i].equals("  ")) {
                break;
            }
        }
        //Down-left
        for (int i=1; y-i>-1 && x-i>-1; i++) {
            if (board[x-i][y-i].equals(bishop) || board[x-i][y-i].equals(queen)) {
                return true;
            }
            if (!board[x - i][y - i].equals("  ")) {
                break;
            }
        }

        //no checks
        return false;
    }

    /**
     * this function simulates a move and looks to see if it results in a self-check, returns true if so
     * @param board the board to look at
     * @param x1 x point of the piece to move
     * @param y1 y point of the piece to move
     * @param x2 x point of the square to move to
     * @param y2 y point of the square to move to
     * @param colour the colour of the moving piece
     * @return true if the move will cause a check
     */
    static private boolean moveCheck (String[][] board, int x1, int y1, int x2, int y2,char colour) {
        String[][] copy = new String[8][8];
        for (int i=0; i<8; i++) {
            System.arraycopy(board[i],0,copy[i],0,8);
        }

        copy[x2][y2]=copy[x1][y1];
        copy[x1][y1]="  ";
        return check(copy, colour);
    }

    /**
     * checkmate function
     * @param board the board to look at
     * @param colour the colour to check for
     * @return true if the colour is under checkmate
     */
    static public boolean checkmate (String[][] board,char colour) {
        Point pos1;
        Point pos2;
        if (check(board,colour)) {
            //First two layers to loop, to find pieces
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j].charAt(0) == colour) {
                        pos1 = new Point(i, j);

                        //second pair of loop layers look for potential moves
                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                pos2 = new Point(k, l);
                                //checks if this is a valid move
                                if (colour == 'w' && moveWhitePiece(pos1, pos2, board,false)) {
                                    return false;
                                }
                                if (colour == 'b' && moveBlackPiece(pos1, pos2, board,false)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            //if no valid moves are found
            return true;
        }
        return false;
    }

    /**
     * creates a deep copy of a 2d array and returns
     * @param board the board to copy
     * @return the copy
     */
    static private String[][] boardCopy(String[][]board) {
        String[][] copy = new String[8][8];
        for (int i=0; i<8; i++) {
            System.arraycopy(board[i],0,copy[i],0,8);
        }
        return copy;
    }

    /**
     * checks to see if there is a draw
     * @param board the board to look at
     * @return true if there is a draw
     */
    static public boolean draw (String[][]board) {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (!(board[i][j].charAt(1)=='K') && !(board[i][j].charAt(1)==' ')) {
                    return false;
                }
            }
        }
        return true;
    }
}

