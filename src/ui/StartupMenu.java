package ui;
import javax.swing.*;

public class StartupMenu {

    private JPanel rootPanel;
    private JButton NewPiano;
    private JButton loadButton;
    private JButton exitButton;

    public StartupMenu() {

        JFrame frame = new JFrame("StartupMenu");

        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();

        frame.setLocationRelativeTo(null);

        eventHandlersInitialize(frame);

        AllFrames.noDoubleClicks(new JButton[]{NewPiano, loadButton, exitButton});

        frame.setVisible(true);

    }

    private void eventHandlersInitialize(JFrame frame) {

        NewPiano.addActionListener(e -> {
            frame.setVisible(false);
            java.awt.EventQueue.invokeLater(NewRollMenu::new);
            frame.dispose();

        });

        exitButton.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);
        });

    }

}
