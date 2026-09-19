# Port Plan

## Purpose and scope

This is an accuracy plan for the Java port of **Villager News 1.0 Add-On**. It is based on a second pass through the complete extracted Bedrock behavior pack and resource pack, plus a static comparison with the current Java source and generated assets.

This document records findings only. Creating it did not change Java code, resource assets, generated assets, configuration, or the build.

The aim is to preserve normal Java Edition gameplay while making the port behave like the Bedrock add-on. It should continue to require EMF, ETF, and ESF; none of those mods should be edited or bundled.

## Evidence inspected

| Source | What was checked |
| --- | --- |
| `build/deobfuscated-bedrock-source/full-addon/behavior-pack` | 9 entity definitions, 6 item definitions, 1 recipe, 4 trade tables, 3 functions, the behavior animation/controller, and the complete script event graph |
| `build/deobfuscated-bedrock-source/full-addon/resource-pack` | 9 client-entity definitions, 6 attachables, 2 animation files, 2 animation controllers, 1 render-controller file, 1 material, 400 PNGs, 91 TGAs, and 2,235 OGG sound definitions/files |
| `build/deobfuscated-bedrock-source/full-addon/behavior-pack/scripts/oreville/ebi.js` | The 523 dialogue symbols, non-dialogue event handlers, entity/proxy lifecycle, settings, sound-effect mappings, and timing rules |
| `src/main/java` and `src/main/resources` | Current Fabric behavior, network payloads, CEM/ETF assets, settings UI, generated dialogue catalog, subtitles, items, and mixins |

The Bedrock script is minified and many non-dialogue symbol names remain obfuscated. Its dialogue symbols have been resolved to their real group IDs. Statements below are labelled as **confirmed** when they come directly from readable data or control flow, and as **verify** when the source establishes a feature but its precise semantics still need a side-by-side game test.

## Current coverage at a glance

| Area | Bedrock add-on | Current Java port | Result |
| --- | ---: | ---: | --- |
| Dialogue groups | 523 | 523 referenced by `ContextualDialogueController` | Static group coverage is complete |
| Dialogue voice variants | 2,212 | 2,212 registered sound definitions | Static voice coverage is complete |
| Guide contexts | 491 contexts in 12 categories / 62 sections | 491 contexts in the handbook | Content is present; UI behavior still needs comparison |
| Supplemental sounds | 23 beyond the dialogue clips | 22 classified clips registered; 17 source hurt effects synchronized | `beo` and five animation-controller-only trigger paths remain to be classified |
| Custom items | 6 | 6 | Present |
| Bedrock custom entities | 9 | Native Java villagers, wandering traders, and sheep with names/NBT/CEM | Intentional architectural replacement; needs scenario parity checks |
| User-visible settings | 5 | 4 active, 1 disabled placeholder | Villager Style is intentionally deferred for a later update |
| Resource client entities | 9 | CEM mappings for villagers, baby villagers, wandering trader, Wooly, wool/undercoat | Present but needs a full visual matrix |

The completed 523/523 result means every dialogue group is named somewhere in the Java controller. It does **not** prove that every group can be reached at the right time, with the right speaker, subject, cooldown, or variant. Runtime reachability is the main remaining accuracy task.

## What the deeper deobfuscation found

### 1. The 23 non-dialogue sound definitions are real port work

The Bedrock resource pack contains 2,235 sound definitions. The Java port now registers 2,212 dialogue clips plus the 22 classified supplemental clips. The original 23 non-dialogue entries are not unused duplicates:

- 22 are short animation-effect sounds stored as `sounds/oreville/vn/a` through `v`.
- The script explicitly maps 17 of them into adult and baby hurt animation sequences. The adult sequence is tied to **Villager Gets Hurt** (`wyvzhk`) and the baby sequence to **Baby Villager Gets Hurt** (`ecslqo`).
- The Bedrock animation controller contains effect states for the short sound effects, so these cannot be recovered merely by choosing a dialogue variant.
- The remaining sound, `beo`, carries the source's generic close-caption subtitle and needs its exact trigger identified before porting.

The 22 `a` through `v` clips are now copied and registered under separate Java sound IDs. The 17 direct script hurt effects are synchronized to eligible native villagers and wandering traders; Wooly remains excluded as in the source. The remaining work is to classify the animation-controller-only trigger paths and `beo`. Those clips must not be added to the normal dialogue chooser.

### 2. Villager Style is an actual Bedrock setting

