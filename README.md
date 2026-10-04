# Villager News Addon NeoForge Port

A **NeoForge 1.21.1 port** of the **Villager News Add-On** for Minecraft Java
Edition.

This project brings the original Villager News characters, models, animations,
textures, voice acting, and contextual dialogue to Minecraft Java Edition while
retaining normal Minecraft villager gameplay.

Current release: **1.0.0**

## Features

* Detailed animated Villager News models converted for Entity Model Features
* Biome, profession, and profession-level villager textures
* The Mayor, Testificate Man, Villager Number 5, Villager Number 9, and
  Villager Unreachable as named characters
* Wooly the Sheep and the Villager News wandering trader
* 2,212 original voice clips across 523 dialogue groups
* 22 original short reaction effects, including synchronized villager and
  wandering-trader hurt effects
* Context-aware dialogue for player actions, nearby mobs, weather, dimensions,
  combat, trading, work, sleep, spawning, growth, and other world events
* Multi-part conversations between nearby villagers
* Facial expressions and gestures synchronized with each voice line
* Server-controlled dialogue selection, sound playback, cooldowns, and
  villager behavior
* Speakers look toward the player, entity, block, or villager they are talking
  about
* Removable villager noses, character cosmetics, cosmetic reactions, and
  missing-nose conversations
* Character trades for the Mayor Hat, Testificate Man Helmet, Moustache, and
  Microphone
* Persistent natural spawning for one of each special character in distant
  villages
* A craftable Villager News Handbook
* Optional Mod Menu configuration screen

## Requirements

* **Minecraft Java Edition 1.21.1**
* **NeoForge 21.1.252 or newer**
* **Entity Model Features (EMF) 3.3.9 or newer**
* **Entity Texture Features (ETF) 7.2.4 or newer**
* **Entity Sound Features (ESF) 0.8.2 or newer**

EMF, ETF, and ESF are **required external dependencies**. They are not bundled
with this project.

NeoForge will prevent the mod from loading if any of the required dependencies
are missing or below the minimum supported version.

## Installation

1. Install **Minecraft Java Edition 1.21.1**.
2. Install **NeoForge 21.1.252 or newer** for Minecraft 1.21.1.
3. Install the required versions of:

   * Entity Model Features (EMF) 3.3.9 or newer
   * Entity Texture Features (ETF) 7.2.4 or newer
   * Entity Sound Features (ESF) 0.8.2 or newer
4. Place the Villager News Addon NeoForge Port jar and all required dependency
   jars into your Minecraft `mods` folder.
5. Launch Minecraft using NeoForge.

For multiplayer, the mod and its required dependencies should be installed on
both the server and connecting clients. The dialogue system is controlled by
the server, while EMF, ETF, and ESF provide the client-side model, texture,
and sound functionality required by the mod.

## Characters

Use a name tag on a villager to select a character model and voice:

| Name tag                                    | Character            |
| ------------------------------------------- | -------------------- |
| `Mayor`, `Mayor Villager`, or `The Mayor`   | Mayor Villager       |
| `Testificate Man`                           | Testificate Man      |
| `Villager Number 5` or `Villager #5`        | Villager Number 5    |
| `Villager Number 9` or `Villager #9`        | Villager Number 9    |
| `Villager Unreachable` or `Can't Catch Me!` | Villager Unreachable |

Name a sheep `Wooly` or `Wooly The Sheep` to use Wooly's model, animations,
and sounds.

Ordinary villagers and wandering traders receive their Villager News
appearance and dialogue automatically.

Special characters can also appear naturally as new distant villages are
generated. Each character appears once at a time and becomes eligible to spawn
again after being killed.

## Items

All custom items are available in the **Villager News** creative-mode tab.

Craft the Villager News Handbook from three pieces of paper. It includes the
add-on's overview, special-character and cosmetic guides, settings reference,
and the complete searchable Triggers & Reactions guide.

Shear an adult villager to remove its nose. Interact with that villager while
holding the nose to return it.

The Mayor, Testificate Man, Villager #5, and Villager #9 sell their matching
cosmetics. Cosmetics can be given to ordinary villagers and removed again with
shears.

## Dialogue

Villagers react to what happens around them. They can comment when a player
approaches, stares, changes game mode, wears armor, receives an effect, breaks
or places a block, uses an item, completes a trade, or spawns a villager with a
spawn egg.

They also react to their profession, workstation, level, biome, weather, time
of day, nearby entities, damage source, and other villagers.

The server chooses the exact voice variant and broadcasts its matching
animation. Each speaker remains occupied for the real length of the clip,
preventing unrelated lines from overlapping.

Conversation partners take turns and continue looking at each other throughout
multi-part exchanges.

## Building from Source

This repository contains the NeoForge 1.21.1 source for the port.

### Prerequisites

You will need:

* **JDK 21**
* Git
* A working internet connection so Gradle can download the required build
  dependencies

Clone the repository and switch to the NeoForge 1.21.1 branch:

```bash
git clone https://github.com/Akira-Media/villager-news-addon-neoforge-port.git
cd villager-news-addon-neoforge-port
git checkout neoforge-1.21.1
```

On Windows, build the project with:

```powershell
.\gradlew.bat clean build
```

On Linux or macOS:

```bash
./gradlew clean build
```

The distributable mod jar will be created in:

```text
build/libs/
```

For the 1.0.0 release, the resulting artifact is:

```text
villager_news_addon_port-1.0.0.jar
```

### Development dialogue test command

The project includes an operator-only dialogue test command for development
builds.

To enable it, set:

```properties
dialogue_test_command=true
```

in `gradle.properties` before building.

In-game, use:

```text
/dialoguetest <1-523>
```

to spawn the matching speaker and subject and play the dialogue group.

You can also use:

```text
/dialoguetest continuous
```

to run all 523 dialogue groups in sequence.

The setting defaults to `false` for release builds.

## Credits

- **Original Villager News Add-On:** Oreville Studios Ltd / Element Animation
- **Original Java port:** MarcYohannTheScripter — [villager-news-bedrock-addon-java-port](https://github.com/MarcYohannTheScripter/villager-news-bedrock-addon-java-port)
- **NeoForge 1.21.1 port:** Akira-Media / marcy

This project is a NeoForge 1.21.1 port based on the original Villager News Add-On
and the existing Java port listed above.

The original characters, models, textures, animations, sounds, voice acting,
and other original assets remain the property of their respective creators.

See [LICENSE](LICENSE) for licensing information.

### AI Assistance

LLM-based AI tools were used to assist with the porting process, particularly
with adapting the project from the original **Minecraft 26.3 Fabric version**
to **Minecraft 1.21.1 NeoForge**, including differences between Minecraft
versions, loaders, and APIs.

The resulting changes were reviewed, integrated, and tested during development.
