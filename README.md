# Klassenserver7bBot
[![CodeFactor](https://www.codefactor.io/repository/github/klassenserver7b/klassenserver7bbot/badge)](https://www.codefactor.io/repository/github/klassenserver7b/klassenserver7bbot)
[![License](https://img.shields.io/github/license/klassenserver7b/Klassenserver7bBot.svg)](https://github.com//klassenserver7b/Klassenserver7bBot/blob/master/LICENSE)
[![Build Status](https://jitci.com/gh/klassenserver7b/Klassenserver7bBot/svg)](https://jitci.com/gh/klassenserver7b/Klassenserver7bBot)
[![Latest Release](https://jitpack.io/v/Klassenserver7b/Klassenserver7bbot.svg)](https://jitpack.io/#Klassenserver7b/Klassenserver7bbot)

This is my Discord Bot written in Java with the Java-Discord-API ([JDA](https://github.com/discord-jda/JDA)).

## Features
- List coming soon

## Install

1. Download `k7bot-$VERSION-full.jar` and put it in your `BOT_DIRECTORY`
2. Download [Java JRE 21](https://www.azul.com/downloads/?version=java-21-lts&package=jre) (tested on Azul 21).
3. Run the Bot using `java -jar ./k7bot-$VERSION-full.jar` in your `BOT_DIRECTORY`
4. Create [new Discord Application](https://discord.com/developers/applications) for the Bot
5. Insert your Tokens in the Autogenerated  `BOT_DIRECTORY/resources/bot.properties`
6. Add the Bot to your Server and have fun

## Self compile and building

### Prerequires
- Download/Use [Java JDK 21](https://www.azul.com/downloads/?version=java-21-lts&package=jdk)
- Download/Use [Maven 3.6+](https://maven.apache.org/download.cgi) (Tested on Maven 3.9.6)

### Compile and package
1. Clone the project `git clone https://github.com/klassenserver7b/klassenserver7bbot.git && cd klassenserver7bbot`
2. run 'mvn package'
3. you can now find your jar at ./target/k7bot-$VERSION-full.jar
4. run it with `java -jar YOUR_JAR_FILE_NAME`

## Support

### You can contact me via

- This [GitHub-Repo](https://github.com/klassenserver7b/Klassenserver7bBot/) and my [GitHub Account](https://github.com/klassenserver7b/)
- Discord: "Klassenserver7b"
- [Discord Server](https://discord.gg/EdKD5FE)
- E-Mail: "klassenserver7bwin10@gmail.com"

### For those who want to develop themselves

**Creating a "normal" Discord Chat-Command:**

1. Create a new command by creating a new class and adding `implements ServerCommand` or `implements HypixelCommand` (whether it is a Music/Tool/Moderation command or depends it on "Hypixel" and their API)
2. Insert new commands in the [CommandManager](https://github.com/klassenserver7b/Klassenserver7bBot/blob/master/src/de/k7bot/manage/CommandManager.java) or the [HypixelCommandManager](https://github.com/klassenserver7b/Klassenserver7bBot/blob/master/src/de/k7bot/hypixel/HypixelCommandManager.java)

**Creating a SlashCommand**

1. Create a new command by creating a new class and adding `implements TopLevelSlashCommand`
2. Insert new commands in the [SlashCommandManager](https://github.com/klassenserver7b/Klassenserver7bBot/blob/master/src/de/k7bot/manage/SlashCommandManager.java)
3. Add your required options in the [SlashCommandManager](https://github.com/klassenserver7b/Klassenserver7bBot/blob/master/src/de/k7bot/manage/SlashCommandManager.java) and your option-previews in [ChartsAutoComplete](https://github.com/klassenserver7b/Klassenserver7bBot/blob/master/src/de/k7bot/listener/ChartsAutocomplete.java)

### A DiscordBot by @Klassenserver7b
