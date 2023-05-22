/*
todo:
make icon filepaths portable
have icons resize when window size changes instead of when grid is generated (but only if grid isn't empty)
have adjacent mine count text resize to fit
hide adjacent mine count if 0
add remaining flag count
make buttons unclickable and give mine tiles celebratory icons when all tiles are uncovered
fix error when starting game with too many mines
optimize by having findSurroundingOffsets return without null entries
 */
package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.*;

public class Minesweeper {
    final int width = 500;
    final Color[] tile_colors = {
            new Color(80, 80, 80),
            new Color(0, 140, 255),
            new Color(0, 150, 0),
            new Color(200, 0, 0),
            new Color(30, 30, 255),
            new Color(200, 200, 0),
            new Color(220, 120, 0),
            new Color(100, 100, 200),
            new Color(255, 120, 180),
            new Color(255, 0, 0)
    };

    ImageIcon flag_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\flag.png");
    ImageIcon flag_icon_scaled;
    ImageIcon mine_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\mine.png");
    ImageIcon mine_icon_scaled;
    ImageIcon qmark_icon_full = new ImageIcon("C:\\Coding2CodeHarder\\Java\\capstone-project-team-levine\\minesweeper\\assets\\question.png");
    ImageIcon qmark_icon_scaled;
    Font tileFont;

    Random random = new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel selection_panel = new JPanel();
    JPanel spinner_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JSpinner width_spinner = new JSpinner();
    JSpinner height_spinner = new JSpinner();
    JSpinner mines_spinner = new JSpinner();
    JLabel width_label = new JLabel();
    JLabel height_label = new JLabel();
    JLabel mines_label = new JLabel();
    JButton regen_button = new JButton();
    JButton[][] buttons;

    int field_rows;
    int field_columns;
    int field_mines;
    int[][] field_layout = null;


    Minesweeper(){

        // Frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(width, 200);
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
        width_spinner.setModel(new SpinnerNumberModel(5, 4, 30, 1));
        width_spinner.addChangeListener(e -> updateMineLimit());
        height_spinner.setModel(new SpinnerNumberModel(5, 3, 30, 1));
        height_spinner.addChangeListener(e -> updateMineLimit());
        mines_spinner.setModel(new SpinnerNumberModel(1, 1, 24, 1));
        updateMineLimit();
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
        spinner_panel.setBounds(0, 0, 2*(width/5), 150);
        spinner_panel.setLayout(new GridLayout(3, 2, 5, 10));

        spinner_panel.add(width_label);
        spinner_panel.add(width_spinner);
        spinner_panel.add(height_label);
        spinner_panel.add(height_spinner);
        spinner_panel.add(mines_label);
        spinner_panel.add(mines_spinner);

        // Selection Panel
        selection_panel.setBounds(0, 0, 2*(width/5), 50);
        selection_panel.setLayout(new BoxLayout(selection_panel, BoxLayout.Y_AXIS));
        selection_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        selection_panel.add(spinner_panel);
        selection_panel.add(regen_button);

        // Title Panel
        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, width-2*(width/5), 200);
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

    private int getMaxFittingFontSize(Graphics g, Font font, String string, int width, int height){
        int minSize = 0;
        int maxSize = 288;
        int curSize = font.getSize();

        while (maxSize - minSize > 2){
            FontMetrics fm = g.getFontMetrics(new Font(font.getName(), font.getStyle(), curSize));
            int fontWidth = fm.stringWidth(string);
            int fontHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();

            if ((fontWidth > width) || (fontHeight > height)){
                maxSize = curSize;
                curSize = (maxSize + minSize) / 2;
            }
            else{
                minSize = curSize;
                curSize = (minSize + maxSize) / 2;
            }
        }

        return curSize;
    }

    private void updateMineLimit() {
        int max = (int)height_spinner.getValue() * (int)width_spinner.getValue() - 9;
        int mines = (int)mines_spinner.getValue();

        if (mines <= max) {
            mines_spinner.setModel(new SpinnerNumberModel(mines, 1, max, 1));
        } else {
            mines_spinner.setModel(new SpinnerNumberModel(max, 1, max, 1));
        }
    }

    private void flagTile(int x, int y, boolean question_mark) {
        ImageIcon ico;
        if (question_mark)
            ico = qmark_icon_scaled;
        else
            ico = flag_icon_scaled;

        if (buttons[y][x].getText().equals("")) {
            if (buttons[y][x].getIcon() == null) {
                buttons[y][x].setIcon(ico);
            } else {
                buttons[y][x].setIcon(null);
            }
        }
    }

    private void removeActionAndMouseListeners(JButton button) {
        ActionListener[] action_listeners = button.getActionListeners();
        for (ActionListener listener : action_listeners)
            button.removeActionListener(listener);

        MouseListener[] mouse_listeners = button.getMouseListeners();
        for (MouseListener listener : mouse_listeners)
            button.removeMouseListener(listener);
    }

