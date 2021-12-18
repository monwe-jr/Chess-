import java.awt.*;
import java.util.ArrayList;


public class Potential {
    Piece pieces = new Piece();
    String[][] board = pieces.board;

    private ArrayList<String> piece = new ArrayList<>();       //all the pieces with potential moves
    private ArrayList<Point> pos1 = new ArrayList<>();         //initial locations
    private ArrayList<Point[]> pos2 = new ArrayList<>();       //valid move per location

    Potential() {


    }

    /**
     * Returns a copy of arr
     *
     * @param arr
     * @return
     */
    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }

    /**
     * Returns an array of potential moves of pos1
     *
     * @param pos1 Initial position
     * @param arr  2D array representation of board
     * @return
     */
    private Point[] validMoves(Point pos1, String[][] arr) {
        ArrayList<Point> moves = new ArrayList<>();
        String[][] temp;
        Point[] returns;

            //if pos1 is the location of a white pieces
        if ((board[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    Point pos2 = new Point(j, i);

                    if (Piece.moveWhitePiece(pos1, pos2, temp)) {
                        moves.add(pos2);

                    }
                }
            }
        } else {
            //if pos1 is the location of a black pieces
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    Point pos2 = new Point(j, i);

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


    /**
     * Finds all the pieces of a specified colour and keeps the pieces type, pieces location, and potential moves of each piece found
     *
     * @param c Specified colour
     */
    public void getMoves(char c) {
        piece.clear();
        pos1.clear();
        pos2.clear();

        //Only keep memory of white pieces
        if (c == 'w') {
            for (int i = board.length - 1; i >= 0; i--) {
                for (int j = 0; j < board.length; j++) {
                    Point p = new Point(j, i);

                    if (board[p.x][p.y].charAt(0) == 'w') {
                        piece.add(board[p.x][p.y]);
                        pos1.add(p);
                        pos2.add(validMoves(p, board));

                    }

                }
            }
        } else {
            //Only keep memory of black pieces
            for (int i = board.length - 1; i >= 0; i--) {
                for (int j = 0; j < board.length; j++) {
                    Point p = new Point(j, i);

                    if (board[p.x][p.y].charAt(0) == 'b') {
                        piece.add(board[p.x][p.y]);
                        pos1.add(p);
                        pos2.add(validMoves(p, board));

                    }

                }
            }


        }
    }


    //returns ArrayList containing pieces type
    public ArrayList<String> getPieces() {
        return piece;
    }

    //returns ArrayList containing initial positions
    public ArrayList<Point> getPos1() {
        return pos1;
    }

    //returns ArrayList containing potential moves of each initial position
    public ArrayList<Point[]> getPos2() {
        return pos2;
    }


}
