package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;
import java.util.Map;

/**
 * Class that represents a Discord Event that the users create while
 * interacting with the bot.
 */
@Builder
@Data
public class DiscordEvent {
    // Key names for DiscordEvent entity in DDB Table
    private static final String EVENT_ID_KEY = "eventId";
    private static final String CREATED_BY_KEY = "createdBy";
    private static final String NAME_KEY = "name";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String DESCRIPTION_KEY = "description";

    public static final String EVENT_ID_LABEL = "Event ID";
    public static final String CREATED_BY_LABEL = "Created by";
    public static final String NAME_LABEL = "Name";
    public static final String TIMESTAMP_LABEL = "Timestamp";
    public static final String DESCRIPTION_LABEL = "Description";

    /**
     * The unique event ID for the event created with the bot
     */
    String eventId;

    /**
     * The user ID for Discord user that created the event
     */
    String createdBy;

    /**
     * The name/title of the event
     */
    String name;

    /**
     * The epoch timestamp at which the event will start
     */
    Instant timestamp;

    /**
     * (Optional) The description for the event
     */
    String description;

    /**
     * AWS DDB SETTER
     * Creates an AWS DDB-processable item for read/writes with the AWS SDK client
     *
     * @param discordEvent DiscordEvent pojo
     * @return Item in ddb domain
     */
    public static Map<String, AttributeValue> toDDBItem(DiscordEvent discordEvent) {
        return ImmutableMap.of(
            EVENT_ID_KEY, AttributeValue.builder().s(discordEvent.eventId).build(),
            CREATED_BY_KEY, AttributeValue.builder().s(discordEvent.createdBy).build(),
            NAME_KEY, AttributeValue.builder().s(discordEvent.name).build(),
            TIMESTAMP_KEY, AttributeValue.builder().s(discordEvent.timestamp.toString()).build(),
            DESCRIPTION_KEY, AttributeValue.builder().s(discordEvent.description).build()
        );
    }

    /**
     * AWS DDB GETTER
     * Creates a client-side instance of {@link DiscordEvent} from attributes map
     *
     * @param map Map<String, AttributeValue> map from DDB reads
     * @return A discord event
     */
    public static DiscordEvent fromDDBMap(Map<String, AttributeValue> map) {
        return DiscordEvent.builder()
            .eventId(map.get(EVENT_ID_KEY).s())
            .createdBy(map.get(CREATED_BY_KEY).s())
            .name(map.get(NAME_KEY).s())
            .timestamp(DateUtils.parseIso8601Date(
                map.get(TIMESTAMP_KEY).s()
            ))
            .description(map.get(DESCRIPTION_KEY).s())
            .build();
    }
}
