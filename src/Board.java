import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board implements MouseListener {


    String[][] board;
   JFrame frame = new JFrame();
   JPanel panel = new JPanel(new GridLayout(8,8));

    Board(String[][] board) {
        this.board = board;


        frame.setTitle("Chess");
        int c = 0;
        int k=0;



        for (int i=0;i<8;i++){
            for (int j = 0; j < 8; j++) {
                JPanel grid= new JPanel(new GridLayout(1,1));
                grid.add(new JLabel(new ImageIcon()));
                grid.addMouseListener(this);

                if (c == 0) {
                    grid.setBackground(Color.white);
                } else {
                    grid.setBackground(Color.darkGray);
                }

                c = c+1;
                c = c % 2;


                panel.add(grid, k);
                k++;
            }

            c = c+1;
            c = c % 2;
        }




        int count = 0;
        for (int i = board.length-1; i >=0; i--) {
            for (int j = 0; j < board.length  ; j++) {
                Point p = new Point(j,i);
                int x = convertArrayToBoard(p);

                if(x == count) {

                    if(board[p.x][p.y] == "wP"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("pawnW.png")));
                    }
                    else if(board[p.x][p.y] == "bP"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("pawnB.png")));
                    }
                    else if(board[p.x][p.y] == "wR"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("rookW.png")));
                    }
                    else if(board[p.x][p.y] == "bR"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("rookB.png")));

                    }
                    else if(board[p.x][p.y] == "wN"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("horseW.png")));

                    }
                    else if(board[p.x][p.y] == "bN"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("horseB.png")));

                    }
                    else if(board[p.x][p.y] == "wB"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("bishopW.png")));

                    }
                    else if(board[p.x][p.y] == "bB"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("bishopB.png")));

                    }
                    else if(board[p.x][p.y] == "wQ"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("queenW.png")));

                    }
                    else if(board[p.x][p.y] == "bQ"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("queenB.png")));

                    }
                    else if(board[p.x][p.y] == "wK"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("kingW.png")));


                    }
                    else if(board[p.x][p.y] == "bK"){
                        ((JPanel) panel.getComponent(x)).remove(0);
                        ((JPanel) panel.getComponent(x)).add(new JLabel(new ImageIcon("kingB.png")));

                    }

                }


                count++;
            }

        }
        frame.addMouseListener(this);
        frame.setSize(800, 800);
        frame.add(panel);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);


    }

    private Point convertBoardToArray(int x){
        int pos = 0;
        Point p = null;

        for (int i = board.length-1; i >=0; i--) {
            for (int j = 0; j <board[i].length ; j++) {

                if (pos == x){
                    p.setLocation(j,i);
                }

                pos++;
            }
        }

        return p;
    }



    private int convertArrayToBoard(Point p){
        int k = 0;
        int x =0;

        for (int i = board.length-1; i >=0; i--) {
            for (int j = 0; j <board[i].length ; j++) {
                if(p.getX() == j && p.getY() ==i) {
                    x = k;
                }

                k++;
            }
        }

        return x;
    }





    @Override
    public void mouseClicked(MouseEvent e) {

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



