package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.ListDiscordEventsCommandArgs;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Behaviour for the !my-events command.
 * Implements {@link ListDiscordEventsStrategy} by listing all the
 * events that have been created by the requesting user.
 */
@Slf4j
public class ListDiscordEventsForCurrentUserStrategy implements ListDiscordEventsStrategy {

    private static final String EMBED_TITLE = "Your Events";
    private final static String EMBED_DESCRIPTION = "Here are the events you created:";

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public ListDiscordEventsForCurrentUserStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        return Mono.just(context.getMessageCreateEvent().getMessage().getAuthor())
            .flatMap(optionalUser -> discordEventReactiveRepository.listDiscordEventCreatedByUser(
                ListDiscordEventsCommandArgs.builder()
                    .guildId(context.getGuild().getId().asString())
                    .userId(optionalUser.orElseThrow().getId().asString())
                    .build())
                .flatMap(discordEvents -> context.getMessageCreateEvent().getMessage().getChannel()
                    .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec -> {
                        embedCreateSpec
                            .setTitle(EMBED_TITLE)
                            .setDescription(EMBED_DESCRIPTION)
                            .setColor(Color.DARK_GOLDENROD)
                            .setTimestamp(Instant.now());

                        AtomicInteger eventCounter = new AtomicInteger(1);
                        discordEvents.forEach(discordEvent ->
                            BotMessages.DiscordEventSummaryFieldBuilder.builder()
                                .discordEvent(discordEvent)
                                .embedCreateSpec(embedCreateSpec)
                                .count(eventCounter.getAndIncrement())
                                .build()
                                .withNumeratedTitle()
                                .withEntityAndDeleteCodeDescription()
                                .buildField()
                        );
                    }))
                )
                .then()
            );
    }

    /**
     * Execute strategy from a {@link MessageCreateEvent}
     *
     * @param messageCreateEvent event initiated by user message
     * @return Mono<Void>
     */
    public Mono<Void> execute(MessageCreateEvent messageCreateEvent) {
        return messageCreateEvent.getGuild()
            .flatMap(guild -> execute(DiscordCommandContext.builder()
                .tokens(ImmutableList.of()) // no-op
                .guild(guild)
                .messageCreateEvent(messageCreateEvent)
                .build()
            ));
    }
}
