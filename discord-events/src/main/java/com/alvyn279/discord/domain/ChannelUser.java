package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Class that defines a user of the discord-events bot.
 */
@Data
@Builder
public class ChannelUser {
    private final String name;
    private final String role;
}
