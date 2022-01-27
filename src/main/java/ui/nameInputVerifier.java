package ui;

import javax.swing.*;
import java.util.Objects;

public class nameInputVerifier extends InputVerifier {

    private final JTextField field;
    private final JLabel error;

    public nameInputVerifier(JTextField field, JLabel error) {
        this.field = field;
        this.error = error;
    }

    @Override
    public boolean shouldYieldFocus(JComponent source, JComponent target) {
        if (!verify(source)) {
            field.setText("NewPiano");
            error.setVisible(true);
        }
        else {
            error.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean verify(JComponent input) {
        return !field.getText().contains(" ") && !Objects.equals(field.getText(), "");
    }
}
