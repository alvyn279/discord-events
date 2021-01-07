/**
 * Gets the environment variable or throws an Error
 *
 * @param envVarKey
 */
export const getEnvVar = (envVarKey: string): string => {
  if (!process.env[envVarKey]) {
    throw new Error(`Missing environment variable ${envVarKey}`)
  }
  return process.env[envVarKey] as string;
}
