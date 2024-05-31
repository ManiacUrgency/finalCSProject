import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.*;

public class SudokuGUI {

    private Cell[][] cells;
    private static final int SIZE = 9, GAP = 2;
    private final JFrame jFrame;
    private int[][] board;
    private int[][] initialBoard; // Store the initial state of the puzzle
    private Clip audioClip;
    private boolean isAudioPlaying = false;
    private boolean isAudioPaused = false;
    private JPanel sidebar;
    private String filePath = new File(SudokuGUI.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    private AtomicBoolean stopSolving; // Flag to stop solution generation

    public SudokuGUI(int[][] puzzle) {
        jFrame = new JFrame("SudokuGUI - Play");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setPreferredSize(new Dimension(800, 600)); // Increase width to accommodate sidebar
        this.board = new int[SIZE][SIZE];
        this.initialBoard = new int[SIZE][SIZE];
        copyBoard(puzzle, this.board);
        copyBoard(puzzle, this.initialBoard); // Save the initial state
        stopSolving = new AtomicBoolean(false); // Initialize the stop flag
        buildUi();
        jFrame.pack();
        jFrame.setVisible(true);
    }

    void buildUi() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        jFrame.add(containerPanel);

        // Main panel for the Sudoku board
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(SIZE, SIZE, GAP, GAP));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        mainPanel.setPreferredSize(new Dimension(600, 600)); // Ensure the board remains square
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        cells = new Cell[SIZE][SIZE];

        // Create cells and add to main panel
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(board[row][col]);
                cells[row][col] = cell;
                mainPanel.add(cell);

                // Set borders to create thick lines between 3x3 subgrids
                Border border = BorderFactory.createMatteBorder(
                    row % 3 == 0 ? 3 : 1, // Top
                    col % 3 == 0 ? 3 : 1, // Left
                    (row + 1) % 3 == 0 ? 3 : 1, // Bottom
                    (col + 1) % 3 == 0 ? 3 : 1, // Right
                    Color.BLACK
                );
                cell.setBorder(border);
            }
        }

        // Buttons
        Dimension buttonSize = new Dimension(100, 30);

        JButton playButton = new JButton("Play");
        playButton.setPreferredSize(buttonSize);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSolving.set(true); // Stop solving when switching to play mode
                resetBoard(); // Reset the board to the initial state
            }
        });

        JButton solutionButton = new JButton("Solution");
        solutionButton.setPreferredSize(buttonSize);
        solutionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSolving.set(false); // Allow solving to continue
                new Thread(() -> {
                    clearUserInputs(); // Clear user inputs before solving
                    int[][] solvedBoard = new int[SIZE][SIZE];
                    copyBoard(board, solvedBoard);

                    SudokuSolver solver = new SudokuSolver(SudokuGUI.this, stopSolving);
                    solver.solveSudoku(solvedBoard);
                }).start();
            }
        });

        JButton sidebarButton = new JButton("Sidebar");
        sidebarButton.setPreferredSize(new Dimension(80, 30));
        sidebarButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        sidebarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sidebar.isVisible()) {
                    sidebar.setVisible(false);
                    jFrame.setSize(new Dimension(jFrame.getWidth() - 200, jFrame.getHeight()));
                } else {
                    sidebar.setVisible(true);
                    jFrame.setSize(new Dimension(jFrame.getWidth() + 200, jFrame.getHeight()));
                }
            }
        });

        // Add buttons to the bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(playButton);
        bottomPanel.add(solutionButton);
        bottomPanel.add(sidebarButton);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Create sidebar panel with the same height as the main panel
        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, mainPanel.getHeight()));
        sidebar.setVisible(false); // Hide sidebar by default
        containerPanel.add(sidebar, BorderLayout.EAST);

        // Add components to the sidebar
        sidebar.add(new JLabel("Audio Control"), BorderLayout.NORTH);
        JButton playAudioButton = new JButton("Play Audio");
        playAudioButton.setPreferredSize(new Dimension(180, 30));
        playAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAudioPlaying) {
                    isAudioPlaying = true;
                    isAudioPaused = false;
                    playAudio();
                } else if (isAudioPaused) {
                    isAudioPaused = false;
                    resumeAudio();
                } else {
                    isAudioPaused = true;
                    pauseAudio();
                }
            }
        });

        JButton stopAudioButton = new JButton("Stop Audio");
        stopAudioButton.setPreferredSize(new Dimension(180, 30));
        stopAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAudioPlaying) {
                    isAudioPlaying = false;
                    isAudioPaused = false;
                    stopAudio();
                }
            }
        });

        JPanel audioControlPanel = new JPanel();
        audioControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        audioControlPanel.add(playAudioButton);
        audioControlPanel.add(stopAudioButton);
        sidebar.add(audioControlPanel, BorderLayout.SOUTH);

        loadAudioClip(filePath + "/CSA-FinalProject-SudokuSolver/finalAudio1.wav"); // Provide the correct path to the audio file
    }

    private void playAudio() {
        if (audioClip != null) {
            audioClip.start();
            audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio
        }
    }

    private void pauseAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    private void resumeAudio() {
        if (audioClip != null) {
            audioClip.start();
            audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio
        }
    }

    private void stopAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
        }
    }

    private void loadAudioClip(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void clearUserInputs() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (initialBoard[row][col] == 0) { // Only clear cells that were initially empty
                    board[row][col] = 0;
                }
            }
        }
        updateBoard(board);
    }

    private void resetBoard() {
        copyBoard(initialBoard, board); // Restore the initial state of the board
        updateBoard(board); // Update the GUI
    }

    private void copyBoard(int[][] source, int[][] destination) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                destination[row][col] = source[row][col];
            }
        }
    }

    public void updateBoard(int[][] board) {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    cells[row][col].setValue(board[row][col]);
                    cells[row][col].updateColor(); // Ensure color updates
                }
            }
        });
    }
}
