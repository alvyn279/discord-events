package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.exception.AccessDeniedException;
import com.alvyn279.discord.repository.DeleteDiscordEventCommandArgs;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Deletes a single DiscordEvent by looking at the one argument given with
 * !delete-events [str], which corresponds to the event's message id.
 */
@Slf4j
public class DeleteSingleDiscordEventStrategy implements DeleteDiscordEventsStrategy {

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public DeleteSingleDiscordEventStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        return discordEventReactiveRepository.deleteDiscordEvent(DeleteDiscordEventCommandArgs.builder()
            .guildId(context.getGuild().getId().asString())
            .userId(context.getMessageCreateEvent().getMessage().getAuthor().orElseThrow().getId().asString())
            .deleteCode(context.getTokens().get(1))
            .build()
        )
            .flatMap(discordEvent -> context.getMessageCreateEvent().getMessage().getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                    BotMessages.attachDeleteConfirmationToEmbed(embedCreateSpec, discordEvent)))
            )
            .onErrorResume(AccessDeniedException.class, e -> {
                log.error(e.getMessage());
                return context.getMessageCreateEvent().getMessage().getChannel()
                    .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                        BotMessages.attachDeleteAccessDeniedToEmbed(embedCreateSpec, e.getDiscordEvent()))
                    );
            })
            .then();
    }
}
