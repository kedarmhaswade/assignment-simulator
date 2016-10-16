package org.kedar.pra;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ListIterator;

/**
 * <p>
 *     Models a container to hold the {@linkplain Submission}s.
 *     Provides API to submit and retrieve a submission.
 *     Note that all the Submissions in this pool must be in {@linkplain SubmissionState#IN_FLIGHT} state.
 * </p>
 * Created by kedar on 10/15/16.
 */
public class SubmissionPool {

    /** Used for better iteration efficiency, since to be fair, the Submission that has been here the
     * longest should be removed first.
     */
    private final LinkedHashSet<Submission> pool;

    public SubmissionPool() {
        this.pool = new LinkedHashSet<>();
    }
    /**
     * Retrieves the Submissions that the given Learner should provideReview.
     * @param me
     * @return
     */
    public Submission retrieve(Learner me) {
        //return pool.stream().filter(submission -> submission.lid != me.lid).findFirst().orElse(null);  // TODO more rules are required, so maybe we'll need to collect
        Iterator<Submission> iter = pool.iterator();
        while (iter.hasNext()) {
            Submission next = iter.next();
            if (next.owner.lid != me.lid && ! next.reviewers.contains(me)) {
                iter.remove();
                return next;
            }
        }
        return null;
    }

    public void submit(Submission submission) {
        if (! submission.isReviewable())
            throw new IllegalStateException("The given submission is not reviewable, since it has enough reviews" +
                    " already: " + submission.reviewers.size());
        boolean added = pool.add(submission);
        assert added : "Strange, serious bug, the submission: " + submission + " could not be added to the pool because it already exists there!";
    }


    public int size() {
        return pool.size();
    }

    /** Same as {@linkplain #submit(Submission)}, but clarifies the purpose that this is the same
     * submission being submitted for yet another review by yet another reviewer
     *
     * @param reviewable
     */
    public void resubmit(Submission reviewable) {
        pool.add(reviewable); // ignores return value TODO
    }
}
