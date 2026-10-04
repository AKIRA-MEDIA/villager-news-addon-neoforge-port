# Villager News Addon NeoForge Port

A public **NeoForge 1.21.1 fork** of the Villager News Java Edition port, maintained by Akira-Media.

It brings the Villager News characters, models, animations, textures, voice acting and contextual dialogue to Minecraft Java Edition 1.21.1 on NeoForge, while keeping normal Minecraft villager gameplay.

This fork is based on the Fabric port for Minecraft 26.3 by MarcYohannTheScripter, which is itself based on the original Villager News Add-On by Oreville Studios Ltd and Element Animation. See [Credits](#credits).

Version in this repository: **1.0.2**

## Features

* Detailed animated Villager News models converted for Entity Model Features
* Biome, profession, and profession-level villager textures
* The Mayor, Testificate Man, Villager Number 5, Villager Number 9, and Villager Unreachable as named characters
* Wooly the Sheep, in both woolly and sheared forms, and the Villager News wandering trader
* A baby villager model of its own
* 2,212 original voice clips across 523 dialogue groups
* 22 original short reaction effects, including synchronized villager and wandering-trader hurt effects
* Context-aware dialogue for player actions, nearby mobs, weather, dimensions, combat, trading, work, sleep, spawning, growth, and other world events
* Multi-part conversations between nearby villagers
* Facial expressions, lip sync and gestures synchronized with each voice line, plus a turn-in-place animation
* Server-controlled dialogue selection, sound playback, cooldowns, and villager behavior
* Speakers look toward the player, entity, block, or villager they are talking about
* Removable villager noses, character cosmetics, cosmetic reactions, and missing-nose conversations
* Sign boards that villagers can hold, with 87 messages
* Character trades for the Mayor Hat, Testificate Man Helmet, Moustache, and Microphone
* Persistent natural spawning for one of each special character in distant villages
* A craftable Villager News Handbook
* A settings screen, opened from the handbook or from the config button in the NeoForge Mods list
* A setting that makes the Villager News models and animations take priority over resource packs such as Fresh Animations
* An operator-only `/dialoguetest` command for checking any dialogue group

## What is different in this fork

This is a port of the Fabric / Minecraft 26.3 version to a much older game version and a different loader, so some things had to be done another way.

| Area | Fabric port (Minecraft 26.3) | This fork (NeoForge 1.21.1) |
| --- | --- | --- |
| Platform | Fabric loader and Fabric API, Java 25 | NeoForge 21.1.x, Java 21 |
| Config screen | Mod Menu | Config button in the NeoForge Mods list; Mod Menu is not used |
| Events, networking, commands | Fabric API callbacks and packets | NeoForge events, payload registration and command events |
| Mod metadata | `fabric.mod.json` | `neoforge.mods.toml` |
| Item models | 26.x item definitions | 1.21.1 model layout, using `neoforge:separate_transforms` for the worn hat, handbook and cosmetics; flat item icons use front lighting |
| Spawn eggs | Dedicated textures | Registered with a white tint so the textures show unchanged |
| Sign board on villagers | Render-state layer using 26.x sign textures | Rewritten as a 1.21.1 render layer using the `entity/signs` sheets. 1.21.1 has no pale oak sign, so that slot draws oak |
| Villager held items | Positioned by an EMF attachment | Drawn at vanilla's own position by a client mixin; EMF's position came out low and upside down in 1.21.1 |
| Baby villagers | Own model layer | Chosen by a model rule in `villager.properties`, drawn at twice the size to cancel vanilla's baby scaling, with the clothing painted into the skin sheet by hand |
| Wooly the sheep | Model attached to the sheep root | 1.21.1 sheep have no root part, so the model is attached to the body with a rotation and offset correction |
| Vanilla trade and celebrate sounds | Removed by a mixin | Cancelled on the server by an event handler |
| Baby villager spawn-egg reaction | Mixin on `SpawnEggItem` | NeoForge events (the mixin failed on 1.21.1) |
| Turn-in-place animation | Fed from a renderer mixin | Fed from a render event |
| `/dialoguetest` | Only if a build flag is set | Always registered, operator permission only |
| Resource pack priority | Resource packs always override the mod's models | Optional setting (on by default) that puts the mod's models above resource packs such as Fresh Animations |
| Mixins | Includes render-state and profession-layer mixins | Six common mixins and two client mixins; the render-state and profession-layer mixins were not needed in 1.21.1 |

## Requirements

* **Minecraft Java Edition 1.21.1**
* **NeoForge 21.1.252 or newer**
* **Entity Model Features (EMF) 3.3.9 or newer**
* **Entity Texture Features (ETF) 7.2.4 or newer**
* **Entity Sound Features (ESF) 0.8.2 or newer**

EMF, ETF, and ESF are **required external dependencies**. They are not bundled with this project.

NeoForge will prevent the mod from loading if any of the required dependencies are missing or below the minimum supported version.

## Installation

1. Install **Minecraft Java Edition 1.21.1**.
2. Install **NeoForge 21.1.252 or newer** for Minecraft 1.21.1.
3. Install the required versions of:

   * Entity Model Features (EMF) 3.3.9 or newer
   * Entity Texture Features (ETF) 7.2.4 or newer
   * Entity Sound Features (ESF) 0.8.2 or newer
4. Place the Villager News Addon NeoForge Port jar and all required dependency jars into your Minecraft `mods` folder.
5. Launch Minecraft using NeoForge.

For multiplayer, the mod and its required dependencies should be installed on both the server and connecting clients. The dialogue system is controlled by the server, while EMF, ETF, and ESF provide the client-side model, texture, and sound functionality required by the mod.

## Characters

Use a name tag on a villager to select a character model and voice:

| Name tag                                    | Character            |
| ------------------------------------------- | -------------------- |
| `Mayor`, `Mayor Villager`, or `The Mayor`   | Mayor Villager       |
| `Testificate Man`                           | Testificate Man      |
| `Villager Number 5` or `Villager #5`        | Villager Number 5    |
| `Villager Number 9` or `Villager #9`        | Villager Number 9    |
| `Villager Unreachable` or `Can't Catch Me!` | Villager Unreachable |

Name a sheep `Wooly` or `Wooly The Sheep` to use Wooly's model, animations, and sounds.

Ordinary villagers and wandering traders receive their Villager News appearance and dialogue automatically.

Special characters can also appear naturally as new distant villages are generated. Each character appears once at a time and becomes eligible to spawn again after being killed.

A villager named `jeb_` gets a rainbow nose.

## Items

All custom items are available in the **Villager News** creative-mode tab, including a spawn egg for each special character.

Craft the Villager News Handbook from three pieces of paper. It includes the add-on's overview, special-character and cosmetic guides, settings reference, and the complete searchable Triggers & Reactions guide.

Shear an adult villager to remove its nose. Interact with that villager while holding the nose to return it.

The Mayor, Testificate Man, Villager #5, and Villager #9 sell their matching cosmetics. Cosmetics can be given to ordinary villagers and removed again with shears.

Right-click a villager while holding a sign to give it a sign board. An axe cycles through the messages, and shears take the sign back.

## Dialogue

Villagers react to what happens around them. They can comment when a player approaches, stares, changes game mode, wears armor, receives an effect, breaks or places a block, uses an item, completes a trade, or spawns a villager with a spawn egg.

They also react to their profession, workstation, level, biome, weather, time of day, nearby entities, damage source, and other villagers.

The server chooses the exact voice variant and broadcasts its matching animation. Each speaker remains occupied for the real length of the clip, preventing unrelated lines from overlapping.

Conversation partners take turns and continue looking at each other throughout multi-part exchanges.

The vanilla villager and wandering trader yes, no and celebrate sounds are silenced so that only the Villager News voice lines play.

## Configuration

Open the settings from the Villager News Handbook, or use the config button next to the mod in the NeoForge Mods list. Settings are saved to your config folder. Settings that affect dialogue are controlled by the server, and only operators can change them on a multiplayer server. The **Override Resource Packs** setting is stored on your own computer and applies only to you; changing it reloads your resource packs.

## Compatibility and known issues

* **Resource packs that replace villager or sheep models (for example Fresh Animations)** normally override this mod's models and animations, because resource packs take priority over mod resources. The **Override Resource Packs** setting (on by default) puts the model, rule and texture files this mod ships above every resource pack, so its villagers, wandering trader and Wooly keep their models and animations; turn it off to let the resource pack win. Both replace the whole villager model, so they cannot be merged, and the setting only covers the files this mod ships. See [upstream issue 5](https://github.com/MarcYohannTheScripter/villager-news-bedrock-addon-java-port/issues/5).
* **Reaction lines for mobs newer than 1.21.1.** The creaking, copper golem, happy ghast and sulfur cube lines exist, but those mobs do not exist in 1.21.1. They only trigger on a mob that has been renamed to that name (for example a zombie named `Creaking`).
* **Baby villager clothing** is painted into the skin sheet by hand, because 1.21.1 does not draw the clothing layer on babies. The colours are close to, but not exactly, the original.
* A short flicker-smoothing step that the Fabric port applied to villager clothing changes was not recreated. No flicker has been observed in 1.21.1.
* Not every dialogue group and trigger has been played and checked in game, and dedicated-server and multiplayer testing has been limited.

If you find a problem, please open an issue (see [Reporting problems](#reporting-problems)).

## Building from Source

This repository contains the NeoForge 1.21.1 source for the port.

### Prerequisites

You will need:

* **JDK 21**
* Git
* A working internet connection so Gradle can download the required build dependencies
* The EMF, ETF and ESF jars for NeoForge 1.21, which are not included in this repository:

  * `entity_model_features-3.3.9-1.21-neoforge.jar`
  * `entity_texture_features-7.2.4-1.21-neoforge.jar`
  * `entity_sound_features-0.8.2-1.21-neoforge.jar`

  Download them from their official pages. Put a copy of each in `libs/` (needed to compile) and in `run/mods/` (needed for `runClient`). Create the folders if they do not exist.

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

For the current version, the resulting artifact is:

```text
villager_news_addon_port-1.0.2.jar
```

Other useful tasks:

| Task | Purpose |
| --- | --- |
| `./gradlew runClient` | Start a development client |
| `./gradlew runServer` | Start a development server |
| `./gradlew compileJava` | Compile only |
| `./gradlew processResources` | Copy resources into the build (then press `F3+T` in game to reload) |

The original Fabric source remains available in the upstream repository.

### Development dialogue test command

The project includes an operator-only dialogue test command. It is registered in every build, and needs operator permission (cheats on, or `/op` on a server).

In-game, use:

```text
/dialoguetest <1-523>
```

to spawn the matching speaker and subject and play the dialogue group.

You can also use:

```text
/dialoguetest continuous
```

to run all 523 dialogue groups in sequence. This takes hours, so it is best used for part of the list while watching the log.

Groups that involve mobs newer than 1.21.1 report that the subject could not be summoned.

## Testing

This port has been tested in a development client and in a clean Prism Launcher instance (NeoForge 21.1.255) containing only this mod and EMF, ETF and ESF. Testing covered startup and mixin log checks, a comparison of files and methods against the original source, `/dialoguetest` groups, and play of trades, bells, sleeping, babies, spawn eggs, cosmetics, signs, Wooly and the handbook. There are no automated tests.

## Reporting problems

Open an issue at <https://github.com/Akira-Media/villager-news-addon-neoforge-port/issues> and include:

* Your `logs/latest.log` (and `logs/debug.log` if a mixin is involved)
* Your NeoForge, EMF, ETF and ESF versions
* The steps to reproduce the problem

Problems that also happen in the Fabric port are best reported upstream.

## Credits

- **Original Villager News Add-On:** Oreville Studios Ltd / Element Animation
- **Original Java port (Fabric, Minecraft 26.3):** MarcYohannTheScripter (marcy) - [villager-news-bedrock-addon-java-port](https://github.com/MarcYohannTheScripter/villager-news-bedrock-addon-java-port)
- **NeoForge 1.21.1 port and maintenance:** Akira-Media

This project is a NeoForge 1.21.1 port based on the original Villager News Add-On and the existing Java port listed above.

The original characters, models, textures, animations, sounds, voice acting, and other original assets remain the property of their respective creators.

The NeoForge port code and changes written by Akira-Media are free to use under the MIT licence (see [LICENSE-PORT](LICENSE-PORT)): you may use, modify and redistribute them, including in your own projects. This does not cover the original Villager News models, textures, animations, sounds, voice acting and dialogue, which remain the property of their creators, or the code inherited from the upstream Java port, which is covered only as described in [LICENSE](LICENSE) (the Fabric template code is CC0).

### AI Assistance

This port was produced with substantial help from an LLM-based AI assistant (Claude, made by Anthropic).
