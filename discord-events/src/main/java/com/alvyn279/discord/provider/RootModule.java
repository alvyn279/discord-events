package com.alvyn279.discord.provider;

import com.alvyn279.discord.repository.DiscordEventReactiveRepository;
import com.alvyn279.discord.repository.DiscordEventReactiveRepositoryImpl;
import com.alvyn279.discord.utils.EnvironmentUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/**
 * Main provider module for Guice. It resolves instances of:
 * - Async clients
 * - AWS configuration constants
 */
public class RootModule extends AbstractModule {

    private static final String AWS_DEFAULT_REGION_KEY = "AWS_DEFAULT_REGION";

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    static Region provideRegion() {
        return Region.of(
            EnvironmentUtils.getEnvVar(AWS_DEFAULT_REGION_KEY)
        );
    }

    @Provides
    static DynamoDbAsyncClient provideDynamoDbAsyncClient(Region region) {
        return DynamoDbAsyncClient.builder()
            .region(region)
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
    }

    @Provides
    static DiscordEventReactiveRepository provideDiscordEventReactiveRepository(
        DynamoDbAsyncClient client) {
        return DiscordEventReactiveRepositoryImpl.builder()
            .client(client)
            .build();
    }
}
