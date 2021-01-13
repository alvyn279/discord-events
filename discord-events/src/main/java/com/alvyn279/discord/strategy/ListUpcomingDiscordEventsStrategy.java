package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.ListDiscordEventsCommandArgs;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements {@link ListDiscordEventsStrategy} by listing all upcoming
 * discord events in DDB based on an upcoming limit.
 */
public class ListUpcomingDiscordEventsStrategy implements ListDiscordEventsStrategy {

    private final static String EMBED_TITLE = "Upcoming events";
    private final static String EMBED_DESCRIPTION = "Here are the upcoming events:";

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public ListUpcomingDiscordEventsStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        ListDiscordEventsCommandArgs commandArgs = ListDiscordEventsCommandArgs.builder()
            .guildId(context.getGuild().getId().asString())
            .upcomingLimit(Integer.parseInt(context.getTokens().get(1)))
            .currentDate(Instant.now())
            .build();

        return discordEventReactiveRepository.listDiscordEventsByUpcoming(commandArgs)
            .flatMap(discordEvents -> context.getMessageCreateEvent().getMessage().getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec -> {
                    embedCreateSpec
                        .setTitle(EMBED_TITLE)
                        .setDescription(EMBED_DESCRIPTION)
                        .setColor(Color.HOKI)
                        .setTimestamp(Instant.now());

                    // TODO: run reactions to counter to describe event
                    AtomicInteger eventCounter = new AtomicInteger(1);
                    discordEvents.forEach(discordEvent ->
                        BotMessages.createListDiscordEventItemSummary(
                            embedCreateSpec,
                            discordEvent,
                            eventCounter.getAndIncrement()
                        )
                    );
                }))
            ).then();
    }
}
