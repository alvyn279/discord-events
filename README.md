<div align="center">
    <h2>discord-events</h2>
    <img 
        src="https://i.imgur.com/iNHa4y9.png"
        width=450
    />
    <p>Re-inventing for fun an AWS-backed Discord bot to schedule events.</p>
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

## 🛠 Bot Setup

- **Prod bot**: Auto-scaled ECS service running the Java app with its own AWS DDB table
- **Dev bot**: Containerized Java app running locally with its own AWS DDB table 

1. You can have two application instances: `discord-events-bot` for prod and `discord-events-bot-test` for dev. Create an application and associated bot for each desired instance through [Discord developer portal](https://discord.com/developers/applications/).
2. Enable all *Privileged Gateway Intents* for each bot on the Discord developer portal.
3. Add each bot to your desired servers with the following link (use CLIENT ID token from developer portal): `https://discordapp.com/api/oauth2/authorize?scope=bot&client_id=<YOUR CLIENT ID>`.
4. Set env vars in your shell:
    | Name  | Description |
    | ------------- | ------------- |
    | `AWS_ACCOUNT`  | AWS account ID  |
    | `AWS_DEFAULT_REGION`  | AWS region (ex: `us-east-1`)  |
    | `AWS_ACCESS_KEY_ID` *  | AWS credentials  |
    | `AWS_SECRET_ACCESS_KEY` *  | AWS credentials  |
    | `DISCORD_BOT_TOKEN`  | Bot token from Discord Developer Portal  |
    | `DISCORD_EVENTS_ENV`  | Execution env for discord-events (`dev`\|`prod`)  |
    | `DISCORD_EVENTS_TABLE_NAME`  | Name your DDB table, the app will differentiate that of dev and prod  |

<small>\* Any AWS credentials setup can replace these for the bot's Java execution.</small>

### Switching between prod and dev bots
You can switch bots by changing the following env vars in your shell:
- `DISCORD_BOT_TOKEN`
- `DISCORD_EVENTS_ENV`

Aliases should do the trick:
```sh
alias botProd="export DISCORD_BOT_TOKEN=<your prod bot token> && export DISCORD_EVENTS_ENV=prod"
alias botDev="export DISCORD_BOT_TOKEN=<your dev bot token> && export DISCORD_EVENTS_ENV=dev"
```

## 🤖 Running the bot locally
1. Deploy the CDK dev stack
    1. [Switch to your dev bot](#switching-between-prod-and-dev-bots)
    2. `cd discord-events-cdk`
    3. `yarn install && yarn run build`
    4. `yarn run cdk deploy DiscordEventsDevStack`
2. From root dir, `docker-compose up`


## ☁ Deploy the bot to AWS

1. [Switch to your prod bot](#switching-between-prod-and-dev-bots)
2. `cd discord-events-cdk`
3. `yarn install && yarn run build`
4. `yarn run cdk deploy DiscordEventsStack`

You can always have your CI/CD pipeline deploy the newest version of your bot, just make sure that all the necessary environment variables (as indicated in setup) are set in the pipeline execution.


## 🤮 Hardcoded stuff

1. Timezone defaulted to Eastern Canada, change in `utils/DateUtils.java`.


## 👩🏾‍💻Usage

| Command  | Example |
| ------------- | ------------- |
| `!create-event [title:str] [date:date] [time:time] [description:str]?` | <img src="https://i.imgur.com/LsWoRyZ.png" width=450 /> |
| `!delete-events [deleteCode:str]*`  | <img src="https://i.imgur.com/6PRA5b5.png" width=450 /> |
| `!my-events` | <img src="https://i.imgur.com/UYlbMV8.png" width=450 />  |
| `!list-events [[upcoming:num]] [[on:date]] [[from:date] [to:date]]`  | <img src="https://i.imgur.com/b09TUPR.png" width=450 /> |
| `!remind-events [on|off:str]?`  | <img src="https://i.imgur.com/G7eTOVh.png" width=450 />  |
| `!help-events`  | <img src="https://i.imgur.com/VXYxM0w.png" width=450 />  |


## 📖 Resources
* [How to Make a Discord Bot: an Overview and Tutorial](https://www.toptal.com/chatbot/how-to-make-a-discord-bot)
