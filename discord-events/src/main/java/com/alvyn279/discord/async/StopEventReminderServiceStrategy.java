package com.alvyn279.discord.async;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Concrete strategy that attemps to cancel a {@link EventCheckerTask} through
 * the {@link EventsCheckerScheduler} based on different states of the latter.
 */
public class StopEventReminderServiceStrategy implements EventReminderServiceStrategy {

    private final EventsCheckerScheduler eventsCheckerScheduler;

    @Inject
    public StopEventReminderServiceStrategy(EventsCheckerScheduler eventsCheckerScheduler) {
        this.eventsCheckerScheduler = eventsCheckerScheduler;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        return context.getMessageCreateEvent().getMessage().getChannel()
            .flatMap(messageChannel -> {
                if (!eventsCheckerScheduler.isSafeToStop()) {
                    return messageChannel.createEmbed(BotMessages::eventRemindersAlreadyOff);
                }

                eventsCheckerScheduler.stopEventChecker();

                return messageChannel.createEmbed(BotMessages::eventRemindersTurnedOff);
            })
            .then();
    }
}
