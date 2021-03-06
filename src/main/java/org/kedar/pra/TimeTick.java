package org.kedar.pra;


/**
 * <p>
 * Models an immutable, non-negative, monotonically increasing firstSubmissionStartTick of time. It could be modeled as a natural number, but
 * this abstraction could be useful to provide additional flexibility. This is a pure <i>value class</i>.
 * </p>
 * Created by kedar on 10/15/16.
 */
public final class TimeTick implements Comparable<TimeTick> {

    private final long value;

    public TimeTick(long value) {
        if (value < 0)
            throw new IllegalArgumentException("negative value not allowed: " + value);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public long minus(TimeTick that) {
        return this.value - that.value;
    }
    public boolean happensBefore(TimeTick that) {
        return this.compareTo(that) < 0;
    }
    public boolean isConcurrentWith(TimeTick that) {
        return this == that || this.compareTo(that) == 0;
    }
    public boolean happensAfter(TimeTick that) {
        return this.compareTo(that) > 0;
    }

    public boolean elapsedSinceTo(int n, TimeTick newer) {
        // "this" was in the past, which means newer > this
        return newer.value - this.value == n;
    }
    @Override
    public int compareTo(TimeTick that) {
        return Long.compare(this.value, that.value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TimeTick) {
            TimeTick that = (TimeTick)o;
            return this.value == that.value;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    /**
     * <p>
     *     Returns an instance of {@linkplain TimeTick} since the <i>beginning of time</i>. This is useful to represent
     *     the {@linkplain System#currentTimeMillis()} as a TimeTick.
     * </p>
     * @param milliSecondTimeStampPast the millisecond mark that represents the beginning of time; typically these
     *                                 are counted since Unix epoch
     * @return
     */
    public static TimeTick since(long milliSecondTimeStampPast) {
        if (milliSecondTimeStampPast < 0)
            throw new IllegalArgumentException("invalid timestamp(ms) for the beginning of time: " + milliSecondTimeStampPast);
        long curr = System.currentTimeMillis();
        if (milliSecondTimeStampPast > curr)
            throw new IllegalArgumentException("milliSecondTimeStampPast should be in the past, not future: " + milliSecondTimeStampPast);
        return new TimeTick(curr - milliSecondTimeStampPast);
    }
}