    private int[][] findSurroundingOffsets(int row, int column) {
        int[][] offsets = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};
        if (column == 0) {
            offsets[0] = null;
            offsets[3] = null;
            offsets[5] = null;
        } else if (column == field_columns-1) {
            offsets[2] = null;
            offsets[4] = null;
            offsets[7] = null;
        }
        if (row == 0) {
            offsets[0] = null;
            offsets[1] = null;
            offsets[2] = null;
        } else if (row == field_rows-1) {
            offsets[5] = null;
            offsets[6] = null;
            offsets[7] = null;
        }
        System.out.println("offsets: " + Arrays.deepToString(offsets));
        return offsets;
    }

    private void generateLayout(int cent_x, int cent_y) {
        field_layout = new int[field_rows][field_columns];

        // Calculate number of tiles are adjacent to the first uncovered tile
        int[][] selection_offsets = findSurroundingOffsets(cent_y, cent_x);
        int selection_protected = 9;
        for (int[] loc : selection_offsets)
            if (loc == null)
                selection_protected--;

        // Possible number of tiles
        int possible_tiles = field_rows * field_columns - selection_protected;
        int[] locations = new int[field_mines + selection_protected];

        // Mark off tiles surrounding first clicked
        int p = 0;
        for (int[] offset : selection_offsets) {
            if (offset != null) {
                locations[p] = (offset[1]+cent_y)*field_columns + (offset[0]+cent_x) + 1;
                p++;
            }
        }
        locations[p] = cent_y*field_columns + cent_x + 1; // First clicked tile

        // Randomly locate tiles
        for (int i = selection_protected; i < locations.length; i++) {
            //System.out.println("generating random position from 1-" + (possible_tiles-i+1));
            locations[i] = random.nextInt(possible_tiles-i) + 1;
        }
        System.out.println("mine locations: " + Arrays.toString(locations));

        // Arrange tiles on the 2d array, making sure not to place them on each other or tile adjacent to the first tile
        int[] locations_placed = locations.clone();

        for (int i = selection_protected; i < locations.length; i++) {
            int pos = locations[i];
            int[] placed_locations_sorted = Arrays.copyOf(locations_placed, i);
            Arrays.sort(placed_locations_sorted);

            // add 1 position for each position that exists yet and is before this
            for (int loc : placed_locations_sorted)
                if (loc <= pos)
                    pos++;

            locations_placed[i] = pos;

            // Place the value into that position on the 2d array
            int row = (pos-1) / field_columns;
            int column = (pos) - (row*field_columns) - 1;
            field_layout[row][column] = -9;

            // Circle around and add adjacent count numbers
            // efficiently avoid OOB indexes by making a list of tiles to check and removing outside ones
            int[][] inc_offsets = findSurroundingOffsets(row, column);
            System.out.println("row: " + row + " column: " + column);
            System.out.println();
            for (int[] offset : inc_offsets)
                if (offset != null)
                    field_layout[row+offset[1]][column+offset[0]]++;
        }
        System.out.println("placed mine locations: " + Arrays.toString(locations_placed));
    }

    private void generateGrid() {
        button_panel.removeAll();
        button_panel.validate();

        field_rows = (int)height_spinner.getValue();
        field_columns = (int)width_spinner.getValue();
        field_mines = (int)mines_spinner.getValue();

        button_panel.setLayout(new GridLayout(field_rows, field_columns));

        buttons = new JButton[field_rows][field_columns];

        for (int i=0; i < field_rows; i++) {
            for (int j=0; j < field_columns; j++) {
                buttons[i][j] = new JButton();
                //buttons[i][j].setBackground(new Color(150, 150, 150));
                buttons[i][j].setBackground(new Color(60, 140, 60));
                buttons[i][j].setMargin(new Insets(0, 0, 0, 0));
                button_panel.add(buttons[i][j]);
                buttons[i][j].setFocusable(false);

                buttons[i][j].addActionListener(generateTileActionListener(j, i));
                buttons[i][j].addMouseListener(generateTileMouseListener(j, i));
            }
        }

        frame.setSize(width, 200 + (int)(width * ((double)field_rows/field_columns)));

        frame.validate();
        frame.repaint();

        int button_tallness = buttons[0][0].getSize().height;
        flag_icon_scaled = new ImageIcon(flag_icon_full.getImage().getScaledInstance(button_tallness, button_tallness, Image.SCALE_DEFAULT));
        mine_icon_scaled = new ImageIcon(mine_icon_full.getImage().getScaledInstance(button_tallness, button_tallness, Image.SCALE_DEFAULT));
        qmark_icon_scaled = new ImageIcon(qmark_icon_full.getImage().getScaledInstance(button_tallness, button_tallness, Image.SCALE_DEFAULT));
        // Calculate font that fits in tiles
        double fontSize = 72.0 * button_tallness / Toolkit.getDefaultToolkit().getScreenResolution();
        tileFont = new Font("Comic Sans MS", Font.PLAIN, (int)fontSize);

        field_layout = null;
    }

    private ActionListener generateTileActionListener(int x, int y) {
        return e -> {
            JButton tile = buttons[y][x];
            // If button is covered and not flagged, then uncover
            if (tile.getText().equals("") && tile.getIcon() == null) {
                // Generate field layout if this is the first to be uncovered
                if (field_layout == null) {
                    generateLayout(x, y);
                }
                int content = field_layout[y][x];
                if (content >= 0) {
                    // If not a mine
                    // show number
                    tile.setBackground(tile_colors[0]);
                    tile.setFont(tileFont);
                    tile.setText(((Integer) content).toString());
                    tile.setForeground(tile_colors[content]);

                    // if no mines adjacent, then uncover adjacent tiles
                    if (content == 0) {
                        int[][] adjacent = findSurroundingOffsets(y, x);
                        for (int[] offset : adjacent)
                            if (offset != null)
                                buttons[y+offset[1]][x+offset[0]].doClick(0);
                    }
                } else {
                    // If is a mine
                    // go through each tile
                    for (int i = 0; i < field_rows; i++) {
                        for (int j = 0; j < field_columns; j++) {
                            JButton other_tile = buttons[i][j];

                            // reveal if mine
                            if (field_layout[i][j] < 0) {
                                other_tile.setBackground(tile_colors[9]);
                                other_tile.setIcon(mine_icon_scaled);
                            }
                            // disable by removing listeners (.setEnable(false) greys out colors, which looks bad)
                            removeActionAndMouseListeners(other_tile);
                        }
                    }
                }
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
