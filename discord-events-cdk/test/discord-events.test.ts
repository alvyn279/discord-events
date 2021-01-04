import { expect as expectCDK, haveResourceLike } from '@aws-cdk/assert';
import * as cdk from '@aws-cdk/core';
import * as DiscordEventsCdk from '../lib/discord-events-stack';

test('Discord Events Stack', () => {
  // GIVEN
  const app = new cdk.App();

  // WHEN
  const stack = new DiscordEventsCdk.DiscordEventsStack(app, 'MyTestStack', {
    env: {
      account: 'random',
      region: 'us-east-1',
    },
    partitionKeyName: 'randomPartitionKeyName',
  });

  // THEN
  expectCDK(stack).to(haveResourceLike('AWS::DynamoDB::Table', {
    KeySchema: [
      {
        AttributeName: 'randomPartitionKeyName',
        KeyType: 'HASH',
      },
    ],
    AttributeDefinitions: [
      {
        AttributeName: 'randomPartitionKeyName',
        AttributeType: 'S',
      },
    ],
    ProvisionedThroughput: {
      ReadCapacityUnits: 5,
      WriteCapacityUnits: 5
    },
  }));
});
