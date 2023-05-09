import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Minesweeper implements ActionListener{

    Random random = new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JButton[][] buttons;

    int grid_rows = 4;
    int grid_columns = 6;

    Minesweeper(){

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(120, 120, 120));
        textfield.setForeground(new Color(50, 50, 200));
        textfield.setFont(new Font("Edwardian Script ITC", Font.PLAIN, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Minesweeper");
        textfield.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0,0,800,100);

        button_panel.setLayout(new GridLayout(grid_rows, grid_columns));
        button_panel.setBackground(new Color(150, 150, 150));

        buttons = new JButton[grid_rows][grid_columns];

        for (int i=0; i < grid_rows; i++) {
            for (int j=0; j < grid_columns; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(new Color(150,150,150));
                button_panel.add(buttons[i][j]);
                buttons[i][j].setFocusable(false);
                buttons[i][j].addActionListener(this);
            }
        }

        title_panel.add(textfield);
        frame.add(title_panel,BorderLayout.NORTH);
        frame.add(button_panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i=0; i < grid_rows; i++) {
            for (int j=0; j < grid_columns; j++) {
                if(e.getSource().equals(buttons[i][j])) {
                    if(buttons[i][j].getText().equals("")) {
                        // perform check
                        // set button text & color to result (text = mine/adjacent mines, foreground = text color,
                        // background = gray normally or red if mine
                        buttons[i][j].setBackground(new Color(80, 80, 80));
                        buttons[i][j].setForeground(new Color(255, 255,  255));
                        buttons[i][j].setText(" ");
                    }
                }
            }
        }
    }
}