The source handbook and behavior UI expose these five settings:

1. Show Subtitles
2. Villager Chattiness
3. Rare Villager Voiceines
4. Spawn Special Villagers
5. Villager Style: Vanilla, Actions & Stuff, Actions & Stuff: Flat

The first four have Java equivalents. The Java handbook currently renders **Villager Style** as a disabled `Villager News` button. Bedrock stores style per player and applies it through entity render properties. The port should not pretend that its disabled button is a usable setting.

The Java solution should be an optional compatibility layer for a real Java Actions & Stuff port, if and when one exists. The user has explicitly deferred this work, so no Villager Style parity or integration will be added in the current update. It must stay optional and must not become a required dependency when it is implemented later.

### 3. Bedrock uses custom entities and temporary proxies

The source uses nine custom entity definitions and script-managed replacement/proxy state. It copies native villager information such as biome variant, mark variant, skin ID, name, and other state into its renderable entities. It also monitors entity spawn, load, removal, damage, death, and data-driven triggers to keep those pairs synchronized.

The Java port deliberately uses native Java entities and attaches the visuals/behavior by name, NBT, CEM, and controller state. This is usually the better Java architecture because it preserves native AI, trading, riding, beds, knockback, saves, and compatibility. Recreating Bedrock's hidden proxy entities would be a regression risk, not a default goal.

What still needs checking is observable parity: named special villagers, Wooly, and the wandering trader must have the same visible identity, available interactions, sound behavior, persistence, damage response, and lifecycle from a player's point of view.

### 4. The source event surface is broader than a group-reference scan

The Bedrock script subscribes to more than dialogue selection. Its event surface includes player join/leave/spawn, entity spawn/load/remove/death/hurt, data-driven entity events, effects, item use/start use/start use on/complete use, player game-mode and inventory changes, block break/place/interact, entity interaction, projectiles, buttons, world lifecycle, and script events.

The Java controller already covers many of these, but each event must be tested for the source's ordering rules. Important examples found in the source are:

- microphone use starts a dedicated held-item animation;
- baby birth triggers both the baby line and nearby-villager response;
- spawn-egg villager creation triggers a dedicated baby/adult path;
- bell reactions wait 15 ticks, search a 50-block radius, then stagger speakers by 0–4 ticks;
- native villagers are replaced and restored while preserving their render/state values;
- active dialogue is cleared on damage under source-defined conditions;
- the source uses explicit delayed sleep and wake sequencing;
- player reputation, conversation pairing, cosmetics, signs, professions, trading, items, weather, dates, and nearby-entity reactions all use their own state rather than one generic nearby-event rule.

## Port decisions

### Keep as intentional Java choices

- Use native Java `Villager`, `WanderingTrader`, and `Sheep` entities instead of Bedrock-style proxy entities.
- Keep EMF/ETF/ESF as declared external requirements. Do not modify or bundle them.
- Keep the Java creative tab, optional Mod Menu entry, server-to-client dialogue payload, and gated `/dialoguetest` command. They are Java quality-of-life/debugging features, not attempts to imitate Bedrock internals.
- Keep the custom four-line subtitle overlay requested for the Java port. It is an intentional presentation enhancement, but it should remain controlled by the source-equivalent Show Subtitles setting.
- Keep the current sound/animation payload approach so a server selects one authoritative variant and clients animate the same selection.

### Replace or complete

- Leave the disabled Villager Style placeholder unchanged until the user starts that work.
- Complete classification for the remaining `beo` and animation-controller-only supplemental sound triggers.
- Replace static "all group IDs occur in source" confidence with automated scenario coverage and runtime evidence.
- Replace broad behavior guesses with source-derived target selection, interruption priority, cooldown, and state-transition rules.
- Update user-facing version text that still says 26.2 before the next release; the build and mod metadata already target 26.3.

### Do not do

- Do not create duplicate villagers, hidden carrier mobs, or render-only fake entities just to mirror Bedrock implementation details.
- Do not make Actions & Stuff or Mod Menu mandatory.
- Do not add every found OGG to the random voice-line pool.
- Do not treat source asset count differences as proof that dialogue is missing without mapping the file to an event/effect.

## Feature-by-feature plan

Every handbook group has a current Java reference. The following matrix defines what must be verified or completed beyond that static result.

