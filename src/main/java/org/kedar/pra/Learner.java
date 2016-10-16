package org.kedar.pra;

import java.util.*;

import static org.kedar.pra.Config.*;
import static org.kedar.pra.Config.REVIEWS_REQUIRED_PER_LEARNER;
import static org.kedar.pra.Config.WORK_TICKS;
import static org.kedar.pra.LearnerState.*;
import static org.kedar.pra.SubmissionState.COMPLETE;
import static org.kedar.pra.SubmissionState.IN_FLIGHT;
import static org.kedar.pra.SubmissionState.SUBMITTED_TO_REVIEW;

/**
 * <p>
 * Models a Learner with an emphasis on its state management.
 * In a way, a learner is a finite state machine.
 * </p>
 * Created by kedar on 10/15/16.
 *
 * @see LearnerState
 */
public final class Learner {

    /* final fields are package-private for easier access from within the package,
    alternatively we could use Lombok */
    final int lid;
    final TimeTick firstSubmissionStartTick;
    LearnerState state = INACTIVE;
    Deque<Submission> reviewsDone;
    final Deque<Submission> submissions; // the head of the queue is current submission
    final int trueGrade;
    final int reviewBias;


    private Learner(int lid, TimeTick firstSubmissionStartTick, LearnerState state, int trueGrade, int reviewBias) {
        this.lid = lid;
        this.firstSubmissionStartTick = firstSubmissionStartTick;
        this.state = state;
        this.reviewsDone = new LinkedList<>();
        this.submissions = new LinkedList<>();
        this.trueGrade = trueGrade;
        this.reviewBias = reviewBias;
    }

    public static Learner worker(int lid, TimeTick tick, int trueGrade, int reviewBias) {
        if (tick == null)
            throw new IllegalArgumentException("null time firstSubmissionStartTick");
        if (lid < 0)
            throw new IllegalArgumentException("invalid learner ID: " + lid);
        return new Learner(lid, tick, WORKING, trueGrade, reviewBias);
    }

    /**
     * A cumbersome implementation of the Learner state machine.
     * <p>
     * &lt;rant> mutable state! &lt;/rant>
     * </p>
     *
     * @param at   the current tick coming in from Simulator
     * @param pool the pool of submissions that is manipulated in response to state changes
     * @return
     */
    public Submission respondTo(TimeTick at, SubmissionPool pool) {
        if (at.happensBefore(this.firstSubmissionStartTick)) { // it's not time to start working yet
            return null;
        }
        Submission latestSubmission = submissions.peekLast();
        if (latestSubmission == null) { // first submission task
            LinkedList<Learner> reviewers = new LinkedList<>();
            latestSubmission = new Submission(this, Assignment.getId(), at, IN_FLIGHT, reviewers, 0);
            submissions.addLast(latestSubmission);
            this.state = WORKING;
            return null;
        } else if (latestSubmission.isComplete(at)) {
            if (! latestSubmission.isSubmittedToReview(at)) {
                pool.submit(latestSubmission);
                latestSubmission.state = SUBMITTED_TO_REVIEW;
                return latestSubmission;
            }
            if (this.reviewsDone.size() == REVIEWS_REQUIRED_PER_LEARNER) { // done reviewing
                this.state = WAITING_FOR_GRADE;
                if (latestSubmission.isFailing(at)) {
                    handleFailedSubmission(latestSubmission, at);
                    this.state = WORKING;
                    return null;
                }
            } else {
                if (latestReviewTimeSimulated(at)) {
                    final Submission reviewable = reviewsDone.peekLast();
                    reviewable.provideReview(this, at);
                    if (reviewable.reviewers.size() < Config.REVIEWS_REQUIRED_PER_SUBMISSION) {
                        pool.resubmit(reviewable);
                    }
                    if (reviewsDone.size() == REVIEWS_REQUIRED_PER_LEARNER) {
                        this.state = WAITING_FOR_GRADE;
                        return null;
                    } else {
                        Submission forReview = pool.retrieve(this);
                        reviewsDone.addLast(forReview);
                        this.state = REVIEWING;
                        return null;
                    }
                } else {
                    // simply work on reviews
                    if (reviewsDone.isEmpty() || reviewsDone.size() < REVIEWS_REQUIRED_PER_LEARNER) {
                        this.state = REVIEWING;
                        Submission forReview = pool.retrieve(this);
                        if (forReview == null) {
                            // nothing to review!
                        } else {
                            reviewsDone.addLast(forReview);
                        }
                        return null;
                    }
                }
            }
        } else if (latestSubmission.isFailing(at)) {
            // get back to work; start next submission
            handleFailedSubmission(latestSubmission, at);
            this.state = WORKING;
            return null;
        } else if (latestSubmission.isPassing(at)) {
            this.state = SLEEPING;
            return null;
        } else if (latestSubmission.isPending(at)) {
            this.state = WORKING;
            return null;
        }
        throw new IllegalStateException("bug, this learner: " + this + " is in illegal state, no transition " +
                "at time tick: " + at);
    }

    private void handleFailedSubmission(Submission previous, TimeTick at) {
        LinkedList<Learner> reviewers = new LinkedList<>();
        Submission latestSubmission = new Submission(this, Assignment.getId(), at, IN_FLIGHT, reviewers, previous.sequenceNumber + 1);
        this.submissions.addLast(latestSubmission);
        this.reviewsDone = new LinkedList<>(); // need to do reviews again
    }

    private boolean latestReviewTimeSimulated(TimeTick at) {
        Submission latest = reviewsDone.peekLast();
        if (latest == null)
            return false;
        return (at.getValue() - latest.tick.getValue()) >= REVIEW_TICKS;
    }

    public static Learner fromInputLine(String line) {
        String[] parts = line.split("\\s+");
        return new Learner(Integer.valueOf(parts[0]),
                new TimeTick(Integer.valueOf(parts[1])),
                LearnerState.INACTIVE,
                Integer.valueOf(parts[2]),
                Integer.valueOf(parts[3]));
    }

    @Override
    public String toString() {
        return this.lid + ", " + this.state;
    }
}