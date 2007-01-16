package de.uni_koeln.spinfo.strings.algo.lca;

/**
 * This is a cycle detector based on Exercise 7 of section 3.1 of Volume 2 of
 * Knuth's "The Art of Computer Programming". This is guaranteed to detect an
 * infinite cycle, and may or may not spot a transient repeat.
 * 
 * @author A. G. McDowell (original version)
 */
public class CycleDetector {
    /**
     * Holds an infrequently changed pointer. We detect that we are in a cycle
     * when this object is seen again. The time between pointer changes
     * repeatedly doubles, so even very long cycles will eventually be caught,
     * but the pointer does change so even cycles that start only after a long
     * time will eventually be caught.
     */
    private Object slow;

    /** Steps from last change to next change */
    private long waitSteps;

    /** Steps remaining to wait */
    private long toStep;

    /** Standard constructor */
    public CycleDetector() {
        slow = null;
        waitSteps = 1;
        toStep = 1;
    }

    /**
     * Check a new pointer, which must not be null
     * 
     * @param o
     *            and object to check against a previous object
     * @exception NullPointerException
     *                if o is null
     * @exception IllegalStateException
     *                guaranteed eventually if the stream of objects gets stuck
     *                in an infinite loop: perhaps also thrown for shorter
     *                repeats
     */
    public void check(Object o) throws NullPointerException,
            IllegalStateException {
        try {
            // System.err.println("Check " + o + " vs " + slow);
            if (o.equals(slow))
                throw new IllegalStateException("Repeated object");
            if (--toStep <= 0) {
                slow = o;
                waitSteps = waitSteps * 2;
                toStep = waitSteps;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
