import java.util.concurrent.atomic.AtomicBoolean;

public class SudokuSolver {

    private static final int SIZE = 9;
    private SudokuGUI gui;
    private AtomicBoolean stopSolving;

    public SudokuSolver(SudokuGUI gui, AtomicBoolean stopSolving) {
        this.gui = gui;
        this.stopSolving = stopSolving;
    }

    public boolean solveSudoku(int[][] board) {
        return solve(board);
    }

    private boolean solve(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (stopSolving.get()) {
                    return false; // Stop solving if the flag is set
                }
                if (board[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            gui.updateBoard(board); // Update the GUI
                            try {
                                Thread.sleep(100); // Pause for 100 milliseconds to visualize the steps
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            if (solve(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                            gui.updateBoard(board); // Update the GUI after backtracking
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num || 
                board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private void copyBoard(int[][] source, int[][] destination) {
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(source[row], 0, destination[row], 0, SIZE);
        }
    }
}
