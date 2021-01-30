package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.GuildUtils;
import com.alvyn279.discord.repository.dto.ListDiscordEventsCommandDTO;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.utils.DateUtils;
import com.google.inject.Inject;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implements {@link ListDiscordEventsStrategy} by listing all events
 * in DDB based on a given date.
 */
public class ListDiscordEventsOnDateStrategy implements ListDiscordEventsStrategy {

    private final static String EMBED_TITLE = "Events";
    private final static String EMBED_DESCRIPTION_FORMAT_STR = "Here are the events on %1$s:";

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public ListDiscordEventsOnDateStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        Instant targetDate = DateUtils.fromDate(context.getTokens().get(1));
        ListDiscordEventsCommandDTO commandArgs = ListDiscordEventsCommandDTO.builder()
            .guildId(context.getGuild().getId().asString())
            .startDateTime(targetDate)
            .endDateTime(DateUtils.nextDaySameTime(targetDate))
            .build();

        return discordEventReactiveRepository.listDiscordEventsByDateTimeRange(commandArgs)
            .flatMap(discordEvents -> context.getMessageCreateEvent().getMessage().getChannel()
                .flatMap(messageChannel -> GuildUtils.retrieveGuildUsers(context.getGuild())
                    .flatMap(usersMap -> messageChannel.createEmbed(embedCreateSpec -> {
                        embedCreateSpec
                            .setTitle(EMBED_TITLE)
                            .setDescription(String.format(
                                EMBED_DESCRIPTION_FORMAT_STR,
                                context.getTokens().get(1)
                            ))
                            .setColor(Color.DEEP_SEA)
                            .setTimestamp(Instant.now());

                        BotMessages.attachDiscordEventsListToEmbed(embedCreateSpec, discordEvents, usersMap);
                    })))
            ).then();
    }
}
