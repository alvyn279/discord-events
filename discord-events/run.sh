#!/bin/sh

AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION \
DISCORD_BOT_TOKEN=$DISCORD_BOT_TOKEN \
AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
java -jar /bin/run.jar
