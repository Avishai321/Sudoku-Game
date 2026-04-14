import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class Tile extends JPanel {
    public boolean hint;
    public int row;
    public int col;
    public int box;

    public static final String POS_DIGITS = "123456789";
    private final JTextField textField;

    // BORING STUFF HERE
    private final Color editableBackground = new Color(255, 255, 255);
    private final Color hintBackground = new Color(166, 166, 166);

    private final Color editableForeground = new Color(0, 102, 204);
    private final Color hintForeground = Color.BLACK;

    public static final Color errorColor = new Color(255, 190, 190);
    public static final Color successColor = new Color(190, 255, 190);

    public final Font hintFont = new Font("Arial", Font.BOLD, 32);
    public final Font editableFont = new Font("Tahoma", Font.PLAIN, 32);


    public Tile(boolean hint, int row, int col, TileChangeListener listener) {
        this.hint = hint;
        this.row = row;
        this.col = col;

        this.box = (row / 3) * 3 + (col / 3);

        setPreferredSize(new Dimension(Board.TILE_SIZE, Board.TILE_SIZE));
        setLayout(new GridLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        textField = new JTextField();
        textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        textField.setPreferredSize(new Dimension(Board.TILE_SIZE, Board.TILE_SIZE));
        textField.setHorizontalAlignment(JTextField.CENTER);
        setAsHint(this.hint);

        // limit the text field to only one digit and more features
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                if (text == null) {
                    fb.replace(offset, length, null, attrs);
                    return;
                }

                // 0 will empty the cell
                if (text.equals("0")) {
                    super.replace(fb, 0, fb.getDocument().getLength(), "", attrs);
                }

                // make sure it's only valid digits (1-9)
                if (!POS_DIGITS.contains(text)) return;

                // limit to only one digit
                if (fb.getDocument().getLength() + text.length() - length <= 1) {
                    super.replace(fb, offset, length, text, attrs);
                }

                // if there is already a digit in the tile - replace it with the last digit of the new text
                else {
                    char lastDigit = text.charAt(text.length() - 1);
                    super.replace(fb, 0, fb.getDocument().getLength(), String.valueOf(lastDigit), attrs);
                }
            }
        });

        //TODO it captures presses of the Enter key, find a useful thing to do with it
        // note: to capture the Tab key there is need to take it from the operating system or just listen to it
        // in order to steal it from the OS, use: textField.setFocusTraversalKeysEnabled(false);
        //     after that, it's possible to use KeyListener and compare to KeyEvent.VK_TAB
        // in order to just listen to the press, use: FocusListener, which has the methods focusGained() and focusLost()
        textField.addActionListener(e -> {
            System.out.println("Enter hitted");
        });

        // detect changes in the textField and send the signal to the listener
        this.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                listener.onTileUpdated(Tile.this);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                listener.onTileUpdated(Tile.this);
            }

            // it seems that it captures things like font changes, not anything useful
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        add(textField);
    }

    public void setAsHint(boolean hint) {
        this.hint = hint;
        if (this.hint) {
            textField.setEditable(false);
            textField.setFont(hintFont);
            textField.setForeground(hintForeground);
            textField.setBackground(hintBackground);
            textField.setFocusable(false);
        }
        else {
            textField.setEditable(true);
            textField.setFont(editableFont);
            textField.setForeground(editableForeground);
            textField.setBackground(editableBackground);
            textField.setFocusable(true);
        }
    }

    public void setHighlight(Color color) {
        // color null will restore the default color
        if (color == null) this.textField.setBackground(!this.hint ? editableBackground : hintBackground);

        else this.textField.setBackground(color);
    }

    public void setValue(int num) {
        this.textField.setText(String.valueOf(num));
    }

    public boolean isHint() {return hint;}

    public boolean hasDigit() {
        return !this.textField.getText().isEmpty();
    }

    public int getValue() {
        if (!hasDigit()) return 0;
        return Integer.parseInt(this.textField.getText());
    }

}