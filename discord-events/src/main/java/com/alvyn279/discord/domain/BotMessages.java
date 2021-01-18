package com.alvyn279.discord.domain;

import com.alvyn279.discord.utils.DateUtils;
import com.google.common.collect.ImmutableList;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class with predetermined string formats for the
 * messages sent from the DiscordEvents bot to the channel.
 */
public class BotMessages {

    private static final String DISCORD_EVENT_DESCRIPTION_HEADLINE_FORMAT_STR = "**%s**, by %s\n";
    private static final String DISCORD_EVENTS_DELETE_CONFIRMATION_TITLE = "Deleted Event";
    private static final String DISCORD_EVENTS_DELETE_CONFIRMATION_DESCRIPTION_FORMAT_STR =
        "You deleted the event called \"%s\" happening on %s.";
    private static final String DISCORD_EVENTS_DELETE_ACCESS_DENIED_TITLE = "Cannot Delete Event";
    private static final String DISCORD_EVENTS_DELETE_ACCESS_DENIED_DESCRIPTION_FORMAT_STR =
        "You cannot delete \"%s\" because you are not the one that created it.";
    private static final String DISCORD_EVENTS_HELP_TITLE = "discord-events Help";
    private static final String DISCORD_EVENTS_THUMBNAIL_LINK =
        "https://cdn.betterttv.net/emote/57b377aae42b335143d48993/3x";
    private static final String ERROR_STATE_GENERIC_TITLE = "Oops! Something went wrong.";
    private static final String ERROR_STATE_GENERIC_DESCRIPTION = "Use `!events-help` command to make sure you are using" +
        " commands correctly.";
    private static final String EMOJI_AND_TITLE_FORMAT_STR = "**%s** %s";
    private static final String HELP_SECTION_CREATE = "Create event";
    private static final String HELP_SECTION_CREATE_INFO =
        "`!create-event [title:str] [date:date] [time:time] [description:str]?`";
    private static final String HELP_SECTION_DELETE = "Delete event";
    private static final String HELP_SECTION_DELETE_INFO =
        "`!delete-events [deleteCode:str]`";
    private static final String HELP_SECTION_FORMATS = "Formats";
    private static final String HELP_SECTION_FORMATS_INFO =
        "`[date]:  MM/DD/YYYY (ex: 01/16/2021, 2/5/2021)`\n" +
            "`[time]:  hh[0-23]:mm[0-59] (ex: 19:30, 2:30, 14:30)`\n" +
            "`[str]:   \"some text\"`\n" +
            "`[]?:     optional input`";
    private static final String HELP_SECTION_LIST = "Listing events";
    private static final String HELP_SECTION_LIST_INFO =
        "`!my-events`\n" +
            "`!list-events`\n" +
            "`!list-events [upcoming:num]`\n" +
            "`!list-events [on:date]`\n" +
            "`!list-events [from:date] [to:date]`";
    private static final String UNKNOWN_USER = "Unknown user";

    private static final List<SectionTuple> HELP_SECTION_TUPLE_LIST;

    @Builder
    @Data
    private static class SectionTuple {
        private final String section;
        private final String sectionInfo;
    }

    static {
        HELP_SECTION_TUPLE_LIST = ImmutableList.<SectionTuple>of(
            SectionTuple.builder().section(HELP_SECTION_CREATE).sectionInfo(HELP_SECTION_CREATE_INFO).build(),
            SectionTuple.builder().section(HELP_SECTION_DELETE).sectionInfo(HELP_SECTION_DELETE_INFO).build(),
            SectionTuple.builder().section(HELP_SECTION_LIST).sectionInfo(HELP_SECTION_LIST_INFO).build(),
            SectionTuple.builder().section(HELP_SECTION_FORMATS).sectionInfo(HELP_SECTION_FORMATS_INFO).build()
        );
    }

    /**
     * Fills an embed with a numerated list of {@link DiscordEvent}s with their time,
     * title, description.
     *
     * @param embedCreateSpec Embed creation spec object that can be modified
     * @param discordEvents   List of discord events
     */
    public static void attachDiscordEventsListToEmbed(EmbedCreateSpec embedCreateSpec,
                                                      List<DiscordEvent> discordEvents,
                                                      Map<String, User> usernamesMap) {
        AtomicInteger eventCounter = new AtomicInteger(1);
        discordEvents.forEach(discordEvent ->
            BotMessages.DiscordEventSummaryFieldBuilder.builder()
                .discordEvent(discordEvent)
                .embedCreateSpec(embedCreateSpec)
                .count(eventCounter.getAndIncrement())
                .build()
                .withNumeratedTitle()
                .withDescriptionHeadlineEventNameAndCreatedBy(
                    usernamesMap.containsKey(discordEvent.getCreatedBy()) ?
                        usernamesMap.get(discordEvent.getCreatedBy()).getUsername() :
                        UNKNOWN_USER
                )
                .withEntityDescription()
                .buildField()
        );
    }

