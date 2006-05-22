package de.uni_koeln.spinfo.strings.algo.lca;

/**
 * Simple 'StopWatch' class to accumulate timing info
 * 
 * @author A. G. McDowell (original version)
 */
public class Swatch {
    /** Time when stopwatch last started */
    private long whenStarted;

    /**
     * Time accumulated so far. Same type as Java's time/calendar stuff, and
     * 2^64 millis is a LONG time, so no overflow problems
     */
    private long sofar;

    /** Whether stopwatch started */
    boolean started;

    /**
     * Create a Swatch, which will be stopped with 0 time accumulated so far
     */
    public Swatch() {
        started = false;
        sofar = 0;
    }

    /**
     * Start the Swatch, throwing an exception if already running.
     * 
     * @exception IllegalStateException
     *                if already running
     */
    public void start() {
        if (started)
            throw new IllegalStateException("Tried to start running Swatch");
        whenStarted = System.currentTimeMillis();
        started = true;
    }

    /**
     * Stop the Swatch, throwing an exception if already stopped.
     * 
     * @exception IllegalStateException
     *                if already stopped
     */
    public void stop() {
        if (!started)
            throw new IllegalStateException("Tried to stop stopped Swatch");
        sofar += System.currentTimeMillis() - whenStarted;
        started = false;
    }

    /**
     * Return the number of milliseconds accumulated so far. This is safe
     * because 2^64 milliseconds is a huge amount of time, and if this is in
     * danger of overflow, so is System.currentTimeMillis() so you can't trust
     * Java's timers anyway.
     * 
     * @return the number of milliseconds accumulated so far
     */
    public long millis() {
        if (!started)
            return sofar;
        return sofar + System.currentTimeMillis() - whenStarted;
    }

    /**
     * return whether running or not
     * 
     * @return true iff running
     */
    public boolean isRunning() {
        return started;
    }

    /**
     * display the current state
     * 
     * @return a string saying whether running or not and the amount of time
     *         accumulated so far in seconds (this will include values after the
     *         decimal point to the grain of milliseconds - perhaps more
     *         depending on Java's enthusiasm for decimal places)
     */
    public String toString() {
        return "running: " + isRunning() + " time: " + millis() * 1.0e-3;
    }
}
