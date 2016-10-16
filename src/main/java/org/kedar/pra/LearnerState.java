package org.kedar.pra;

import java.util.EnumSet;

/**
 * <p>
 *     Denotes the state a learner can be in at any given <i>firstSubmissionStartTick</i>. The state transitions are dictated by the
 *     rules. In the real-world however, the transitions occur more randomly and a learner could be in more than one
 *     state simultaneously. It's possible to create such combined states using an {@linkplain EnumSet}.
 * </p>
 * Created by kedar on 10/15/16.
 */
public enum LearnerState {
    INACTIVE,
    WORKING, /* working on my submission */
    REVIEWING, /* reviewing others' submissions */
    WAITING_FOR_GRADE, /* done with submission, reviews, now waiting for others to complete their reviews */
    SLEEPING, /* there's nothing to do, do next assignment, or take another course on Coursera ;) */
}
