import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private static int moves = 0;
    private static final JLabel movesLabel = new JLabel("Moves: 0");

    private static final JButton autoSolveButton = new JButton("Auto Solve");

    public ControlPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridLayout(1, 2));
        //TODO implemant setBackground() with a soft color

        add(movesLabel);

        //TODO find a way to make SudokuSolver auto solve the sudoku with a small delay between actions
        add(autoSolveButton);
    }

    private static void updateLabel() {
        movesLabel.setText("Moves " + moves);
    }

    public static void increaseMoves() {
        moves++;
        updateLabel();
    }

    public static void setMoves(int x) {
        moves = x;
        updateLabel();
    }

    public static void resetMoves() {
        moves = 0;
        updateLabel();
    }
}
