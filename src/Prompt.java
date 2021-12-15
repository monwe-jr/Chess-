import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class Prompt extends JFrame implements ActionListener {


    public static String mode;
    public static int depth;
    private final JFrame frame = new JFrame("Chess");
    private final JPanel panel = new JPanel();
    private JLabel gameMode;
    JButton button1;
    JButton button2;
    JButton button3;


    Prompt() {
        frame.setSize(600, 600);
        button1 = new JButton("Human vs AI");
        button2 = new JButton("Human vs Human");
        button1.addActionListener(this);
        button2.addActionListener(this);
        gameMode = new JLabel("Mode selected: ", SwingConstants.CENTER);
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button1);
        panel.add(button2);
        panel.add(gameMode);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * GUI component to prompt for difficulty if player chooses Human vs AI option
     *
     * @param l ActionListener
     */
    private void difficultyPromp(ActionListener l) {
        frame.setSize(600, 600);
        button1 = new JButton("Easy");
        button2 = new JButton("Normal");
        button3 = new JButton("Hard");

        button1.addActionListener(l);
        button2.addActionListener(l);
        button3.addActionListener(l);
        gameMode = new JLabel("Difficulty selected: ", SwingConstants.CENTER);
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(gameMode);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (mode == null) {
            if (e.getSource() == button1) {
                mode = "AI";
                gameMode.setText("Mode selected: Human vs " + mode);
                button1.removeActionListener(this);
                button2.removeActionListener(this);
            } else {
                mode = "Human";
                gameMode.setText("Mode selected: Human vs " + mode);
                button1.removeActionListener(this);
                button2.removeActionListener(this);



                frame.dispose(); //////////////////////FIXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            }

        }

        if (mode.equals("AI")) {
            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (e.getSource() == button1) {
                        depth = 1;
                    } else if (e.getSource() == button2) {
                        depth = 2;
                    } else if (e.getSource() == button3) {
                        depth = 3;
                    }



                    if (depth == 1) {
                        gameMode.setText("Difficulty selected: " + "Easy");
                    } else if (depth == 2) {
                        gameMode.setText("Difficulty selected: " + "Normal");
                    } else if (depth == 3) {
                        gameMode.setText("Difficulty selected: " + "Hard");
                    }


                    button1.removeActionListener(this);
                    button2.removeActionListener(this);
                    button3.removeActionListener(this);



                    frame.dispose();//////////////////////FIXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


                }
            };
            difficultyPromp(listener);

        }



    }




}
