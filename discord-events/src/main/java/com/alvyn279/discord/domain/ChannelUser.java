package com.alvyn279.discord.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChannelUser {
    public String name;
    public String role;
}
