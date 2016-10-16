package org.kedar.pra;

/**
 * <p>
 *     Represents the state of a particular {@linkplain Submission}.
 * </p>
 * Created by kedar on 10/15/16.
 */
public enum SubmissionState {
    IN_FLIGHT, /* working on it */
    COMPLETE, /*  work finished */
    SUBMITTED_TO_REVIEW, /* ready to be put in submission pool for review */
    FAILING, /* the submission has been graded, but fails */
    PASSING /* the submission has been graded and it passes all the criteria */
}
