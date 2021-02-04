package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.repository.dto.ListDiscordEventsCommandDTO;
import com.google.inject.Inject;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Lists the events that one can attend and provide emoji reactions
 * for users to interact with.
 * <p>
 * Users can only attend events up the limit supported by numbered
 * emojis (10).
 */
public class AttendDiscordEventStrategy {

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    private static final Integer REACTION_LIMIT = 10;

    @Inject
    public AttendDiscordEventStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    public Mono<Void> execute(DiscordCommandContext context) {
        Guild guild = context.getGuild();
        MessageCreateEvent event = context.getMessageCreateEvent();
        Message msg = event.getMessage();
        ListDiscordEventsCommandDTO dto = ListDiscordEventsCommandDTO.builder()
            .guildId(guild.getId().asString())
            .currentDateTime(Instant.now())
            .upcomingLimit(REACTION_LIMIT)
            .build();

        return discordEventReactiveRepository.listDiscordEventsByUpcomingWithLimit(dto)
            .flatMap(discordEvents -> msg.getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                    BotMessages.attachAttendableDiscordEvents(embedCreateSpec, discordEvents)
                ))
                .flatMap(message -> {
                    // TODO: add message reactions (1 to length of discord events)
                    // TODO: store message id, discord events in that order in memory.
                    return Mono.empty();
                })
            );
    }
}

