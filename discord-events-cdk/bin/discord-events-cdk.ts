import * as cdk from '@aws-cdk/core';
import { DiscordEventsCdkStack } from '../lib/discord-events-cdk-stack';

const app = new cdk.App();
new DiscordEventsCdkStack(app, 'DiscordEventsCdkStack');
