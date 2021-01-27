import * as cdk from '@aws-cdk/core';
import { DiscordEventsEnvVars, DiscordEventsStack } from '../lib/discord-events-stack';
import { getEnvVar } from '../lib/utils';

const DDB_PARTITION_KEY = 'guildId';
const DDB_SORT_KEY = 'datetimeCreatedBy';
const CLUSTER_NAME = 'discord-events-cluster';
const SERVICE_NAME = 'discord-events-service';

const AWS_ACCOUNT = getEnvVar('AWS_ACCOUNT');
const AWS_ACCESS_KEY_ID = getEnvVar('AWS_ACCESS_KEY_ID');
const AWS_DEFAULT_REGION = getEnvVar('AWS_DEFAULT_REGION');
const AWS_SECRET_ACCESS_KEY = getEnvVar('AWS_SECRET_ACCESS_KEY');
const DISCORD_BOT_TOKEN = getEnvVar('DISCORD_BOT_TOKEN');
const DISCORD_EVENTS_TABLE_NAME = getEnvVar('DISCORD_EVENTS_TABLE_NAME');
const DISCORD_EVENTS_ENV = getEnvVar('DISCORD_EVENTS_ENV');

const app = new cdk.App();

const env: cdk.Environment = {
  account: AWS_ACCOUNT,
  region: AWS_DEFAULT_REGION,
};

const isProd: boolean = DISCORD_EVENTS_ENV === 'prod';
const stackId: string = isProd ? 'DiscordEventsStack' : 'DiscordEventsDevStack';

// Have this same suffix handling for DDB table name in
// https://github.com/alvyn279/discord-events/blob/main/discord-events/src/main/java/com/alvyn279/discord/utils/EnvironmentUtils.java
const ddbTableName: string = isProd ? DISCORD_EVENTS_TABLE_NAME : `${DISCORD_EVENTS_TABLE_NAME}Test`;

const ENV_VARS: DiscordEventsEnvVars = {
  AWS_DEFAULT_REGION,
  DISCORD_BOT_TOKEN,
  AWS_ACCESS_KEY_ID,
  AWS_SECRET_ACCESS_KEY,
  DISCORD_EVENTS_TABLE_NAME: ddbTableName,
  DISCORD_EVENTS_ENV,
};

new DiscordEventsStack(app, stackId, {
  clusterName: CLUSTER_NAME,
  ddbPartitionKeyName: DDB_PARTITION_KEY,
  ddbSortKeyName: DDB_SORT_KEY,
  ddbTableName,
  env,
  environmentVariables: ENV_VARS,
  isProd,
  serviceName: SERVICE_NAME,
});
