package com.alvyn279.discord.utils;

import java.util.Map;

public class EnvironmentUtils {

    private static final String DISCORD_EVENTS_ENV_KEY = "DISCORD_EVENTS_ENV";
    private static final String DISCORD_EVENTS_PROD_IDENTIFIER = "prod";
    private static final String DISCORD_EVENTS_TABLE_NAME_KEY = "DISCORD_EVENTS_TABLE_NAME";
    private static final String DISCORD_EVENTS_TABLE_NAME_NON_PROD_SUFFIX = "Test";

    /**
     * Tries to retrieve the an env var in execution environment variables.
     * Ideally, this method is called at the earliest opportunity in execution.
     *
     * @param envVarKey Environment variable name
     * @return String Environment variable value
     * @throws Error Will terminate if the token is not set an env var or
     *               execution does not have the required permissions to read env vars
     */
    public static String getEnvVar(String envVarKey) {
        try {
            Map<String, String> env = System.getenv();
            if (!env.containsKey(envVarKey)) {
                throw new Error(String.format("`%s` is not set as environment variable", envVarKey));
            }
            return env.get(envVarKey);
        } catch (SecurityException e) {
            throw new Error("Security policy doesn't allow access to system environment", e);
        }
    }

    /**
     * Retrieves the DDB table name in the AWS account based on the
     * execution's environment. If we're on prod, just use the DDB table name
     * that is provided as env var. If not (dev env), append a CDK-compliant
     * suffix.
     *
     * @return DDB string table name
     */
    public static String getDDBTableName() {
        final String env = getEnvVar(DISCORD_EVENTS_ENV_KEY);
        final String ddbTableName = getEnvVar(DISCORD_EVENTS_TABLE_NAME_KEY);

        return env.equals(DISCORD_EVENTS_PROD_IDENTIFIER) ?
            ddbTableName : ddbTableName.concat(DISCORD_EVENTS_TABLE_NAME_NON_PROD_SUFFIX);
    }
}
