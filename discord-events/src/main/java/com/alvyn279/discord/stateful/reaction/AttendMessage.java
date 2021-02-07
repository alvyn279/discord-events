package com.alvyn279.discord.stateful.reaction;

import com.alvyn279.discord.domain.BotMessages;
import com.alvyn279.discord.domain.DiscordEvent;
import com.alvyn279.discord.domain.GuildUtils;
import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.repository.dto.DiscordEventDTO;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Object for messages sent by the bot in response
 * to `!attend-event` command.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class AttendMessage extends BaseMessage implements ReactableMessage {
    @NonNull
    private final Guild guild;
    @NonNull
    private final Message message;
    @NonNull
    private final DiscordEventReactiveRepository repository;
    @NonNull
    private List<DiscordEvent> discordEvents;

    /**
     * Utility function to replace the modified discord event in the
     * list of discord events by re-creating it.
     *
     * @param discordEvent updated discord event
     * @param index        index at which it appeared in the list
     * @return list of the newly update list of discord events
     */
    private List<DiscordEvent> popAndReplaceAtIndex(DiscordEvent discordEvent, Integer index) {
        ImmutableList.Builder<DiscordEvent> listBuilder = ImmutableList.builder();
        for (int i = 0; i < discordEvents.size(); ++i) {
            if (i == index) {
                listBuilder.add(discordEvent);
            } else {
                listBuilder.add(discordEvents.get(i));
            }
        }
        List<DiscordEvent> updatedEvents = listBuilder.build();
        discordEvents = updatedEvents;
        return updatedEvents;
    }

    @Override
    public Mono<Void> onReactionAdd(ReactionAddEvent event) {
        String attendeeId = event.getUserId().asString();
        return retrieveRawUnicode(event.getEmoji())
            .map(rawReactionEmojiStr -> GuildUtils.getNumberedEmojiIndex(rawReactionEmojiStr)
                .map(index -> repository.saveDiscordEvent(DiscordEventDTO
                    .copyOfBuilder(discordEvents.get(index))
                    .attendees(ImmutableSet.<String>builder()
                        .addAll(discordEvents.get(index).getAttendees())
                        .add(attendeeId)
                        .build())
                    .build())
                    .flatMap(discordEvent -> GuildUtils.retrieveGuildUsers(guild)
                        .flatMap(stringUserMap -> message.edit(
                            messageEditSpec -> messageEditSpec.setEmbed(
                                embedCreateSpec ->
                                    BotMessages.attachAttendableDiscordEvents(
                                        embedCreateSpec,
                                        popAndReplaceAtIndex(discordEvent, index),
                                        stringUserMap
                                    )
                            )))))
                .orElse(Mono.empty())
            )
            .orElse(Mono.empty())
            .then();
    }

    @Override
    public Mono<Void> onReactionRemove(ReactionRemoveEvent event) {
        // TODO: use repository to remove reactions
        return Mono.empty();
    }
}