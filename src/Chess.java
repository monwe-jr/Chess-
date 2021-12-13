import javax.swing.*;
import java.awt.*;

public class Chess {
Board board = new Board();

    Chess() {



<<<<<<< Updated upstream
    //These global variables represent the row values of the board, to be used throughout the code
    int a = 0;
    int b = 1;
    int c = 2;
    int d = 3;
    int e = 4;
    int f = 5;
    int g = 6;
    int h = 7;

 Chess(){
        String[][] board = new String[8][8];
        setupBoard(board);
        drawBoard(board);

        board[e][3]="wK";
        Point point = new Point(e,4);
        Point point2 = new Point (e,5);
        moveWhitePiece(point,point2,board);
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
        //Set pawn's first move values to true
        for (int i=0;i<8; i++) {
            wPawns[i]=true;
            bPawns[i]=true;
        }
    }

    /**
     * Draws a board in console
     * @param in board that needs to be drawn
     */
    private void drawBoard(String[][]in) {
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
    private boolean moveWhitePiece(Point pos1, Point pos2, String[][]board) {
        int x1 = (int)pos1.getX();
        int x2 = (int)pos2.getX();
        int y1 = (int)pos1.getY()-1;
        int y2 = (int)pos2.getY()-1;
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
            if ((x1 == x2-1 || x1 == x2+1)&&(y2-y1==1)) {
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
            //check if movement is in same spot
            if ((x1==x2 && y1==y2)&&!(y1==y2 || x1==x2)) {
                System.out.println("ERROR 108: Invalid movement");
                return false;
            }
        }
        System.out.println("Unknown Error: ???");
        return false;
    }




 public static void main(String[] args) {
  new Chess();
 }
=======
    }

>>>>>>> Stashed changes

    public static void main(String[] args) {
        new Chess();
    }


}
