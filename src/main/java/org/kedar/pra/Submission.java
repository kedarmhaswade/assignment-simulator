package org.kedar.pra;

import static org.kedar.pra.SubmissionState.FAILING;
import static org.kedar.pra.SubmissionState.IN_FLIGHT;
import static org.kedar.pra.SubmissionState.PASSING;

/**
 * <p>
 *     An immutable class that models a submission by a {@linkplain Learner} for an {@linkplain Assignment}.
 * </p>
 * <p>
 *     It's a conscious choice to make the instances of this class immutable. The idea is that once
 *     made a submission is indestructible. This is very useful because though an assignment can have
 *     multiple submissions and it appears that a mutable object can better capture the scenario, a more
 *     powerful abstraction is that a submission made at a given {@linkplain TimeTick} is a <i>fact</i>
 *     that will always remain so.
 * </p>
 * Created by kedar on 15/10/16.
 */
public final class Submission {

    /* final fields are package-private for easier access from within the package, alternatively we could use Lombok */
    final String lid;
    final String aid;
    final int reviews;
    final TimeTick tick;
    final SubmissionState state;

    /**
     * Constructs an instance of this class. Uses the telescoping constructor pattern which could be retrofitted to
     * use a builder.
     * @param lid the id of the learner
     * @param aid the id of the assignment
     * @param reviews number of reviews this submission has received
     * @param tick the TimeTick when this submission occurred
     * @param state the "birth state" of this submission
     */
    private Submission(String lid, String aid, int reviews, TimeTick tick, SubmissionState state) {
        if (lid == null || aid == null || tick == null || state == null)
            throw new IllegalArgumentException("invalid learner id or assignment id or tick, or state: " + lid + ", " + aid + ", " + tick + ", " + state);
        this.lid = lid;
        this.aid = aid;
        this.tick = tick;
        if (reviews <= 0)
            throw new IllegalArgumentException("reviews may not be <= 0: " + reviews);
        this.reviews = reviews;
        this.state = state;
    }

    /**
     * <p>
     *     A factory method to create the <i>default</i> instances of this class.
     * </p>
     * @param lid the learner ID
     * @param aid the assignment ID
     * @param tick the time of birth
     *
     * @return an instance of this class
     */
    public static Submission create(String lid, String aid, TimeTick tick) {
        return new Submission(lid, aid, 0, tick, IN_FLIGHT);
    }

    public Submission fail(TimeTick tick) {
        return new Submission(lid, aid, reviews, tick, FAILING);
    }
    public Submission pass(TimeTick tick) {
        return new Submission(lid, aid, reviews, tick, PASSING);
    }

    @Override
    public String toString() {
        return "lid: " + lid + ", aid: " + aid + ", time tick: " + tick + ", number of reviews: " + reviews + ", " + ", state: " + state;
    }
}
