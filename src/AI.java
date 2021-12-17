import java.awt.*;
import java.util.ArrayList;

public class AI {
    Piece pieces = new Piece();
    String[][] board = pieces.board;
    Potential moves = new Potential();
    int depth;

    AI(int d) {
        this.depth = d;


    }

    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }


    private int score(String s, String[][] arr) {
        int score = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr.length; j++) {
                Point p = new Point(j, i);

                if (s == "w") {
                    if (arr[p.x][p.y] == "wP") {
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

                    if (arr[p.x][p.y] == "bP") {
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


    private int evaluation(String s1, String s2, String[][] arr) {
        return (score(s1, arr) - score(s2, arr));
    }


    private boolean terminalTest(Color c) {
        if (checkmate(white) || draw()) {
            return true;
        }

        return false;
    }


    public  int[] minimax(String[][] arr, int d, boolean maximizing) {
        String[][] temp;
        int[] returns = new int[3];
        int value;
        int score;
        int selectedPos1 = 0;
        int selectedPos2 = 0;

        if (d == 0 || terminalTest(white)) {
            if (terminalTest(white)) {
                if (piece.checkmate(white)) {
                    returns[2] = Integer.MAX_VALUE;
                    return returns;
                } else if (piece.checkmate(black)) {
                    returns[2] = Integer.MIN_VALUE;
                    return returns;
                } else {
                    returns[2] = 0;
                    return returns;


                }
            } else {
                returns[2] = evaluation("w", "b", arr);
            }
        }

        if (maximizing) {
            value = Integer.MIN_VALUE;
            moves.getMoves("w");
            ArrayList<String> pieces = moves.getPieces();
            ArrayList<Point> pos1 = moves.getPos1();
            ArrayList<Point[]> pos2 = moves.getPos2();

            for (int i = 0; i < pieces.size(); i++) {
                Point[] positions = pos2.get(i);

                for (int j = 0; j < positions.length; j++) {
                    Point p = positions[j];
                    temp = copyOf(arr);
                    temp[p.x][p.y] = pieces.get(i);
                    score = minimax(temp, depth - 1, false)[2];

                    if (score > value) {
                        value = score;
                        selectedPos1 = convertArrayToBoard(pos1.get(i));
                        selectedPos2 = convertArrayToBoard(positions[j]);

                    }


                }
            }

            returns[0] = selectedPos1;
            returns[1] = selectedPos2;
            returns[2] = value;
            return returns;


        } else {
            value = Integer.MAX_VALUE;
            moves.getMoves("black");
            ArrayList<String> pieces = moves.getPieces();
            ArrayList<Point> pos1 = moves.getPos1();
            ArrayList<Point[]> pos2 = moves.getPos2();

            for (int i = 0; i < pieces.size(); i++) {
                Point[] positions = pos2.get(i);

                for (int j = 0; j < positions.length; j++) {
                    Point p = positions[j];
                    temp = copyOf(arr);
                    temp[p.x][p.y] = pieces.get(i);
                    score = minimax(temp, depth - 1, true)[2];

                    if (score < value) {
                        value = score;
                        selectedPos1 = convertArrayToBoard(pos1.get(i));
                        selectedPos2 = convertArrayToBoard(positions[j]);

                    }


                }
            }

        }

        returns[0] = selectedPos1;
        returns[1] = selectedPos2;
        returns[2] = value;
        return returns;
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


}