package org.kedar.pra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.kedar.pra.Config.*;
import static org.kedar.pra.SubmissionState.*;

/**
 * <p>
 * Models a submission by a {@linkplain Learner} for an {@linkplain Assignment} by
 * a {@linkplain Learner}.
 * </p>
 * Created by kedar on 10/15/16.
 */
public final class Submission {

    /* final fields are package-private for easier access from within the package, alternatively we could use Lombok */
    final Learner owner;
    /**
     * Assignment ID, modeled an int, could be a String
     */
    final int aid;
    final TimeTick tick;
    SubmissionState state;
    final LinkedList<Learner> reviewers;
    final int sequenceNumber;
    private final Map<Learner, TimeTick> reviewerTicks;

    /**
     * Constructs an instance of this class. Uses the telescoping constructor pattern which could be retrofitted to
     * use a builder.
     *
     * @param owner the learner who owns this Submission
     * @param aid   the id of the assignment
     * @param tick  the TimeTick when this submission occurred
     * @param state the "birth state" of this submission
     */
    Submission(Learner owner, int aid, TimeTick tick,
               SubmissionState state, LinkedList<Learner> reviewers, int sequenceNumber) {
        if (owner == null || tick == null || state == null)
            throw new IllegalArgumentException("invalid owner, tick, or state: " + owner + ", " + tick + ", " + state);
        this.owner = owner;
        this.aid = aid;
        this.tick = tick;
        this.state = state;
        this.reviewers = reviewers;
        this.sequenceNumber = sequenceNumber;
        this.reviewerTicks = new HashMap<>();
    }

    /**
     * <p>
     * A factory method to create the <i>default</i> instances of this class.
     * </p>
     *
     * @param owner the learner
     * @param aid   the assignment ID
     * @param tick  the time of birth
     * @return an instance of this class
     */
    public static Submission create(Learner owner, int aid, TimeTick tick) {
        return new Submission(owner, aid, tick, IN_FLIGHT, Utils.EMPTY_LEARNER_LIST, 0);
    }

    @Override
    public String toString() {
        return "lid: " + owner.lid + ", aid: " + aid + ", time firstSubmissionStartTick: " + tick + ", number of reviews: " + reviewers.size() + ", " + ", state: " + state;
    }

    public boolean isComplete(TimeTick at) {
        if (this.state == COMPLETE)
            return true;
        if (at.getValue() - this.tick.getValue() >= Config.WORK_TICKS) {
            return true;
        }
        return false;
    }

    public boolean isReviewable() {
        return this.reviewers.size() < REVIEWS_REQUIRED_PER_SUBMISSION; // TODO >=0?
    }

    public int currentScore() {
        int rs = reviewers.size();
        int score = owner.trueGrade * rs;
        for (Learner reviewer : reviewers) {
            score += reviewer.reviewBias;
        }
        return score;
    }
    public boolean isFailing(TimeTick at) {
        return state == FAILING || isComplete(at) && currentScore() < PASSING_POINTS;
    }

    public boolean isSubmittedToReview(TimeTick at) {
        return this.state == SUBMITTED_TO_REVIEW;
    }

    public boolean isPassing(TimeTick at) {
        return state == PASSING || isComplete(at) && currentScore() >= PASSING_POINTS;
    }

    // Private business
    private void ensureComplete(TimeTick at) {
        if (!isComplete(at))
            throw new IllegalStateException("provideReview number mismatch, expected: " + REVIEWS_REQUIRED_PER_SUBMISSION + ", found: " + reviewers.size());
    }

    private void ensureReviewable() {
        if (!isReviewable())
            throw new IllegalStateException("number of reviews: " + this.reviewers.size() + " is not smaller than required: " + REVIEWS_REQUIRED_PER_SUBMISSION);

    }

    /**
     * Creates the string representation of this Submission as required by the assignment.
     * @return
     */
    public String toOutput() {
        StringBuilder buf = new StringBuilder(100);
        buf.append(owner.lid)
                .append(" ")
                .append(sequenceNumber)
                .append(" ")
                .append(tick)
                .append(" ")
                .append(currentScore())
                .append(" ")
                .append(gradeTick());
        return buf.toString();
    }

    private int gradeTick() {
        if (this.reviewers.size() < REVIEWS_REQUIRED_PER_SUBMISSION)
            return -1;
        return (int) this.reviewerTicks.get(this.reviewers.peekLast()).getValue();
    }

    public void provideReview(Learner learner, TimeTick at) {
        this.reviewers.addLast(learner);
        this.reviewerTicks.put(learner, at);
    }

    public boolean isPending(TimeTick at) {
        return at.getValue() - this.tick.getValue() < WORK_TICKS;
    }
}
