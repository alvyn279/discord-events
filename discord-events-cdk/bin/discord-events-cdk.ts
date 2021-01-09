import * as cdk from '@aws-cdk/core';
import { DiscordEventsEnvVars, DiscordEventsStack } from '../lib/discord-events-stack';
import { getEnvVar } from '../lib/utils';

const AWS_ACCOUNT = '459641237997';
const DDB_PARTITION_KEY = 'guildId';
const DDB_SORT_KEY = 'datetimeCreatedBy';
const CLUSTER_NAME = 'discord-events-cluster';
const SERVICE_NAME = 'discord-events-service';

const AWS_DEFAULT_REGION = getEnvVar('AWS_DEFAULT_REGION');
const DISCORD_BOT_TOKEN = getEnvVar('DISCORD_BOT_TOKEN');
const AWS_ACCESS_KEY_ID = getEnvVar('AWS_ACCESS_KEY_ID');
const AWS_SECRET_ACCESS_KEY = getEnvVar('AWS_SECRET_ACCESS_KEY');
const DISCORD_EVENTS_TABLE_NAME = getEnvVar('DISCORD_EVENTS_TABLE_NAME');

const ENV_VARS: DiscordEventsEnvVars = {
  AWS_DEFAULT_REGION,
  DISCORD_BOT_TOKEN,
  AWS_ACCESS_KEY_ID,
  AWS_SECRET_ACCESS_KEY,
  DISCORD_EVENTS_TABLE_NAME
};

const app = new cdk.App();

const env: cdk.Environment = {
  account: AWS_ACCOUNT,
  region: AWS_DEFAULT_REGION,
};

new DiscordEventsStack(app, 'DiscordEventsStack', {
  env,
  environmentVariables: ENV_VARS,
  clusterName: CLUSTER_NAME,
  serviceName: SERVICE_NAME,
  ddbPartitionKeyName: DDB_PARTITION_KEY,
  ddbSortKeyName: DDB_SORT_KEY,
  ddbTableName: DISCORD_EVENTS_TABLE_NAME,
});
