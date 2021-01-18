package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.utils.DateUtils;
import com.google.inject.Inject;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

/**
 * Implements {@link CreateDiscordEventStrategy} by writing a discord event
 * to DDB with all its properties.
 */
public class CreateFullDiscordEventStrategy implements CreateDiscordEventStrategy {

    public static final String DEFAULT_CREATOR = "BOT";

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public CreateFullDiscordEventStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        MessageCreateEvent event = context.getMessageCreateEvent();
        Guild guild = context.getGuild();
        List<String> tokens = context.getTokens();
        Message msg = event.getMessage();
        // Support optional descriptions. We cannot adopt a null
        // attribute model for {@link DiscordEvent} because Discord
        // does support setting null attributes for embeds.
        String desc = (tokens.size() == 5 ? tokens.get(4) : "");

        DiscordEvent discordEvent = DiscordEvent.builder()
            .guildId(guild.getId().asString())
            .timestamp(DateUtils.fromDateAndTime(tokens.get(2), tokens.get(3)))
            .createdBy(msg.getAuthor().isPresent() ?
                msg.getAuthor().get().getId().asString() : DEFAULT_CREATOR)
            .messageId(msg.getId().asString())
            .name(tokens.get(1))
            .description(desc)
            .build();

        //TODO: check existence before writing

        return discordEventReactiveRepository.saveDiscordEvent(discordEvent)
            // Announce newly created event
            .flatMap(discordEventRes -> msg.getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                    embedCreateSpec
                        .setTitle(discordEventRes.getName())
                        .setDescription(discordEventRes.getDescription())
                        .setColor(Color.GREEN)
                        .addField(
                            DiscordEvent.CREATED_BY_LABEL,
                            msg.getAuthor().get().getUsername(),
                            true)
                        .addField(
                            DiscordEvent.DATETIME_LABEL,
                            DateUtils.prettyPrintInstantInLocalTimezone(discordEventRes.getTimestamp()),
                            true)
                        .setTimestamp(Instant.now())
                ))
            ).then();
    }
}

