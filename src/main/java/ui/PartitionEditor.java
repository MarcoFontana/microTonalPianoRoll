package ui;

import Score.*;
import audio.GeneralSynth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.TreeMap;

public class PartitionEditor {

    private JPanel rootPanel;
    private JComboBox<String> NoteDurationMenu;
    private JScrollPane PianoRollExplorer;
    private final JLayeredPane PianoRoll;
    private JSlider BpmPicker;
    private JLabel BpmValue;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JComboBox<String> instrumentBox;
    private JPanel bpmPanel;
    private JPanel playPanel;
    private JButton saveButton;
    private final int nRows;
    private final int nCols;
    private final Score currScore;
    private int currDuration;
    private GeneralSynth player;
    private boolean isPaused;
    private final int minFreq;
    private final int maxFreq;
    private final String name;

    public  PartitionEditor(Score score, String name) {

        JFrame frame=new JFrame(name);
        this.name = name;
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        isPaused = false;
        nRows = score.getnSteps();
        nCols = 40;

        NoteDurationMenu.setSelectedItem("QUARTER");
        currDuration = (int)Math.pow(2 , NoteDurationMenu.getSelectedIndex());
        BpmPicker.setValue(score.getBpm());
        BpmValue.setText(String.valueOf(BpmPicker.getValue()));

        minFreq = score.getBottomFreq();
        maxFreq = score.getTopFreq();
        currScore = score;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)(screenSize.getWidth() / 2), (int)(screenSize.getHeight() / 2));
        Dimension rootPanelSize = new Dimension((int)(screenSize.getWidth() / 1.5), (int)(screenSize.getHeight() / 1.5));
        rootPanel.setPreferredSize(rootPanelSize);
        PianoRollExplorer.setMaximumSize(windowSize);

        PianoRoll = new JLayeredPane();
        PianoRoll.setLayout(new GridBagLayout());
        PianoRollExplorer.setViewportView(PianoRoll);
        createGrid(PianoRoll);

        AllFrames.noDoubleClicks(new JButton[]{playButton, pauseButton, stopButton, saveButton});


        eventHandlersInitialize(frame);
        fillEditorFromScore();
        setKeyBinds();
        frame.pack();
        //AllFrames.noDoubleClicks([]);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

    }

    public PartitionEditor(int nRows, int nCols, int maxFreq, int minFreq, String name) {
        JFrame frame=new JFrame(name);
        this.name = name;
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        isPaused = false;
        this.nRows = nRows;
        this.nCols = nCols;
        NoteDurationMenu.setSelectedItem("QUARTER");
        currDuration = (int)Math.pow(2 , NoteDurationMenu.getSelectedIndex());
        BpmValue.setText(String.valueOf(BpmPicker.getValue()));

        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        currScore = new Score(maxFreq, minFreq, nRows, BpmPicker.getValue());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)(screenSize.getWidth() / 2), (int)(screenSize.getHeight() / 2));
        Dimension rootPanelSize = new Dimension((int)(screenSize.getWidth() / 1.5), (int)(screenSize.getHeight() / 1.5));
        rootPanel.setPreferredSize(rootPanelSize);
        PianoRollExplorer.setMaximumSize(windowSize);

        //initializeImges();
        //playButton.setIcon(new ImageIcon("img/play-button.png"));

        PianoRoll = new JLayeredPane();
        PianoRoll.setLayout(new GridBagLayout());
        PianoRollExplorer.setViewportView(PianoRoll);
        createGrid(PianoRoll);

        AllFrames.noDoubleClicks(new JButton[]{playButton, pauseButton, stopButton, saveButton});


        eventHandlersInitialize(frame);
        setKeyBinds();
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
            BpmValue.setText(String.valueOf(source.getValue()));
        });

        NoteDurationMenu.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                currDuration = (int)Math.pow(2 , NoteDurationMenu.getSelectedIndex());
                pianoListener.setCurrDuration((int)Math.pow(2 , NoteDurationMenu.getSelectedIndex()));
            }
        });

        PianoRoll.addMouseListener(pianoListener);

        playButton.addActionListener(e -> {
            if (currScore.length() >= 0){
                playButton.setEnabled(false);
                BpmPicker.setEnabled(false);
                pianoListener.setPlaying(true);
                saveButton.setEnabled(false);

                SwingWorker<Integer, String> playScore = new SwingWorker<>() {
                    @Override
                    protected Integer doInBackground() {

                        if (!isPaused) {
                            player = new GeneralSynth(currScore, (String) instrumentBox.getSelectedItem());
                        } else {
                            isPaused = false;
                        }

                        System.out.println("play");

                        setProgress(1);

                        double stopTime = player.PlayScore();

                        SwingWorker<Integer, String> playStatusHandler = new SwingWorker<>() {

                            private boolean naturalFinish;

                            @Override
                            protected Integer doInBackground() {

                                System.out.println("waiting");

                                naturalFinish = player.stopAt(stopTime);

                                return 1;
                            }

                            @Override
                            protected void done() {
                                System.out.println("waitingDone");
                                if (naturalFinish) {
                                    pauseButton.setEnabled(false);
                                    stopButton.setEnabled(false);
                                    player = null;
                                    isPaused = false;
                                    playButton.setEnabled(true);
                                    BpmPicker.setEnabled(true);
                                    pianoListener.setPlaying(false);
                                    saveButton.setEnabled(true);
                                }
                                super.done();
                            }
                        };

                        playStatusHandler.execute();


                        return 1;
                    }

                    @Override
                    protected void done() {
                        System.out.println("playDone");
                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        super.done();
                    }
                };

                playScore.execute();
            }

        });

        pauseButton.addActionListener(e -> {

            pauseButton.setEnabled(false);

            SwingWorker<Integer, String> pauseScore = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() {

                    player.stop();
                    isPaused = true;
                    System.out.println("pause");

                    return 1;
                }

                @Override
                protected void done() {
                    playButton.setEnabled(true);
                    stopButton.setEnabled(true);
                    System.out.println("pause Done");
                    super.done();
                }
            };

            pauseScore.execute();

        });

        stopButton.addActionListener(e -> {
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);

            SwingWorker<Integer, String> stopScore = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() {

                    if (!isPaused){
                        player.stop();
                    }
                    player = null;
                    isPaused = false;
                    //playScore.cancel(true);
                    return 1;
                }

                @Override
                protected void done() {

                    System.out.println("stop");
                    playButton.setEnabled(true);
                    BpmPicker.setEnabled(true);
                    pianoListener.setPlaying(false);
                    saveButton.setEnabled(true);
                    super.done();
                }
            };

            stopScore.execute();

        });

        saveButton.addActionListener(e -> SaveScore.save(currScore, name));

    }

     /*private void initializeImges() {

        noteImg = null;
        try {
            noteImg = ImageIO.read(new File("Img/Notes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void createGrid(JLayeredPane panel) {

        int segments = 4;
        double freqStep = (double) (maxFreq - minFreq) / (nRows - 1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double noteWidth = screenSize.getWidth() / 20;
        double noteHeight = noteWidth / 6;

        Dimension rollLength = new Dimension((int)((nCols * noteWidth) + (noteWidth / 5)), (int)((nRows * noteHeight) + (noteHeight)));
        //Dimension keyDim = new Dimension((int)screenSize.getWidth() / 20, (int)screenSize.getHeight() / 35);

        panel.setPreferredSize(rollLength);

        GridBagConstraints cellConstraints;
        cellConstraints = new GridBagConstraints();

        cellConstraints.weightx = 4;
        cellConstraints.weighty = 1;
        //cellConstraints.fill =  GridBagConstraints.HORIZONTAL;

        //JLabel[][] gridSegments = new JLabel[nRows][nCols];

        cellConstraints.gridx = 0;
        /*cellConstraints.gridy = 0;
        JLabel segs = new JLabel();
        segs.setOpaque(true);
        segs.setBackground(Color.green);
        segs.setPreferredSize(new Dimension(1, 3));
        panel.add(segs, cellConstraints);*/
        cellConstraints.gridheight = 1;
        cellConstraints.gridy = 0;
        for (int col = 1; col < nCols*segments; col++) {

            JSeparator seg = new JSeparator();
            //seg.setOpaque(true);
            cellConstraints.gridx = col;
            //seg.setBackground(Color.green);
            //seg.setMaximumSize(new Dimension(1, 3));
            panel.add(seg, cellConstraints, 1);

        }
        cellConstraints.gridx = 0;
        cellConstraints.fill =  GridBagConstraints.BOTH;
        for (int row = 0; row < nRows; row++) {
            JLabel seg = new JLabel();
            //cellConstraints.gridheight = 4;
            seg.setHorizontalAlignment(SwingConstants.CENTER);
            seg.setBorder(BorderFactory.createLineBorder(Color.red));
            seg.setText(String.valueOf((double) Math.round((maxFreq - (freqStep * row)) * 100) / 100));
            //seg.setOpaque(true);
            cellConstraints.gridy = row;
            panel.add(seg,cellConstraints, 1);
        }

        cellConstraints.gridx = 1;
        cellConstraints.gridwidth = (nCols*segments) - 1;
        for (int row = 0; row < nRows; row++) {
            GridLabels backgroundLabel = new GridLabels(cellConstraints.gridwidth, row % 2 == 0);
            cellConstraints.gridy = row;
            panel.add(backgroundLabel,cellConstraints, 1);
        }

        //cellConstraints.fill =  GridBagConstraints.HORIZONTAL;


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

    private void setKeyBinds() {

        InputMap inputMap;
        ActionMap actionMap;

        inputMap = NoteDurationMenu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = NoteDurationMenu.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("1"), "selectWHOLE");
        actionMap.put("selectWHOLE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteDurationMenu.setSelectedItem("WHOLE");
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("2"),
                "selectHALF");
        actionMap.put("selectHALF", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteDurationMenu.setSelectedItem("HALF");
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("3"),
                "selectQUARTER");
        actionMap.put("selectQUARTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteDurationMenu.setSelectedItem("QUARTER");
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("4"),
                "selectEIGHTH");
        actionMap.put("selectEIGHTH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteDurationMenu.setSelectedItem("EIGHTH");
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("5"),
                "selectSIXTEENTH");
        actionMap.put("selectSIXTEENTH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteDurationMenu.setSelectedItem("SIXTEENTH");
            }
        });

        inputMap = playButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "released");

        inputMap = pauseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "released");


        inputMap = stopButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "released");


    }

    private void fillEditorFromScore() {

        TreeMap<Integer, Chord> treeScore = currScore.getScore();

        for (Map.Entry<Integer, Chord> entry: treeScore.entrySet()) {

            int cellX = entry.getKey() + 1;
            Chord  chord = entry.getValue();

            for (Note note : chord.getChord()) {
                int cellY = nRows - note.getRelativePitch();
                Point cell = new Point(cellX, cellY);
                MouseListener[] listeners = PianoRoll.getMouseListeners();
                ((PianoRollMouseListener)listeners[0]).loadLabel(cell, note, PianoRoll);
            }

        }

    }

}
