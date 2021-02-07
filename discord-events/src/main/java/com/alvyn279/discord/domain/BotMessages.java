package com.alvyn279.discord.domain;

import com.alvyn279.discord.utils.DateUtils;
import com.google.common.collect.ImmutableList;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alvyn279.discord.utils.DiscordStringUtils.EMPTY;

/**
 * Utility class with predetermined string formats for the
 * messages sent from the DiscordEvents bot to the channel.
 */
public class BotMessages {

    public static final String EMOJI_AND_TITLE_FORMAT_STR = "**%s** %s";

    private static final String DISCORD_EVENT_DESCRIPTION_HEADLINE_FORMAT_STR = "**%s**, by %s\n";
    private static final String DISCORD_EVENTS_ATTEND_EVENTS_TITLE = "Attend Events";
    private static final String DISCORD_EVENTS_ATTEND_EVENTS_DESCRIPTION = "React to the events you wish to attend.";
    private static final String DISCORD_EVENTS_ATTENDEES_ENUMERATION_FORMAT_STR = "%s will be there.";
    private static final String DISCORD_EVENTS_ATTENDEES_NONE = "No one";
    private static final String DISCORD_EVENTS_DELETE_ACCESS_DENIED_TITLE = "Cannot Delete Event";
    private static final String DISCORD_EVENTS_DELETE_ACCESS_DENIED_DESCRIPTION_FORMAT_STR =
        "You cannot delete \"%s\" because you are not the one that created it.";
    private static final String DISCORD_EVENTS_DELETE_CONFIRMATION_TITLE = "Deleted Event";
    private static final String DISCORD_EVENTS_DELETE_CONFIRMATION_DESCRIPTION_FORMAT_STR =
        "You deleted the event called \"**%s**\" happening on %s.";
    private static final String DISCORD_EVENTS_DELETE_MULTIPLE_CONFIRMATION_TITLE = "Deleted Events";
    private static final String DISCORD_EVENTS_HELP_TITLE = "discord-events Help";
    private static final String DISCORD_EVENTS_NONE_FOUND = "No events were found.";
    private static final String DISCORD_EVENTS_THUMBNAIL_LINK =
        "https://cdn.betterttv.net/emote/57b377aae42b335143d48993/3x";
    private static final String ERROR_STATE_GENERIC_TITLE = "Oops! Something went wrong.";
    private static final String ERROR_STATE_GENERIC_DESCRIPTION = "Use `!events-help` command to make sure you are using" +
        " commands correctly.";
    private static final String EVENT_REMINDERS_ON = "Event reminders are on.";
    private static final String EVENT_REMINDERS_OFF = "Event reminders are off.";
    private static final String EVENT_REMINDERS_STATUS = "Event Reminders Status";
    private static final String EVENT_REMINDERS_STATUS_DESCRIPTION_FORMAT_STR = "Event reminders are currently " +
        "turned on in text channel: #**%s**.";
    private static final String EVENT_REMINDERS_TURNED_OFF = "You will not be reminded of upcoming events anymore.";
    private static final String EVENT_REMINDERS_TURNED_OFF_TITLE = "Event reminders: OFF";
    private static final String EVENT_REMINDERS_TURNED_ON = "You will be reminded on this channel about any" +
        " event starting soon.";
    private static final String EVENT_REMINDERS_TURNED_ON_TITLE = "Event reminders: ON";
    private static final String HELP_SECTION_CREATE = "Create event";
    private static final String HELP_SECTION_CREATE_INFO =
        "`!create-event [title:str] [date:date] [time:time] [description:str]?`";
    private static final String HELP_SECTION_DELETE = "Delete event";
    private static final String HELP_SECTION_DELETE_INFO =
        "`!delete-events [deleteCode:str]*`";
    private static final String HELP_SECTION_FORMATS = "Formats";
    private static final String HELP_SECTION_FORMATS_INFO =
        "`[date]:  MM/DD/YYYY (ex: 01/16/2021, 2/5/2021)`\n" +
            "`[time]:  hh[0-23]:mm[0-59] (ex: 19:30, 2:30, 14:30)`\n" +
            "`[str]:   \"some text\"`\n" +
            "`[]?:     optional input`\n" +
            "`[]*:     repeatable one to many times`";
    private static final String HELP_SECTION_LIST = "Listing events";
    private static final String HELP_SECTION_LIST_INFO =
        "`!my-events`\n" +
            "`!list-events`\n" +
            "`!list-events [upcoming:num]`\n" +
            "`!list-events [on:date]`\n" +
            "`!list-events [from:date] [to:date]`";
    private static final String HELP_SECTION_REMINDERS = "Event reminders";
    private static final String HELP_SECTION_REMINDERS_INFO = "`!remind-events [on|off:str]?\n`";
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
            SectionTuple.builder().section(HELP_SECTION_REMINDERS).sectionInfo(HELP_SECTION_REMINDERS_INFO).build(),
            SectionTuple.builder().section(HELP_SECTION_FORMATS).sectionInfo(HELP_SECTION_FORMATS_INFO).build()
        );
    }

    /**
     * Fills an embed with a numerated list of the attendable {@link DiscordEvent}s.
     *
     * @param embedCreateSpec embed to be modified
     * @param discordEvents   attendable events
     */
    public static void attachAttendableDiscordEvents(EmbedCreateSpec embedCreateSpec,
                                                     List<DiscordEvent> discordEvents,
                                                     Map<String, User> usersMap) {
        if (discordEvents.isEmpty()) {
            attachNoDiscordEventsDescription(embedCreateSpec);
            return;
        }

        embedCreateSpec
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.RAISE_HAND,
                DISCORD_EVENTS_ATTEND_EVENTS_TITLE))
            .setDescription(DISCORD_EVENTS_ATTEND_EVENTS_DESCRIPTION)
            .setColor(Color.LIGHT_SEA_GREEN);

        AtomicInteger eventCounter = new AtomicInteger(0);
        discordEvents.forEach(discordEvent -> {
                String attendeesList;
                if (discordEvent.getAttendees().isEmpty()) {
                    attendeesList = DISCORD_EVENTS_ATTENDEES_NONE;
                } else {
                    attendeesList = StringUtils.join(
                        discordEvent.getAttendees().stream()
                            .map(s -> usersMap.containsKey(s) ? usersMap.get(s).getUsername() : UNKNOWN_USER)
                            .toArray(String[]::new),
                        ",");
                }

                BotMessages.DiscordEventSummaryFieldBuilder.builder()
                    .discordEvent(discordEvent)
                    .embedCreateSpec(embedCreateSpec)
                    .count(eventCounter.getAndIncrement())
                    .build()
                    .withNumeratedTitleAndEventName()
                    .withEntityAttendeesDescription(attendeesList)
                    .buildField();
            }
        );
    }

    /**
     * Sets the description of the embed to indicate that no
     * items were found.
     *
     * @param embedCreateSpec embed to be modified
     */
    private static void attachNoDiscordEventsDescription(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec.setDescription(DISCORD_EVENTS_NONE_FOUND);
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
        if (discordEvents.isEmpty()) {
            attachNoDiscordEventsDescription(embedCreateSpec);
            return;
        }

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
     * Fills an embed with a numerated list of {@link DiscordEvent}s created by
     * a single user.
     *
     * @param embedCreateSpec Embed creation spec object that can be modified
     * @param discordEvents   List of discord events
     */
    public static void attachDiscordEventsPersonalListToEmbed(EmbedCreateSpec embedCreateSpec,
                                                              List<DiscordEvent> discordEvents) {
        if (discordEvents.isEmpty()) {
            attachNoDiscordEventsDescription(embedCreateSpec);
            return;
        }

        AtomicInteger eventCounter = new AtomicInteger(1);
        discordEvents.forEach(discordEvent ->
            BotMessages.DiscordEventSummaryFieldBuilder.builder()
                .discordEvent(discordEvent)
                .embedCreateSpec(embedCreateSpec)
                .count(eventCounter.getAndIncrement())
                .build()
                .withNumeratedTitle()
                .withDescriptionHeadlineEventName()
                .withEntityAndDeleteCodeDescription()
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
     * Confirmation message for deletion of multiple discord events
     *
     * @param embedCreateSpec      embed to be modified
     * @param deletedDiscordEvents discordEvents that were just deleted
     */
    public static void attachDeleteMultipleConfirmationToEmbed(EmbedCreateSpec embedCreateSpec,
                                                               List<DiscordEvent> deletedDiscordEvents) {
        StringBuilder description = new StringBuilder();
        deletedDiscordEvents
            .forEach(discordEvent -> description
                .append(String.format(
                    DISCORD_EVENTS_DELETE_CONFIRMATION_DESCRIPTION_FORMAT_STR,
                    discordEvent.getName(),
                    DateUtils.prettyPrintInstantInLocalTimezone(discordEvent.getTimestamp()))
                )
                .append("\n")
            );
        embedCreateSpec
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.GARBAGE,
                DISCORD_EVENTS_DELETE_MULTIPLE_CONFIRMATION_TITLE
            ))
            .setDescription(description.toString())
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
     * Message to inform that the reminders for events are turned on
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void eventRemindersOn(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setColor(Color.ORANGE)
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.NOTIFS_ON,
                BotMessages.EVENT_REMINDERS_STATUS
            ))
            .setDescription(BotMessages.EVENT_REMINDERS_ON);
    }

    /**
     * Message to inform that the reminders for events are turned off.
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void eventRemindersOff(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setColor(Color.ORANGE)
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.NOTIFS_OFF,
                BotMessages.EVENT_REMINDERS_STATUS
            ))
            .setDescription(BotMessages.EVENT_REMINDERS_OFF);
    }

    /**
     * Message to inform of the text channel in which the event
     * reminders are turned on.
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void eventRemindersOnStatus(EmbedCreateSpec embedCreateSpec, String channelName) {
        BotMessages.eventRemindersOn(embedCreateSpec);
        embedCreateSpec
            .setDescription(String.format(
                EVENT_REMINDERS_STATUS_DESCRIPTION_FORMAT_STR,
                channelName
            ));
    }

    /**
     * Message to inform that the reminders for events were turned on.
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void eventRemindersTurnedOn(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setColor(Color.GREEN)
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.NOTIFS_ON,
                BotMessages.EVENT_REMINDERS_TURNED_ON_TITLE
            ))
            .setDescription(BotMessages.EVENT_REMINDERS_TURNED_ON);
    }

    /**
     * Message to inform that the reminders for events were turned off.
     *
     * @param embedCreateSpec embed to be modified
     */
    public static void eventRemindersTurnedOff(EmbedCreateSpec embedCreateSpec) {
        embedCreateSpec
            .setColor(Color.GREEN)
            .setTitle(String.format(
                EMOJI_AND_TITLE_FORMAT_STR,
                Emoji.NOTIFS_OFF,
                BotMessages.EVENT_REMINDERS_TURNED_OFF_TITLE
            ))
            .setDescription(BotMessages.EVENT_REMINDERS_TURNED_OFF);
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
        private String title = EMPTY;
        @Builder.Default
        private String description = EMPTY;
        @Builder.Default
        private String descriptionHeadline = EMPTY;
        @Builder.Default
        private Boolean inline = false;

        /**
         * Common extract of string builder for the description
         * portion of the field.
         *
         * @return builder
         */
        private StringBuilder commonDescription() {
            if (discordEvent.getDescription().isEmpty()) {
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
         * Sets the title to have a number, the name of the event, and the
         * time at which it will occur.
         *
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withNumeratedTitleAndEventName() {
            this.title = new StringBuilder()
                .append("[")
                .append(count)
                .append("] ")
                .append(String.format(
                    "**%s**, %s\n",
                    discordEvent.getName(),
                    DateUtils.prettyPrintInstantInLocalTimezone(discordEvent.getTimestamp())
                ))
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
         * Sets the field description to be a single-liner with
         * the list of comma-separated values of all attendees
         * to the event.
         *
         * @param attendeesList String: comma-separated usernames
         * @return builder
         */
        public DiscordEventSummaryFieldBuilder withEntityAttendeesDescription(String attendeesList) {
            this.description = new StringBuilder()
                .append(String.format(
                    DISCORD_EVENTS_ATTENDEES_ENUMERATION_FORMAT_STR,
                    attendeesList
                ))
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
