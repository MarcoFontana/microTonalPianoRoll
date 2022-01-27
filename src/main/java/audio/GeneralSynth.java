package audio;

import Score.*;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import com.softsynth.shared.time.TimeStamp;

import java.util.Map;

public class GeneralSynth {

    private final Score scoreToPlay;
    private Synthesizer synth;
    private SineOscillator osc;
    private LineOut out;

    public GeneralSynth(Score scoreToPlay) {
        this.scoreToPlay = scoreToPlay;
        initSynth();
    }

    public void PlayScore() {

        double timeNow = synth.getCurrentTime();

        TimeStamp startTime = new TimeStamp(timeNow + 0.1);

        out.start(startTime);

        queueScore(timeNow);

        synth.stop();
    }

    private void initSynth() {

        synth = JSyn.createSynthesizer();

        synth.add(osc = new SineOscillator());
        synth.add(out = new LineOut());
        osc.output.connect(0, out.input, 0);
        osc.output.connect(0, out.input, 1);
        osc.amplitude.set(0);


        synth.start();

    }

    private void queueScore(double timeNow) {

        for (Map.Entry<Integer, Chord> entry : scoreToPlay.getScore().entrySet()) {
            Integer relTime = entry.getKey();
            Chord chord = entry.getValue();
            double sTime = scoreToPlay.getChordStart(relTime);
            TimeStamp chordStartTime = new TimeStamp(timeNow + sTime);
            for (Note note : chord.getChord()) {
                double freq = note.getPitch(scoreToPlay.getFreqStep());
                double duration = note.getDuration(scoreToPlay.getBpm());

                osc.noteOn(freq, 0.6, chordStartTime);
                osc.noteOff(chordStartTime.makeRelative(duration));
            }

        }
    }

}