| Source handbook area | Confirmed source behavior | Current port position | Next action |
| --- | --- | --- | --- |
| Player Actions | Everyday actions, player presence, movement, equipment, effects, reputation, game mode, death, and sleeping reactions | Groups are mapped | Scenario-test spectator vs survival flight, stationary delays, armor changes, effects, chattiness, cooldowns, and player-specific reputation. Verify that frozen ticks stop all new server dialogue. |
| Blocks & Building | Break/place/use groups, redstone, buttons, levers, bells, fire, and explosives | Groups are mapped; bell has a source-derived delay/stagger path | Compare block filters, search ranges, line-of-sight, speaker selection, and whether the source interrupts existing speech. Keep the exact 15-tick/50-block bell behavior only after confirming its active-dialogue rules. |
| Mobs & Creatures | Hostile, animal, baby, friendly, unusual, entity death, and entity-damage reactions | Groups are mapped | Build a registry audit from the 23 animal and 19 hostile source groups. Verify modern Java entities such as armadillo, axolotl, and nautilus. Verify the subject is actually the observed entity and is within source range/visibility limits. |
| Villager Life | Idle/wander, profession/workstation, sleep/home, food/pickups, birth/growth, naming, spawning, curing | Groups are mapped | Compare scheduled work, idle eligibility, held items, bed entry order, snore interruption, wake conditions, and baby growth/birth/spawn-egg paths. Run all profession/biome/level combinations. |
| Baby Villagers | Distinct voice set and reactions for birth, movement, interaction, cosmetics, food, names, bell/world reactions | Groups are mapped | Test every adult/baby branch separately. Confirm baby CEM, face state, sounds, idles, and locomotion never fall back to a vanilla stiff pose during custom behavior. |
| Damage & Danger | Direct player, mob, environmental, potion/projectile, hazard, panic, and witness reactions | Groups are mapped | Create a priority table: sustained danger must supersede nearby-world chatter; relief can occur only after the danger state ends; a new hurt event must not restart an already-valid sustained-danger line. Add source-mapped Foley after the sound audit. |
| Trading | Trade open/complete/refusal/reputation plus wandering-trader contexts | Groups are mapped | Verify the trade screen remains open, trade session state survives GUI transitions, source selection is trader vs player correct, and wandering-trader vanilla vocal sounds are not heard alongside custom dialogue. |
| Cosmetics & Noses | Equip/remove cosmetics, return/remove noses, reactions, signs | Items and reactions are present | Check adult/baby/special/profession/cosmetic combinations, shearing state, sign ownership/message lifetime, player held/equipped transforms, and no texture/model bleed. |
| Travel & Vehicles | Boat and minecart reactions | Groups are mapped | Verify rider/passenger roles, entering/exiting transitions, ranges, and target gaze. Do not fire merely because a vehicle exists nearby. |
| World & Time | Time, weather, dimensions, locations, real-world weekday/weekend, special dates/seasons | Groups are mapped | Make a calendar/time-zone policy explicit and test it. Compare dimension/location requirements, weather transition vs continuous weather behavior, lightning/firework range, and one-shot daily/date cooldowns. |
| Conversations | Gatherings, gossip, wandering pairs, campfire conversations | Pair system is present | Mirror source pair ownership, 2.5-block partner distance, line of sight, turn-taking, pair cancellation, and long cooldowns. Validate speaker/subject identity with two same-type villagers and with specials. |
| Special Characters | Mayor, #5, #9, Testificate Man, Wooly, Untouchable, other-villager reactions | Named identities and custom visuals exist | Compare each special's spawn, name aliases, voice family, custom idle, item/cosmetic state, damage/panic, and reaction roles. Specifically audit #9 microphone idle behavior and Wooly's graze/shear/regrowth state. |

## Entity and lifecycle parity

