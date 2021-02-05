package com.alvyn279.discord.strategy;

import com.alvyn279.discord.stateful.async.EventsCheckerScheduler;
import com.alvyn279.discord.stateful.async.EventsCheckerTask;
import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Concrete strategy that gives the status of the events reminder {@link EventsCheckerTask}
 * through the {@link EventsCheckerScheduler}.
 */
public class StatusEventReminderServiceStrategy implements EventReminderServiceStrategy {

    private final EventsCheckerScheduler eventsCheckerScheduler;

    @Inject
    public StatusEventReminderServiceStrategy(EventsCheckerScheduler eventsCheckerScheduler) {
        this.eventsCheckerScheduler = eventsCheckerScheduler;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        return context.getMessageCreateEvent().getMessage().getChannel()
            .flatMap(messageChannel -> {
                String guildId = context.getGuild().getId().asString();

                if (eventsCheckerScheduler.getGuildEventsCheckerTask(guildId) != null) {
                    return eventsCheckerScheduler.getGuildEventsCheckerTask(guildId)
                        .getSubscribedChannel().getRestChannel().getData()
                        .flatMap(channelData -> messageChannel.createEmbed(embedCreateSpec ->
                            BotMessages.eventRemindersOnStatus(embedCreateSpec, channelData.name().get())
                        ));
                }

                return messageChannel.createEmbed(BotMessages::eventRemindersOff);
            })
            .then();
    }
}
