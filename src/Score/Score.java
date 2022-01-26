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
        freqStep = (double)frequencyRange / steps;
        this.bpm = bpm;
    }

    public void addNote(Note note, Integer startBlock) {

        if (score.containsKey(startBlock)) {
            score.get(startBlock).addNote(note);
        }
        else {
            score.put(startBlock, new Chord(note));
        }

    }

    public void deleteNote(Note note, Integer startBlock) {

        if (score.containsKey(startBlock)) {
            score.get(startBlock).deleteNote(note);
            if(score.get(startBlock).isEmpty()) {
                score.remove(startBlock);
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

}
