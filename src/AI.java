import java.awt.*;
import java.util.ArrayList;


@SuppressWarnings({"StringEquality", "IfStatementWithIdenticalBranches", "AccessStaticViaInstance"})
public class AI {
    Piece pieces = new Piece();
    String[][] board = pieces.board;
    //These piece tables are used by the heuristic to evaluate the board
    //Pawns
    static int[][] pawnTable = {
            {900,900,900,900,900,900,900,900},
            {50,50,50,50,50,50,50,50},
            {10,10,20,30,30,20,10,10},
            {5,5,10,25,25,10,5,5},
            {0,0,0,20,20,0,0,0},
            {5,-5,-10,0,0,-10,-5,5},
            {5,10,10,-20,-20,10,10,5},
            {0,0,0,0,0,0,0,0}
    };
    //Knights
    static int[][] knightTable = {
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,0,0,0,0,-20,-40},
            {-30,0,10,15,15,10,0,-30},
            {-30,5,15,20,20,15,5,30},
            {-30,0,15,20,20,15,0,-30},
            {-30,5,10,15,15,10,5,-30},
            {-40,-20,0,5,5,0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    //Bishops
    static int[][] bishopTable = {
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,0,0,0,0,0,0,-10},
            {-10,0,5,10,10,5,0,-10},
            {-10,5,5,10,10,5,5,-10},
            {-10,0,10,10,10,10,0,-10},
            {-10,10,10,10,10,10,10,-10},
            {-10,5,0,0,0,0,5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}

    };
    //Rooks
    static int[][] rookTable = {
            {0,0,0,0,0,0,0,0},
            {5,10,10,10,10,10,10,5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {0,0,0,5,5,0,0,0}
    };
    //Queen
    static int[][] queenTable = {
            {-20,-10,-10,-5,-5,-10,-10,-20},
            {-10,0,0,0,0,0,0,-10},
            {-10,0,5,5,5,5,0,-10},
            {-5,0,5,5,5,5,0,-5},
            {-5,0,5,5,5,5,0,0},
            {-10,0,5,5,5,5,5,-10},
            {-10,0,0,0,0,5,0,-10},
            {-20,-10,-10,-5,-5,-10,-10,-20}
    };
    //King
    static int[][] kingTable = {
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {20, 30, 10,  0,  0, 10, 30, 20}
    };

    AI() {


    }


    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }

    /**
     * Converts array coordinates to board coordinates
     *
     * @param p Board coordinate
     * @return Array coordinate
     */
    private int convertArrayToBoard(Point p) {
        int k = 0;
        int x = 0;

        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[i].length; j++) {
                if (p.getX() == j && p.getY() == i) {
                    x = k;
                }

                k++;
            }
        }

        return x;
    }

    static public int heuristic(String[][] board) {
        int value = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == "wP") value -= 100;
                if (board[i][j] == "wN") value -= 320;
                if (board[i][j] == "wB") value -= 330;
                if (board[i][j] == "wR") value -= 500;
                if (board[i][j] == "wQ") value -= 900;
                if (board[i][j] == "wK") value -= 20000;

                if (board[i][j] == "bP") value += 100+pawnTable[j][i];
                if (board[i][j] == "bN") value += 320+knightTable[j][i];
                if (board[i][j] == "bB") value += 330+bishopTable[j][i];
                if (board[i][j] == "bR") value += 500+rookTable[j][i];
                if (board[i][j] == "bQ") value += 900+queenTable[j][i];
                if (board[i][j] == "bK") value += 20000+kingTable[j][i];
            }
        }
        return value;
    }


    private ArrayList<Point> getMoves(String[][] arr, char c) {
        ArrayList<Point> temp = new ArrayList<>();

        for (int i = arr.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr.length; j++) {
                if (board[j][i].charAt(0) == c) {
                    temp.add(new Point(j, i));
                }

            }
        }

        return temp;
    }


    /**
     *
     * @param arr The game minimax will recursively run on
     * @param d the depth
     * @param alpha alpha for alpha beta pruning
     * @param beta  beta for alpha beta pruning
     * @param maximizing maximizing prayer
     * @return initial position, selected position,and minimax value
     */
    public int[] minimax(String[][] arr, int d, int alpha, int beta, boolean maximizing) {
        int[] result = new int[3];
        String[][] temp;
        int value;
        int score;


        if (d == 0 || Piece.checkmate(arr,'w') || Piece.checkmate(arr,'b') || Piece.draw(arr)) {
            result[2] = heuristic(arr);
            if (Piece.checkmate(arr,'w')) result[2] = Integer.MAX_VALUE;
            if (Piece.checkmate(arr,'b')) result[2] = Integer.MIN_VALUE;
            return result;
        }


        if (maximizing) {
            value = Integer.MIN_VALUE;
            ArrayList<Point> moves = getMoves(arr, 'b');
            for (Point move : moves) {
                for (Point valid : validMoves(move, arr)) {

                    temp = copyOf(arr);
                    Piece.moveBlackPiece(move, valid, temp);
                    score = minimax(temp, d - 1, alpha, beta, false)[2];

                    if (score > value) {
                        value = score;
                        result[0] = convertArrayToBoard(move);
                        result[1] = convertArrayToBoard(valid);
                        result[2] = value;
                    }

                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break;
                    }
                }

            }

            return result;

        } else {
            value = Integer.MAX_VALUE;
            ArrayList<Point> moves = getMoves(arr, 'w');
            for (Point move : moves) {
                for (Point valid : validMoves(move, arr)) {

                    temp = copyOf(arr);
                    Piece.moveWhitePiece(move, valid, temp);
                    score = minimax(temp, d - 1, alpha, beta, true)[2];

                    if (score < value) {
                        value = score;
                        result[0] = convertArrayToBoard(move);
                        result[1] = convertArrayToBoard(valid);
                        result[2] = value;
                    }

                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }

            }

            return result;
        }
    }


    /**
     * Returns an array of potential moves of pos1 (the initial point)
     *
     * @param pos1 Initial position
     * @param arr  2D array representation of board
     */
    private Point[] validMoves(Point pos1, String[][] arr) {
        ArrayList<Point> moves = new ArrayList<>();
        String[][] temp;
        Point[] returns;

        //if pos1 is the location of a white pieces
        if ((arr[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    Point pos2 = new Point(j, i);

                    if (Piece.moveWhitePiece(pos1, pos2, temp)) {
                        moves.add(pos2);

                    }
                }
            }
        } else if (arr[pos1.x][pos1.y].charAt(0) == 'b') {
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


}