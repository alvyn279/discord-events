package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Default behaviour for the !list-events command.
 * Implements {@link ListDiscordEventsStrategy} by listing the upcoming
 * 5 events.
 */
public class ListDiscordEventsDefaultStrategy extends ListUpcomingDiscordEventsStrategy {

    private static final Integer DEFAULT_UPCOMING_LIMIT = 5;

    @Inject
    public ListDiscordEventsDefaultStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        super(discordEventReactiveRepository);
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        context.getTokens().add(Integer.toString(DEFAULT_UPCOMING_LIMIT));
        return super.execute(context);
    }
}
