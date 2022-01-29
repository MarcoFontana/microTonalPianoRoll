package ui;

import Score.Note;
import Score.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;

public class PianoRollMouseListener extends MouseAdapter {

    private final int nRows;
    private final int nCols;
    private final Score currScore;
    private int currDuration;
    private final LinkedHashMap<Integer, NoteLabel> notes;
    private boolean isPlaying;
    private final MouseAdapter mouseListener;

    public PianoRollMouseListener(int rows, int cols, Score currScore, int currDur) {
        this.nRows = rows;
        this.nCols = cols;
        this.currScore = currScore;
        currDuration = currDur;
        notes = new LinkedHashMap<>();
        isPlaying = false;

        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && !isPlaying) {
                    NoteLabel lab = (NoteLabel) e.getComponent();
                    lab.setVisible(false);
                    currScore.deleteNote(lab.getNote(), lab.getTime());
                    notes.remove((lab.getTime() * 10000) + lab.getNote().getRelativePitch());
                    lab.getParent().remove(lab);
                }
            }
        };

    }

    public void setCurrDuration(int currDuration) {
        this.currDuration = currDuration;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (!isPlaying){
            Point coordinates = e.getPoint();
            JLayeredPane PianoRoll = (JLayeredPane) e.getComponent();
            GridBagLayout gridL = (GridBagLayout) PianoRoll.getLayout();

            Point cell = gridL.location(coordinates.x, coordinates.y);
            int[][] dims = gridL.getLayoutDimensions();
            int totHeight = PianoRoll.getHeight();
            int topPadding = (totHeight - dims[1][0] * nRows) / 2;
            if (coordinates.y <= topPadding) {
                cell.y = -1;
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                if ((cell.x > 0 && cell.x < nCols * 4) && (cell.y >= 0 && cell.y < nRows + 1)) {

                    GridBagConstraints labelConstraints;
                    labelConstraints = new GridBagConstraints();
                    labelConstraints.gridx = cell.x;
                    labelConstraints.gridy = cell.y;
                    labelConstraints.fill = GridBagConstraints.BOTH;
                    //int[][] dims = gridL.getLayoutDimensions();

                    boolean isOverlapping = false;

                    //TODO filter for notes over the grid length
                    for (int i = cell.x; i < (cell.x + currDuration); i++) {
                        if (notes.containsKey((i * 10000) + (nRows - cell.y))) {
                            isOverlapping = true;

                            createLabel(cell, i - cell.x, labelConstraints, PianoRoll, false);

                            break;
                        }
                    }

                    if (!isOverlapping) {

                        createLabel(cell, currDuration, labelConstraints, PianoRoll, false);

                    }

                    PianoRoll.revalidate();
                    PianoRoll.repaint();

                }
            }
        }

    }

    public void loadLabel(Point cell, Note note, JLayeredPane PianoRoll) {

        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.gridx = cell.x;
        constraints.gridy = cell.y;
        constraints.fill = GridBagConstraints.BOTH;

        int duration = note.getRelativeDuration();

        createLabel(cell, duration, constraints, PianoRoll, true);
    }

    private void createLabel(Point cell, int duration, GridBagConstraints labelConstraints, JLayeredPane PianoRoll, boolean isLoaded) {

        labelConstraints.gridwidth = duration;
        Note newNote = new Note((nRows - cell.y), duration);

        if (!isLoaded){
            currScore.addNote(newNote, cell.x);
        }
        NoteLabel seg = new NoteLabel(newNote, cell.x);
        seg.addMouseListener(mouseListener);
        PianoRoll.add(seg, labelConstraints, 1);
        notes.put((cell.x * 10000) + (nRows - cell.y), seg);

    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
