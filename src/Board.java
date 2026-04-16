import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {
    private final int BOARD_SIZE;
    public static int TILE_SIZE;

    // BORING STUFF HERE
    private final Color boxLineColor = new Color(50, 50, 50);
    private final BasicStroke boxStroke = new BasicStroke(1.8f);

    public Board(int boardSize, SudokuSolver solver) {
        BOARD_SIZE = boardSize;
        TILE_SIZE = BOARD_SIZE / 9;

        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setLayout(new GridLayout(9, 9, 2, 2));
        setBackground(new Color(214, 214, 214));

        // add all tiles to the panel's grid
        Tile[][] board = solver.getBoard();
        for (Tile[] tiles : board) {
            for (int c = 0; c < board[0].length; c++) {
                add(tiles[c]);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // painting boxes borders
        for (int i = 0; i < 4; i++) {
            g2d.setColor(boxLineColor);
            g2d.setStroke(boxStroke); // border width, available only with g2d

            g2d.drawLine(0, i * TILE_SIZE * 3, BOARD_SIZE, i * TILE_SIZE * 3); // row
            g2d.drawLine(i * TILE_SIZE * 3, 0, i * TILE_SIZE * 3, BOARD_SIZE); // col
        }
    }
}
