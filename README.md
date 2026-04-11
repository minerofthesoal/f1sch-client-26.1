# f1sch client - Minecraft 26.1 / 26.1.1

A client-side Fabric mod for Minecraft 26.1/26.1.1 with 25+ features including extended reach, fly, ESP, auto-combat, teleport, X-Ray, knockback, and more.

**Supported Versions:** 26.1 | 26.1.1

## Features

| Feature | Description | Default Key |
|---------|-------------|-------------|
| **Reach** | Extend block/entity interaction range (3-50 blocks) | `R` |
| **Fly** | Survival flight with adjustable speed | `G` |
| **ESP** | Entity tracers + path trace to mobs/players | `X` |
| **Auto Hit** | Automatically attacks nearest entity | `V` |
| **Low HP Kill** | Targets entities below a health threshold | `B` |
| **Eating Assist** | Auto-eats best food from hotbar when hungry | `N` |
| **Jesus** | Walk on water and lava | `U` |
| **NoFall** | Prevents fall damage | `I` |
| **Fullbright** | Night vision (max gamma) | `L` |
| **Speed** | Ground speed multiplier | `O` |
| **X-Ray** | See ores/chests/spawners through blocks | `Z` |
| **Knockback** | Massive knockback on hit (up to 2500) | `J` |
| **Teleport** | Instant teleport to any coordinates | `T` |
| **Scaffold** | Auto-place blocks below you | `.` |
| **Auto Totem** | Auto-move totems to offhand | `M` |
| **Auto Armor** | Auto-equip best armor | `,` |
| **HUD Toggle** | Show/hide status overlay | `H` |
| **Config Screen** | ClickGUI with categories and toggles | `Right Shift` |

Plus 40+ additional features in Wurst, Meteor+, and Pro categories.

## Installation

### Client Mod

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 26.1/26.1.1
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (0.145.4+26.1.1)
3. Download the latest JAR from [Releases](../../releases)
4. Place the JAR in `.minecraft/mods/`
5. Launch Minecraft

### Server Addon (Optional)

**Option A: Fabric Mod** (requires Fabric on server)
```bash
cd server-addon && ../gradlew build
```
Place `f1sch-server-addon-3.0.0.jar` in server `mods/` folder.

**Option B: Data Pack** (works on ANY server)
```bash
cd server-addon && ../gradlew buildDatapack
```
Place `f1sch-server-addon-datapack-3.0.0.zip` in `world/datapacks/`.
Use `/trigger f1sch.help` in-game for commands.

## Building from Source

Requires **JDK 25**.

```bash
git clone https://github.com/minerofthesoal/f1sch-client-26.1.git
cd f1sch-client-26.1
./gradlew build
```

## Tech Stack

| Component | Version |
|-----------|---------|
| Minecraft | 26.1.1 |
| Fabric Loader | 0.18.6 |
| Fabric API | 0.145.4+26.1.1 |
| Fabric Loom | 1.16.1 |
| Java | 25 |
| Gradle | 8.14 |

## License

Apache-2.0
