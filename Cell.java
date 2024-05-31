import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Cell extends JTextField {

    private static final int SIZE = 2;
    private boolean isGiven; // To track if the cell is a default value

    public Cell(int value) {
        super(SIZE);
        setHorizontalAlignment(JTextField.CENTER);
        setFont(new Font("Arial", Font.BOLD, 20));
        setOpaque(true);
        setPreferredSize(new Dimension(35, 35));
        if (value != 0) {
            setText(String.valueOf(value));
            setEditable(false);
            isGiven = true;
        } else {
            setText("");
            setEditable(true);
            isGiven = false;
            addDocumentListener();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    String currentText = getText();

                    // Allow only digits (1-9) and limit to one digit
                    if (!Character.isDigit(c) || c == '0' || currentText.length() >= 1) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Allow backspace and delete keys
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                        setText("");
                    }
                }
            });
        }
        setBorderColor();
    }

    private void addDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }

            private void validateInput() {
                String text = getText();
                if (text.length() > 1 || !text.matches("[1-9]?")) {
                    SwingUtilities.invokeLater(() -> {
                        setText(text.length() > 0 ? text.substring(0, 1) : "");
                    });
                }
                setBorderColor();
            }
        });
    }

    private void setBorderColor() {
        if (isGiven) {
            setBorder(BorderFactory.createLineBorder(Color.BLUE));
            setBackground(new Color(173, 216, 230)); // Lighter blue color
        } else if (getText().equals("")) {
            setBorder(BorderFactory.createLineBorder(Color.RED));
            setBackground(new Color(255, 200, 200)); // Lighter red color
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GREEN));
            setBackground(new Color(200, 255, 200)); // Lighter green color
        }
    }

    public void setValue(int value) {
        if (value != 0) {
            setText(String.valueOf(value));
            setEditable(false);
            isGiven = true;
        } else {
            setText("");
            setEditable(true);
            isGiven = false;
        }
        setBorderColor();
    }

    public void updateColor() {
        setBorderColor();
    }
}
