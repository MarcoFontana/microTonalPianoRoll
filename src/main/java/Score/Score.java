package Score;

import java.util.TreeMap;

public class Score {

    private final TreeMap<Integer, Chord> score;
    private final int topFreq;
    private final int bottomFreq;
    private final int frequencyRange;
    private final int nSteps;
    private final double freqStep;
    private int bpm;

    public Score(int topFreq, int bottomFreq, int steps, int bpm) {

        score = new TreeMap<>();
        this.topFreq = topFreq;
        this.bottomFreq = bottomFreq;
        frequencyRange = topFreq - bottomFreq;
        nSteps = steps;
        freqStep = (double)frequencyRange / (steps - 1);
        this.bpm = bpm;
    }

    public void addNote(Note note, int startBlock) {

        int key = startBlock - 1;
        if (score.containsKey(key)) {
            score.get(key).addNote(note);
        }
        else {
            score.put(key, new Chord(note));
        }

    }

    public void deleteNote(Note note, int startBlock) {
        int key = startBlock - 1;
        if (score.containsKey(key)) {
            score.get(key).deleteNote(note);
            if(score.get(key).isEmpty()) {
                score.remove(key);
            }
        }
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getFrequencyRange() {
        return frequencyRange;
    }

    public int getNSteps() {
        return nSteps;
    }

    public int getBpm() {
        return bpm;
    }

    public TreeMap<Integer, Chord> getScore() {
        return score;
    }

    public int getTopFreq() {
        return topFreq;
    }

    public int getBottomFreq() {
        return bottomFreq;
    }

    public double getFreqStep() {
        return freqStep;
    }

    public double getChordStart(int key) {
        return key / 4.0 * (60.0/bpm);
    }

    public int length() {
        return score.lastKey();
    }
}
