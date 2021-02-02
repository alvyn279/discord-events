package com.alvyn279.discord.utils;

import discord4j.common.util.Snowflake;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set of string utils pertaining to the Discord model
 */
public class DiscordStringUtils {

    public static final String EMPTY = "";

    private static final String TOKENIZER_GROUP_DELIMITER_REGEX = "\"([^\"]*)\"|'([^']*)'|(\\S+)";

    private static boolean isEmbeddedString(String s) {
        return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"));
    }

    /**
     * Checks if the given string is a delete code (snowflake message id).
     *
     * @param s input token string to be tested
     * @return boolean
     */
    public static boolean isDeleteCode(String s) {
        try {
            Snowflake.of(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Strips a string of the first and last character. This can be used to
     * remove quotes from a string.
     *
     * @param s target string
     * @return stripped string
     */
    public static String stripFirstAndLastChar(String s) {
        return s.substring(1, s.length() - 1);
    }

    /**
     * Bare bones tokenizer. Tokenizes a string into a list of string tokens. It
     * takes into consideration tokens that are delimited by " or '.
     * <p>
     * Example:
     * "!create-event \“Event title\” 2021/02/02 19:00 \“Event description\”" ->
     * ["!create-event", "Event title", "2021/02/02", "19:00", "Event description"]
     *
     * @param commandAndArgs The entire command written by the user
     * @return List of string tokens
     */
    public static List<String> tokenizeCommandAndArgs(String commandAndArgs) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern
            .compile(TOKENIZER_GROUP_DELIMITER_REGEX)
            .matcher(commandAndArgs);

        while (matcher.find()) {
            String token;
            String[] matchGroups = {matcher.group(1), matcher.group(2), matcher.group(3)};
            if (matchGroups[0] != null) {
                token = isEmbeddedString(matchGroups[0]) ? stripFirstAndLastChar(matchGroups[0]) : matchGroups[0];
            } else if (matchGroups[1] != null) {
                token = isEmbeddedString(matchGroups[1]) ? stripFirstAndLastChar(matchGroups[1]) : matchGroups[1];
            } else {
                token = matchGroups[2];
            }
            tokens.add(token);
        }
        return tokens;
    }
}
