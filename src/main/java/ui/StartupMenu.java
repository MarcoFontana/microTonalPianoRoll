package ui;
import Score.SaveScore;
import Score.Score;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class StartupMenu {

    private JPanel rootPanel;
    private JButton NewPiano;
    private JButton loadButton;
    private JButton exitButton;
    private Score score;

    public StartupMenu() {

        JFrame frame = new JFrame("Microtonal Piano Roll");

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

        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "XML", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(((Component) e.getSource()).getParent());

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getName();
                score = SaveScore.load(fileName);
                if (score != null){
                    frame.setVisible(false);
                    EventQueue.invokeLater(() -> new PartitionEditor(score, fileName.substring(0, fileName.lastIndexOf("."))));
                    frame.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(null, fileName + " is not a valid Piano roll file");
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "please select a .xml file");
            }
        });

    }

}
