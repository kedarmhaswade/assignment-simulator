package org.kedar.pra;

import java.util.EnumSet;

/**
 * <p>
 *     Denotes the state a learner can be in at any given <i>tick</i>. The state transitions are dictated by the
 *     rules. In the real-world however, the transitions occur more randomly and a learner could be in more than one
 *     state simultaneously. It's possible to create such combined states using an {@linkplain EnumSet}.
 * </p>
 * Created by kedar on 15/10/16.
 */
public enum LearnerState {
    REVIEWING, /* reviewing others' submissions */
    SLEEPING, /* there's nothing to do -- take another course on Coursera ;) */
    WAITING_FOR_GRADE, /* done with submission, waiting for others to complete their reviews */
    WORKING /* working on my submission */
}
