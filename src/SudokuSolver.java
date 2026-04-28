import javax.swing.*;
import java.util.Arrays;
import java.util.Random;

public class SudokuSolver implements TileChangeListener {
    //TODO modify this class to use logical data only instead of creating and changing ui which might take much longer
    private Tile[][] board;

    private final boolean[] rowsCollisions = new boolean[9];
    private final boolean[] colsCollisions = new boolean[9];
    private final boolean[] boxesCollisions = new boolean[9];

    private final int hints;
    private final int[] randomHints = new int[81];
    private int possibleSolutions = 0;

    private Runnable onMoveMadeCallback;
    private Runnable onResetCallback;
    private Runnable onAutoSolveDoneCallback;

    private final Random random = new Random();

    public SudokuSolver(int hints) {
        this.hints = hints;

        initializeBoard();
        fillBoard(0, 0);
        removeHints();
        resetStatistics();
    }

    public void initializeBoard() {
        board = new Tile[9][9];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Tile tile = new Tile(true, i, j, this);
                board[i][j] = tile;
            }
        }
    }

    public boolean fillBoard(int row, int col) {
        if (row == 9) return true;

        Tile tile = board[row][col];

        int[] digits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(digits);

        for (int i = 0; i < 9; i++) {
            int value = digits[i];

            if (isValidMove(row, col, value)) {
                tile.setValue(value);

                int nextRow = (col == 8) ? row + 1 : row;
                int nextCol = (col == 8) ? 0 : col + 1;

                boolean hasSolution = fillBoard(nextRow, nextCol);

                if (hasSolution) return true;
            }

            tile.setValue(0);
        }

        return false;
    }

    public void shuffleArray(int[] arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int randomIndex = random.nextInt(i + 1);

            int temp = arr[i];
            arr[i] = arr[randomIndex];
            arr[randomIndex] = temp;
        }
    }

    public boolean isValidMove(int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            if (i != col && board[row][i].hasDigit() && board[row][i].getValue() == value) return false; // row
            if (i != row && board[i][col].hasDigit() && board[i][col].getValue() == value) return false; // col
        }

        // box
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (r != row && c != col && board[r][c].hasDigit() && board[r][c].getValue() == value) return false;
            }
        }

        return true;
    }

    // get the next editable tile by the current one (null to start from 0,0), returns null if no nextEditable found
    //TODO build simple array with editables indexes when removing hints instead of checking it this way
    public Tile getNextEditable(Tile currentTile) {
        int row = (currentTile == null) ?
                0 : (currentTile.getCol() == 8) ? currentTile.getRow() + 1 : currentTile.getRow();
        int col = (currentTile == null || currentTile.getCol() == 8) ? 0 :  currentTile.getCol() + 1;

        for (int r = row; r < board.length; r++) {
            for (int c = col; c < board[0].length; c++) {
                Tile nextTile = board[r][c];
                if (!nextTile.isHint()) {
                    return nextTile;
                }
                col = 0; // making sure the next row starts from col 0
            }
        }
        return null;
    }

    public void resetBoard() {
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (!tile.isHint()) tile.setValue(0);
            }
        }
        resetStatistics();
    }

    public void setTilesFocusable(boolean focusable) {
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (!tile.isHint()) tile.setTextFocusable(focusable);
            }
        }
    }

    public boolean autoSolveHelper(Tile tile, int delay) {
        // THIS METHOD RUNS ON A DIFFERENT THREAD, MAKE SURE YOUT KEEP IT SAFE (with setValueSafe())
        if (tile == null) return true;

        for (int val = 1; val <= 9; val++) {
            // setValueSafe() is a bit slow for it waits until SwingUtilities will set the value to the tile
            tile.setValueSafe(val);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (isValidBoard()) {
                boolean hasSolution = autoSolveHelper(getNextEditable(tile), delay);
                if (hasSolution) return true;
            }

            tile.setValueSafe(0);
        }

        return false;
    }

    public void autoSolve(int delay) {
        resetBoard();
        setTilesFocusable(false);

        new Thread(() -> {
            autoSolveHelper(getNextEditable(null), delay);

            SwingUtilities.invokeLater(() -> {
                setTilesFocusable(true);
                if (onAutoSolveDoneCallback != null) onAutoSolveDoneCallback.run();
            });

        }).start();
    }

    public void updateSolutions(int row, int col, int maxSolutionsToFind) {
        if (possibleSolutions >= maxSolutionsToFind) return;

        if (row == 9) {
            possibleSolutions++;
            return;
        }

        int nextRow = (col == 8) ? row + 1 : row;
        int nextCol = (col == 8) ? 0 : col + 1;

        Tile currentTile = board[row][col];

        if (currentTile.isHint()) {
            updateSolutions(nextRow, nextCol, maxSolutionsToFind);
            return;
        }

        for (int val = 1; val <= 9; val++) {
            if (isValidMove(row, col, val)) {
                currentTile.setValue(val);
                updateSolutions(nextRow, nextCol, maxSolutionsToFind);
                currentTile.setValue(0);
            }
        }
    }

    public void removeHints() {
        for (int i = 0; i < 81; i++) {randomHints[i] = i;}
        shuffleArray(randomHints);

        int currentHints = 81;
        int randomHintIndex = 0;

        while (currentHints > hints) {
            if (randomHintIndex >= randomHints.length) break;

            int index = randomHints[randomHintIndex++];
            int row = index / 9;
            int col = index % 9;

            Tile tile = board[row][col];
            int value = tile.getValue();

            tile.setHint(false);
            tile.setValue(0);

            possibleSolutions = 0;
            updateSolutions(0, 0, 2);

            if (possibleSolutions == 1) {
                currentHints--;
            }
            else {
                // revert changes
                tile.setHint(true);
                tile.setValue(value);
            }
        }
    }

    public boolean isValidBoard() {
        for (int i = 0; i < 9; i++) {
            if (rowsCollisions[i]) return false;
            if (colsCollisions[i]) return false;
            if (boxesCollisions[i]) return false;
        }
        return true;
    }

    public void updateCollisions(Tile tile) {
        int val = tile.getValue();

        // check row
        for (int c = 0; c < 9; c++) {
            Tile other = board[tile.getRow()][c];
            if (other.hasDigit() && other != tile && other.getValue() == val) {
                rowsCollisions[tile.getRow()] = true;
                break; // there is no need to check for more collisions in the same row
            }
        }

        // check column
        for (int r = 0; r < 9; r++) {
            Tile other = board[r][tile.getCol()];
            if (other.hasDigit() && other != tile && other.getValue() == val) {
                colsCollisions[tile.getCol()] = true;
                break;
            }
        }

        // check box
        int startRow = (tile.getRow() / 3) * 3;
        int startCol = (tile.getCol() / 3) * 3;

        boxCheck: for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                Tile other = board[r][c];
                if (other.hasDigit() && other != tile && other.getValue() == val) {
                    boxesCollisions[tile.getBox()] = true;
                    break boxCheck;
                }
            }
        }
    }

    public void validateBoard() {
        // reset collisions
        Arrays.fill(rowsCollisions, false);
        Arrays.fill(colsCollisions, false);
        Arrays.fill(boxesCollisions, false);


        int emptyTiles = 0;

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile == null) continue;

                if (tile.hasDigit()) updateCollisions(tile);
                else emptyTiles++;
            }
        }

        boolean solved =  (emptyTiles == 0 && isValidBoard());

        // highlight tiles
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = board[r][c];
                if (tile == null) continue;

                if (solved) tile.highlight(Tile.successColor);

                else {
                    boolean hasError = rowsCollisions[r] || colsCollisions[c] || boxesCollisions[tile.getBox()];
                    tile.highlight((hasError) ? Tile.errorColor : null);
                }
            }
        }
    }

    public void resetStatistics() {
        if (board == null || board.length < 1) return;

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile == null) continue;
                tile.setMoves(0);
            }
        }

        if (onResetCallback != null) {
            onResetCallback.run();
        }
    }

    @Override
    public void onTileUpdated(Tile tile, boolean countAsMove) {
        validateBoard();

        if (countAsMove) {
            if (onMoveMadeCallback != null) onMoveMadeCallback.run();
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setOnMoveMadeCallback(Runnable onMoveMadeCallback) {
        this.onMoveMadeCallback = onMoveMadeCallback;
    }
    public void setOnResetCallback(Runnable onResetCallback) {
        this.onResetCallback = onResetCallback;
    }
    public void setOnAutoSolveDoneCallback(Runnable onAutoSolveDoneCallback) {
        this.onAutoSolveDoneCallback = onAutoSolveDoneCallback;
    }
}
