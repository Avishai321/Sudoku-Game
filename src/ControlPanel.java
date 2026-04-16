import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private static int moves = 0;
    private static final JLabel movesLabel = new JLabel("Moves: 0");

    private final int AUTO_SOLVE_DELAY = 1;
    private static final JButton autoSolveButton = new JButton("Auto Solve");

    private static final JButton resetBoardButton = new JButton("Reset Board");

    //TODO add a reset button, and a reset method in SudokuSolver to activate, it should reset all tiles to 0

    public ControlPanel(int width, int height, SudokuSolver solver) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridLayout(1, 2));

        add(movesLabel);

        autoSolveButton.setFocusable(false);
        autoSolveButton.addActionListener(e -> {
            //TODO turn this button to a cancel button while the solver is working
            // currently the solver makes sure to diable and enable the button

            Thread autoSolveThread = new Thread(() -> {
                solver.autoSolve(AUTO_SOLVE_DELAY, autoSolveButton);
            });
            autoSolveThread.start();
        });

        add(autoSolveButton);

        resetBoardButton.setFocusable(false);
        resetBoardButton.addActionListener(e -> {
            solver.resetBoard();
        });

        add(resetBoardButton);
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
