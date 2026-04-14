import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final JFrame frame = new JFrame();

        frame.setTitle("Sudoku");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board(450, 35);
        frame.add(board);
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
