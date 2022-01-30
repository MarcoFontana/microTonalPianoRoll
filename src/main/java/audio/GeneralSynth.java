package audio;

import Score.*;
import audio.instruments.SquareTremoloInst;
import audio.instruments.SubSynth;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.instruments.SubtractiveSynthVoice;
import com.jsyn.unitgen.*;
import com.jsyn.util.VoiceAllocator;
import com.softsynth.shared.time.TimeStamp;

import java.util.TreeMap;

public class GeneralSynth {

    private static final int MAX_VOICES = 16;
    private static final double STARTUP_DELAY = 0.2;
    private final Score scoreToPlay;
    private Synthesizer synth;
    private LineOut out;
    private VoiceAllocator allocator;
    private String selectedInstrument;
    private double playTime;
    private double accDelay;


    public GeneralSynth(Score scoreToPlay, String selectedInstrument) {
        this.scoreToPlay = scoreToPlay;
        this.playTime = 0;
        this.selectedInstrument = selectedInstrument;
        accDelay = 0;
        initSynth();
    }

    public GeneralSynth(Score scoreToPlay, double playTime) {
        this.scoreToPlay = scoreToPlay;
        this.playTime = playTime;
        accDelay = 0;
        initSynth();
    }

    public double PlayScore() {

        synth.start();
        int nextIndex = 0;
        if (playTime > 0) {
            nextIndex = (int)Math.floor(playTime * 4.0 / 60.0 * scoreToPlay.getBpm());
        }
        double timeNow = synth.getCurrentTime();

        TimeStamp startTime = new TimeStamp(timeNow + STARTUP_DELAY);

        double stopTime = 0;

        try {
            if (scoreToPlay.getChordStart(scoreToPlay.length()) - playTime <= 0) {
                stopTime = queueScore(timeNow + STARTUP_DELAY, scoreToPlay.length());
            }
            else {
                stopTime = queueScore(timeNow + STARTUP_DELAY, nextIndex);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        out.start(startTime);

        return stopTime;

    }

    private double queueScore(double timeNow, int startIndex) throws InterruptedException {

        TreeMap<Integer, Chord> scoreTree = scoreToPlay.getScore();
        double currPlayTime = 0;

        for (int relTime = startIndex; relTime <= scoreToPlay.length(); relTime++) {

            if (scoreTree.containsKey(relTime)) {

                Chord chord = scoreTree.get(relTime);
                double sTime = scoreToPlay.getChordStart(relTime) - (playTime - accDelay);
                TimeStamp chordStartTime = sTime < 0 ? new TimeStamp(timeNow) : new TimeStamp(timeNow + sTime);

                for (Note note : chord.getChord()) {

                    double freq = note.getPitch(scoreToPlay.getFreqStep(), scoreToPlay.getBottomFreq());
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

                    if (sTime < 0) {
                        currPlayTime = duration;
                    }
                    else {
                        if (currPlayTime < sTime + duration) {
                            currPlayTime = sTime + duration;
                        }
                    }


                }

            }

        }

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
        accDelay += STARTUP_DELAY;
        synth.stop();
    }

    private void initSynth() {

        synth = JSyn.createSynthesizer();
        UnitVoice[] voices = new UnitVoice[MAX_VOICES];

        synth.add(out = new LineOut());

        for (int i = 0; i < MAX_VOICES; i++) {

            UnitVoice voice;
            //TODO more instruments
            switch (selectedInstrument) {
                case "sawWave": {
                    voice = new SubSynth();
                    break;
                }
                case "squareWave": {
                    voice = new SquareTremoloInst();
                    break;
                }
                default:{
                    voice = new SubtractiveSynthVoice();
                    break;
                }

            }
            synth.add((UnitGenerator) voice);
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
