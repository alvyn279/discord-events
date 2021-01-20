package com.alvyn279.discord.async;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.Emoji;
import com.alvyn279.discord.domain.GuildUtils;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.repository.ListDiscordEventsCommandArgs;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import lombok.Builder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Task ran on a thread that will check whether there is an
 * event that is coming up soon. It uses the DDB repo and Discord
 * context to notify the subscribed channel.
 */
@Builder
public class EventCheckerTask implements Runnable {

    private static final String REMINDER_TITLE = "Events Happening Soon!";
    // TODO: insert @here mention for notifs
    private static final String REMINDER_DESCRIPTION = "Just a reminder that" +
        " these events are happening soon:";
    private static final Integer EVENT_CHECK_TIME_DELTA_IN_MINUTES = 15;

    private final DiscordEventReactiveRepository repository;
    private final MessageChannel messageChannel;
    private final Guild guild;

    // We keep an in-memory cache of string IDs of {@link DiscordEvent}s
    // that have already been notified for. This allows to limit the
    // reminders for that event to 1 to the subscribed message channel.
    private static final Set<String> notifiedDiscordEvents;

    static {
        notifiedDiscordEvents = new HashSet<>();
    }

    public void run() {
        repository.listDiscordEventsByUpcomingWithTimeLimit(ListDiscordEventsCommandArgs.builder()
            .guildId(guild.getId().asString())
            .currentDateTime(Instant.now())
            .upcomingTimeLimit(Duration.ofMinutes(EVENT_CHECK_TIME_DELTA_IN_MINUTES))
            .build()
        )
            .flatMap(discordEvents -> Mono.just(discordEvents.stream()
                .filter(discordEvent -> !notifiedDiscordEvents.contains(discordEvent.getMessageId()))
                .collect(Collectors.toList())))
            .flatMap(unNotifiedDiscordEvents -> {
                if (!unNotifiedDiscordEvents.isEmpty()) {
                    notifiedDiscordEvents.addAll(unNotifiedDiscordEvents.stream()
                        .map(DiscordEvent::getMessageId)
                        .collect(Collectors.toList())
                    );
                    return GuildUtils.retrieveGuildUsers(guild)
                        .flatMap(usersMap -> messageChannel.createEmbed(embedCreateSpec -> {
                            embedCreateSpec
                                .setTitle(String.format(
                                    BotMessages.EMOJI_AND_TITLE_FORMAT_STR,
                                    Emoji.ALARM_CLOCK,
                                    REMINDER_TITLE))
                                .setDescription(REMINDER_DESCRIPTION)
                                .setColor(Color.ENDEAVOUR)
                                .setTimestamp(Instant.now());

                            BotMessages.attachDiscordEventsListToEmbed(
                                embedCreateSpec, unNotifiedDiscordEvents, usersMap);
                        }));
                }
                return Mono.empty();
            })
            .then()
            .subscribe();
    }
}
