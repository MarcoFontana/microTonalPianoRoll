package Score;

import java.util.ArrayList;

public class Chord {

    private ArrayList<Note> chord;

    public Chord() {
        chord = new ArrayList<>();
    }

    public Chord(Note note) {
        chord = new ArrayList<>();
        addNote(note);
    }

    public void addNote(Note note){

        if(!chord.contains(note)) {
            chord.add(note);
        }

    }

    public void deleteNote(Note note){
        chord.remove(note);
    }

    public ArrayList<Note> getChord() {
        return chord;
    }

    public boolean isEmpty() {
        return getChord().isEmpty();
    }

    public void setChord(ArrayList<Note> chord) {
        this.chord = chord;
    }
}
