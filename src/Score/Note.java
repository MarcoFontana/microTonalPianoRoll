package Score;

import java.util.Objects;

public class Note {

    private final int relativePitch;
    private final int relativeDuration;

    public Note(int relPitch, int relDur) {
        relativePitch = relPitch;
        relativeDuration = relDur;
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

    public double getPitch(double freqStep) {
        return freqStep * relativePitch;
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
