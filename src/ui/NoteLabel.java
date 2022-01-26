package ui;

import Score.Note;

import javax.swing.*;

public class NoteLabel extends JLabel {

    private final Note note;
    private final int time;

    public NoteLabel(Note note, int time) {
        super();
        this.note = note;
        this.time = time;
    }

    public Note getNote() {
        return note;
    }

    public int getTime() {
        return time;
    }
}
