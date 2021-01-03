package com.alvyn279.discord.repository;

import com.alvyn279.discord.domain.DiscordEvent;
import com.google.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/**
 * Implementation of {@link DiscordEventRepository} where the repository uses
 * an async DDB client for its operations.
 *
 * It implements read/writes in a reactive manner.
 */
public class DiscordEventRepositoryImpl implements DiscordEventRepository {

    private final DynamoDbAsyncClient client;

    @Inject
    DiscordEventRepositoryImpl(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @Override
    public DiscordEvent saveDiscordEvent(DiscordEvent discordEvent) {
        // TODO: implement save to db
        return null;
    }
}
