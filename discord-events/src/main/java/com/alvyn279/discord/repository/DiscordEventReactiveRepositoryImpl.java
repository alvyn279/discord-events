package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Implementation of {@link DiscordEventReactiveRepository} where the repository uses
 * an async DDB client for its operations.
 *
 * It implements read/writes in a reactive manner.
 */
@Slf4j
public class DiscordEventReactiveRepositoryImpl implements DiscordEventReactiveRepository {

    private static final String DISCORD_EVENTS_TABLE_NAME = "DiscordEvents";

    private final DynamoDbAsyncClient client;

    @Inject
    DiscordEventReactiveRepositoryImpl(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent) {
        PutItemRequest putDiscordEventRequest = PutItemRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .item(DiscordEvent.toDDBItem(discordEvent))
            .build();

        return Mono.fromFuture(client.putItem(putDiscordEventRequest))
            .map(putItemResponse -> discordEvent)
            .onErrorResume(throwable -> {
                log.error("Error writing to DDB", throwable);
                return Mono.just(discordEvent);
            });
    }
}
