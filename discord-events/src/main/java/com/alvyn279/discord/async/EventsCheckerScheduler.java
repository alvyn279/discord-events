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
    private EventsCheckerTask currentTask;

    @Inject
    public EventsCheckerScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.eventCheckerThread = null;
        this.currentTask = null;
    }

    /**
     * Returns the event checker task that was scheduled.
     *
     * @return EventsCheckerTask runnable task
     */
    public EventsCheckerTask getCurrentTask() {
        return currentTask;
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
     * Schedules the desired {@link EventsCheckerTask} task at desired rate
     * on the scheduler. It sets this.eventCheckerThread {@link Future} that
     * can be used later to cancel the task.
     *
     * @param task      task definition
     * @param initialDelay initial delay
     * @param period       periodic rate
     * @param unit         time unit
     */
    public void scheduleAtFixedRate(EventsCheckerTask task,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {
        eventCheckerThread = scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
        currentTask = task;
    }

    /**
     * Issues a cancel request for the {@link Future}.
     */
    public void stopEventChecker() {
        eventCheckerThread.cancel(true);
        currentTask = null;
    }
}
