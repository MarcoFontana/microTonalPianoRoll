package ui;

import javax.swing.*;

public class AllFrames extends JFrame {

    public AllFrames(String title) {
        super(title);
        setLocationRelativeTo(null);
    }
    
    public static void noDoubleClicks(JButton[] buttons) {
        for (JButton button :
                buttons) {
            button.setMultiClickThreshhold(500);
        }
    }

}
