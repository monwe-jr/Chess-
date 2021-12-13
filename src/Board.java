import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board implements MouseListener {

   JFrame frame = new JFrame();
   JPanel panel = new JPanel(new GridLayout(8,8));

    Board() {


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



        ((JPanel) panel.getComponent(0)).remove(0);
        ((JPanel) panel.getComponent(0)).add(new JLabel(new ImageIcon("rookB.png")));
        ((JPanel) panel.getComponent(1)).remove(0);
        ((JPanel) panel.getComponent(1)).add(new JLabel(new ImageIcon("horseB.png")));
        ((JPanel) panel.getComponent(2)).remove(0);
        ((JPanel) panel.getComponent(2)).add(new JLabel(new ImageIcon("bishopB.png")));
        ((JPanel) panel.getComponent(4)).remove(0);
        ((JPanel) panel.getComponent(4)).add(new JLabel(new ImageIcon("queenB.png")));
        ((JPanel) panel.getComponent(3)).remove(0);
        ((JPanel) panel.getComponent(3)).add(new JLabel(new ImageIcon("kingB.png")));
        ((JPanel) panel.getComponent(5)).remove(0);
        ((JPanel) panel.getComponent(5)).add(new JLabel(new ImageIcon("bishopB.png")));
        ((JPanel) panel.getComponent(6)).remove(0);
        ((JPanel) panel.getComponent(6)).add(new JLabel(new ImageIcon("horseB.png")));
        ((JPanel) panel.getComponent(7)).remove(0);
        ((JPanel) panel.getComponent(7)).add(new JLabel(new ImageIcon("rookB.png")));
        ((JPanel) panel.getComponent(8)).remove(0);
        ((JPanel) panel.getComponent(8)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(9)).remove(0);
        ((JPanel) panel.getComponent(9)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(10)).remove(0);
        ((JPanel) panel.getComponent(10)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(11)).remove(0);
        ((JPanel) panel.getComponent(11)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(12)).remove(0);
        ((JPanel) panel.getComponent(12)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(13)).remove(0);
        ((JPanel) panel.getComponent(13)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(14)).remove(0);
        ((JPanel) panel.getComponent(14)).add(new JLabel(new ImageIcon("pawnB.png")));
        ((JPanel) panel.getComponent(15)).remove(0);
        ((JPanel) panel.getComponent(15)).add(new JLabel(new ImageIcon("pawnB.png")));

        ((JPanel) panel.getComponent(63)).remove(0);
        ((JPanel) panel.getComponent(63)).add(new JLabel(new ImageIcon("rookW.png")));
        ((JPanel) panel.getComponent(62)).remove(0);
        ((JPanel) panel.getComponent(62)).add(new JLabel(new ImageIcon("horseW.png")));
        ((JPanel) panel.getComponent(61)).remove(0);
        ((JPanel) panel.getComponent(61)).add(new JLabel(new ImageIcon("bishopW.png")));
        ((JPanel) panel.getComponent(59)).remove(0);
        ((JPanel) panel.getComponent(59)).add(new JLabel(new ImageIcon("kingW.png")));
        ((JPanel) panel.getComponent(60)).remove(0);
        ((JPanel) panel.getComponent(60)).add(new JLabel(new ImageIcon("queenW.png")));
        ((JPanel) panel.getComponent(58)).remove(0);
        ((JPanel) panel.getComponent(58)).add(new JLabel(new ImageIcon("bishopW.png")));
        ((JPanel) panel.getComponent(57)).remove(0);
        ((JPanel) panel.getComponent(57)).add(new JLabel(new ImageIcon("horseW.png")));
        ((JPanel) panel.getComponent(56)).remove(0);
        ((JPanel) panel.getComponent(56)).add(new JLabel(new ImageIcon("rookW.png")));
        ((JPanel) panel.getComponent(55)).remove(0);
        ((JPanel) panel.getComponent(55)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(54)).remove(0);
        ((JPanel) panel.getComponent(54)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(53)).remove(0);
        ((JPanel) panel.getComponent(53)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(52)).remove(0);
        ((JPanel) panel.getComponent(52)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(51)).remove(0);
        ((JPanel) panel.getComponent(51)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(50)).remove(0);
        ((JPanel) panel.getComponent(50)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(49)).remove(0);
        ((JPanel) panel.getComponent(49)).add(new JLabel(new ImageIcon("pawnW.png")));
        ((JPanel) panel.getComponent(48)).remove(0);
        ((JPanel) panel.getComponent(48)).add(new JLabel(new ImageIcon("pawnW.png")));


        frame.addMouseListener(this);
        frame.setSize(800, 800);
        frame.add(panel);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);


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



