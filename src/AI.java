import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class AI {
    Piece pieces = new Piece();
    String[][] board = pieces.board;
    Random num = new Random();
    private ArrayList<Point> firstPositions = new ArrayList<>();         //initial locations
    private ArrayList<Point> secondPositions = new ArrayList<>();       //valid move per location


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


    /**
     * Scores the state of the board
     *
     * @param c   score
     * @param arr board that will be scored
     * @return
     */
    private int score(char c, String[][] arr) {
        int score = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr.length; j++) {
                Point p = new Point(j, i);

                if (c == 'w') {
                    if (Piece.checkmate(arr, 'b')) {
                        score += 9000;
                    } else if (arr[p.x][p.y] == "wP") {
                        score += 1;
                    } else if (arr[p.x][p.y] == "wN") {
                        score += 3;
                    } else if (arr[p.x][p.y] == "wB") {
                        score += 3;
                    } else if (arr[p.x][p.y] == "wR") {
                        score += 5;
                    } else if (arr[p.x][p.y] == "wQ") {
                        score += 8;
                    } else {
                        score += 100;
                    }


                } else {
                    if (Piece.checkmate(arr, 'w')) {
                        score += 9000;
                    } else if (arr[p.x][p.y] == "bP") {
                        score += 1;
                    } else if (arr[p.x][p.y] == "bN") {
                        score += 3;
                    } else if (arr[p.x][p.y] == "bB") {
                        score += 3;
                    } else if (arr[p.x][p.y] == "wR") {
                        score += 5;

                    } else if (arr[p.x][p.y] == "bQ") {
                        score += 8;
                    } else {
                        score += 100;
                    }


                }

            }
        }

        return score;
    }

    //evaluates the score of the AI
    private int evaluation(char c1, char c2, String[][] arr) {
        return (score(c1, arr) - score(c2, arr));
    }


    private boolean terminalTest(String[][] arr, char c) {
        if (Piece.checkmate(arr, c) || Piece.draw(arr)) {
            return true;
        }
        return false;

    }


    public int[] minimax(String[][] arr, int d, int alpha, int beta, boolean maximizing) {
        int[] result = new int[3];
        String[][] temp;
        int value;
        int score;


        if (d == 0) {
            result[2] = evaluation('b', 'w', arr);
            return result;
        }


        if (maximizing) {
            value = Integer.MIN_VALUE;
            getMoves('b', arr);

            for (int i = 0; i < firstPositions.size(); i++) {
                temp = copyOf(arr);
                Piece.moveBlackPiece(firstPositions.get(i), secondPositions.get(i), temp);
                score = minimax(temp, d - 1, alpha, beta, false)[2];

                if (score > value) {
                    value = score;
                    result[0] = convertArrayToBoard(firstPositions.get(i));
                    result[1] = convertArrayToBoard(secondPositions.get(i));

                    result[2] = value;

                }

                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }

            }

            return result;

        } else {
            value = Integer.MAX_VALUE;
            getMoves('b', arr);


            for (int i = 0; i < firstPositions.size(); i++) {

                temp = copyOf(arr);
                Piece.moveWhitePiece(firstPositions.get(i), secondPositions.get(i), temp);
                value = Math.min(value, minimax(temp, d - 1, alpha, beta, true)[2]);


                result[2] = value;
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }


            }
            return result;
        }


    }


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
    public void getMoves(char c, String[][] arr) {
        if (!firstPositions.isEmpty()) {
            firstPositions.clear();
            secondPositions.clear();
        }

        //Only keep memory of white pieces
        if (c == 'w') {
            for (int i = arr.length - 1; i >= 0; i--) {
                for (int j = 0; j < arr.length; j++) {
                    Point p = new Point(j, i);
                    if (arr[p.x][p.y].charAt(0) == 'w') {
                        Point[] valid = validMoves(p, arr);
                        for (int k = 0; k < valid.length; k++) {
                            firstPositions.add(p);
                            secondPositions.add(valid[k]);
                        }


                    }

                }
            }
        } else {
            //Only keep memory of black pieces
            for (int i = arr.length - 1; i >= 0; i--) {
                for (int j = 0; j < arr.length; j++) {
                    Point p = new Point(j, i);
                    if (arr[p.x][p.y].charAt(0) == 'b') {
                        Point[] valid = validMoves(p, arr);
                        for (int k = 0; k < valid.length; k++) {
                            firstPositions.add(p);
                            secondPositions.add(valid[k]);
                        }


                    }


                }
            }


        }
    }


}