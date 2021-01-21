package com.alvyn279.discord.async;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton scheduler that can schedule periodically-ran {@link EventsCheckerTask}
 * on a thread different than the main one.
 * <p>
 * Being a singleton, stateless commands from the discord context can easily refer
 * back to the task to cancel it.
 */
@Singleton
public class EventsCheckerScheduler {

    private final ScheduledExecutorService scheduledExecutorService;
    private Future<?> eventCheckerThread;

    @Inject
    public EventsCheckerScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.eventCheckerThread = null;
    }

    /**
     * Schedules the desired {@link Runnable} task at desired rate on the
     * scheduler. It sets this.eventCheckerThread {@link Future} that can
     * be used later to cancel the task.
     *
     * @param command      task definition
     * @param initialDelay initial delay
     * @param period       periodic rate
     * @param unit         time unit
     */
    public void scheduleAtFixedRate(Runnable command,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {
        eventCheckerThread = scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * Checks if it is safe to schedule a task on the scheduler.
     *
     * @return boolean
     */
    public boolean isSafeToStart() {
        return eventCheckerThread == null || eventCheckerThread.isDone();
    }

    /**
     * Checks if it is safe to cancel the task.
     *
     * @return boolean
     */
    public boolean isSafeToStop() {
        return !eventCheckerThread.isCancelled() && !eventCheckerThread.isDone();
    }

    /**
     * Issues a cancel request for the {@link Future}.
     */
    public void stopEventChecker() {
        eventCheckerThread.cancel(true);
    }
}
