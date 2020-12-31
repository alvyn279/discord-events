package com.alvyn279.discord.utils;

import com.alvyn279.discord.domain.ChannelUser;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<ChannelUser> CHANNEL_USERS = Arrays.asList(
        ChannelUser.builder().name("vk").role("non gereur").build(),
        ChannelUser.builder().name("sim").role("non gereur").build(),
        ChannelUser.builder().name("sam").role("non gereur").build(),
        ChannelUser.builder().name("kaks").role("non gereur").build()
    );
}
