import java.awt.*;

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
     * sets up the starting state of a board
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
     * Method to initiate piece movement
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

        //move a Pawn
        if (piece == "wP") {
            //if the movement is within same column
            if ( x1 == x2) {
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == 1 || (y2-y1 == 2 && y1 == 1)) {
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
                    if (board[x1+1][y1].charAt(0)=='w') {
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
                    if (board[x1+1][y1].charAt(0)=='b') {
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
                    if (board[x1+1][y1].charAt(0)=='w') {
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
                    if (board[x1+1][y1].charAt(0)=='b') {
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

        //move a Pawn
        if (piece == "bP") {
            //if the movement is within same column
            if ( x1 == x2) {
                //if the movement is within valid range (can only move 2 squares if its on second row)
                if ( y2-y1 == -1 || (y2-y1 == -2 && y1 == 6)) {
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
                    if (board[x1+1][y1].charAt(0)=='b') {
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
                    if (board[x1+1][y1].charAt(0)=='w') {
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
                    if (board[x1+1][y1].charAt(0)=='b') {
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
                    if (board[x1+1][y1].charAt(0)=='w') {
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



}
