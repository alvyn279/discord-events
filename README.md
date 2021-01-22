<div align="center">
    <h2>discord-events</h2>
    <img 
        src="https://i.imgur.com/iNHa4y9.png"
        width=450
    />
    <p>Re-inventing for fun an AWS-backed Discord bot to schedule events</p>
    <code>!help-events</code>
</div>

## 🕌 Design 

### Infra
Java bot runs in a ECS-managed Docker image on an EC2 instance. It uses VPC endpoints to store and read into a DynamoDB Table holding the events. These interactions are hidden from the Internet.

### Bot
Powered by [Discord4J](https://github.com/Discord4J/Discord4J).

### Main features
Manage events (create, view, & delete) for multiple discord servers.

### Reminder feature
Opt-in feature where a separate thread in the bot checks for events and notifies a channel 15 minutes before it occurs.

## 🤖 Running the bot locally

1. Set env vars:
    - `AWS_DEFAULT_REGION`
    - `DISCORD_BOT_TOKEN`
    - `AWS_ACCESS_KEY_ID`
    - `AWS_SECRET_ACCESS_KEY`
    - `DISCORD_EVENTS_TABLE_NAME`
2. Run `docker-compose up`


## ☁ Deploy the bot to AWS
Assuming you have AWS credentials set up,
1. `cd discord-events-cdk`
2. `yarn install && yarn run build`
3. `yarn run cdk deploy DiscordEventsStack`


## 🤮 Hardcoded stuff

1. Timezone defaulted to Eastern Canada, change in `utils/DateUtils.java`
