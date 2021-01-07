import * as cdk from '@aws-cdk/core';
import { RemovalPolicy } from '@aws-cdk/core';
import * as ddb from '@aws-cdk/aws-dynamodb';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as ecrAssets from '@aws-cdk/aws-ecr-assets';
import * as ecs from '@aws-cdk/aws-ecs';
import * as ecsPatterns from '@aws-cdk/aws-ecs-patterns';

/**
 * Props for {@link DiscordEventsStack}
 */
export interface DiscordEventsStackProps extends cdk.StackProps {
  ddbPartitionKeyName: string,
  ddbTableName: string,
  clusterName: string,
  serviceName: string,
  environmentVariables?: DiscordEventsEnvVars
}

/**
 * Env vars needed for local build and deployment pipeline
 * for discord-events service.
 */
export interface DiscordEventsEnvVars {
  AWS_DEFAULT_REGION: string,
  DISCORD_BOT_TOKEN: string,
  AWS_ACCESS_KEY_ID: string,
  AWS_SECRET_ACCESS_KEY: string,
  DISCORD_EVENTS_TABLE_NAME: string,
}

/**
 * Stack that defines the AWS resources needed running the
 * discord-events bot.
 */
export class DiscordEventsStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props: DiscordEventsStackProps) {
    super(scope, id, props);

    // Create events table
    new ddb.Table(this, 'DiscordEventsTable', {
      tableName: props.ddbTableName,
      partitionKey: {
        name: props.ddbPartitionKeyName,
        type: ddb.AttributeType.STRING,
      },
      removalPolicy: RemovalPolicy.DESTROY,
    });

    // Create service
    const discordEventsImage: ecrAssets.DockerImageAsset = new ecrAssets.DockerImageAsset(
      this, 'DiscordEventsImage', {
      directory: '../discord-events', // relative to package.json
      buildArgs: {
        ...props.environmentVariables,
      },
    });

    const discordEventsCluster: ecs.Cluster = new ecs.Cluster(this, 'DiscordEventsCluster', {
      clusterName: props.clusterName,
    });

    discordEventsCluster.addCapacity('DiscordEventsClusterScalingGroup', {
      instanceType: new ec2.InstanceType('t2.micro'),
      desiredCapacity: 1,
      maxCapacity: 1,
      minCapacity: 0,
    });

    const discordEventsService: ecsPatterns.ApplicationLoadBalancedEc2Service =
      new ecsPatterns.ApplicationLoadBalancedEc2Service(
      this, 'DiscordEventsService', {
        cluster: discordEventsCluster,
        memoryLimitMiB: 512,
        cpu: 5,
        desiredCount: 1,
        serviceName: props.serviceName,
        publicLoadBalancer: true,
        minHealthyPercent: 0,
        maxHealthyPercent: 100,
        taskImageOptions: {
          image: ecs.ContainerImage.fromDockerImageAsset(discordEventsImage),
          environment: {
            ...props.environmentVariables,
          },
        },
    });
    
    discordEventsImage.repository.grantPull(discordEventsService.taskDefinition.taskRole);
  }
}
