import com.sun.source.util.TaskEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings({"StringEquality", "ConstantConditions"})
public class Board extends JFrame implements MouseListener {
    AI bot;
    Piece pieces = new Piece();
    Random num = new Random();
    boolean input; //used to disable user input
    public String mode;  //mode of game
    public int depth;  //minimax depth
    int turn; //turn
    boolean gameOver; //game over
    boolean stopAI;  //stops thefrom mkaing a move if true
    String[][] board = pieces.board;  //2D array that represents the state of the game
    JFrame frame = new JFrame("Chess");
    JPanel panel = new JPanel(new GridLayout(8, 8));  //initializes JPanel layout
    Timer timer = new Timer();
    Point bKing; //location of black king
    Point wKing; //location of white king


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
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }//constructor for human vs human


    Board(String m, int d) {
        this.mode = m;
        this.depth = d;
        bot = new AI();
        gameOver = false;
        input = true;
        turn = 0;
        createBoard();
        updateGUI(board);
        frame.addMouseListener(this);
        frame.setSize(900, 900);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }//constructor for human vs AI


    /**
     * Creates initial grid
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
     * Converts board coordinates to array coordinates
     *
     * @param x Board Coordinate
     */
    private Point convertBoardToArray(int x) {
        int pos = 0;


        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[i].length; j++) {

                if (pos == x) {
                    return new Point(j, i);
                }

                pos++;
            }
        }
        return null;

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
     * Updates GUI after move is made on the board representation
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
                        wKing = p;


                    } else if (board[p.x][p.y] == "bK") {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(kingB));
                        bKing = p;

                    } else {
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon()));
                    }
                }

                count++;
            }

        }

    }


    /**
     * Returns a copy of arr
     */
    private String[][] copyOf(String[][] arr) {
        String[][] temp = new String[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }

        return temp;
    }


    /**
     * Returns an array of potential moves of pos1 (the initial point)
     *
     * @param pos1 Initial position
     * @param arr  2D array representation of board
     */
    private ArrayList<Point> validMoves(Point pos1, String[][] arr) {
        ArrayList<Point> returns = new ArrayList<>();
        String[][] temp;
        Point pos2;

        if ((board[pos1.x][pos1.y].charAt(0) == 'w')) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    pos2 = new Point(j, i);

                    if (Piece.moveWhitePiece(pos1, pos2, temp)) {
                        returns.add(pos2);

                    }
                }
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr.length; j++) {
                    temp = copyOf(arr);
                    pos2 = new Point(j, i);

                    if (Piece.moveBlackPiece(pos1, pos2, temp)) {
                        returns.add(pos2);

                    }
                }
            }
        }

        return returns;
    }


    /**
     * Returns an ArrayList of kills the pieces at pos1 can make
     *
     * @param pos1 Initial position
     * @param p    ArrayList of potential moves
     */
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


    /**
     * Returns the original color of position p
     *
     * @param p the position that will be restored
     * @return the original color of the grid after the board was created
     */
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


    /**
     * This method implements pawn promotion for the human player
     *
     * @param p the position of the pawn at the end of the board
     */
    private void promotion(Point p) {
        input = false;

        //stop the AI from making a move
        if (mode == "AI") {
            stopAI = true;
        }

        //creating buttons and panel
        JPanel buttonPanel = new JPanel();
        JButton button1 = new JButton();
        JButton button2 = new JButton();
        JButton button3 = new JButton();
        JButton button4 = new JButton();
        int x = convertArrayToBoard(p);

        if (p.y == 7) {   //if a white pawn is at the end of the board
            button1.setIcon(rookW);
            button2.setIcon(horseW);
            button3.setIcon(bishopW);
            button4.setIcon(queenW);
            ActionListener listener = e -> {

                if (e.getSource() == button1) {  //if the user selects the Rook
                    board[p.x][p.y] = "wR";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;
                    stopAI = false;
                    AIMove();


                } else if (e.getSource() == button2) { //if the user selects the Knight
                    board[p.x][p.y] = "wN";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;
                    stopAI = false;
                    AIMove();


                } else if (e.getSource() == button3) { //if the user selects the Bishop
                    board[p.x][p.y] = "wB";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;
                    stopAI = false;
                    AIMove();


                } else if (e.getSource() == button4) {  //if the user selects the Queen
                    board[p.x][p.y] = "wQ";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;
                    stopAI = false;
                    AIMove();


                }
            };


            button1.addActionListener(listener);
            button2.addActionListener(listener);
            button3.addActionListener(listener);
            button4.addActionListener(listener);

        } else {  //if a black pawn is at the end of the board

            button1.setIcon(rookB);
            button2.setIcon(horseB);
            button3.setIcon(bishopB);
            button4.setIcon(queenB);
            ActionListener listener = e -> {

                if (e.getSource() == button1) {  //if the user selects the Rook
                    board[p.x][p.y] = "bR";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button2) { //if the user selects the Knight
                    board[p.x][p.y] = "bN";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button3) {  //if the user selects the Bishop
                    board[p.x][p.y] = "bB";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                } else if (e.getSource() == button4) {  //if the user selects the Queen
                    board[p.x][p.y] = "bQ";
                    frame.remove(buttonPanel);
                    updateGUI(board);
                    frame.invalidate();
                    frame.validate();
                    frame.repaint();
                    input = true;


                }

            };

            //adding button functionality
            button1.addActionListener(listener);
            button2.addActionListener(listener);
            button3.addActionListener(listener);
            button4.addActionListener(listener);
        }
        //adding buttons to the tile
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


    /**
     * This method implements pawn promotion for the AI
     * @param p  the position of the pawn at the end of the board
     */
    private void promotionAI(Point p) {
        int random = num.nextInt(2);
        input = false;

       //change to Bishop or Knight
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

        //change to Rook or Queen
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

            //change to queen
        } else if (depth == 3) {
            board[p.x][p.y] = "bQ";

            updateGUI(board);
            frame.invalidate();
            frame.validate();
            frame.repaint();
            input = true;

        }

    }



    /**
     * Checks for checkmate on both players
     */
    private void checkmate() {
        JLabel msg = new JLabel();
        msg.setSize(300, 300);

        if (Piece.checkmate(board, 'w') || Piece.checkmate(board, 'b')) {
            if (Piece.checkmate(board, 'b')) {

                gameOver = true;
                TimerTask task1 = new TimerTask() {
                    @Override
                    public void run() {
                        frame.dispose();
                    }
                };
                timer.schedule(task1, 1000);
                msg.setText("White wins!");
                panel.add(msg);
                frame.setSize(600,600);
                frame.add(panel, BorderLayout.CENTER);
                frame.setVisible(true);
                TimerTask task2 = new TimerTask() {
                    @Override
                    public void run() {
                        frame.dispose();
                    }
                };

                timer.schedule(task2, 2000);

            } else if (Piece.checkmate(board, 'w')) {

                gameOver = true;
                TimerTask task1 = new TimerTask() {
                    @Override
                    public void run() {
                        frame.dispose();
                    }
                };
                timer.schedule(task1, 1000);
                msg.setText("Black wins!");
                panel.add(msg);
                frame.setSize(600,600);
                frame.add(panel, BorderLayout.CENTER);
                frame.setVisible(true);
                TimerTask task2 = new TimerTask() {
                    @Override
                    public void run() {
                        frame.dispose();
                    }
                };
                timer.schedule(task2, 2000);

            }

        }
    }


    /**
     * Checks for a draw
     */
    private void draw() {
        JLabel msg = new JLabel("Draw!");

        msg.setSize(300, 300);
        if (Piece.draw(board)) {

            gameOver = true;
            TimerTask task1 = new TimerTask() {
                @Override
                public void run() {
                    frame.dispose();
                }
            };

            timer.schedule(task1, 1000);
            panel.removeAll();
            panel.add(msg);
            frame.setSize(600,600);
            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
            TimerTask task2 = new TimerTask() {
                @Override
                public void run() {
                    frame.dispose();
                }
            };
            timer.schedule(task2, 2000);

        }
    }


    /**
     * Sets background colour of a king into red if that king is in check
     */
    private void check() {
        int wK = convertArrayToBoard(wKing);
        int bK = convertArrayToBoard(bKing);

        if (Piece.check(board, 'w') || Piece.check(board, 'b')) {

            if (Piece.check(board, 'w')) {
                panel.getComponent(wK).setBackground(new Color(255, 51, 51));

            } else if (Piece.check(board, 'b')) {
                panel.getComponent(bK).setBackground(new Color(255, 51, 51));
            }


        } else {
            panel.getComponent(wK).setBackground(colorAt(wKing));
            panel.getComponent(bK).setBackground(colorAt(bKing));

        }

    }


    /**
     * Generates a move for the AI
     */
    private void AIMove() {
        if (!stopAI) {
            int[] result = bot.minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            pos1 = convertBoardToArray(result[0]);
            pos2 = convertBoardToArray(result[1]);


            Piece.moveBlackPiece(pos1, pos2, board);
            updateGUI(board);
            frame.setVisible(true);

            //Pawn promotion
            if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                promotionAI(pos2);
            }
            check();
            draw();
            checkmate();
            pos1 = null;
            pos2 = null;
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Point pos1;
    Point pos2;
    Point pos1temp;
    Point pos2temp;
    ArrayList<Point> temp = new ArrayList<>();
    ArrayList<Point> killTemp = new ArrayList<>();


    @SuppressWarnings({"StringEquality", "ConstantConditions"})
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getComponent().getX() / e.getComponent().getWidth() + 1;        //column of clicked
        int y = 7 - e.getComponent().getY() / e.getComponent().getHeight() + 1;   //row of clicked
        int j; // location of clicked converted to board coordinates


        if (!gameOver) {
            if (input) {  //if the program is allowing an input

                if (mode == "Human") {  //Human vs Human
                    if (turn == 0) {

                        //white initial selection of Human vs Human
                        if (pos1 == null) {
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


                        } else {

                            //white selected move
                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
                            }

                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                pos2 = new Point(x - 1, y - 1);
                                j = convertArrayToBoard(pos1);
                                panel.getComponent(j).setBackground(colorAt(pos1));
                                if (temp.contains(pos2)) {
                                    Piece.moveWhitePiece(pos1, pos2, board);
                                    Piece.drawBoard(board);
                                    updateGUI(board);
                                    frame.setVisible(true);

                                    //pawn promotion
                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotion(pos2);
                                    }

                                    check();
                                    draw();
                                    checkmate();
                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;

                                } else {

                                    //if white changes initial section
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

                                        } else {

                                            //selected move after white initial selection is changed
                                            for (Point point : temp) {
                                                int k = convertArrayToBoard(point);
                                                panel.getComponent(k).setBackground(colorAt(point));
                                            }
                                            if (board[pos1.x][pos1.y].charAt(0) != 'b') {
                                                pos2 = new Point(x - 1, y - 1);
                                                j = convertArrayToBoard(pos1);
                                                panel.getComponent(j).setBackground(colorAt(pos1));
                                                if (temp.contains(pos2)) {
                                                    Piece.moveWhitePiece(pos1, pos2, board);
                                                    Piece.drawBoard(board);
                                                    updateGUI(board);
                                                    frame.setVisible(true);

                                                    //pawn promotion
                                                    if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                                        promotion(pos2);
                                                    }

                                                    check();
                                                    draw();
                                                    checkmate();
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

                        //black initial selection
                        if (pos1 == null) {
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


                        } else {

                            //black selected move
                            for (Point value : temp) {
                                int k = convertArrayToBoard(value);
                                panel.getComponent(k).setBackground(colorAt(value));
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

                                    //pawn promotion
                                    if (pos2.y == 0 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                        promotion(pos2);
                                    }

                                    check();
                                    draw();
                                    checkmate();
                                    pos1 = null;
                                    pos2 = null;
                                    turn += 1;
                                    turn = turn % 2;


                                } else {

                                    //if black changes initial section
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


                                        } else {

                                            //selected move after black initial selection is changed
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

                                                    check();
                                                    draw();
                                                    checkmate();
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

                    }

                } else { //end of human version beginning of Human vs AI

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    //white initial selection
                    if (pos1 == null) {
                        pos1 = new Point(x - 1, y - 1);
                        pos1temp = pos1;

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


                    } else {

                        //white selected move
                        for (Point value : temp) {
                            int k = convertArrayToBoard(value);
                            panel.getComponent(k).setBackground(colorAt(value));
                        }

                        if (board[pos1.x][pos1.y].charAt(0) != 'b') {  ////////////////////////////////////////////////////////////////////
                            pos2 = new Point(x - 1, y - 1);
                            pos2temp = pos2;
                            j = convertArrayToBoard(pos1);
                            panel.getComponent(j).setBackground(colorAt(pos1));
                            if (temp.contains(pos2)) {
                                Piece.moveWhitePiece(pos1, pos2, board);
                                updateGUI(board);
                                frame.setVisible(true);


                                //promotion
                                if (pos2.y == 7 && board[pos2.x][pos2.y].charAt(1) == 'P') {
                                    promotion(pos2);
                                }

                                check();
                                draw();
                                checkmate();
                                pos1 = null;
                                pos2 = null;
                                AIMove(); //AI makes a move


                            } else {

                                //if white changes initial section
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

                                    } else {

                                        //selected move after white initial selection is changed
                                        for (Point point : temp) {
                                            int k = convertArrayToBoard(point);
                                            panel.getComponent(k).setBackground(colorAt(point));
                                        }
                                        if (board[pos1.x][pos1.y].charAt(0) != 'b') {
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

                                                check();
                                                draw();
                                                checkmate();
                                                pos1 = null;
                                                pos2 = null;
                                                AIMove(); //AI makes a move


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


                } //end of AI version


            } //end of input

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