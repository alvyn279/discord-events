package com.alvyn279.discord.utils;

import java.util.Map;

public class EnvironmentUtils {

    private static final String DISCORD_BOT_TOKEN_KEY = "DISCORD_BOT_TOKEN";

    /**
     * Retrieves the required Discord Client token in execution environment
     * variables.
     *
     * @return String token
     * @throws Error Will terminate if the token is not set an env var or
     *               execution does not have the required permissions to read env vars
     */
    public static String getDiscordClientTokenFromEnvVars() {
        try {
            Map<String, String> env = System.getenv();
            if (!env.containsKey(DISCORD_BOT_TOKEN_KEY)) {
                throw new Error("Discord bot token not set as environment variable");
            }
            return env.get(DISCORD_BOT_TOKEN_KEY);
        } catch (SecurityException e) {
            throw new Error("Security policy doesn't allow access to system environment", e);
        }
    }
}
