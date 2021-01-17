package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.GuildUtils;
import com.alvyn279.discord.repository.ListDiscordEventsCommandArgs;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.utils.DateUtils;
import com.google.inject.Inject;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implements {@link ListDiscordEventsStrategy} by listing all events
 * in DDB based on a date range.
 */
public class ListDiscordEventsInDateRangeStrategy implements ListDiscordEventsStrategy {

    private final static String EMBED_TITLE = "Events";
    private final static String EMBED_DESCRIPTION_FORMAT_STR = "Here are the events between %1$s and %2$s:";

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public ListDiscordEventsInDateRangeStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        ListDiscordEventsCommandArgs commandArgs = ListDiscordEventsCommandArgs.builder()
            .guildId(context.getGuild().getId().asString())
            .startDateTime(DateUtils.fromDate(context.getTokens().get(1)))
            .endDateTime(DateUtils.fromDate(context.getTokens().get(2)))
            .build();

        return discordEventReactiveRepository.listDiscordEventsByDateTimeRange(commandArgs)
            .flatMap(discordEvents -> context.getMessageCreateEvent().getMessage().getChannel()
                .flatMap(messageChannel -> GuildUtils.retrieveGuildUsers(context.getGuild())
                    .flatMap(usersMap -> messageChannel.createEmbed(embedCreateSpec -> {
                        embedCreateSpec
                            .setTitle(EMBED_TITLE)
                            .setDescription(String.format(
                                EMBED_DESCRIPTION_FORMAT_STR,
                                context.getTokens().get(1),
                                context.getTokens().get(2)
                            ))
                            .setColor(Color.DEEP_LILAC)
                            .setTimestamp(Instant.now());

                        BotMessages.attachDiscordEventsListToEmbed(embedCreateSpec, discordEvents, usersMap);
                    })))
            ).then();
    }
}
