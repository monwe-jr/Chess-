import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 3p71 Term Project: Chess
 *
 * Francis Monwe
 * 6724355
 *
 * Jashandeep Pannu
 * 6505861
 * 
 * This is the main class
 * It implements and run the entire program as well as handling a portion of the GUI components and conditions
 */

public class Chess {
    Board board;
    Timer timer = new Timer();
    String mode;
    int depth;
    JFrame frame = new JFrame("Prompt");
    JPanel panel = new JPanel();
    JLabel gameMode;
    JButton button1;
    JButton button2;
    JButton button3;

    Chess() {
        prompt();
    }


    /**
     * Prompt menu for chess game
     */
    private void prompt() {
        frame.setSize(600, 600);
        ActionListener listener1 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //Mode selection
                if (e.getSource() == button1) {
                    mode = "AI";
                    gameMode.setText("Mode Selected: Human vs " + mode);
                    button1.removeActionListener(this);
                    button2.removeActionListener(this);
                } else {
                    mode = "Human";
                    gameMode.setText("Mode Selected: Human vs " + mode);
                    button1.removeActionListener(this);
                    button2.removeActionListener(this);


                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            frame.dispose();
                            board = new Board(mode);
                        }
                    };

                    timer.schedule(task, 1000);

                }


                //Difficulty selection if mode is Human vs AI
                if (mode.equals("AI")) {
                    ActionListener listener2 = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (e.getSource() == button1) {
                                depth = 1;
                            } else if (e.getSource() == button2) {
                                depth = 3;
                            } else if (e.getSource() == button3) {
                                depth = 4;
                            }


                            if (depth == 1) {
                                gameMode.setText("Difficulty Selected: " + "Easy");
                            } else if (depth == 2) {
                                gameMode.setText("Difficulty Selected: " + "Normal");
                            } else if (depth == 4) {
                                gameMode.setText("Difficulty Selected: " + "Hard");
                            }


                            button1.removeActionListener(this);
                            button2.removeActionListener(this);
                            button3.removeActionListener(this);

                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    frame.dispose();
                                    board = new Board(mode, depth);
                                }
                            };

                            timer.schedule(task, 1000);
                        }
                    };

                    button1 = new JButton("Easy");
                    button2 = new JButton("Normal");
                    button3 = new JButton("Hard");
                    button1.addActionListener(listener2);
                    button2.addActionListener(listener2);
                    button3.addActionListener(listener2);
                    gameMode = new JLabel("Difficulty selected: ", SwingConstants.CENTER);
                    panel.setLayout(new GridLayout(0, 1));
                    panel.add(button1);
                    panel.add(button2);
                    panel.add(button3);
                    panel.add(gameMode);
                    frame.add(panel, BorderLayout.CENTER);
                    frame.setVisible(true);

                }
            }
        };

        gameMode = new JLabel("Mode Selected: ", SwingConstants.CENTER);
        button1 = new JButton("Human vs AI");
        button2 = new JButton("Human vs Human");
        button1.addActionListener(listener1);
        button2.addActionListener(listener1);
        panel.setLayout(new GridLayout(0, 1));
        panel.add(button1);
        panel.add(button2);
        panel.add(gameMode);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


    public static void main(String[] args) {
        new Chess();
    }
}

