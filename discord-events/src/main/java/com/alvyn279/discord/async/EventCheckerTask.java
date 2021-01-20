package com.alvyn279.discord.async;

import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Builder;

/**
 * Task ran on a thread that will check whether there is an
 * event that is coming up soon. It uses the DDB repo and Discord
 * context to notify the subscribed channel.
 */
@Builder
public class EventCheckerTask implements Runnable {

    DiscordEventReactiveRepository repository;
    MessageChannel messageChannel;

    public void run() {
        // TODO: interact with DDB and logic for sending reminder
        messageChannel
            .createEmbed(embedCreateSpec -> embedCreateSpec.setTitle("wasaaaaaaaaa"))
            .then()
            .subscribe();
    }
}
