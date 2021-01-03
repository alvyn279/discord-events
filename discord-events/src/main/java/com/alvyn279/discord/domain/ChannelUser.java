package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Value;

/**
 * Class that defines a user of the discord-events bot.
 */
@Value
@Builder
public class ChannelUser {
    String name;
    String role;
}
