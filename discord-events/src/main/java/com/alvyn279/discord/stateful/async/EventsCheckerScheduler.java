package com.alvyn279.discord.stateful.async;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton scheduler that can schedule periodically-ran {@link EventsCheckerTask}
 * on a thread different than the main one. It holds a thread pool to distribute
 * threads amongst the guilds that opt in the events reminder feature.
 * <p>
 * Being a singleton, stateless commands from the discord context can easily refer
 * back to the task to cancel it.
 */
@Singleton
public class EventsCheckerScheduler {

    private final ScheduledExecutorService scheduledExecutorService;
    private final Map<String, EventsCheckerExecution> executions;

    /**
     * Tuple class to hold the execution of an events-
     * checker task. It holds the task executed and
     * its completion object for cancelling purposes.
     */
    @Builder
    @Data
    private static class EventsCheckerExecution {
        private EventsCheckerTask task;
        private Future<?> eventCompletion;
    }

    @Inject
    public EventsCheckerScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.executions = new HashMap<>();
    }

    /**
     * Returns the event checker task that was scheduled.
     *
     * @param guildId guild id snowflake
     * @return EventsCheckerTask nullable/runnable task
     */
    public EventsCheckerTask getGuildEventsCheckerTask(String guildId) {
        if (!executions.containsKey(guildId)) return null;
        return executions.get(guildId).getTask();
    }

    /**
     * Checks if it is safe to schedule a task for the guild on the scheduler.
     *
     * @param guildId guild id snowflake
     * @return boolean
     */
    public boolean isSafeToStart(String guildId) {
        if (!executions.containsKey(guildId)) return true;
        Future<?> eventCompletion = executions.get(guildId).getEventCompletion();
        return eventCompletion == null || eventCompletion.isDone();
    }

    /**
     * Checks if it is safe to cancel the task for the guild.
     *
     * @param guildId guild id snowflake
     * @return boolean
     */
    public boolean isSafeToStop(String guildId) {
        if (!executions.containsKey(guildId)) return false;
        Future<?> eventCompletion = executions.get(guildId).getEventCompletion();
        return !eventCompletion.isCancelled() && !eventCompletion.isDone();
    }

    /**
     * Schedules the desired {@link EventsCheckerTask} task at desired rate
     * on the scheduler. It sets this.eventCheckerThread {@link Future} that
     * can be used later to cancel the task.
     *
     * @param guildId      guild id snowflake
     * @param task         task definition
     * @param initialDelay initial delay
     * @param period       periodic rate
     * @param unit         time unit
     */
    public void scheduleAtFixedRate(String guildId,
                                    EventsCheckerTask task,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {
        executions.put(guildId, EventsCheckerExecution.builder()
            .eventCompletion(scheduledExecutorService
                .scheduleAtFixedRate(task, initialDelay, period, unit)
            )
            .task(task)
            .build()
        );
    }

    /**
     * Issues a cancel request for the execution's {@link Future}.
     * Must be called after a successful `this.isSafeToStop()` call.
     *
     * @param guildId guild id snowflake
     */
    public void stopEventChecker(String guildId) {
        executions.get(guildId).getEventCompletion().cancel(true);
        executions.get(guildId).setTask(null);
    }
}
