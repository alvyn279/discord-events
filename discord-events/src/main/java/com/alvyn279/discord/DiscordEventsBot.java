package com.alvyn279.discord;

import com.alvyn279.discord.domain.CommandReaction;
import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.provider.RootModule;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.repository.DiscordEventReactiveRepositoryImpl;
import com.alvyn279.discord.utils.Constants;
import com.alvyn279.discord.utils.DateUtils;
import com.alvyn279.discord.utils.EnvironmentUtils;
import com.alvyn279.discord.utils.StringUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Discord Bot implementation that schedules events.
 * <p>
 * DiscordEvents Bot entrypoint for the long-running process.
 */
@Slf4j
public class DiscordEventsBot {

    private static final String DISCORD_BOT_TOKEN_KEY = "DISCORD_BOT_TOKEN";
    private static final String DISCORD_COMMAND_PREFIX = "!";
    private static final String DISCORD_EVENTS_COMMAND_PING = "ping";
    private static final String DISCORD_EVENTS_COMMAND_CREATE_EVENT = "create-event";
    private static final String BOT = "BOT";

    private static final Map<String, CommandReaction> commands = new HashMap<>();

    static {
        final Injector injector = Guice.createInjector(new RootModule());
        final DiscordEventReactiveRepository discordEventRepo = injector.getInstance(
            DiscordEventReactiveRepositoryImpl.class
        );

        commands.put(DISCORD_EVENTS_COMMAND_PING, event ->
            event.getMessage().getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(
                    embedCreateSpec -> {
                        embedCreateSpec
                            .setTitle("Pong mf!")
                            .setDescription("tt le monde est sus. J'suis en train d'apprendre reactive programming")
                            .setTimestamp(Instant.now());
                        Constants.CHANNEL_USERS
                            .forEach(user -> embedCreateSpec.addField(user.getName(), user.getRole(), true));
                    }))
                .then()
        );

        commands.put(DISCORD_EVENTS_COMMAND_CREATE_EVENT, event -> event.getGuild()
            .flatMap(guild -> Mono.just(event.getMessage().getContent())
                .flatMap(s -> {
                    // COMMAND FORMAT: !create-event “Event title” 2021/02/02 19:00 “Event description”
                    // Parse tokens and create discord-events object
                    List<String> tokens = StringUtils.tokenizeCommandAndArgs(s);
                    Message msg = event.getMessage();
                    DiscordEvent discordEvent = DiscordEvent.builder()
                        .guildId(guild.getId().asString())
                        .timestamp(DateUtils.fromDateAndTime(tokens.get(2), tokens.get(3)))
                        .createdBy(msg.getAuthor().isPresent() ?
                            msg.getAuthor().get().getId().asString() : BOT)
                        .messageId(msg.getId().asString())
                        .name(tokens.get(1))
                        .description(tokens.get(4))
                        .build();

                    // Write discord event to DDB
                    return discordEventRepo.saveDiscordEvent(discordEvent)
                        // Announce newly created event
                        .flatMap(discordEventRes -> msg.getChannel()
                            .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                                embedCreateSpec
                                    .setTitle(discordEventRes.getName())
                                    .setDescription(discordEventRes.getDescription())
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
                        );
                })
                .then())
        );
    }

    public static void main(String[] args) {
        final String discordClientToken = EnvironmentUtils.getEnvVar(DISCORD_BOT_TOKEN_KEY);
        final GatewayDiscordClient client = DiscordClientBuilder
            .create(discordClientToken)
            .build()
            .login()
            .block();

        // Create the command handler
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(messageCreateEvent -> Mono.just(messageCreateEvent.getMessage().getContent())
                .flatMap(messageContent -> Flux.fromIterable(commands.entrySet())
                    .filter(entry -> messageContent.startsWith(String.format(
                        "%1$s%2$s", DISCORD_COMMAND_PREFIX, entry.getKey())))
                    .flatMap(entry -> entry.getValue()
                        .execute(messageCreateEvent)
                        // We retry in the case of a cold request where
                        // reactor.netty.http.client.PrematureCloseException is thrown.
                        // This should still be fine for any DDB operations
                        // if consistency checks are made at write-time.
                        .retry(1)
                        .onErrorResume(throwable -> {
                            log.error("Error with discord-events", throwable);
                            return Mono.empty();
                        }))
                    .next()))
            .subscribe();

        client.onDisconnect().block();
    }

    /**
     * Test function used for placeholder test suite.
     *
     * @return 1
     */
    public static int someFunction() {
        return 1;
    }
}
