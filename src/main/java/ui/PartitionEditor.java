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
    private final PianoRoll PianoRoll;
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

        nRows = score.getNSteps();
        nCols = 40;
        BpmPicker.setValue(score.getBpm());

        minFreq = score.getBottomFreq();
        maxFreq = score.getTopFreq();
        currScore = score;

        PianoRoll = new PianoRoll(maxFreq, minFreq, nRows, nCols);

        defaultValuesInit();

        eventHandlersInitialize(frame);
        fillEditorFromScore();
        setKeyBinds();
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

    }

    public PartitionEditor(int nRows, int nCols, int maxFreq, int minFreq, String name) {
        JFrame frame=new JFrame(name);
        this.name = name;
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.nRows = nRows;
        this.nCols = nCols;

        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        currScore = new Score(maxFreq, minFreq, nRows, BpmPicker.getValue());

        PianoRoll = new PianoRoll(maxFreq, minFreq, nRows, nCols);

        defaultValuesInit();

        eventHandlersInitialize(frame);
        setKeyBinds();
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

    }

    private void defaultValuesInit() {

        isPaused = false;

        NoteDurationMenu.setSelectedItem("QUARTER");
        currDuration = (int)Math.pow(2 , NoteDurationMenu.getSelectedIndex());
        BpmValue.setText(String.valueOf(BpmPicker.getValue()));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)(screenSize.getWidth() / 2), (int)(screenSize.getHeight() / 2));
        Dimension rootPanelSize = new Dimension((int)(screenSize.getWidth() / 1.5), (int)(screenSize.getHeight() / 1.5));
        rootPanel.setPreferredSize(rootPanelSize);
        PianoRollExplorer.setMaximumSize(windowSize);

        PianoRollExplorer.setViewportView(PianoRoll);


        AllFrames.noDoubleClicks(new JButton[]{playButton, pauseButton, stopButton, saveButton});

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

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, true), "released");

        inputMap = pauseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "released");


        inputMap = stopButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true), "released");

        inputMap = saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "released");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, true), "released");


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
