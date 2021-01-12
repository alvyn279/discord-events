package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.ListDiscordEventsCommandArgs;
import com.alvyn279.discord.utils.EnvironmentUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DiscordEventReactiveRepository} where the repository uses
 * an async DDB client for its operations.
 * <p>
 * It implements read/writes in a reactive manner.
 */
@Slf4j
@Builder
public class DiscordEventReactiveRepositoryImpl implements DiscordEventReactiveRepository {

    private static final String DISCORD_EVENTS_TABLE_NAME_KEY = "DISCORD_EVENTS_TABLE_NAME";
    private static final String DISCORD_EVENTS_TABLE_NAME = EnvironmentUtils.getEnvVar(DISCORD_EVENTS_TABLE_NAME_KEY);

    private final DynamoDbAsyncClient client;

    @Override
    public Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent) {
        PutItemRequest putDiscordEventRequest = PutItemRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .item(DiscordEvent.toDDBItem(discordEvent))
            .build();

        return Mono.fromCompletionStage(client.putItem(putDiscordEventRequest))
            .flatMap(putItemResponse -> {
                SdkHttpResponse httpResponse = putItemResponse.sdkHttpResponse();
                log.info("Wrote to DDB event {}: {} {}",
                    discordEvent.getMessageId(),
                    httpResponse.statusCode(),
                    httpResponse.statusText().isPresent() ? httpResponse.statusText().get() : "");
                return Mono.just(discordEvent);
            })
            .onErrorResume(throwable -> {
                log.error("Error writing to DDB", throwable);
                return Mono.just(discordEvent);
            });
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByUpcoming(ListDiscordEventsCommandArgs args) {
        // TODO: check input.guildId, input.currentDate, input.upcomingLimit

        Map<String, String> expressionAttributesNames = ImmutableMap.of(
            "#guildId", DiscordEvent.PARTITION_KEY,
            "#datetimeCreatedBy", DiscordEvent.SORT_KEY
        );

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
            ":guildIdValue", AttributeValue.builder().s(args.getGuildId()).build(),
            ":datetimeCreatedByValue", AttributeValue.builder().s(args.getCurrentDate().toString()).build()
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .keyConditionExpression("#guildId = :guildIdValue and #datetimeCreatedBy >= :datetimeCreatedByValue")
            .limit(args.getUpcomingLimit())
            .expressionAttributeNames(expressionAttributesNames)
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return Mono.fromCompletionStage(client.query(queryRequest))
            .flatMap(queryResponse -> {
                log.info("Read from DDB table: {} items", queryResponse.count());
                return Mono.just(queryResponse.items().stream()
                    .map(DiscordEvent::fromDDBMap)
                    .collect(Collectors.toList())
                );
            })
            .onErrorResume(throwable -> {
                log.error("Error reading from DDB", throwable);
                return Mono.error(throwable);
            });
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByDate(ListDiscordEventsCommandArgs input) {
        // TODO: implement
        return null;
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByDateRange(ListDiscordEventsCommandArgs input) {
        // TODO: implement
        return null;
    }
}
