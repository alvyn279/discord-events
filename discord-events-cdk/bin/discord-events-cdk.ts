import * as cdk from '@aws-cdk/core';
import { DiscordEventsEnvVars, DiscordEventsStack } from '../lib/discord-events-stack';
import { getEnvVar } from '../lib/utils';

const AWS_ACCOUNT = '459641237997';
const PARTITION_KEY = 'eventId';
const CLUSTER_NAME = 'discord-events-cluster';
const SERVICE_NAME = 'discord-events-service';

const AWS_DEFAULT_REGION = getEnvVar('AWS_DEFAULT_REGION');
const DISCORD_BOT_TOKEN = getEnvVar('DISCORD_BOT_TOKEN');
const AWS_ACCESS_KEY_ID = getEnvVar('AWS_ACCESS_KEY_ID');
const AWS_SECRET_ACCESS_KEY = getEnvVar('AWS_SECRET_ACCESS_KEY');

const ENV_VARS = {
  AWS_DEFAULT_REGION,
  DISCORD_BOT_TOKEN,
  AWS_ACCESS_KEY_ID,
  AWS_SECRET_ACCESS_KEY,
} as DiscordEventsEnvVars;

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
  partitionKeyName: PARTITION_KEY,
});
