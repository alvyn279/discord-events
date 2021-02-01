package com.alvyn279.discord.domain;

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

/**
 * Class that represents a Discord Event that the users create while
 * interacting with the bot.
 * <p>
 * DynamoDB:
 * PK: `{guildID}`
 * SK: `{timestamp}#{createdBy}`
 */
@Builder
@Data
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

    /**
     * AWS DDB SETTER
     * Creates an AWS DDB-processable item for read/writes with the AWS SDK client
     *
     * @param discordEvent DiscordEvent pojo
     * @return Item in ddb domain
     */
    public static Map<String, AttributeValue> toDDBItem(DiscordEvent discordEvent) {
        return ImmutableMap.<String, AttributeValue>builder()
            .put(PARTITION_KEY, AttributeValue.builder().s(discordEvent.guildId).build())
            .put(SORT_KEY, AttributeValue.builder().s(
                DatetimeCreatedBy.builder()
                    .datetime(discordEvent.timestamp)
                    .createdBy(discordEvent.createdBy)
                    .build()
                    .asString())
                .build())
            .put(MESSAGE_ID_KEY, AttributeValue.builder().s(discordEvent.messageId).build())
            .put(CREATED_BY_KEY, AttributeValue.builder().s(discordEvent.createdBy).build())
            .put(DESCRIPTION_KEY, AttributeValue.builder().s(discordEvent.description).build())
            .put(NAME_KEY, AttributeValue.builder().s(discordEvent.name).build())
            .put(ATTENDEES_KEY, AttributeValue.builder().ss(discordEvent.attendees).build())
            .build();
    }

    /**
     * AWS DDB GETTER
     * Creates a client-side instance of {@link DiscordEvent} from attributes map
     *
     * @param map Map<String, AttributeValue> map from DDB reads
     * @return A discord event
     */
    public static DiscordEvent fromDDBMap(Map<String, AttributeValue> map) {
        DatetimeCreatedBy datetimeCreatedBy = DatetimeCreatedBy.from(map.get(SORT_KEY).s());
        return DiscordEvent.builder()
            .guildId(map.get(PARTITION_KEY).s())
            .timestamp(datetimeCreatedBy.datetime)
            .createdBy(datetimeCreatedBy.createdBy)
            .messageId(map.get(MESSAGE_ID_KEY).s())
            .createdBy(map.get(CREATED_BY_KEY).s())
            .name(map.get(NAME_KEY).s())
            .description(map.get(DESCRIPTION_KEY).s())
            .attendees(ImmutableSet.copyOf(
                map.get(ATTENDEES_KEY).ss()
            ))
            .build();
    }
}
