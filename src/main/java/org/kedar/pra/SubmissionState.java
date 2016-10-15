package org.kedar.pra;

/**
 * <p>
 *     Represents the state of a particular {@linkplain Submission}.
 * </p>
 * Created by kedar on 15/10/16.
 */
public enum SubmissionState {
    FAILING, /* the submission has been graded, but fails */
    IN_FLIGHT, /* the submission has being graded, not all reviewers' grades are in yet */
    PASSING /* the submission has been graded and it passes all the criteria */
}
