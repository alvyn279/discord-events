package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.google.inject.Inject;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Implementation of {@link DiscordEventRepository} where the repository uses
 * an async DDB client for its operations.
 *
 * It implements read/writes in a reactive manner.
 */
public class DiscordEventRepositoryImpl implements DiscordEventRepository {

    private static final String DISCORD_EVENTS_TABLE_NAME = "DiscordEvents";

    private final DynamoDbAsyncClient client;

    @Inject
    DiscordEventRepositoryImpl(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent) {
        PutItemRequest putDiscordEventRequest = PutItemRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .item(DiscordEvent.toDDBItem(discordEvent))
            .build();
        // TODO: Finish reactive integration
        return null;
    }
}
