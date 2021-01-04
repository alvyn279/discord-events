import * as cdk from '@aws-cdk/core';
import { RemovalPolicy } from '@aws-cdk/core';
import * as ddb from '@aws-cdk/aws-dynamodb';

/**
 * Props for {@link DiscordEventsStack}
 */
export interface DiscordEventsStackProps extends cdk.StackProps {
  partitionKeyName: string,
}

/**
 * Stack that defines the AWS resources needed running the
 * discord-events bot.
 */
export class DiscordEventsStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props: DiscordEventsStackProps) {
    super(scope, id, props);

    new ddb.Table(this, 'DiscordEventsTable', {
      partitionKey: {
        name: props.partitionKeyName,
        type: ddb.AttributeType.STRING,
      },
      removalPolicy: RemovalPolicy.DESTROY,
    });
  }
}
