package com.alvyn279.discord.strategy;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordCommandContext;
import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.exception.AccessDeniedException;
import com.alvyn279.discord.repository.dto.DeleteDiscordEventCommandDTO;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.utils.DiscordStringUtils;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Deletes multiple events by looking at the multiple delete
 * codes passed as args !delete-events [str] [str] [str] ...
 */
@Slf4j
public class DeleteMultipleDiscordEventsStrategy implements DeleteDiscordEventsStrategy {

    private final DiscordEventReactiveRepository discordEventReactiveRepository;

    @Inject
    public DeleteMultipleDiscordEventsStrategy(DiscordEventReactiveRepository discordEventReactiveRepository) {
        this.discordEventReactiveRepository = discordEventReactiveRepository;
    }

    @Override
    public Mono<Void> execute(DiscordCommandContext context) {
        final Flux<DiscordEvent> deletedDiscordEvents = Flux.concat(
            extractDeleteCodes(context.getTokens()).stream()
                .map(deleteCode -> discordEventReactiveRepository.deleteDiscordEvent(
                    DeleteDiscordEventCommandDTO.builder()
                        .guildId(context.getGuild().getId().asString())
                        .userId(context.getMessageCreateEvent().getMessage().getAuthor().orElseThrow().getId().asString())
                        .deleteCode(deleteCode)
                        .build())
                    .onErrorResume(AccessDeniedException.class, e -> {
                        log.error(e.getMessage());
                        context.getMessageCreateEvent().getMessage().getChannel()
                            .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                                BotMessages.attachDeleteAccessDeniedToEmbed(embedCreateSpec, e.getDiscordEvent()))
                            );
                        return Mono.empty();
                    })
                    .onErrorResume(throwable -> {
                        log.error(throwable.getMessage());
                        return Mono.empty();
                    })
                )
                .collect(Collectors.toList())
        );

        return deletedDiscordEvents
            .collect(Collectors.toList())
            .flatMap(discordEvents -> context.getMessageCreateEvent().getMessage().getChannel()
                .flatMap(messageChannel -> messageChannel.createEmbed(embedCreateSpec ->
                    BotMessages.attachDeleteMultipleConfirmationToEmbed(embedCreateSpec, discordEvents)
                ))
            )
            .then();
    }

    /**
     * Parses the list of string tokens to return the events to delete
     * (delete codes).
     *
     * @param tokens list of string tokens from command
     * @return list of delete code string tokens in order
     */
    private List<String> extractDeleteCodes(List<String> tokens) {
        // !delete-events [str] [str] [str] ...
        return tokens.stream()
            .skip(1)
            // sprinkle in some weak validation
            .filter(DiscordStringUtils::isDeleteCode)
            .collect(Collectors.toList());
    }
}