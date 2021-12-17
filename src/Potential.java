import java.awt.*;
import java.util.ArrayList;

public class Potential {
    Piece pieces = new Piece();
    String[][] board = pieces.board;

    private  ArrayList<String> piece = new ArrayList<>();
    private  ArrayList<Point> pos1 = new ArrayList<>();
    private  ArrayList<Point[]> pos2 = new ArrayList<>();

    Potential() {


    }

    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }

    private Point[] validMoves(Point pos1, String[][] arr) {
        ArrayList<Point> moves = new ArrayList<>();
        String[][] temp;
        Point[] returns;


        if ((board[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    Point  pos2 = new Point(j, i);

                    if (Piece.moveWhitePiece(pos1, pos2, temp)) {
                        moves.add(pos2);

                    }
                }
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    Point  pos2 = new Point(j, i);

                    if (Piece.moveBlackPiece(pos1, pos2, temp)) {
                        moves.add(pos2);

                    }
                }
            }
        }


        returns = new Point[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            returns[i] = moves.get(i);

        }


        return returns;
    }



    public void getMoves(String s) {
        piece.clear();
        pos1.clear();
        pos2.clear();

        if( s == "w"){
            for (int i = board.length - 1; i >= 0; i--) {
                for (int j = 0; j < board.length; j++) {
                    Point p = new Point(j, i);

                    if (board[p.x][p.y].charAt(0) == 'w') {
                            piece.add(board[p.x][p.y]);
                            pos1.add(p);
                            pos2.add(validMoves(p,board));

                    }

                }
            }
        }else{
            for (int i = board.length - 1; i >= 0; i--) {
                for (int j = 0; j < board.length; j++) {
                    Point p = new Point(j, i);

                    if (board[p.x][p.y].charAt(0) == 'b') {
                        piece.add(board[p.x][p.y]);
                        pos1.add(p);
                        pos2.add(validMoves(p,board));

                    }

                }
            }


        }
    }



    public  ArrayList<String> getPieces() {
        return piece;
    }

    public  ArrayList<Point> getPos1() {
        return pos1;
    }

    public  ArrayList<Point[]> getPos2() {
        return pos2;
    }


}
