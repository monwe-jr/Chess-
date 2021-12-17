import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;


public class Board extends JFrame implements MouseListener {
    boolean input;
    public String mode;
    public int depth;
    Piece pieces = new Piece();
    Random num = new Random();
    int turn; //turn
    boolean gameOver; //game over
    String[][] board = pieces.board;  //2D array that represents the state of the game
    JFrame frame = new JFrame("Chess");
    JPanel panel = new JPanel(new GridLayout(8, 8));  //initializes JPanel layout


    ImageIcon pawnW = new ImageIcon("pawnW.png");    //white pawn
    ImageIcon pawnB = new ImageIcon("pawnB.png");    //black pawn
    ImageIcon rookW = new ImageIcon("rookW.png");    //white rook
    ImageIcon rookB = new ImageIcon("rookB.png");     //black rook
    ImageIcon horseW = new ImageIcon("horseW.png");   //white knight
    ImageIcon horseB = new ImageIcon("horseB.png");   //black knight
    ImageIcon bishopW = new ImageIcon("bishopW.png");  //white bishop
    ImageIcon bishopB = new ImageIcon("bishopB.png");  //black bishop
    ImageIcon queenW = new ImageIcon("queenW.png");    //white queen
    ImageIcon queenB = new ImageIcon("queenB.png");    //black queen
    ImageIcon kingW = new ImageIcon("kingW.png");      //black king
    ImageIcon kingB = new ImageIcon("kingB.png");      //black king


    Board(String m) {
        this.mode = m;

        gameOver = false;
        input = true;
        turn = 0;
        createBoard();
        updateGUI(board);
        frame.addMouseListener(this);
        frame.setSize(900, 900);
        frame.setResizable(false);
        frame.add(panel);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }//constructor for human vs human


    Board(String m, int d) {
        this.mode = m;
        this.depth = d;

        gameOver = false;
        input = true;
        turn = 0;
        createBoard();
        updateGUI(board);
        frame.addMouseListener(this);
        frame.setSize(900, 900);
        frame.setResizable(false);
        frame.add(panel);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }//constructor for human vs AI