    /**
     * Confirmation message for deletion of a discord event
     *
     * @param embedCreateSpec embed to be modified
     * @param discordEvent    discord event that was just deleted
     */
    public static void attachDeleteConfirmationToEmbed(EmbedCreateSpec embedCreateSpec, DiscordEvent discordEvent) {
        embedCreateSpec
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.GARBAGE,
                DISCORD_EVENTS_DELETE_CONFIRMATION_TITLE
            ))
            .setDescription(String.format(
                DISCORD_EVENTS_DELETE_CONFIRMATION_DESCRIPTION_FORMAT_STR,
                discordEvent.getName(),
                DateUtils.prettyPrintInstantInLocalTimezone(discordEvent.getTimestamp())
            ))
            .setColor(Color.GREEN)
            .setTimestamp(Instant.now());
    }

    /**
     * Confirmation message for access denied during
     * deletion of a discord event
     *
     * @param embedCreateSpec embed to be modified
     * @param discordEvent    discord event that was attempted to be deleted
     */
    public static void attachDeleteAccessDeniedToEmbed(EmbedCreateSpec embedCreateSpec, DiscordEvent discordEvent) {
        embedCreateSpec
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.RED_CROSS,
                DISCORD_EVENTS_DELETE_ACCESS_DENIED_TITLE
            ))
            .setDescription(String.format(
                DISCORD_EVENTS_DELETE_ACCESS_DENIED_DESCRIPTION_FORMAT_STR,
                discordEvent.getName()
            ))
            .setColor(Color.RED)
            .setTimestamp(Instant.now());
    }

    /**
     * Creates the discord-events help message.
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void help(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.BOOK,
                DISCORD_EVENTS_HELP_TITLE
            ))
            .setColor(Color.DISCORD_BLACK)
            .setThumbnail(DISCORD_EVENTS_THUMBNAIL_LINK);

        HELP_SECTION_TUPLE_LIST
            .forEach(sectionTuple -> embedCreateSpec.addField(
                sectionTuple.getSection(),
                sectionTuple.getSectionInfo(),
                false
            ));
    }

    /**
     * Creates the discord-events oops (`something went wrong`) message
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void oops(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setColor(Color.RED)
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.RED_CROSS,
                BotMessages.ERROR_STATE_GENERIC_TITLE
            ))
            .setDescription(BotMessages.ERROR_STATE_GENERIC_DESCRIPTION);
    }

    /**
     * Builder class to obtain a differently formatted field
     * encapsulating {@link DiscordEvent} information on a Discord
     * embed.
     */
    @Data
    @Builder
    public static class DiscordEventSummaryFieldBuilder {

        @NonNull
        private final EmbedCreateSpec embedCreateSpec;
        @NonNull
        private final DiscordEvent discordEvent;
        @NonNull
        private final Integer count;

        @Builder.Default
        private String title = "";
        @Builder.Default
        private String description = "";
        @Builder.Default
        private String descriptionHeadline = "";
        @Builder.Default
        private Boolean inline = false;

        /**
         * Common extract of string builder for the description
         * portion of the field.
         *
         * @return builder
         */
        private StringBuilder commonDescription() {
            if (discordEvent.getDescription().equals("")) {
                return new StringBuilder();
            }
            return new StringBuilder()
                .append(discordEvent.getDescription())
                .append("\n");
        }

        /**
         * Sets a numerated title as part of a list of {@link DiscordEvent}s
         * being shown to the user.
         *
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withNumeratedTitle() {
            this.title = new StringBuilder()
                .append("[")
                .append(count)
                .append("] ")
                .append(DateUtils.prettyPrintInstantInLocalTimezone(discordEvent.getTimestamp()))
                .toString();
            return this;
        }

        /**
         * Sets a description headline with the name of the event
         *
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withDescriptionHeadlineEventName() {
            this.descriptionHeadline = String.format(
                "**%s**\n",
                discordEvent.getName()
            );
            return this;
        }

        /**
         * Sets a description headline with the name of the event plus
         * its creator.
         *
         * @param username discord context user username
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withDescriptionHeadlineEventNameAndCreatedBy(String username) {
            this.descriptionHeadline = String.format(
                DISCORD_EVENT_DESCRIPTION_HEADLINE_FORMAT_STR,
                discordEvent.getName(),
                username
            );
            return this;
        }

        /**
         * Sets a normal description with that of the {@link DiscordEvent} entity.
         *
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withEntityDescription() {
            this.description = commonDescription().toString();
            return this;
        }

        /**
         * Sets a description with the {@link DiscordEvent} messageID (deleteCode) used
         * for potential event deletion by user.
         *
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withEntityAndDeleteCodeDescription() {
            this.description = commonDescription()
                .append(String.format("[*deleteCode*: %s]\n", discordEvent.getMessageId()))
                .toString();
            return this;
        }

        /**
         * Sets inline boolean for the field being added with a {@link DiscordEvent}
         *
         * @param val boolean val
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withInline(Boolean val) {
            this.inline = val;
            return this;
        }

        /**
         * Applies changes to embed by "building" into the embed object
         * reference. Must be called for embed to be modified.
         */
        public void buildField() {
            embedCreateSpec.addField(
                this.title,
                String.format("%s%s", this.descriptionHeadline, this.description),
                this.inline
            );
        }
    }
}
