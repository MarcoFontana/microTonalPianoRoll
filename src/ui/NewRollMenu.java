package ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NewRollMenu {
    private JPanel rootPanel;
    private JTextField name;
    private JSpinner topFreq;
    private JSpinner bottomFreq;
    private JSpinner nSegments;
    private JButton confirmButton;
    private JButton cancelButton;

    public NewRollMenu() {
        JFrame frame = new JFrame("New Piano Roll");

        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.setLocationRelativeTo(null);

        eventHandlersInitialize(frame);

        AllFrames.noDoubleClicks(new JButton[]{confirmButton, cancelButton});

        frame.setVisible(true);

    }

    private void eventHandlersInitialize(JFrame frame){

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                java.awt.EventQueue.invokeLater(StartupMenu::new);
                frame.dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            frame.setVisible(false);
            java.awt.EventQueue.invokeLater(StartupMenu::new);
            frame.dispose();
        });

        confirmButton.addActionListener(e -> {
            frame.setVisible(false);
            java.awt.EventQueue.invokeLater(() -> new PartitionEditor((Integer) nSegments.getValue(),40,
                    (Integer)topFreq.getValue(), (Integer)bottomFreq.getValue(), name.getText()));
            frame.dispose();
        });

    }
}
