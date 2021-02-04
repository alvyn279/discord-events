package com.alvyn279.discord.domain;

import com.alvyn279.discord.repository.dto.DiscordEventDTO;
import com.google.common.collect.ImmutableSet;
import discord4j.common.util.Snowflake;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static com.alvyn279.discord.utils.DiscordStringUtils.EMPTY;

/**
 * Class that represents a Discord Event from the DDB table.
 * <p>
 * DynamoDB:
 * PK: `{guildID}`
 * SK: `{timestamp}#{createdBy}`
 */
@Data
@Builder
public class DiscordEvent {
    // Key names for DiscordEvent entity in DDB Table (attrs)
    public static final String PARTITION_KEY = "guildId";
    public static final String SORT_KEY = "datetimeCreatedBy";
    public static final String MESSAGE_ID_KEY = "messageId";
    public static final String CREATED_BY_KEY = "createdBy";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String ATTENDEES_KEY = "attendees";

    public static final String MESSAGE_ID_LABEL = "Message ID";
    public static final String CREATED_BY_LABEL = "Created by";
    public static final String DATETIME_LABEL = "Time";

    /**
     * The discord guild/server's {@link Snowflake} id
     */
    @NonNull
    private final String guildId;

    /**
     * The Instant object at which the event will start.
     * In DDB, it is stored as a string with ISO-8601 representation.
     */
    @NonNull
    private final Instant timestamp;

    /**
     * The {@link Snowflake} id of the user that created the event
     */
    @NonNull
    private final String createdBy;

    /**
     * The event message's {@link Snowflake} id
     */
    @NonNull
    private final String messageId;

    /**
     * The name/title of the event
     */
    @NonNull
    private final String name;

    /**
     * The description for the event
     */
    @NonNull
    private final String description;

    /**
     * The {@link Snowflake} ids of the event's attendees.
     * This is a readonly value.
     */
    @NonNull
    private final Set<String> attendees;


    /**
     * Returns `datetimeCreatedBy` string for user-defined DiscordEvent.
     * This way end-user does not need to know how to build a
     * {@link DatetimeCreatedBy}.
     *
     * @return string
     */
    public String datetimeCreatedBy() {
        return DatetimeCreatedBy.builder()
            .createdBy(createdBy)
            .datetime(timestamp)
            .build()
            .asString();
    }

    @Builder
    @Data
    private static class DatetimeCreatedBy {
        private static final String DDB_COMPOSITE_KEY_SEPARATOR = "#";

        @NonNull
        private final Instant datetime;

        @NonNull
        private final String createdBy;

        /**
         * Builds the datetime#createdBy sort key value to uniquely identify this
         * event within a guild.
         *
         * @return String `{datetime}#{createdBy}`
         */
        protected String asString() {
            return String.format("%1$s%2$s%3$s",
                datetime.toString(),
                DDB_COMPOSITE_KEY_SEPARATOR,
                createdBy
            );
        }

        /**
         * Builds a {@link DatetimeCreatedBy} based on the string value obtained
         * from the table.
         *
         * @return DatetimeCreatedBy object
         */
        protected static DatetimeCreatedBy from(String datetimeCreatedByString) {
            String[] composite = datetimeCreatedByString.split(DDB_COMPOSITE_KEY_SEPARATOR);
            return DatetimeCreatedBy.builder()
                .datetime(DateUtils.parseIso8601Date(composite[0]))
                .createdBy(composite[1])
                .build();
        }
    }

