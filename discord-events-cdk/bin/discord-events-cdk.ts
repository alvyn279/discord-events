import * as cdk from '@aws-cdk/core';
import { DiscordEventsStack } from '../lib/discord-events-stack';

const AWS_ACCOUNT = '459641237997';
const PARTITION_KEY = 'eventId';

const app = new cdk.App();

const env: cdk.Environment = {
  account: AWS_ACCOUNT,
  region: 'us-east-1',
};

new DiscordEventsStack(app, 'DiscordEventsCdkStack', {
  env,
  partitionKeyName: PARTITION_KEY,
});
