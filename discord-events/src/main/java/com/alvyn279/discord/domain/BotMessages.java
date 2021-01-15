package com.alvyn279.discord.domain;

import com.alvyn279.discord.utils.DateUtils;
import com.google.common.collect.ImmutableList;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class with predetermined string formats for the
 * messages sent from the DiscordEvents bot to the channel.
 */
public class BotMessages {

    public static final String DISCORD_EVENTS_HELP_TITLE = "discord-events Help";
    public static final String DISCORD_EVENTS_THUMBNAIL_LINK =
        "https://cdn.betterttv.net/emote/57b377aae42b335143d48993/3x";
    public static final String ERROR_STATE_GENERIC_TITLE = "Oops! Something went wrong.";
    public static final String ERROR_STATE_GENERIC_DESCRIPTION = "Use `!events-help` command to make sure you are using" +
        " commands correctly.";
    public static final String EMOJI_AND_TITLE_FORMAT_STR = "**%s** %s";
    public static final String HELP_SECTION_CREATE = "Create event";
    public static final String HELP_SECTION_CREATE_INFO =
        "`!create-event [title:str] [date:date] [time:time] [description:str]`";
    public static final String HELP_SECTION_FORMATS = "Formats";
    public static final String HELP_SECTION_FORMATS_INFO =
        "`[date]:  MM/DD/YYYY (ex: 01/16/2021, 2/5/2021)`\n" +
            "`[time]:  hh[0-23]:mm[0-59] (ex: 19:30, 2:30, 14:30)`\n" +
            "`[str]:   \"some text\"`";
    public static final String HELP_SECTION_LIST = "Listing events";
    public static final String HELP_SECTION_LIST_INFO =
        "`!my-events`\n" +
            "`!list-events`\n" +
            "`!list-events [upcoming:num]`\n" +
            "`!list-events [on:date]`\n" +
            "`!list-events [from:date] [to:date]`";
    public static List<SectionTuple> HELP_SECTION_TUPLE_LIST;

    @Builder
    @Data
    private static class SectionTuple {
        private final String section;
        private final String sectionInfo;
    }

    static {
        HELP_SECTION_TUPLE_LIST = ImmutableList.<SectionTuple>of(
            SectionTuple.builder().section(HELP_SECTION_CREATE).sectionInfo(HELP_SECTION_CREATE_INFO).build(),
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
    public static void attachDiscordEventsListToEmbed(EmbedCreateSpec embedCreateSpec, List<DiscordEvent> discordEvents) {
        AtomicInteger eventCounter = new AtomicInteger(1);
        discordEvents.forEach(discordEvent ->
            BotMessages.DiscordEventSummaryFieldBuilder.builder()
                .discordEvent(discordEvent)
                .embedCreateSpec(embedCreateSpec)
                .count(eventCounter.getAndIncrement())
                .build()
                .withNumeratedTitle()
                .withEntityDescription()
                .buildField()
        );
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
     * encapsulating {@DiscordEvent} information on a Discord
     * embed.
     */
    @Data
    @Builder
    public static class DiscordEventSummaryFieldBuilder {

        @NonNull
        private EmbedCreateSpec embedCreateSpec;
        @NonNull
        private DiscordEvent discordEvent;
        @NonNull
        private Integer count;

        @Builder.Default
        private String title = "";
        @Builder.Default
        private String description = "";
        @Builder.Default
        private Boolean inline = false;

        /**
         * Common extract of string builder for the description
         * portion of the field.
         *
         * @return builder
         */
        private StringBuilder commonDescription() {
            return new StringBuilder()
                .append(discordEvent.getName())
                .append("\n")
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
                this.description,
                this.inline
            );
        }
    }
}