    /***
     * Safe copy of raw DDB map of string attribute to value (will not contain nulls). It sets defaults if
     * some of the optional fields are missing.
     *
     * This is the function that a
     * {DynamoDbMapper https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.html}
     * would usually expose as skip-missing-attributes annotation.
     *
     * Assumptions: partition and sort keys are always non-nulls.
     *
     * @param map DDB items maps
     * @return non-null attribute-attributeValue map
     */
    private static Map<String, AttributeValue> safeDDBMap(Map<String, AttributeValue> map) {
        return new ImmutableMap.Builder<String, AttributeValue>()
            .put(PARTITION_KEY, map.get(PARTITION_KEY))
            .put(SORT_KEY, map.get(SORT_KEY))
            .put(MESSAGE_ID_KEY, map.getOrDefault(MESSAGE_ID_KEY, AttributeValue.builder().s(EMPTY).build()))
            .put(CREATED_BY_KEY, map.getOrDefault(CREATED_BY_KEY, AttributeValue.builder().s(EMPTY).build()))
            .put(NAME_KEY, map.getOrDefault(NAME_KEY, AttributeValue.builder().s(EMPTY).build()))
            .put(DESCRIPTION_KEY, map.getOrDefault(DESCRIPTION_KEY, AttributeValue.builder().s(EMPTY).build()))
            .put(ATTENDEES_KEY, map.getOrDefault(ATTENDEES_KEY, AttributeValue.builder().ss(ImmutableSet.of()).build()))
            .build();
    }

    /**
     * AWS DDB SETTER
     * Creates an AWS DDB-processable item for read/writes with the AWS SDK client
     * This will take care of excluding the nullable attributes from the written
     * DDB item/map.
     *
     * @param discordEventDTO DiscordEventDTO pojo
     * @return Item in ddb domain
     */
    public static Map<String, AttributeValue> toDDBItem(DiscordEventDTO discordEventDTO) {
        ImmutableMap.Builder<String, AttributeValue> ddbItemBuilder = ImmutableMap.<String, AttributeValue>builder()
            .put(PARTITION_KEY, AttributeValue.builder().s(discordEventDTO.getGuildId()).build())
            .put(SORT_KEY, AttributeValue.builder().s(
                DatetimeCreatedBy.builder()
                    .datetime(discordEventDTO.getTimestamp())
                    .createdBy(discordEventDTO.getCreatedBy())
                    .build()
                    .asString())
                .build())
            .put(MESSAGE_ID_KEY, AttributeValue.builder().s(discordEventDTO.getMessageId()).build())
            .put(CREATED_BY_KEY, AttributeValue.builder().s(discordEventDTO.getCreatedBy()).build())
            .put(NAME_KEY, AttributeValue.builder().s(discordEventDTO.getName()).build());

        if (discordEventDTO.getDescription() != null) {
            ddbItemBuilder.put(DESCRIPTION_KEY, AttributeValue.builder().s(discordEventDTO.getDescription()).build());
        }

        if (discordEventDTO.getAttendees() != null && !discordEventDTO.getAttendees().isEmpty()) {
            ddbItemBuilder.put(ATTENDEES_KEY, AttributeValue.builder().ss(discordEventDTO.getAttendees()).build());
        }

        return ddbItemBuilder.build();
    }

    /**
     * AWS DDB GETTER
     * Creates a client-side instance of {@link DiscordEvent} from attributes map
     *
     * @param rawMap Map<String, AttributeValue> map from DDB reads
     * @return A discord event
     */
    public static DiscordEvent fromDDBMap(Map<String, AttributeValue> rawMap) {
        Map<String, AttributeValue> map = safeDDBMap(rawMap);
        DatetimeCreatedBy datetimeCreatedBy = DatetimeCreatedBy.from(map.get(SORT_KEY).s());
        return DiscordEvent.builder()
            .guildId(map.get(PARTITION_KEY).s())
            .timestamp(datetimeCreatedBy.datetime)
            .messageId(map.get(MESSAGE_ID_KEY).s())
            .createdBy(map.get(CREATED_BY_KEY).s())
            .name(map.get(NAME_KEY).s())
            .description(map.get(DESCRIPTION_KEY).s())
            .attendees(
                ImmutableSet.copyOf(map.get(ATTENDEES_KEY).ss())
            )
            .build();
    }
}