| Bedrock entity role | Source implementation | Java strategy | Required parity checks |
| --- | --- | --- | --- |
| Base custom villager | Custom renderable entity plus script/proxy synchronization | Native villager plus CEM/ETF and dialogue controller | Profession, biome, level, baby, missing nose, cosmetics, signs, speech, saves, and conversion/cure preservation |
| Mayor | Spawnable/summonable custom villager with trade and schedule components | Named native villager | Scale, hat/monocle/belt, trades, idle voice, job appearance, sleeping, and no generic-character voice leakage |
| Testificate Man | Spawnable/summonable custom villager with trade and schedule components | Named native villager | Its own body/model must remain distinct; cosmetics sold to other villagers must not alter its base model |
| Villager #5 | Spawnable/summonable custom villager | Named native villager | Damage/witness lines, idle, baby/profession state, and response role |
| Villager #9 | Spawnable/summonable custom villager | Named native villager | Microphone ownership/held state, random microphone idle, voice identity, and movement |
| Untouchable Villager | Custom entity with special behavior and component/event set | Named native villager with controller behavior | Exact escape trigger, target, cooldown, immunity/knockback behavior, taunts, and persistence without breaking native villager AI |
| Wooly | Custom sheep with shearing and eat-block events | Named native sheep with CEM/ETF | White base color, wool/undercoat after shearing, graze/regrow transition, face/feet/mouth, wool visibility, sound suppression, and Wooly-only dialogue |
| Wandering Trader | Custom summonable trader with its own timer/event graph | Native wandering trader | Custom appearance, dialogue, trades/despawn/potion behavior, and complete vanilla vocal suppression where custom speech replaces it |
| Source utility/proxy entity | Internal carrier/replacement role | No Java equivalent by design | Confirm no gameplay case requires it before adding any equivalent |

## Animation, model, and render plan

The source has 9 client-entity files, 6 attachables, 2 animation files, 2 animation controllers, a render-controller file, and a custom material. The Java port has 12 CEM models plus ETF property mappings. This is enough to render the intended characters, but it needs a deliberate render parity pass rather than isolated visual fixes.

### Audit matrix

Test each row under adult and baby state where applicable, with no job and every job, every biome variant, with/without a nose, and with each cosmetic:

| Test group | Required observations |
| --- | --- |
| Standard villager | Root pose, head/nose/arms/legs pivots, robe layering, blink/pupil behavior, mouth at rest and during speech, idle, walk, run, turn, sleep, held item, sign |
| Profession/biome/level | Correct texture composition with no one-frame texture switch, no layer clipping, and no lost clothing when a profession changes |
| Baby villager | Correct scaling, custom face/voice/idle/locomotion, no vanilla fallback pose, correct cosmetic attachment |
| Mayor, Testificate Man, #5, #9 | Correct unique base model before cosmetics; all special idles and dialogue gestures; correct turn and locomotion blending |
| Untouchable | Custom escape/turn/run visuals without gliding or vanilla snap turns |
| Wandering Trader | Trader model/texture plus custom voice face and held-item animation |
| Wooly | Base, wool, undercoat, sheared state, face/feet, eye/mouth, graze, idle, walk/run, hurt/speech |
| Player item transforms | Handbook and microphone in first person, third person, left hand, right hand, item frame, dropped item, and held during use |

### Implementation rules

1. Treat render controllers, material/PBR data, texture masks, and attachable layering as separate from geometry. A correct `.jem` shape can still be wrong if it lacks the source texture-selection condition.
2. Preserve the source's 24 FPS keyframe timing. Smoothness problems should be fixed by interpolation, blend-in/out, continuous idle/locomotion state, and turn state selection, not by resampling unrelated animations blindly.
3. Keep target gaze separate from body turn. The head must yaw and pitch toward the true subject within reasonable limits while the body turns only as the source state calls for it.
4. Validate animation interruption with a transition matrix: idle→dialogue, walk→dialogue, dialogue→danger, danger→relief, dialogue→sleep, sleep→interrupted, dialogue→run, and any special state→death/unload.
5. Do not add a new duplicate model to compensate for a texture or layer condition. Fix the CEM/ETF visibility/property condition instead.

## Dialogue behavior plan

### Speaker, subject, and target contract

Each dialogue request should carry and retain:

- **speaker**: the entity that owns the voice, facial animation, gesture, and cooldown;
- **subject**: player, entity, block, location, or none; it determines the context and subtitle meaning;
- **gaze target**: an entity/location used for head/body orientation, which may be absent for self-reactions;
- **priority**: sustained danger, direct damage, sleep/wake, interaction, conversation, world reaction, idle;
- **eligibility**: distance, line of sight, valid state, no conflicting active dialogue, and source-specific cooldown;
- **completion policy**: whether it may be interrupted, queues a relief line, starts another conversation turn, or restores idle.

The source has enough distinct event paths that these values must not be inferred later from the nearest entity. That is how speaker/subject swaps, wrong gaze, and duplicate context lines occur.

### Priority and interruption work

