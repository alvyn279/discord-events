package com.alvyn279.discord.utils;

import java.util.Map;

public class EnvironmentUtils {
    /**
     * Tries to retrieve the an env var in execution environment variables.
     *
     * @return String Environment variable value
     * @param envVarKey Environment variable name
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
}
