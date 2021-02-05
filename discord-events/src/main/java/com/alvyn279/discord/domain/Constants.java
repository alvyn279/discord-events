package com.alvyn279.discord.domain;

import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Constants {

    public static final List<ChannelUser> CHANNEL_USERS = Arrays.asList(
        ChannelUser.builder().name("vk").role("non gereur").build(),
        ChannelUser.builder().name("sim").role("non gereur").build(),
        ChannelUser.builder().name("sam").role("non gereur").build(),
        ChannelUser.builder().name("kaks").role("non gereur").build()
    );

    public static final Map<Integer, String> NUMBER_TO_RAW_EMOJI_STRING = ImmutableMap
        .<Integer, String>builder()
        .put(0, "\u0030\u20E3")
        .put(1, "\u0031\u20E3")
        .put(2, "\u0032\u20E3")
        .put(3, "\u0033\u20E3")
        .put(4, "\u0034\u20E3")
        .put(5, "\u0035\u20E3")
        .put(6, "\u0036\u20E3")
        .put(7, "\u0037\u20E3")
        .put(8, "\u0038\u20E3")
        .put(9, "\u0039\u20E3")
        .build();
}