1. Build a source-derived priority table from all hurt, effect, sleep, trade, interaction, and scheduled idle paths.
2. For persistent hazards such as fire, suffocation, drowning, and effects, play spaced reactions while the state is active. A generic nearby-event line must not win over the self-danger line.
3. Queue relief only when the tracked hazard becomes inactive and the preceding hazard dialogue has safely completed or been deliberately cancelled by the source rule.
4. Keep bedtime speech before entering bed, reserve waking speech for actual morning wake behavior, and cancel sleep voice state on forced wake/bed removal.
5. Reject duplicate group/variant selection across nearby speakers during the group cooldown when the source has alternatives. Variant distribution should use original weights rather than merely random indexes.
6. Make every active sound authoritative from server ticks. Client wall-clock cleanup may stop animation presentation, but it must never authorize another server line while `/tick freeze` is active.

### Runtime dialogue test suite

The existing gated `/dialoguetest` command is useful for media/animation checks. It is not enough for behavior parity because a generated speaker and subject cannot prove ordinary event routing.

Add a developer-only scenario runner or a documented manual matrix with these outcomes per case:

- group ID, chosen variant index, speaker UUID/type/name, subject UUID/type/name, gaze target, distance, line of sight;
- server tick started/ended, whether it was interrupted, and by which priority;
- entity model state, facial state, gesture, locomotion state, and supplemental effect sounds;
- subtitle text/order/position;
- expected source path and cooldown.

Run that matrix for all 523 groups, then keep a separate event-based matrix for the 491 handbook contexts. A group can have multiple triggers, so neither number replaces the other.

## Sound plan

1. Generate a mapping report with all 2,235 source sound keys, source file names, dialogue-group/variant references, animation-controller effect references, and Java registration status.
2. Completed: port the 22 short effect sounds under distinct Java sound IDs and synchronize the 17 source script hurt effects.
3. Trace `beo` and the five animation-controller-only effect paths from source data, then port them only if they are reachable in normal gameplay.
4. Verify all custom vocal participants: villager, baby, named specials, Wooly, and wandering trader. Each needs a deliberate policy for ambient, hurt, death, trade, celebrate, and custom dialogue sound.
5. Test silence as well as overlap. Suppressing a vanilla sound is correct only when the source provides a matching custom sound or intentionally has none.
6. Verify source-category volume, positional behavior, distance attenuation, subtitles, and playback interruption.

## Settings and handbook plan

| Source setting | Java status | Planned result |
| --- | --- | --- |
| Show Subtitles | Client setting and custom overlay | Keep; ensure it governs only Villager News subtitles and persists safely |
| Villager Chattiness | Server setting with four levels | Keep; compare values and cooldown scaling to source behavior |
| Rare Villager Voiceines | Server setting | Keep; compare selection weight/eligibility rather than only the label |
| Spawn Special Villagers | Server setting | Keep; compare lifecycle, one-of-each rule, village distance, death eligibility, and world persistence |
| Villager Style | Disabled Java placeholder | Add optional integration only when a compatible style provider exists; otherwise make availability explicit |

The handbook should continue to expose the complete searchable guide, items, cosmetics, specials, settings, socials, and support. Review it against the Bedrock form for button order, search results, back navigation, settings permissions, external links, and safe handling of malformed or missing resource data. Any external URL should remain a normal user-initiated action.

## Items, trades, and player interaction plan

### Confirmed source inventory

- Handbook: stack size 1, three-paper shapeless recipe, opens the guide UI.
- Mayor Hat, Moustache, Testificate Helmet, and Villager Nose: head-slot cosmetics in Bedrock, with villager/cosmetic behavior controlled by the script.
- Microphone: stack size 1, custom start-use animation.
- Four special-character trade tables with 16-use/2-XP transactions and 16 or 24 emerald prices.

### Required checks

- Crafting recipe unlock/recipe-book behavior and handbook open behavior.
- Handbook and microphone transform/use animation in every view and hand.
- Cosmetic item transforms on a player, cosmetic state on regular/baby/special villagers, and removal/return by shears/nose flow.
- No trade UI closure during controller updates; trade start, completion, refusal, price/reputation, and special offers must point at the current trading villager.
- Inventory pickup, drop, held food/item, and item-complete-use events need source timing and ownership checks.

## Phased delivery order

### Phase 0 — lock a reproducible baseline

- Record the exact Java version, Fabric/EMF/ETF/ESF versions, and the original add-on archive hash.
- Refresh the deobfuscated source output from the original archive and store an inventory snapshot.
- Add no behavior changes in this phase.
- Correct stale 26.2 wording in release documentation as a separate documentation-only change.

### Phase 1 — create proof, not guesses

