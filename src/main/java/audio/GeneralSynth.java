package audio;

import Score.*;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.instruments.SubtractiveSynthVoice;
import com.jsyn.unitgen.*;
import com.jsyn.util.VoiceAllocator;
import com.softsynth.shared.time.TimeStamp;

import java.util.TreeMap;

public class GeneralSynth {

    private static final int MAX_VOICES = 16;
    private final Score scoreToPlay;
    private Synthesizer synth;
    private SineOscillator osc;
    private LineOut out;
    private VoiceAllocator allocator;
    private UnitVoice[] voices;
    private double playTime;


    public GeneralSynth(Score scoreToPlay) {
        this.scoreToPlay = scoreToPlay;
        this.playTime = 0;
        initSynth();
    }

    public GeneralSynth(Score scoreToPlay, double playTime) {
        this.scoreToPlay = scoreToPlay;
        this.playTime = playTime;
        initSynth();
    }

    public double PlayScore() {

        synth.start();
        int nextIndex = 0;
        if (playTime > 0) {
            nextIndex = (int)Math.floor(playTime * 4.0 / 60.0 * scoreToPlay.getBpm());
        }
        double timeNow = synth.getCurrentTime();

        TimeStamp startTime = new TimeStamp(timeNow + 0.1);

        out.start(startTime);

        double stopTime = 0;

        try {
            stopTime = queueScore(timeNow + 0.1, nextIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return stopTime;

    }

    private double queueScore(double timeNow, int startIndex) throws InterruptedException {

        TreeMap<Integer, Chord> scoreTree = scoreToPlay.getScore();
        double currPlayTime = 0;

        for (int relTime = startIndex; relTime <= scoreToPlay.length(); relTime++) {

            if (scoreTree.containsKey(relTime)) {

                Chord chord = scoreTree.get(relTime);
                double sTime = scoreToPlay.getChordStart(relTime) - playTime;
                TimeStamp chordStartTime = new TimeStamp(timeNow + sTime);

                for (Note note : chord.getChord()) {

                    double freq = note.getPitch(scoreToPlay.getFreqStep());
                    double duration;

                    if (sTime < 0){
                        duration = note.getDuration(scoreToPlay.getBpm()) + sTime;
                    }
                    else {
                        duration = note.getDuration(scoreToPlay.getBpm());
                    }
                    int noteNumber = (relTime * 10000) + note.getRelativePitch();

                    allocator.noteOn(noteNumber, freq, 0.6, chordStartTime);
                    allocator.noteOff(noteNumber,chordStartTime.makeRelative(duration));
                    currPlayTime = sTime + duration;


                }

            }

        }

        //synth.sleepUntil(currPlayTime + timeNow);
        return currPlayTime + timeNow;

    }

    public boolean stopAt(double stopTime) {
        boolean naturalFinish = false;

        while (true) {
            if (!synth.isRunning() || (!(synth.getCurrentTime() < stopTime))) break;
        }
        if (synth.isRunning()) {
            stop();
            naturalFinish = true;
        }

        return naturalFinish;
    }

    public void stop() {
        out.stop();
        playTime = synth.getCurrentTime();
        synth.stop();
    }

    private void initSynth() {

        synth = JSyn.createSynthesizer();
        voices = new UnitVoice[MAX_VOICES];

        synth.add(osc = new SineOscillator());
        synth.add(out = new LineOut());

        for (int i = 0; i < MAX_VOICES; i++) {
            SubtractiveSynthVoice voice = new SubtractiveSynthVoice();
            synth.add(voice);
            voice.getOutput().connect(0, out.input, 0);
            voice.getOutput().connect(0, out.input, 1);
            voices[i] = voice;
        }
        allocator = new VoiceAllocator(voices);

        /*osc.output.connect(0, out.input, 0);
        osc.output.connect(0, out.input, 1);
        osc.amplitude.set(0);*/

    }

}
