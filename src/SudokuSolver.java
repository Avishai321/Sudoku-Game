import java.util.Arrays;
import java.util.Random;

public class SudokuSolver implements TileChangeListener {
    private Tile[][] board;

    private final boolean[] rowsCollisions = new boolean[9];
    private final boolean[] colsCollisions = new boolean[9];
    private final boolean[] boxesCollisions = new boolean[9];

    private final int hints;
    private final int[] randomHints = new int[81];

    private int possibleSolutions = 0;

    // GARBAGE
    private final Random random = new Random();

    public SudokuSolver(int hints) {
        // WARNING lower number of hints will make the creation of the board slower
        // this is because the solver ensures there is only one possible solution.
        // 30 hints is a good balance, should take no time, but it's a bit easy to solve; about 6 minutes
        // a good computer can take it easily to 25 hints, but it will take a few seconds, sometimes even more.
        this.hints = hints;

        initializeBoard();
        fillBoard(0, 0);
        removeHints();
        resetTries();
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

    // recursive method that fills the board with hints while keeping it valid
    public boolean fillBoard(int row, int col) {
        // if row got to 9, it means the board has been filled and valid, it's a win
        if (row == 9) return true;

        Tile tile = board[row][col];

        // create an array with random values to try
        int[] digits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(digits);

        for (int i = 0; i < 9; i++) {
            int value = digits[i];

            if (isValidMove(row, col, value)) {
                // the move is valid, assign the digit to the tile and send the next tile
                tile.setValue(value);

                // calculate the next tile's row and col
                int nextRow = (col == 8) ? row + 1 : row;
                int nextCol = (col == 8) ? 0 : col + 1;

                // send the next tile and capture its answer
                boolean hasSolution = fillBoard(nextRow, nextCol);

                // if the next tile returns a positive answer, we can send it back up
                if (hasSolution) return true;
            }

            tile.setValue(0);
        }

        // can't place ANY digit without currpting the board, return a negative answer
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

    // check if a move is safe BEFORE changing the tile's value
    public boolean isValidMove(int row, int col, int value) {
        // check the tile's row and col
        for (int i = 0; i < 9; i++) {
            if (board[row][i].hasDigit() && board[row][i].getValue() == value) return false; // row
            if (board[i][col].hasDigit() && board[i][col].getValue() == value) return false; // col
        }

        // check box
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c].hasDigit() && board[r][c].getValue() == value) return false;
            }
        }

        return true;
    }

    // another recursive method, it updates the possibleSolutions variable
    public void updateSolutions(int row, int col, int solutionsToFind) {
        // stop early if enough solutions already found
        if (possibleSolutions > solutionsToFind) return;

        // if row got to 9, the board finished and valid, then it's a possibleSolution
        if (row == 9) {
            possibleSolutions++;
            return;
        }

        // calculate next tile's row and col
        int nextRow = (col == 8) ? row + 1 : row;
        int nextCol = (col == 8) ? 0 : col + 1;

        Tile currentTile = board[row][col];

        if (currentTile.isHint()) { // skip the tile if it's a hint
            updateSolutions(nextRow, nextCol, solutionsToFind);
            return;
        }

        // try digits 1-9
        for (int val = 1; val <= 9; val++) {
            if (isValidMove(row, col, val)) {
                // it's a valid move, use it and move forward to the next tile
                currentTile.setValue(val);

                updateSolutions(nextRow, nextCol, solutionsToFind);

                currentTile.setValue(0); // reset the value back to leave the board clean
            }
        }
    }

    // removes random hints from the board while making sure there is only one solution
    public void removeHints() {
        for (int i = 0; i < 81; i++) {randomHints[i] = i;}
        shuffleArray(randomHints);

        int currentHints = 81;
        int randomHintIndex = 0;

        while (currentHints > hints) {
            // in case there are no more random indexes to remove AND keep the board valid
            if (randomHintIndex >= randomHints.length) break;

            int index = randomHints[randomHintIndex++]; // assuming randomHints is shuffeled
            int row = index / 9;
            int col = index % 9;

            Tile tile = board[row][col];
            int value = tile.getValue();

            tile.setAsHint(false);
            tile.setValue(0); // removes it's value

            possibleSolutions = 0;
            updateSolutions(0, 0, 1);

            if (possibleSolutions == 1) {
                currentHints--;
            }
            else {
                // revert changes
                tile.setAsHint(true);
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

    // updates collision for one tile's row, col and box
    public void updateCollisions(Tile tile) {
        int val = tile.getValue();

        // check row
        for (int c = 0; c < 9; c++) {
            Tile other = board[tile.row][c];
            if (other.hasDigit() && other != tile && other.getValue() == val) {
                rowsCollisions[tile.row] = true;
                break; // there is no need to check for more collisions in the same row
            }
        }

        // check column
        for (int r = 0; r < 9; r++) {
            Tile other = board[r][tile.col];
            if (other.hasDigit() && other != tile && other.getValue() == val) {
                colsCollisions[tile.col] = true;
                break;
            }
        }

        // check box
        int startRow = (tile.row / 3) * 3;
        int startCol = (tile.col / 3) * 3;

        boxCheck: for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                Tile other = board[r][c];
                if (other.hasDigit() && other != tile && other.getValue() == val) {
                    boxesCollisions[tile.box] = true;
                    break boxCheck;
                }
            }
        }
    }

    public void validateBoard() {
        // reset collisions
        Arrays.fill(rowsCollisions, false); // seems faster than rowsCollisions = new boolean[9]
        Arrays.fill(colsCollisions, false);
        Arrays.fill(boxesCollisions, false);


        int emptyTiles = 0;

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile == null) continue;

                //TODO think about that:
                //if (!tile.isEditable()) continue;

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

                if (solved) tile.setHighlight(Tile.successColor);

                else {
                    boolean hasError = rowsCollisions[r] || colsCollisions[c] || boxesCollisions[tile.box];
                    tile.setHighlight((hasError) ? Tile.errorColor : null);
                }
            }
        }
    }

    public void resetTries() {
        if (board == null || board.length < 1) return;

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile == null) continue;
                tile.moves = 0;
            }
        }
    }

    public int getTotalMoves() {
        if (board == null || board.length < 1) return 0;

        int totalMoves = 0;

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile == null) continue;
                totalMoves += tile.moves;
            }
        }

        return totalMoves;
    }

    @Override
    public void onTileUpdated(Tile tile) {
        validateBoard();
    }

    public Tile[][] getBoard() {
        return board;
    }
}
