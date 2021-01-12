package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Implements {@link ListDiscordEventsStrategy} by listing all upcoming
 * discord events in DDB based on an upcoming limit.
 */
public class ListUpcomingDiscordEventsStrategy implements ListDiscordEventsStrategy {

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public ListUpcomingDiscordEventsStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        // TODO
        return Mono.empty();
    }
}