- Build the static source-to-Java feature map: 523 groups, 491 guide contexts, 23 supplementary sounds, 9 entities, 6 items, 4 trades, and 5 settings.
- Build a manual/dev scenario suite that records speaker, subject, gaze, timing, variant, and state transition.
- Run one visual test world containing every profession, biome, age, special, cosmetic, item, bed, vehicle, workstation, hazard, and relevant mob.

**Exit condition:** every feature has a source reference, Java owner, test scenario, and status. No feature is marked complete because an ID string exists in code.

### Phase 2 — behavior correctness

- Implement source-derived target selection and priority/interruption rules.
- Fix persistent hazard/relief, sleep/wake, trade, spawn/birth, and conversation lifecycle inconsistencies discovered by Phase 1.
- Audit nearby-entity recognition against the source list and Java registry.
- Re-test every scenario affected by shared dialogue routing.

**Exit condition:** no line starts with a swapped speaker/subject, no invalid far-away target is chosen, no generic context overrides a higher-priority self reaction, and no duplicated active line occurs from a routine state transition.

### Phase 3 — audio and animation fidelity

- Complete the remaining supplemental-sound trigger mapping after the 22 classified clips and 17 direct hurt effects.
- Audit all 9 source client entity definitions against CEM/ETF mappings and conditions.
- Fix the animation transition matrix, target gaze, 24 FPS interpolation, idles, walk/run, special idles, and held-item animation.
- Test sound and model behavior in multiplayer and with client/server tick freeze.

**Exit condition:** every custom entity has correct visual, voice, and effect state for idle, walk, run, talk, hurt, sleep, and its special interaction states.

### Phase 4 — settings, style, guide, and compatibility

- Finish source-equivalent settings behavior and permission/persistence rules, excluding Villager Style until the user requests it.
- Add the optional villager-style integration only after choosing a concrete compatible Java provider.
- Validate handbook parity and Mod Menu optionality.
- Verify all native-entity compatibility cases: villagers with professions/trades, wandering trader despawn/potions, Wooly shearing/regrowth, saves, dimensions, and other mods.

**Exit condition:** settings are truthful, safe, and available without a required optional mod; no native Java gameplay is lost to imitate Bedrock internals.

## Acceptance checklist for a release

- [ ] 523 groups and 2,212 dialogue variants remain accounted for, with static and runtime evidence kept separately.
- [ ] All 491 handbook contexts are reachable or explicitly documented as source-only/unavailable in Java.
- [ ] All 23 non-dialogue source sounds are mapped as ported, intentionally omitted, or proven unreachable. The 22 `a` through `v` assets are registered; `beo` remains open.
- [ ] All 9 Bedrock entity roles pass the lifecycle/visual/audio matrix using the native-Java strategy.
- [ ] Every adult/baby/profession/biome/cosmetic/nose/special combination passes the render matrix without clipping, duplicate models, or texture flicker.
- [ ] Every target-sensitive context uses the right speaker, subject, distance, line of sight, and gaze target.
- [ ] Damage, effects, relief, sleep/wake, and conversation interruption pass the priority matrix.
- [ ] Wandering trader and Wooly never layer vanilla vocal sounds over custom ones.
- [ ] The source settings are either implemented accurately or honestly shown as unavailable; Actions & Stuff remains optional.
- [ ] Player-held handbook/microphone transforms work in both hands and first/third-person views.
- [ ] The release build keeps `/dialoguetest` disabled unless a development build explicitly enables it.
- [ ] README and in-game requirements agree with the actual 26.3 build metadata.

## Source references

- `build/deobfuscated-bedrock-source/full-addon/feature-inventory.json`
- `build/deobfuscated-bedrock-source/full-addon/dialogue-symbol-map.json`
- `build/deobfuscated-bedrock-source/full-addon/behavior-pack/scripts/oreville/ebi.js`
- `build/deobfuscated-bedrock-source/full-addon/resource-pack/sounds/sound_definitions.json`
- `build/deobfuscated-bedrock-source/full-addon/resource-pack/animation_controllers/fa2a4464.json`
- `src/main/java/com/vnap/dialogue/ContextualDialogueController.java`
- `src/main/java/com/vnap/client/DialogueAnimationState.java`
- `src/main/java/com/vnap/client/HandbookScreen.java`
- `src/main/resources/assets/villager-news-addon-port/dialogues.json`
- `src/main/resources/assets/villager-news-addon-port/handbook.json`
