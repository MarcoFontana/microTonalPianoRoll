package Score;

import java.util.Objects;

public class Note {

    private int relativePitch;
    private int relativeDuration;

    public Note(int relPitch, int relDur) {
        relativePitch = relPitch;
        relativeDuration = relDur;
    }

    public Note() {
        relativePitch = -1;
        relativeDuration = -1;
    }

    public int getRelativePitch() {
        return relativePitch;
    }

    public int getRelativeDuration() {
        return relativeDuration;
    }

    public double getDuration(int bpm) {
        return relativeDuration / 4.0 * (60.0/bpm);
    }

    public double getPitch(double freqStep, int minFreq) {
        return minFreq + (freqStep * (relativePitch - 1));
    }

    public void setRelativeDuration(int relativeDuration) {
        this.relativeDuration = relativeDuration;
    }

    public void setRelativePitch(int relativePitch) {
        this.relativePitch = relativePitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return relativePitch == note.relativePitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativePitch);
    }
}
