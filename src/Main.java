import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        //TODO fix the border issue (it will curropt when changing the window size)
        final int BOARD_SIZE = 450; // RECOMMENDED 450 (other values may curropt boxes borders)
        final int HINTS = 35;
        final int CONTROL_HEIGHT = 25;

        final JFrame frame = new JFrame();

        frame.setTitle("Sudoku");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        ControlPanel controlPanel = new ControlPanel(BOARD_SIZE, CONTROL_HEIGHT);
        frame.add(controlPanel, BorderLayout.NORTH);

        Board board = new Board(BOARD_SIZE, HINTS);
        frame.add(board, BorderLayout.CENTER);

        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
