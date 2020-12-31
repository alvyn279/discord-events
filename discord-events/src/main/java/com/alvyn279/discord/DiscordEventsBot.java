package com.alvyn279.discord;

import com.alvyn279.discord.domain.Command;
import com.alvyn279.discord.utils.Constants;
import com.alvyn279.discord.utils.EnvironmentUtils;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * A Discord Bot implementation that schedules events.
 * <p>
 * DiscordEvents Bot entrypoint for the long-running process.
 */
public class DiscordEventsBot {

    public static final String DISCORD_EVENTS_COMMAND_PING = "ping";

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put(DISCORD_EVENTS_COMMAND_PING, event ->
            event.getMessage().getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(
                    embedCreateSpec -> {
                        embedCreateSpec
                            .setTitle("Pong mf!")
                            .setDescription("tt le monde est sus. J'suis en train d'apprendre reactive programming")
                            .setTimestamp(Instant.now());
                        Constants.CHANNEL_USERS
                            .forEach(user -> embedCreateSpec.addField(user.name, user.role, true));
                    }
                ).then())
        );
    }

    public static void main(String[] args) {
        final String discordClientToken = EnvironmentUtils.getDiscordClientTokenFromEnvVars();
        final GatewayDiscordClient client = DiscordClientBuilder
            .create(discordClientToken)
            .build()
            .login()
            .block();

        // Create the command handler
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(messageCreateEvent -> Mono.just(messageCreateEvent.getMessage().getContent())
                .flatMap(messageContent -> Flux.fromIterable(commands.entrySet())
                    .filter(entry -> messageContent.startsWith(String.format("!%s", entry.getKey())))
                    .flatMap(entry -> entry.getValue().execute(messageCreateEvent))
                    .next()))
            .subscribe(); // consumes the flux stream

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
