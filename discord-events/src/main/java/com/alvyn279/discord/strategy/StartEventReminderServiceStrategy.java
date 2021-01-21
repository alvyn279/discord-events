package com.alvyn279.discord.strategy;

import com.alvyn279.discord.async.EventCheckerTask;
import com.alvyn279.discord.async.EventsCheckerScheduler;
import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * Concrete strategy that starts a {@link EventCheckerTask} through the {@link EventsCheckerScheduler}
 * based on different states of the latter.
 */
public class StartEventReminderServiceStrategy implements EventReminderServiceStrategy {

    private static final int EVENT_CHECK_INITIAL_DELAY_IN_SECONDS = 0;
    private static final int EVENT_CHECK_INTERVAL_IN_SECONDS = 60;

    private final DiscordEventReactiveRepository discordEventReactiveRepository;
    private final EventsCheckerScheduler eventsCheckerScheduler;

    @Inject
    public StartEventReminderServiceStrategy(DiscordEventReactiveRepository discordEventReactiveRepository,
                                             EventsCheckerScheduler eventsCheckerScheduler) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
        this.eventsCheckerScheduler = eventsCheckerScheduler;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        return context.getMessageCreateEvent().getMessage().getChannel()
            .flatMap(messageChannel -> {
                if (!eventsCheckerScheduler.isSafeToStart()) {
                    return messageChannel.createEmbed(BotMessages::eventRemindersAlreadyOn);
                }

                eventsCheckerScheduler.scheduleAtFixedRate(EventCheckerTask.builder()
                        .messageChannel(messageChannel)
                        .guild(context.getGuild())
                        .repository(discordEventReactiveRepository)
                        .build(),
                    EVENT_CHECK_INITIAL_DELAY_IN_SECONDS, EVENT_CHECK_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);

                return messageChannel.createEmbed(BotMessages::eventRemindersTurnedOn);
            })
            .then();
    }
}
