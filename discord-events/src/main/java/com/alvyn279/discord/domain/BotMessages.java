package com.alvyn279.discord.domain;

import com.alvyn279.discord.utils.DateUtils;
import com.google.common.collect.ImmutableList;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
     * Creates a {@link DiscordEvent} summary used in the !list-events command
     *
     * @param embedCreateSpec embedCreateSpec to be modified
     * @param discordEvent    discord event to list
     * @param count           count used to tag the discord event that the user wants
     *                        to describe later on
     */
    public static void createListDiscordEventItemSummary(EmbedCreateSpec embedCreateSpec,
                                                         DiscordEvent discordEvent,
                                                         Integer count) {
        embedCreateSpec.addField(
            new StringBuilder()
                .append("[")
                .append(count)
                .append("] ")
                .append(DateUtils.prettyPrintInstantInLocalTimezone(discordEvent.getTimestamp()))
                .toString(),
            new StringBuilder()
                .append(discordEvent.getName())
                .append("\n")
                .append(discordEvent.getDescription())
                .append("\n")
                .toString(),
            false
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
}
