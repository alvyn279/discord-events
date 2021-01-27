#!/bin/sh

# Manually injecting variables here is only for dev environment,
# CDK already injects necessary env vars into container execution
# on prod.
AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION \
DISCORD_BOT_TOKEN=$DISCORD_BOT_TOKEN \
AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
DISCORD_EVENTS_ENV=$DISCORD_EVENTS_ENV \
DISCORD_EVENTS_TABLE_NAME=$DISCORD_EVENTS_TABLE_NAME \
java -jar /bin/run.jar
