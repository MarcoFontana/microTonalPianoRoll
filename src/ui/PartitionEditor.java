package ui;

import Score.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PartitionEditor {

    private JPanel rootPanel;
    private JComboBox<String> NoteDurationMenu;
    private JScrollPane PianoRollExplorer;
    private JPanel PianoRoll;
    private JSlider BpmPicker;
    private final int nRows;
    private final int nCols;
    private final Score currScore;
    private int currDuration;

    public PartitionEditor(int nRows, int nCols) {
        JFrame frame=new JFrame("PartitionEditor");
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.nRows = nRows;
        this.nCols = nCols;
        currDuration = 4;

        currScore = new Score(1000, 100, nRows, BpmPicker.getValue());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)screenSize.getWidth() / 2, (int)screenSize.getHeight() / 2);
        rootPanel.setPreferredSize(windowSize);
        PianoRollExplorer.setMaximumSize(windowSize);

        //initializeImges();

        createGrid(PianoRoll);

        eventHandlersInitialize(frame);
        frame.pack();
        //AllFrames.noDoubleClicks([]);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

    }

    private void eventHandlersInitialize(JFrame frame) {

        PianoRollMouseListener pianoListener = new PianoRollMouseListener(nRows, nCols, currScore, currDuration);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                java.awt.EventQueue.invokeLater(StartupMenu::new);
                frame.dispose();
            }
        });

        BpmPicker.addChangeListener(e -> {
            JSlider source = (JSlider)e.getSource();
            if(!source.getValueIsAdjusting()) {
                currScore.setBpm(source.getValue());
            }
        });

        NoteDurationMenu.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                currDuration = 1;
                pianoListener.setCurrDuration(currDuration);
            }
        });

        PianoRoll.addMouseListener(pianoListener);

    }

    /*private void initializeImges() {

        noteImg = null;
        try {
            noteImg = ImageIO.read(new File("Img/Notes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void createGrid(JPanel panel) {

        int segments = 4;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double noteWidth = screenSize.getWidth() / 20;

        Dimension rollLength = new Dimension((int)(nCols * noteWidth), (int)(screenSize.getHeight() / 2.5));
        //Dimension keyDim = new Dimension((int)screenSize.getWidth() / 20, (int)screenSize.getHeight() / 35);

        panel.setPreferredSize(rollLength);

        GridBagConstraints cellConstraints;
        cellConstraints = new GridBagConstraints();

        cellConstraints.weightx = 4;
        cellConstraints.weighty = 1;
        cellConstraints.fill =  GridBagConstraints.BOTH;

        //JLabel[][] gridSegments = new JLabel[nRows][nCols];

        cellConstraints.gridx = 0;
        for (int row = 1; row <= nRows; row++) {
            JLabel seg = new JLabel();
            seg.setBorder(BorderFactory.createLineBorder(Color.red));
            seg.setOpaque(true);
            cellConstraints.gridy = row;
            panel.add(seg,cellConstraints);
        }
        cellConstraints.gridy = 0;
        for (int col = 0; col < nCols*segments; col++) {

            JLabel seg = new JLabel();
            seg.setOpaque(true);
            cellConstraints.gridx = col;

            panel.add(seg, cellConstraints);

        }

        /*for (int row = 0; row < gridSegments.length; row++) {
            cellConstraints.gridy = row;
            for (int col = 0; col < gridSegments[row].length; col++){
                JLabel seg = new JLabel();
                seg.setBorder(BorderFactory.createLineBorder(Color.black));
                //seg.setPreferredSize(keyDim);
                seg.setOpaque(true);
                cellConstraints.gridx = col;

                panel.add(seg,cellConstraints);
            }
        }*/

    }

}