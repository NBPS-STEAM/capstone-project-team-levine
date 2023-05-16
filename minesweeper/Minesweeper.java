/*
todo:
set mine count selector limit whenever rows/column count changed
 */
package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Minesweeper {

    ImageIcon flag_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\flag.png");
    Image flag_icon_scaled;
    ImageIcon mine_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\mine.png");
    Image mine_icon_scaled;
    ImageIcon qmark_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\question.png");
    Image qmark_icon_scaled;

    Random random = new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel selection_panel = new JPanel();
    JPanel spinner_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JSpinner width_spinner, height_spinner, mines_spinner;
    JLabel width_label = new JLabel();
    JLabel height_label = new JLabel();
    JLabel mines_label = new JLabel();
    JButton regen_button = new JButton();
    JButton[][] buttons;


    Minesweeper(){

        // Frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(800, 200);
        frame.setLocationByPlatform(true);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        // Title
        textfield.setForeground(new Color(50, 100, 200));
        textfield.setFont(new Font("Edwardian Script ITC", Font.BOLD, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Minesweeper");
        textfield.setOpaque(true);

        // Setup Spinners
        width_spinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        height_spinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        mines_spinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        // Spinner Labels
        Font label_font = new Font("Comic Sans MS", Font.PLAIN, 20);
        width_label.setFont(label_font);
        width_label.setText("Width: ");
        height_label.setFont(label_font);
        height_label.setText("Height: ");
        mines_label.setFont(label_font);
        mines_label.setText("Mines: ");

        // Generate Field Button
        regen_button.setText("Start");
        regen_button.setFont(label_font);
        regen_button.addActionListener(e -> generateGrid());
        regen_button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spinner Panel
        spinner_panel.setBounds(0, 0, 300, 150);
        spinner_panel.setLayout(new GridLayout(3, 2, 5, 10));

        spinner_panel.add(width_label);
        spinner_panel.add(width_spinner);
        spinner_panel.add(height_label);
        spinner_panel.add(height_spinner);
        spinner_panel.add(mines_label);
        spinner_panel.add(mines_spinner);

        // Selection Panel
        selection_panel.setBounds(0, 0, 300, 50);
        selection_panel.setLayout(new BoxLayout(selection_panel, BoxLayout.Y_AXIS));
        selection_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        selection_panel.add(spinner_panel);
        selection_panel.add(regen_button);

        // Title Panel
        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, 500, 200);
        title_panel.setBackground(new Color(120, 120, 120));

        title_panel.add(selection_panel, BorderLayout.EAST);
        title_panel.add(textfield);

        // Button Panel
        button_panel.setBackground(new Color(150, 150, 150));

        // Finishing
        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel);

        frame.validate();
        frame.repaint();
    }

    private void generateGrid() {
        button_panel.removeAll();
        button_panel.validate();

        int rows = (int)height_spinner.getValue();
        int columns = (int)width_spinner.getValue();

        button_panel.setLayout(new GridLayout(rows, columns));

        buttons = new JButton[rows][columns];

        for (int i=0; i < rows; i++) {
            for (int j=0; j < columns; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(new Color(150, 150, 150));
                button_panel.add(buttons[i][j]);
                buttons[i][j].setFocusable(false);

                buttons[i][j].addActionListener(generateTileActionListener(j, i));
                buttons[i][j].addMouseListener(generateTileMouseListener(j, i));
            }
        }

        frame.setSize(800, 200 + (int)(800 * ((double)rows/columns)));

        int side_len = (frame.getSize().height - 200) / rows;
        flag_icon_scaled = flag_icon_full.getImage().getScaledInstance(side_len, side_len, Image.SCALE_DEFAULT);
        mine_icon_scaled = mine_icon_full.getImage().getScaledInstance(side_len, side_len, Image.SCALE_DEFAULT);
        qmark_icon_scaled = qmark_icon_full.getImage().getScaledInstance(side_len, side_len, Image.SCALE_DEFAULT);

        frame.validate();
        frame.repaint();
    }

    private void flagTile(int x, int y, boolean question_mark) {
        Icon ico;
        if (question_mark)
            ico = new ImageIcon(qmark_icon_scaled);
        else
            ico = new ImageIcon(flag_icon_scaled);

        if (buttons[y][x].getText().equals("")) {
            if (buttons[y][x].getIcon() == null)
                buttons[y][x].setIcon(ico);
            else
                buttons[y][x].setIcon(null);
        }
    }

    private ActionListener generateTileActionListener(int x, int y) {
        return e -> {
            if (buttons[y][x].getText().equals("") && buttons[y][x].getIcon() == null) {
                // perform check
                // set button text & color to result (text = mine/adjacent mines, foreground = text color,
                // background = gray normally or red if mine
                buttons[y][x].setBackground(new Color(80, 80, 80));
                buttons[y][x].setForeground(new Color(255, 255, 255));
                buttons[y][x].setText(" ");
                buttons[y][x].setIcon(null);
                // remove icon if not mine, set icon to mine if so
            }
        };
    }

    private MouseListener generateTileMouseListener(int x, int y) {
        return new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                int modifiers = mouseEvent.getModifiers();
                if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
                    flagTile(x, y, true);
                }
                if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    flagTile(x, y, false);
                }
            }
        };
    }
}
