package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.repository.dto.DeleteDiscordEventCommandDTO;
import com.alvyn279.discord.repository.dto.DiscordEventsCommandDTO;
import com.alvyn279.discord.repository.dto.ListDiscordEventsCommandDTO;
import com.alvyn279.discord.exception.AccessDeniedException;
import com.alvyn279.discord.utils.EnvironmentUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alvyn279.discord.utils.DiscordStringUtils.EMPTY;

/**
 * Implementation of {@link DiscordEventReactiveRepository} where the repository uses
 * an async DDB client for its operations.
 * <p>
 * It implements read/writes in a reactive manner.
 */
@Slf4j
@Builder
public class DiscordEventReactiveRepositoryImpl implements DiscordEventReactiveRepository {

    private static final String DISCORD_EVENTS_TABLE_NAME = EnvironmentUtils.getDDBTableName();

    private final DynamoDbAsyncClient client;

    @Override
    public Mono<DiscordEvent> deleteDiscordEvent(DeleteDiscordEventCommandDTO args) {

        return getDiscordEventByMessageId(args, args.getDeleteCode())
            .flatMap(discordEvent -> {
                Map<String, AttributeValue> keysAttributeValues = ImmutableMap.of(
                    DiscordEvent.PARTITION_KEY, AttributeValue.builder().s(discordEvent.getGuildId()).build(),
                    DiscordEvent.SORT_KEY, AttributeValue.builder().s(discordEvent.datetimeCreatedBy()).build()
                );

                DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                    .tableName(DISCORD_EVENTS_TABLE_NAME)
                    .key(keysAttributeValues)
                    .build();

                return Mono.fromCompletionStage(client.deleteItem(deleteItemRequest))
                    .flatMap(queryResponse -> {
                        SdkHttpResponse httpResponse = queryResponse.sdkHttpResponse();
                        log.info("Deleted DDB event {}: {} {}",
                            discordEvent.getMessageId(),
                            httpResponse.statusCode(),
                            httpResponse.statusText().isPresent() ? httpResponse.statusText().get() : EMPTY);
                        return Mono.just(discordEvent);
                    })
                    .onErrorResume(throwable -> {
                        log.error("Error deleting an event from DDB", throwable);
                        return Mono.error(throwable);
                    });
            });
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithLimit(ListDiscordEventsCommandDTO args) {
        // TODO: check args.guildId, args.currentDateTime, args.upcomingLimit

        Map<String, String> expressionAttributesNames = ImmutableMap.of(
            "#guildId", DiscordEvent.PARTITION_KEY,
            "#datetimeCreatedBy", DiscordEvent.SORT_KEY
        );

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
            ":guildIdValue", AttributeValue.builder().s(args.getGuildId()).build(),
            ":datetimeCreatedByValue", AttributeValue.builder().s(args.getCurrentDateTime().toString()).build()
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
                log.info("Read upcoming events from DDB table: {} items", queryResponse.count());
                return Mono.just(queryResponse.items().stream()
                    .map(DiscordEvent::fromDDBMap)
                    .collect(Collectors.toList())
                );
            })
            .onErrorResume(throwable -> {
                log.error("Error reading upcoming events from DDB", throwable);
                return Mono.error(throwable);
            });
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByUpcomingWithTimeLimit(ListDiscordEventsCommandDTO args) {
        // TODO: check args.guildId, args.currentDateTime and args.upcomingTimeLimit
        Instant startDateTime = args.getCurrentDateTime();
        Instant endDateTime = args.getCurrentDateTime().plus(args.getUpcomingTimeLimit());

        return listDiscordEventsByDateTimeRange(ListDiscordEventsCommandDTO.builder()
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .guildId(args.getGuildId())
            .build());
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventsByDateTimeRange(ListDiscordEventsCommandDTO args) {
        // TODO: check args.guildId, args.startDateTime, args.endDateTime

        Map<String, String> expressionAttributesNames = ImmutableMap.of(
            "#guildId", DiscordEvent.PARTITION_KEY,
            "#datetimeCreatedBy", DiscordEvent.SORT_KEY
        );

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
            ":guildIdValue", AttributeValue.builder().s(args.getGuildId()).build(),
            ":dateTimeStart", AttributeValue.builder().s(args.getStartDateTime().toString()).build(),
            ":dateTimeEnd", AttributeValue.builder().s(args.getEndDateTime().toString()).build()
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .keyConditionExpression("#guildId = :guildIdValue and #datetimeCreatedBy between :dateTimeStart and :dateTimeEnd")
            .expressionAttributeNames(expressionAttributesNames)
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return Mono.fromCompletionStage(client.query(queryRequest))
            .flatMap(queryResponse -> {
                log.info("Read events by range from DDB table: {} items", queryResponse.count());
                return Mono.just(queryResponse.items().stream()
                    .map(DiscordEvent::fromDDBMap)
                    .collect(Collectors.toList())
                );
            })
            .onErrorResume(throwable -> {
                log.error("Error reading events by range from DDB", throwable);
                return Mono.error(throwable);
            });
    }

    @Override
    public Mono<List<DiscordEvent>> listDiscordEventCreatedByUser(ListDiscordEventsCommandDTO args) {
        // We allow users to view their all-time events so they can
        // clean up eventually the events they do not need.
        // TODO: check args.guildId, args.userId

        Map<String, String> expressionAttributesNames = ImmutableMap.of(
            "#guildId", DiscordEvent.PARTITION_KEY,
            "#createdBy", DiscordEvent.CREATED_BY_KEY
        );

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
            ":guildIdValue", AttributeValue.builder().s(args.getGuildId()).build(),
            ":createdByValue", AttributeValue.builder().s(args.getUserId()).build()
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .keyConditionExpression("#guildId = :guildIdValue")
            .filterExpression("#createdBy = :createdByValue")
            .expressionAttributeNames(expressionAttributesNames)
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return Mono.fromCompletionStage(client.query(queryRequest))
            .flatMap(queryResponse -> {
                log.info("Read events by guild and filtered by user from DDB table: {} items", queryResponse.count());
                return Mono.just(queryResponse.items().stream()
                    .map(DiscordEvent::fromDDBMap)
                    .collect(Collectors.toList())
                );
            })
            .onErrorResume(throwable -> {
                log.error("Error reading events by guild and filtering by user from DDB", throwable);
                return Mono.error(throwable);
            });
    }

    @Override
    public Mono<DiscordEvent> saveDiscordEvent(DiscordEvent discordEvent) {
        // TODO: DTO for discord event ?
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
                    httpResponse.statusText().isPresent() ? httpResponse.statusText().get() : EMPTY);
                return Mono.just(discordEvent);
            })
            .onErrorResume(throwable -> {
                log.error("Error writing to DDB", throwable);
                return Mono.error(throwable);
            });
    }

    /**
     * Helper method that gets a {@link DiscordEvent} from the DDB table based
     * on a unique identifier for all events within a discord server.
     * <p>
     * TEMPORARY: this can easily be replaced by adding an index to `messageId`
     * on DDB table.
     *
     * @param args      context args (guild)
     * @param messageId unique identifier for all the events in
     * @return Mono<DiscordEvent>
     */
    private Mono<DiscordEvent> getDiscordEventByMessageId(DiscordEventsCommandDTO args, String messageId) {
        // TODO: check args.guildID and args.messageId

        Map<String, String> expressionAttributesNames = ImmutableMap.of(
            "#guildId", DiscordEvent.PARTITION_KEY,
            "#messageId", DiscordEvent.MESSAGE_ID_KEY
        );

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
            ":guildIdValue", AttributeValue.builder().s(args.getGuildId()).build(),
            ":messageIdValue", AttributeValue.builder().s(messageId).build()
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(DISCORD_EVENTS_TABLE_NAME)
            .keyConditionExpression("#guildId = :guildIdValue")
            .filterExpression("#messageId = :messageIdValue")
            .expressionAttributeNames(expressionAttributesNames)
            .expressionAttributeValues(expressionAttributeValues)
            .build();

        return Mono.fromCompletionStage(client.query(queryRequest))
            .flatMap(queryResponse -> {
                // Validate one event and its access rights
                if (queryResponse.items().size() != 1) {
                    return Mono.error(new Exception(String.format(
                        "Found invalid amount of events with a message ID: %s", messageId)));
                }

                log.info("Found event by message ID from DDB table");
                DiscordEvent discordEvent = queryResponse.items().stream()
                    .map(DiscordEvent::fromDDBMap)
                    .findFirst()
                    .orElseThrow();

                if (!discordEvent.getCreatedBy().equals(args.getUserId())) {
                    return Mono.error(new AccessDeniedException(String.format(
                        "Access denied: User [%s] tried to obtain resource [%s] belonging to user [%s]",
                        args.getUserId(),
                        discordEvent.getMessageId(),
                        discordEvent.getCreatedBy()
                    ), discordEvent));
                }
                return Mono.just(discordEvent);
            })
            .onErrorResume(throwable -> {
                log.error("Error finding one discord event from DDB", throwable);
                return Mono.error(throwable);
            });
    }
}
