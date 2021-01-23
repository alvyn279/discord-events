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

## 🛠 Setup

1. Create an application and associated bot through [Discord developer portal](https://discord.com/developers/applications/).
2. Enable all *Privileged Gateway Intents* for your bot on the Discord developer portal.
3. Add bot to your desired servers with the following link (use CLIENT ID token from developer portal): `https://discordapp.com/api/oauth2/authorize?scope=bot&client_id=<YOUR CLIENT ID>`
4. Set env vars in your shell:
    - `AWS_DEFAULT_REGION`
    - `AWS_ACCESS_KEY_ID` *
    - `AWS_SECRET_ACCESS_KEY` *
    - `DISCORD_BOT_TOKEN`
    - `DISCORD_EVENTS_TABLE_NAME`

\* Any AWS credentials setup can replace these for the bot's Java execution.

## 🤖 Running the bot locally
1. Run `docker-compose up`


## ☁ Deploy the bot to AWS

Assuming you have AWS credentials set up,
1. `cd discord-events-cdk`
2. `yarn install && yarn run build`
3. `yarn run cdk deploy DiscordEventsStack`

You can always have your CI/CD pipeline deploy the newest version of your bot, just make sure that all the necessary environment variables (as indicated in setup) are set in the pipeline execution.


## 🤮 Hardcoded stuff

1. Timezone defaulted to Eastern Canada, change in `utils/DateUtils.java`.


## Documentation
* [How to Make a Discord Bot: an Overview and Tutorial](https://www.toptal.com/chatbot/how-to-make-a-discord-bot)
