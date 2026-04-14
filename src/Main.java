import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //TODO fix the border issue (it will curropt when changing the window size)
        final int BOARD_SIZE = 450; // RECOMMENDED 450 (other values may curropt boxes borders)
        final int HINTS = 35;

        final JFrame frame = new JFrame();

        frame.setTitle("Sudoku");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board(BOARD_SIZE, HINTS);
        frame.add(board);
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
