import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        final int BOARD_SIZE = 450;
        final int HINTS = 35;
        final int CONTROL_HEIGHT = 25;

        final int AUTO_SOLVE_DELAY = 1;

        final JFrame frame = new JFrame();

        frame.setTitle("Sudoku");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        SudokuSolver sudokuSolver = new SudokuSolver(HINTS);

        ControlPanel controlPanel = new ControlPanel(BOARD_SIZE, CONTROL_HEIGHT);
        frame.add(controlPanel, BorderLayout.NORTH);


        sudokuSolver.setOnMoveMadeCallback(controlPanel::increaseMoves);
        sudokuSolver.setOnResetCallback(controlPanel::resetMoves);
        sudokuSolver.setOnAutoSolveDone(controlPanel::enableButtons);

        controlPanel.setAutoSolveCallable(() -> sudokuSolver.autoSolve(AUTO_SOLVE_DELAY));
        controlPanel.setResetBoardCallable(sudokuSolver::resetBoard);

        Board board = new Board(BOARD_SIZE, sudokuSolver);
        frame.add(board, BorderLayout.CENTER);

        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