    /**
     * creates initial grid
     */
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
     * Updates GUI after move is made
     *
     * @param arr 2D array that represents the state of the game
     */
    @SuppressWarnings("StringEquality")
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
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }


    private ArrayList<Point> validMoves(Point pos1, String[][] arr) {
        String[][] temp;
        ArrayList<Point> returns = new ArrayList<>();
        Point pos2;

        if ((board[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    pos2 = new Point(i, j);

                    if (Piece.moveWhitePiece(pos1, pos2, temp)) {
                        returns.add(pos2);

                    }
                }
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    pos2 = new Point(i, j);

                    if (Piece.moveBlackPiece(pos1, pos2, temp)) {
                        returns.add(pos2);

                    }
                }
            }
        }

        return returns;
    }


    private ArrayList<Point> findKill(Point pos1, ArrayList<Point> p) {
        ArrayList<Point> returns = new ArrayList<>();

        for (Point value : p) {
            if ((board[pos1.x][pos1.y].charAt(0) == 'w')) {
                if ((board[value.x][value.y].charAt(0) == 'b')) {
                    returns.add(value);
                }
            } else {
                if ((board[value.x][value.y].charAt(0) == 'w')) {
                    returns.add(value);
                }

            }


        }

        return returns;
    }


    private Color colorAt(Point p) {
        int x = convertArrayToBoard(p);
        if (p.y % 2 == 0) {
            if (x % 2 == 0) {
                return Color.darkGray;
            } else {
                return Color.white;
            }
        } else {
            if (x % 2 == 0) {
                return Color.white;
            } else {
                return Color.darkGray;
            }
        }
    }


    private void promotion(Point p) {
        input = false;

        JPanel buttonPanel = new JPanel();
        JButton button1 = new JButton();
        JButton button2 = new JButton();
        JButton button3 = new JButton();
        JButton button4 = new JButton();
        int x = convertArrayToBoard(p);

        if (p.y == 7) {

            button1.setIcon(rookW);
            button2.setIcon(horseW);
            button3.setIcon(bishopW);
            button4.setIcon(queenW);
            ActionListener listener = e -> {

                if (e.getSource() == button1) {
                    board[p.x][p.y] = "wR";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button2) {
                    board[p.x][p.y] = "wN";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button3) {
                    board[p.x][p.y] = "wB";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button4) {
                    board[p.x][p.y] = "wQ";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                }
            };


            button1.addActionListener(listener);
            button2.addActionListener(listener);
            button3.addActionListener(listener);
            button4.addActionListener(listener);

        } else {

            button1.setIcon(rookB);
            button2.setIcon(horseB);
            button3.setIcon(bishopB);
            button4.setIcon(queenB);
            ActionListener listener = e -> {

                if (e.getSource() == button1) {
                    board[p.x][p.y] = "bR";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button2) {
                    board[p.x][p.y] = "bN";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button3) {
                    board[p.x][p.y] = "bB";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button4) {
                    board[p.x][p.y] = "bQ";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                }

            };

            button1.addActionListener(listener);
            button2.addActionListener(listener);
            button3.addActionListener(listener);
            button4.addActionListener(listener);
        }
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);
        ((JPanel) panel.getComponent(x)).remove(0);
        ((JPanel) panel.getComponent(x)).add(buttonPanel);
        frame.invalidate();
        frame.validate();
        frame.repaint();

    }


    private void promotionAI(Point p) {
        int random = num.nextInt(2);
        input = false;


        if (depth == 1) {
            if (random == 0) {
                board[p.x][p.y] = "bB";
            } else {
                board[p.x][p.y] = "bN";
            }

            updateGUI(board);
            frame.invalidate();
            frame.validate();
            frame.repaint();
            input = true;

        } else if (depth == 2) {
            if (random == 0) {
                board[p.x][p.y] = "bR";
            } else {

                board[p.x][p.y] = "bQ";
            }

            updateGUI(board);
            frame.invalidate();
            frame.validate();
            frame.repaint();
            input = true;
        } else if (depth == 3) {
            board[p.x][p.y] = "bQ";

            updateGUI(board);
            frame.invalidate();
            frame.validate();
            frame.repaint();
            input = true;

        }


    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Point pos1;
    Point pos2;
    ArrayList<Point> temp = new ArrayList<>();
    ArrayList<Point> killTemp = new ArrayList<>();

    @SuppressWarnings({"StringEquality", "ConstantConditions"})
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getComponent().getX() / e.getComponent().getWidth() + 1;
        int y = 7 - e.getComponent().getY() / e.getComponent().getHeight() + 1;
        int j;


        if (!gameOver) {
            if (input) {
                if (mode == "Human") {
                    if (turn == 0) {

                        if (pos1 == null) {                            //white initial selection

                            pos1 = new Point(x - 1, y - 1);
                            temp = validMoves(pos1, board);
                            killTemp = findKill(pos1, temp);

                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                if (board[pos1.x][pos1.y] != "  ") {
                                    j = convertArrayToBoard(pos1);
                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                }

                                for (Point point : temp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                }

                                for (Point point : killTemp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                }

                            } else {
                                pos1 = null;
                            }


                        } else {                   //white selected move

                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
                            }

                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {  ////////////////////////////////////////////////////////////////////
                                pos2 = new Point(x - 1, y - 1);
                                j = convertArrayToBoard(pos1);
                                panel.getComponent(j).setBackground(colorAt(pos1));
                                if (temp.contains(pos2)) {
                                    Piece.moveWhitePiece(pos1, pos2, board);
                                    Piece.drawBoard(board);
                                    updateGUI(board);
                                    frame.setVisible(true);

                                    //promotion
                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotion(pos2);
                                    }

                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;

                                } else {                           //if white changes initial section

                                    if (board[pos2.x][pos2.y].charAt(0) == 'w' && !temp.contains(pos2)) {
                                        pos1 = null;
                                        if (pos1 == null) {
                                            pos1 = pos2;
                                            temp = validMoves(pos1, board);
                                            killTemp = findKill(pos1, temp);

                                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                                if (board[pos1.x][pos1.y] != "  ") {
                                                    j = convertArrayToBoard(pos1);
                                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                                }

                                                for (Point point : temp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                                }

                                                for (Point point : killTemp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                                }

                                            } else {
                                                pos1 = null;
                                            }

                                        } else {                    //selected move after white initial selection is changed

                                            for (Point point : temp) {
                                                int k = convertArrayToBoard(point);
                                                panel.getComponent(k).setBackground(colorAt(point));
                                            }
                                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {//////////////////////////////////////////////////////////////////////////////////
                                                pos2 = new Point(x - 1, y - 1);
                                                j = convertArrayToBoard(pos1);
                                                panel.getComponent(j).setBackground(colorAt(pos1));
                                                if (temp.contains(pos2)) {
                                                    Piece.moveWhitePiece(pos1, pos2, board);
                                                    Piece.drawBoard(board);
                                                    updateGUI(board);
                                                    frame.setVisible(true);

                                                    //promotion
                                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                                        promotion(pos2);
                                                    }


                                                    pos1 = null;
                                                    pos2 = null;
                                                    turn += 1;
                                                    turn = turn % 2;


                                                }

                                            } else {
                                                pos1 = null;
                                            }
                                        }
                                    }

                                }

                            } else {
                                pos1 = null;
                            }
                        }


                    } else { ////////////////////////////black move

                        if (pos1 == null) {                                       //black initial selection
                            pos1 = new Point(x - 1, y - 1);
                            temp = validMoves(pos1, board);
                            killTemp = findKill(pos1, temp);

                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                if (board[pos1.x][pos1.y] != "  ") {
                                    j = convertArrayToBoard(pos1);
                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                }


                                for (Point point : temp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                }

                                for (Point point : killTemp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                }

                            } else {
                                pos1 = null;
                            }


                        } else {                                                  //black selected move

                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
                            }

                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {////////////////////////////////////////////////////////
                                pos2 = new Point(x - 1, y - 1);
                                j = convertArrayToBoard(pos1);
                                panel.getComponent(j).setBackground(colorAt(pos1));
                                if (temp.contains(pos2)) {
                                    Piece.moveBlackPiece(pos1, pos2, board);
                                    Piece.drawBoard(board);
                                    updateGUI(board);
                                    frame.setVisible(true);

                                    //promotion
                                    if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotion(pos2);
                                    }


                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;


                                } else {                             //if black changes initial section

                                    if (board[pos2.x][pos2.y].charAt(0) == 'b' && !temp.contains(pos2)) {
                                        pos1 = null;
                                        if (pos1 == null) {
                                            pos1 = pos2;
                                            temp = validMoves(pos1, board);
                                            killTemp = findKill(pos1, temp);


                                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                                if (board[pos1.x][pos1.y] != "  ") {
                                                    j = convertArrayToBoard(pos1);
                                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                                }


                                                for (Point point : temp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                                }

                                                for (Point point : killTemp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                                }

                                            } else {
                                                pos1 = null;
                                            }


                                        } else {                                     //selected move after black initial selection is changed

                                            for (Point point : temp) {
                                                int k = convertArrayToBoard(point);
                                                panel.getComponent(k).setBackground(colorAt(point));
                                            }
                                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                                pos2 = new Point(x - 1, y - 1);
                                                j = convertArrayToBoard(pos1);
                                                panel.getComponent(j).setBackground(colorAt(pos1));
                                                if (temp.contains(pos2)) {
                                                    Piece.moveBlackPiece(pos1, pos2, board);
                                                    Piece.drawBoard(board);
                                                    updateGUI(board);
                                                    frame.setVisible(true);

                                                    //promotion
                                                    if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                                        promotion(pos2);
                                                    }

                                                    pos1 = null;
                                                    pos2 = null;
                                                    turn += 1;
                                                    turn = turn % 2;


                                                }

                                            } else {
                                                pos1 = null;
                                            }
                                        }

                                    }

                                }

                            } else {
                                pos1 = null; ///////////////////////////////////////////////////////////
                            }


                        }

                    }

                } //////////// /////////////////////////////////////////////////////////////////////////////end of human version
                else {
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (turn == 0) {

                        if (pos1 == null) {                            //white initial selection
                            pos1 = new Point(x - 1, y - 1);


                            temp = validMoves(pos1, board);
                            killTemp = findKill(pos1, temp);

                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                if (board[pos1.x][pos1.y] != "  ") {
                                    j = convertArrayToBoard(pos1);
                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                }

                                for (Point point : temp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                }

                                for (Point point : killTemp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                }

                            } else {
                                pos1 = null;
                            }


                        } else {                   //white selected move

                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
                            }

                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {  ////////////////////////////////////////////////////////////////////
                                pos2 = new Point(x - 1, y - 1);
                                j = convertArrayToBoard(pos1);
                                panel.getComponent(j).setBackground(colorAt(pos1));
                                if (temp.contains(pos2)) {
                                    Piece.moveWhitePiece(pos1, pos2, board);
                                    Piece.drawBoard(board);
                                    updateGUI(board);
                                    frame.setVisible(true);

                                    //promotion
                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotion(pos2);
                                    }

                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;

                                } else {                           //if white changes initial section

                                    if (board[pos2.x][pos2.y].charAt(0) == 'w' && !temp.contains(pos2)) {
                                        pos1 = null;
                                        if (pos1 == null) {
                                            pos1 = pos2;
                                            temp = validMoves(pos1, board);
                                            killTemp = findKill(pos1, temp);

                                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                                if (board[pos1.x][pos1.y] != "  ") {
                                                    j = convertArrayToBoard(pos1);
                                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                                }

                                                for (Point point : temp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                                }

                                                for (Point point : killTemp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                                }

                                            } else {
                                                pos1 = null;
                                            }

                                        } else {                    //selected move after white initial selection is changed

                                            for (Point point : temp) {
                                                int k = convertArrayToBoard(point);
                                                panel.getComponent(k).setBackground(colorAt(point));
                                            }
                                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {//////////////////////////////////////////////////////////////////////////////////
                                                pos2 = new Point(x - 1, y - 1);
                                                j = convertArrayToBoard(pos1);
                                                panel.getComponent(j).setBackground(colorAt(pos1));
                                                if (temp.contains(pos2)) {
                                                    Piece.moveWhitePiece(pos1, pos2, board);
                                                    Piece.drawBoard(board);
                                                    updateGUI(board);
                                                    frame.setVisible(true);

                                                    //promotion
                                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                                        promotion(pos2);
                                                    }


                                                    pos1 = null;
                                                    pos2 = null;
                                                    turn += 1;
                                                    turn = turn % 2;


                                                }

                                            } else {
                                                pos1 = null;
                                            }
                                        }
                                    }

                                }

                            } else {
                                pos1 = null;
                            }
                        }


                    } else { ////////////////////////////black move

                        if (pos1 == null) {                                       //black initial selection
                            pos1 = new Point(x - 1, y - 1);
                            temp = validMoves(pos1, board);
                            killTemp = findKill(pos1, temp);

                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                if (board[pos1.x][pos1.y] != "  ") {
                                    j = convertArrayToBoard(pos1);
                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                }


                                for (Point point : temp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                }

                                for (Point point : killTemp) {
                                    int k = convertArrayToBoard(point);
                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                }

                            } else {
                                pos1 = null;
                            }


                        } else {                                                  //black selected move

                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
                            }

                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {////////////////////////////////////////////////////////
                                pos2 = new Point(x - 1, y - 1);
                                j = convertArrayToBoard(pos1);
                                panel.getComponent(j).setBackground(colorAt(pos1));
                                if (temp.contains(pos2)) {
                                    Piece.moveBlackPiece(pos1, pos2, board);
                                    Piece.drawBoard(board);
                                    updateGUI(board);
                                    frame.setVisible(true);

                                    //promotion
                                    if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotionAI(pos2);
                                    }


                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;


                                } else {                             //if black changes initial section

                                    if (board[pos2.x][pos2.y].charAt(0) == 'b' && !temp.contains(pos2)) {
                                        pos1 = null;
                                        if (pos1 == null) {
                                            pos1 = pos2;
                                            temp = validMoves(pos1, board);
                                            killTemp = findKill(pos1, temp);


                                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                                if (board[pos1.x][pos1.y] != "  ") {
                                                    j = convertArrayToBoard(pos1);
                                                    panel.getComponent(j).setBackground(new Color(255, 255, 105));
                                                }


                                                for (Point point : temp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 255, 153));
                                                }

                                                for (Point point : killTemp) {
                                                    int k = convertArrayToBoard(point);
                                                    panel.getComponent(k).setBackground(new Color(255, 51, 51));
                                                }

                                            } else {
                                                pos1 = null;
                                            }


                                        } else {                                     //selected move after black initial selection is changed

                                            for (Point point : temp) {
                                                int k = convertArrayToBoard(point);
                                                panel.getComponent(k).setBackground(colorAt(point));
                                            }
                                            if (board[pos1.x][pos1.y].charAt(0) != 'w') {
                                                pos2 = new Point(x - 1, y - 1);
                                                j = convertArrayToBoard(pos1);
                                                panel.getComponent(j).setBackground(colorAt(pos1));
                                                if (temp.contains(pos2)) {
                                                    Piece.moveBlackPiece(pos1, pos2, board);
                                                    Piece.drawBoard(board);
                                                    updateGUI(board);
                                                    frame.setVisible(true);

                                                    //promotion
                                                    if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                                        promotionAI(pos2);
                                                    }

                                                    pos1 = null;
                                                    pos2 = null;
                                                    turn += 1;
                                                    turn = turn % 2;


                                                }

                                            } else {
                                                pos1 = null;
                                            }
                                        }

                                    }

                                }

                            } else {
                                pos1 = null; ///////////////////////////////////////////////////////////
                            }


                        }

                    }//end of black move for AI version

                }//////////// /////////////////////////////////////////////////////////////////////////////end of AI version

            }//end of input

        }// end of game over
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