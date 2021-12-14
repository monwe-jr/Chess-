import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Board implements MouseListener {

    Piece pieces = new Piece();
    String[][] board = pieces.board;
    JFrame frame = new JFrame();
    JPanel panel = new JPanel(new GridLayout(8, 8));

    ImageIcon pawnW = new ImageIcon("pawnW.png");
    ImageIcon pawnB = new ImageIcon("pawnB.png");
    ImageIcon rookW = new ImageIcon("rookW.png");
    ImageIcon rookB = new ImageIcon("rookB.png");
    ImageIcon horseW = new ImageIcon("horseW.png");
    ImageIcon horseB = new ImageIcon("horseB.png");
    ImageIcon bishopW = new ImageIcon("bishopW.png");
    ImageIcon bishopB = new ImageIcon("bishopB.png");
    ImageIcon queenW = new ImageIcon("queenW.png");
    ImageIcon queenB = new ImageIcon("queenB.png");
    ImageIcon kingW = new ImageIcon("kingW.png");
    ImageIcon kingB = new ImageIcon("kingB.png");


    Board() {

        frame.setTitle("Chess");
        createBoard();
        updateGUI(board);

        frame.addMouseListener(this);
        frame.addMouseListener(this);
        frame.setSize(800, 800);
        frame.add(panel);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);


    }


    private void createBoard() {
        int c = 0;
        int k = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JPanel grid = new JPanel(new GridLayout(1, 1));
                grid.add(new JLabel(new ImageIcon()));
                grid.addMouseListener(this);

                if (c == 0) {
                    grid.setBackground(Color.white);
                } else {
                    grid.setBackground(Color.darkGray);
                }

                c = c + 1;
                c = c % 2;


                panel.add(grid, k);
                k++;
            }

            c = c + 1;
            c = c % 2;
        }

    }

    private Point convertBoardToArray(int x) {
        int pos = 0;
        Point p = null;

        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[i].length; j++) {

                if (pos == x) {
                    p.setLocation(j, i);
                }

                pos++;
            }
        }

        return p;
    }


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


    private void updateGUI(String[][] arr) {
        int count = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr.length; j++) {
                Point p = new Point(j, i);
                int x = convertArrayToBoard(p);

                if (x == count) {

                    if (arr[p.x][p.y] == "wP") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(pawnW));
                    } else if (arr[p.x][p.y] == "bP") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(pawnB));
                    } else if (arr[p.x][p.y] == "wR") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(rookW));
                    } else if (arr[p.x][p.y] == "bR") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(rookB));

                    } else if (arr[p.x][p.y] == "wN") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(horseW));

                    } else if (arr[p.x][p.y] == "bN") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(horseB));

                    } else if (arr[p.x][p.y] == "wB") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(bishopW));

                    } else if (arr[p.x][p.y] == "bB") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(bishopB));

                    } else if (arr[p.x][p.y] == "wQ") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(queenW));

                    } else if (arr[p.x][p.y] == "bQ") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(queenB));

                    } else if (arr[p.x][p.y] == "wK") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(kingW));


                    } else if (board[p.x][p.y] == "bK") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(kingB));

                    } else {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon()));
                    }

                }


                count++;
            }

        }

    }


    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                temp[i][j] = arr[i][j];
            }

        }
        return temp;

    }


    private ArrayList<Point> validMoves(Point pos1, String[][] arr) {
        String[][] temp;
        ArrayList<Point> returns = new ArrayList<>();
        Point pos2;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                temp = copyOf(arr);
                pos2 = new Point(i, j);

                if (pieces.moveWhitePiece(pos1, pos2, temp)) {
                    returns.add(pos2);

                }
            }
        }


        return returns;
    }



    Point pos1;
    Point pos2;
    ArrayList<Point> temp = new ArrayList<>();
    @Override
    public void mouseClicked(MouseEvent e) {

        int x = e.getComponent().getX() / e.getComponent().getWidth() + 1;
        int y = 7 - e.getComponent().getY() / e.getComponent().getHeight() + 1;


        if (pos1 == null) {
            pos1 = new Point(x - 1, y - 1);
            System.out.println(board[pos1.x][pos1.y]);
            temp = validMoves(pos1, board);


            for (int i = 0; i < temp.size(); i++) {
                int k = convertArrayToBoard(temp.get(i));


                panel.getComponent(k).setBackground(Color.yellow);

            }

        } else {

            for (int i = 0; i < temp.size(); i++) {
                int k = convertArrayToBoard(temp.get(i));

                if (temp.get(i).y % 2 == 0) {
                    if (k % 2 == 0) {
                        panel.getComponent(k).setBackground(Color.darkGray);
                    } else {
                        panel.getComponent(k).setBackground(Color.white);
                    }
                } else {
                    if (k % 2 == 0) {
                        panel.getComponent(k).setBackground(Color.white);
                    } else {
                        panel.getComponent(k).setBackground(Color.darkGray);
                    }
                }
            }

            pos2 = new Point(x - 1, y - 1);
            if (temp.contains(pos2)) {
                pieces.moveWhitePiece(pos1, pos2, board);
                pieces.drawBoard(board);
                updateGUI(board);
                frame.setVisible(true);
                pos1 = null;
                pos2 = null;

            } else {

                if (board[pos2.x][pos2.y].charAt(0) == 'w' && !temp.contains(pos2)) {
                    pos1 = null;
                    if (pos1 == null) {
                        pos1 = pos2;
                        System.out.println(board[pos1.x][pos1.y]);
                        temp = validMoves(pos1, board);

                        for (int i = 0; i < temp.size(); i++) {
                            int k = convertArrayToBoard(temp.get(i));

                            panel.getComponent(k).setBackground(Color.yellow);
                        }

                    } else {

                        for (int i = 0; i < temp.size(); i++) {
                            int k = convertArrayToBoard(temp.get(i));

                            if (temp.get(i).y % 2 == 0) {
                                if (k % 2 == 0) {
                                    panel.getComponent(k).setBackground(Color.darkGray);
                                } else {
                                    panel.getComponent(k).setBackground(Color.white);
                                }
                            } else {
                                if (k % 2 == 0) {
                                    panel.getComponent(k).setBackground(Color.white);
                                } else {
                                    panel.getComponent(k).setBackground(Color.darkGray);
                                }
                            }
                        }

                        pos2 = new Point(x - 1, y - 1);
                        if (temp.contains(pos2)) {
                            pieces.moveWhitePiece(pos1, pos2, board);

                            pieces.drawBoard(board);
                            updateGUI(board);
                            frame.setVisible(true);
                            pos1 = null;
                            pos2 = null;

                        }

                    }
                }

            }

        }
    }







    @Override
    public void mousePressed(MouseEvent e) {


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}



