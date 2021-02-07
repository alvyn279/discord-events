package com.alvyn279.discord.repository.dto;

import com.alvyn279.discord.domain.DiscordEvent;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.Set;

/**
 * Data transfer object for saving a {@link DiscordEvent} to the data store.
 * <p>
 * The difference with {@link DiscordEvent} is that this DTO has nullable
 * fields and is used for client-side-created discord events.
 * <p>
 * IMPORTANT: It is not used as source of truth for discord events.
 */
@Data
@Builder
public class DiscordEventDTO {
    @NonNull
    private final String guildId;
    @NonNull
    private final Instant timestamp;
    @NonNull
    private final String createdBy;
    @NonNull
    private final String messageId;
    @NonNull
    private final String name;

    private final String description;

    private final Set<String> attendees;

    /**
     * Utility function for generating a DTO builder based on
     * an already existing {@link DiscordEvent}. This is useful
     * when building a modified discord event DTO to save.
     *
     * @param discordEvent original discord event
     * @return DTO builder that can be further modified
     */
    public static DiscordEventDTO.DiscordEventDTOBuilder copyOfBuilder(DiscordEvent discordEvent) {
        return DiscordEventDTO.builder()
            .guildId(discordEvent.getGuildId())
            .timestamp(discordEvent.getTimestamp())
            .createdBy(discordEvent.getCreatedBy())
            .messageId(discordEvent.getMessageId())
            .name(discordEvent.getName())
            .description(discordEvent.getDescription().isEmpty() ?
                null : discordEvent.getDescription())
            .attendees(discordEvent.getAttendees().isEmpty() ?
                null : discordEvent.getAttendees());
    }
}
