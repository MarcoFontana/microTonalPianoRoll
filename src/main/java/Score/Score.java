package Score;

import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Score {

    private TreeMap<Integer, Chord> score;
    private int topFreq;
    private int bottomFreq;
    private int frequencyRange;
    private int nSteps;
    private double freqStep;
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

    public Score() {
        score = new TreeMap<>();
        topFreq = 10000;
        bottomFreq = 20;
        frequencyRange = topFreq - bottomFreq;
        nSteps = 12;
        freqStep = (double)frequencyRange / (nSteps - 1);
        this.bpm = 120;
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

        Integer integer;

        try{
            integer = score.lastKey();
        }catch (NoSuchElementException e) {
            integer = -1;
        }
        return integer;
    }

    public void setBottomFreq(int bottomFreq) {
        this.bottomFreq = bottomFreq;
    }

    public void setFreqStep(double freqStep) {
        this.freqStep = freqStep;
    }

    public void setScore(TreeMap<Integer, Chord> score) {
        this.score = score;
    }

    public void setNSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    public void setFrequencyRange(int frequencyRange) {
        this.frequencyRange = frequencyRange;
    }

    public void setTopFreq(int topFreq) {
        this.topFreq = topFreq;
    }

}
