package ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
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
    private JLabel nameErrorMessage;
    private JLabel nOfNotesError;
    private JLabel freqError;

    public NewRollMenu() {
        JFrame frame = new JFrame("New Piano Roll");

        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        name.setInputVerifier(new nameInputVerifier(name,nameErrorMessage));
        nSegments.setModel(new SpinnerNumberModel(12, 1, 60, 1));
        topFreq.setModel(new SpinnerNumberModel(10000, 20, 20000, 1));
        bottomFreq.setModel(new SpinnerNumberModel(20, 20, 20000, 1));

        frame.pack();
        nameErrorMessage.setVisible(false);
        nOfNotesError.setVisible(false);
        freqError.setVisible(false);

        frame.setLocationRelativeTo(null);

        eventHandlersInitialize(frame);

        AllFrames.noDoubleClicks(new JButton[]{confirmButton, cancelButton});

        frame.setVisible(true);

    }

    private void eventHandlersInitialize(JFrame frame){

        ChangeListener spinnerListener = e -> {
            if (nOfNotesError.isVisible() || freqError.isVisible()) {
                nOfNotesError.setVisible(false);
                freqError.setVisible(false);
            }
        };

        topFreq.addChangeListener(spinnerListener);
        bottomFreq.addChangeListener(spinnerListener);
        nSegments.addChangeListener(spinnerListener);

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

            if ((int)topFreq.getValue() <= (int)bottomFreq.getValue() ||
                    ((int)topFreq.getValue() - (int)bottomFreq.getValue()) < (int)nSegments.getValue()) {
                nOfNotesError.setVisible(true);
                freqError.setVisible(true);
            }
            else {
                frame.setVisible(false);
                java.awt.EventQueue.invokeLater(() -> new PartitionEditor((Integer) nSegments.getValue(), 40,
                        (Integer) topFreq.getValue(), (Integer) bottomFreq.getValue(), name.getText()));
                frame.dispose();
            }

        });

    }

}
