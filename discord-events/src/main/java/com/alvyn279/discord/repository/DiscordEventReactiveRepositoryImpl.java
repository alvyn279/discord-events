package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Implementation of {@link DiscordEventReactiveRepository} where the repository uses
 * an async DDB client for its operations.
 * <p>
 * It implements read/writes in a reactive manner.
 */
@Slf4j
public class DiscordEventReactiveRepositoryImpl implements DiscordEventReactiveRepository {

    // TODO: get discord events table name from env vars
    private static final String DISCORD_EVENTS_TABLE_NAME = "DiscordEventsStack-DiscordEventsTable9A201A75-SX8T2BAK2N8Y";

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

        return Mono.fromCompletionStage(client.putItem(putDiscordEventRequest))
            .flatMap(putItemResponse -> {
                SdkHttpResponse httpResponse = putItemResponse.sdkHttpResponse();
                log.info(String.format("Wrote to DDB event %s: %s %s",
                    discordEvent.getEventId(),
                    httpResponse.statusCode(),
                    httpResponse.statusText().isPresent() ? httpResponse.statusText().get() : ""));
                return Mono.just(discordEvent);
            })
            .onErrorResume(throwable -> {
                log.error("Error writing to DDB", throwable);
                return Mono.just(discordEvent);
            });
    }
}
