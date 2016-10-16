package org.kedar.pra;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * <p>
 *     Models a simulation of the <i>Peer-reviewed Assignments</i>. Parses the input comprised of time ticks and learner
 *     data and runs a main loop through all the time ticks and learners. The output can be gathered on each time
 *     firstSubmissionStartTick, or filtered on a {@linkplain Submission}.
 * </p>
 * Created by kedar on 10/15/16.
 */
public class Simulator {

    public static void main(String[] args) {
        Object[] input = processInput(System.in);
        int ticks = (Integer)input[0];
        Set<Learner> learners = (Set<Learner>)input[1];
        mainLoop(0, ticks, learners, submission -> {
            if (submission != null)
                System.out.println(submission.toOutput());
        });
    }

    static Object[] processInput(InputStream is) {
        Scanner scanner = new Scanner(is);
        Integer ticks = Integer.parseInt(scanner.nextLine().trim());
        int nLearners = Integer.parseInt(scanner.nextLine().trim());
        Set<Learner> learners = new HashSet<>(nLearners);
        IntStream.range(0, nLearners).forEach(lid -> learners.add(Learner.fromInputLine(scanner.nextLine().trim())));
        return new Object[] {ticks, learners};
    }

    private static void mainLoop(int fromInc, int toExc, Set<Learner> learners, Consumer<Submission> consumer) {
        SubmissionPool sPool = new SubmissionPool();
        for (int i = fromInc; i < toExc; i++) {
            // i is the "current" firstSubmissionStartTick
            TimeTick curr = new TimeTick(i);
//            System.out.println("curr tick: " + curr);
            Iterator<Learner> iter = learners.iterator();
            while (iter.hasNext()) {
                Learner learner = iter.next();
                final Submission returned = learner.respondTo(curr, sPool);
                consumer.accept(returned);
            }
        }
//        System.out.println("number of un-reviewed submissions: " + sPool.size());
//        System.out.println("state of learners: ");
//        learners.stream().forEach(l -> System.out.println(l));
    }
}