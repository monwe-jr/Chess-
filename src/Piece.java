import java.awt.*;
import java.util.ArrayList;

public class Piece {
    //These global variables represent the row values of the board, to be used throughout the code
    int a = 0;
    int b = 1;
    int c = 2;
    int d = 3;
    int e = 4;
    int f = 5;
    int g = 6;
    int h = 7;


    public static String[][] board = new String[8][8];



    Piece (){

        setupBoard(board);
        drawBoard(board);


    }


    /**
     * Sets up the starting state of a board
     * @param board the board to be set up
     */
    private void setupBoard(String[][] board) {


        //Setting up white pieces
        board[a][0] = "wR";
        board[b][0] = "wN";
        board[c][0] = "wB";
        board[d][0] = "wQ";
        board[e][0] = "wK";
        board[f][0] = "wB";
        board[g][0] = "wN";
        board[h][0] = "wR";
        for (int i=0;i<8;i++) {
            board[i][1] = "wP";
        }
        //Setting up black pieces
        board[a][7] = "bR";
        board[b][7] = "bN";
        board[c][7] = "bB";
        board[d][7] = "bQ";
        board[e][7] = "bK";
        board[f][7] = "bB";
        board[g][7] = "bN";
        board[h][7] = "bR";
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
            for (int j=0; j<in.length; j++) {
                System.out.print("| "+in[j][i]+" ");
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
     */
    static public boolean moveWhitePiece(Point pos1, Point pos2, String[][]board) {
        int x1 = (int)pos1.x;
        int x2 = (int)pos2.x;
        int y1 = (int)pos1.y;
        int y2 = (int)pos2.y;
        String piece = board[x1][y1];

        if (piece == "  " || piece.charAt(0)=='b' ) {
            System.out.println("SELECTION ERROR: White piece was not chosen");
            return false;
        }
        //Look for self-check
        if (moveCheck(board,x1,y1,x2,y2,'w')) return false;

        //move a Pawn
        if (piece == "wP") {
            //if the movement is within same column
            if ( x1 == x2) {
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == 1 || (y2-y1 == 2 && y1 == 1 && board[x1][y1+1]=="  ")) {
                    //if the space is empty
                    if (board[x2][y2]=="  ") {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";

                        return true;
                    } else {
                        System.out.println("ERROR 100: Invalid movement");
                        return false;
                    }
                } else {
                    System.out.println("ERROR 101: Invalid movement");
                    return false;
                }
            }

            //if the movement is a diagonal take
            if ((x1 == x2-1 || x1 == x2+1) && (y2-y1==1)) {
                if (board[x2][y2].charAt(0)=='b'){
                    board[x2][y2]=piece;
                    board[x1][y1]="  ";
                    return true;
                } else {
                    System.out.println("ERROR 102: Invalid movement");
                    return false;
                }
            }
        }




        //move the King
        if (piece == "wK") {
            //check if movement is to same spot
            if (x1==x2 && y1==y2) {
                System.out.println("ERROR 105: Invalid movement");
                return false;
            }
            //check range of movement
            if ((x2-x1==1 || x2==x1 || x2-x1==-1)&&(y2-y1==1 || y2==y1 || y2-y1==-1)) {
                //check if space is empty or if there's a black piece
                if (board[x2][y2]=="  " || board[x2][y2].charAt(0)=='b') {
                    board[x2][y2]=piece;
                    board[x1][y1]="  ";
                    return true;
                } else {
                    System.out.println("ERROR 106: Invalid movement");
                    return false;
                }
            } else {
                System.out.println("ERROR 107: Invalid movement");
                return false;
            }
        }

        //Rook
        if (piece=="wR") {
            //check if movement is in same spot and if it is either exclusively vertical or horizontal
            if ((x1==x2 && y1==y2)&&!(y1==y2 || x1==x2)) {
                System.out.println("ERROR 108: Invalid movement");
                return false;
            }
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            System.out.println("ERROR 111: Invalid movement");
            return false;
        }

        //Bishop
        if (piece=="wB") {
            //Error if movement is within same column or row
            if (y1==y2 || x1==x2) {
                System.out.println("ERROR 112: Invalid movement");
                return false;
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 113: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 114: Invalid movement");
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
                        System.out.println("ERROR 115: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 116: Invalid movement");
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
                        System.out.println("ERROR 117: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 118: Invalid movement");
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
                        System.out.println("ERROR 119: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 120: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Queen
        if (piece=="wQ") {
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 121: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 122: Invalid movement");
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
                        System.out.println("ERROR 123: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 124: Invalid movement");
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
                        System.out.println("ERROR 125: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1].charAt(0)=='b') {
                        System.out.println("ERROR 126: Invalid movement");
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
                        System.out.println("ERROR 127: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1].charAt(0)=='b') {
                        System.out.println("ERROR 128: Invalid movement");
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
                        System.out.println("ERROR 129: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 130: Invalid movement");
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
                        System.out.println("ERROR 131: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 132: Invalid movement");
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
                        System.out.println("ERROR 133: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1+i][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 134: Invalid movement");
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
                        System.out.println("ERROR 135: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking black piece
                    if (board[x1-i][y1-i].charAt(0)=='b') {
                        System.out.println("ERROR 136: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Knight
        if (piece == "wN") {
            //upwards
            if ((y2==y1+2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            //downwards
            if ((y2==y1-2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            //right
            if ((x2==x1+2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            //left
            if ((x2==x1-2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='w')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            System.out.println("ERROR 137: Invalid movement");
            return false;
        }

        System.out.println("Unknown Error: ???");
        return false;
    }

    /**
     * Performs black piece movement
     * @param pos1 position of pieces that needs to be moved
     * @param pos2 position
     * @param board board where movement takes place
     */
    static public boolean moveBlackPiece(Point pos1, Point pos2, String[][]board) {
        int x1 = (int)pos1.x;
        int x2 = (int)pos2.x;
        int y1 = (int)pos1.y;
        int y2 = (int)pos2.y;
        String piece = board[x1][y1];

        if (piece == "  " || piece.charAt(0)=='w' ) {
            System.out.println("SELECTION ERROR: Black piece was not chosen");
            return false;
        }
        //If this move would cause a self-check
        if (moveCheck(board,x1,y1,x2,y2,'b')) return false;

        //move a Pawn
        if (piece == "bP") {
            //if the movement is within same column
            if ( x1 == x2) {
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == -1 || (y2-y1 == -2 && y1 == 6  && board[x1][y1-1]=="  ")) {
                    //if the space is empty
                    if (board[x2][y2]=="  ") {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    } else {
                        System.out.println("ERROR 100: Invalid movement");
                        return false;
                    }
                } else {
                    System.out.println("ERROR 101: Invalid movement");
                    return false;
                }
            }
            //if the movement is a diagonal take
            if ((x1 == x2-1 || x1 == x2+1) && (y2-y1==-1)) {
                if (board[x2][y2].charAt(0)=='w'){
                    board[x2][y2]=piece;
                    board[x1][y1]="  ";
                    return true;
                } else {
                    System.out.println("ERROR 102: Invalid movement");
                    return false;
                }
            }
        }

        //move the King
        if (piece == "bK") {
            //check if movement is to same spot
            if (x1==x2 && y1==y2) {
                System.out.println("ERROR 105: Invalid movement");
                return false;
            }
            //check range of movement
            if ((x2-x1==1 || x2==x1 || x2-x1==-1)&&(y2-y1==1 || y2==y1 || y2-y1==-1)) {
                //check if space is empty or if there's a black piece
                if (board[x2][y2]=="  " || board[x2][y2].charAt(0)=='w') {
                    board[x2][y2]=piece;
                    board[x1][y1]="  ";
                    return true;
                } else {
                    System.out.println("ERROR 106: Invalid movement");
                    return false;
                }
            } else {
                System.out.println("ERROR 107: Invalid movement");
                return false;
            }
        }

        //Rook
        if (piece=="bR") {
            //check if movement is in same spot and if it is either exclusively vertical or horizontal
            if ((x1==x2 && y1==y2)&&!(y1==y2 || x1==x2)) {
                System.out.println("ERROR 108: Invalid movement");
                return false;
            }
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        System.out.println("ERROR 110: Invalid movement");
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
                        System.out.println("ERROR 109: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        System.out.println("ERROR 110: Invalid movement");
                        return false;
                    }
                }
            }
            System.out.println("ERROR 111: Invalid movement");
            return false;
        }

        //Bishop
        if (piece=="bB") {
            //Error if movement is within same column or row
            if (y1==y2 || x1==x2) {
                System.out.println("ERROR 112: Invalid movement");
                return false;
            }
            //up-right diagonal
            if (y2-y1 > 0 && x2-x1 > 0) {
                //loop up each tile
                for (int i=1; (i+y1<8 && i+x1<8);i++) {
                    //check for a blocking black piece
                    if (board[x1+i][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 113: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 114: Invalid movement");
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
                        System.out.println("ERROR 115: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 116: Invalid movement");
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
                        System.out.println("ERROR 117: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 118: Invalid movement");
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
                        System.out.println("ERROR 119: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 120: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Queen
        if (piece=="bQ") {
            //if movement is upwards
            if (y2-y1 > 0 && x1==x2) {
                //loop up each tile
                for (int i=1; i+y1<8;i++) {
                    //check for a blocking black piece
                    if (board[x1][y1+i].charAt(0)=='b') {
                        System.out.println("ERROR 121: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 122: Invalid movement");
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
                        System.out.println("ERROR 123: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 124: Invalid movement");
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
                        System.out.println("ERROR 125: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1-i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1].charAt(0)=='w') {
                        System.out.println("ERROR 126: Invalid movement");
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
                        System.out.println("ERROR 127: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (x1+i == x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1].charAt(0)=='w') {
                        System.out.println("ERROR 128: Invalid movement");
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
                        System.out.println("ERROR 129: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 130: Invalid movement");
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
                        System.out.println("ERROR 131: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1+i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1+i].charAt(0)=='w') {
                        System.out.println("ERROR 132: Invalid movement");
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
                        System.out.println("ERROR 133: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1+i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1+i][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 134: Invalid movement");
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
                        System.out.println("ERROR 135: Invalid movement");
                        return false;
                    }
                    //check for move condition
                    if (y1-i == y2 && x1-i==x2) {
                        board[x2][y2]=piece;
                        board[x1][y1]="  ";
                        return true;
                    }
                    //check for blocking white piece
                    if (board[x1-i][y1-i].charAt(0)=='w') {
                        System.out.println("ERROR 136: Invalid movement");
                        return false;
                    }
                }
            }
        }

        //Knight
        if (piece == "bN") {
            //upwards
            if ((y2==y1+2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            //downwards
            if ((y2==y1-2 && (x1==x2-1 || x1==x2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            //right
            if ((x2==x1+2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                check(board,'w');
                return true;
            }
            //left
            if ((x2==x1-2 && (y1==y2-1 || y1==y2+1))&&!(board[x2][y2].charAt(0)=='b')) {
                board[x2][y2]=piece;
                board[x1][y1]="  ";
                return true;
            }
            System.out.println("ERROR 137: Invalid movement");
            return false;
        }

        System.out.println("Unknown Error: ???");
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
        String king = new StringBuilder().append(colour).append("K").toString();
        char opposite;
        if (colour=='w') {
            opposite = 'b';
        } else {
            opposite = 'w';
        }
        String knight = new StringBuilder().append(opposite).append("N").toString();
        String queen = new StringBuilder().append(opposite).append("Q").toString();
        String rook = new StringBuilder().append(opposite).append("R").toString();
        String bishop = new StringBuilder().append(opposite).append("B").toString();
        String pawn = new StringBuilder().append(opposite).append("P").toString();
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

        //This block checks for checks from enemy pawns
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
            if (board[x-i][y].charAt(0)==colour) {
                break;
            }
        }
        //Right
        for (int i=1; x+i<8; i++) {
            if (board[x+i][y].equals(queen) || board[x+i][y].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (board[x+i][y].charAt(0)==colour) {
                break;
            }
        }
        //Upwards
        for (int i=1; y+i<8; i++) {
            if (board[x][y+i].equals(queen) || board[x][y+i].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (board[x][y+i].charAt(0)==colour) {
                break;
            }
        }
        //Downwards
        for (int i=1; y-i>-1; i++) {
            if (board[x][y-i].equals(queen) || board[x][y-i].equals(rook)) {
                return true;
            }
            //break if blocked by own colour
            if (board[x][y-i].charAt(0)==colour) {
                break;
            }
        }

        //Diagonal checks for bishops and queen
        //Up-right
        for (int i=1; y+i<8 && x+i<8; i++) {
            if (board[x+i][y+i].equals(bishop) || board[x+i][y+i].equals(queen)) {
                return true;
            }
            if (board[x+i][y+i].charAt(0)==colour) {
                break;
            }
        }
        //Up-left
        for (int i=1; y+i<8 && x-i>-1; i++) {
            if (board[x-i][y+i].equals(bishop) || board[x-i][y+i].equals(queen)) {
                return true;
            }
            if (board[x-i][y+i].charAt(0)==colour) {
                break;
            }
        }
        //Down-right
        for (int i=1; y-i>-1 && x+i<8; i++) {
            if (board[x+i][y-i].equals(bishop) || board[x+i][y-i].equals(queen)) {
                return true;
            }
            if (board[x+i][y-i].charAt(0)==colour) {
                break;
            }
        }
        //Down-left
        for (int i=1; y-i>-1 && x-i>-1; i++) {
            if (board[x-i][y-i].equals(bishop) || board[x-i][y-i].equals(queen)) {
                return true;
            }
            if (board[x-i][y-i].charAt(0)==colour) {
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
        if (check(copy,colour)) {
            return true;
        }

        return false;
    }

    /**
     * checkmate function
     * @param board the board to look at
     * @param colour the colour to check for
     * @return true if the colour is under checkmate
     */
    static public boolean checkmate (String[][] board,char colour) {
        String[][]temp;
        Point pos1;
        Point pos2;
        //First two layers to loop, to find pieces
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (board[i][j].charAt(0)==colour) {
                    pos1 = new Point(i,j);

                    //second pair of loop layers look for potential moves
                    for (int k=0; k<8; k++) {
                        for (int l=0; l<8;l++) {
                            pos2 = new Point(k,l);
                            temp = boardCopy(board);
                            //checks if this is a valid move
                            if (colour=='w' && moveWhitePiece(pos1,pos2,temp)) {
                                return false;
                            }
                            if (colour=='b' && moveBlackPiece(pos1,pos2,temp)) {
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

