package com.alvyn279.discord.domain;

import com.alvyn279.discord.utils.DateUtils;
import discord4j.core.spec.EmbedCreateSpec;

/**
 * Utility class with predetermined string formats for the
 * messages sent from the DiscordEvents bot to the channel.
 */
public class BotMessages {

    public static String ERROR_STATE_GENERIC_TITLE = "Oops! Something went wrong.";
    public static String ERROR_STATE_GENERIC_DESCRIPTION = "Use `!events-help` command to make sure you are using" +
        " commands correctly.";

    /**
     * Creates a discord events summary used in the !list-events command
     * @param embedCreateSpec embedCreateSpec to be modified
     * @param discordEvent discord event to list
     * @param count count used to tag the discord event that the user wants
     *              to describe later on
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
}
