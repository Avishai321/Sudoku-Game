import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private final SudokuSolver solver;

    private int moves = 0;
    private final JLabel movesLabel = new JLabel("Moves: 0");

    private final int AUTO_SOLVE_DELAY = 1;

    public ControlPanel(int width, int height, SudokuSolver solver) {
        setPreferredSize(new Dimension(width, height));
        this.solver = solver;

        setLayout(new GridLayout(1, 2));

        add(movesLabel);

        JButton autoSolveButton = getAutoSolveButton();
        add(autoSolveButton);

        JButton resetBoardButton = getResetBoardButton();
        add(resetBoardButton);
    }

    private JButton getAutoSolveButton() {
        JButton autoSolveButton = new JButton("Auto Solve");
        autoSolveButton.setFocusable(false);
        autoSolveButton.addActionListener(e -> {
            //TODO turn this button to a cancel button while the solver is working
            // currently the solver makes sure to diable and enable the button

            //TODO disable resetBoard button as well when auto solving

            Thread autoSolveThread = new Thread(() -> {
                solver.autoSolve(AUTO_SOLVE_DELAY, autoSolveButton);
            });
            autoSolveThread.start();
        });

        return autoSolveButton;
    }

    private JButton getResetBoardButton() {
        JButton resetBoardButton = new JButton("Reset Board");

        resetBoardButton.setFocusable(false);
        resetBoardButton.addActionListener(e -> {
            solver.resetBoard();
        });

        return resetBoardButton;
    }

    private void updateLabel() {
        movesLabel.setText("Moves " + moves);
    }

    public void increaseMoves() {
        moves++;
        updateLabel();
    }

    public void resetMoves() {
        moves = 0;
        updateLabel();
    }
}
