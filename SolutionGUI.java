import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class SolutionGUI {

    private Cell[][] cells;
    private static final int SIZE = 9, GAP = 2;
    private final JFrame jFrame;
    private int[][] board;

    public SolutionGUI(int[][] solution) {
        jFrame = new JFrame("SudokuGUI - Solution");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        this.board = solution;
        buildUi();
        jFrame.pack();
        jFrame.setVisible(true);
    }

    void buildUi() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(SIZE, SIZE, GAP, GAP));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        jFrame.add(mainPanel, BorderLayout.CENTER);

        cells = new Cell[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(board[row][col], true); // Use the solution mode
                cells[row][col] = cell;
                mainPanel.add(cell);

                // Set borders to create thick lines between 3x3 subgrids
                Border border = BorderFactory.createMatteBorder(
                    row % 3 == 0 ? 3 : 1, // Top
                    col % 3 == 0 ? 3 : 1, // Left
                    (row + 1) % 3 == 0 ? 3 : 1, // Bottom
                    (col + 1) % 3 == 0 ? 3 : 1, // Right
                    java.awt.Color.BLACK
                );
                cell.setBorder(border);
            }
        }
    }

    public void updateBoard(int[][] solution) {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    cells[row][col].setValue(solution[row][col]);
                    cells[row][col].updateColor(); // Ensure color updates
                }
            }
        });
    }
}
