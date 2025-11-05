# BoxPvPCore - Minecraft Plugin

A comprehensive Box PvP gamemode plugin for Minecraft Paper servers (1.16 - 1.21.x).

## What is Box PvP?

Box PvP is a Minecraft gamemode that combines resource grinding with intense PvP combat. Players mine in public areas to earn money, then use that money to buy better gear and dominate opponents in the arena!

## Features

### Current Features (v1.0.0)

#### Custom Commands
- `/spawn` - Teleport to spawn point
- `/stats [player]` - View player statistics (kills, deaths, K/D ratio, balance)
- `/shop` - Open the shop GUI to buy gear
- `/arena [join|leave|list]` - Arena matchmaking system
- `/warp <name>` - Teleport to warp locations (trade, crate, afk, etc.)
- `/warp` - List all available warps
- `/setspawn` - Set spawn point (Admin only)
- `/setwarp <name>` - Create a warp location (Admin only)

#### Economy System
- Players start with $100
- Earn $50 per kill
- Lose $25 on death
- Buy weapons, armor, and items from the shop

#### Shop System
Interactive GUI shop with:
- Iron Sword ($100)
- Diamond Sword ($500)
- Iron Armor Set ($300)
- Diamond Armor Set ($1000)
- Golden Apples x5 ($150)
- Ender Pearls x3 ($200)
- Arrows x64 ($50)

#### Player Stats Tracking
- Kills
- Deaths
- K/D Ratio
- Balance
- Last login time

#### Warp System
Custom teleport locations for your server:
- **Trade Warp** - Where players trade materials after mining
- **Crate Warp** - Location for opening crates with keys
- **AFK Warp** - Designated AFK zone
- Admins can create unlimited custom warps
- Warp data persists across server restarts

## Installation

### Step 1: Build the Plugin

Run the build command:
```bash
mvn clean package
```

The compiled plugin will be at: `target/BoxPvPCore-1.0.0.jar`

### Step 2: Install on Your Server

1. Make sure you have a Paper server (1.16 - 1.21.x)
   - Download Paper from: https://papermc.io/downloads
   
2. Copy `BoxPvPCore-1.0.0.jar` to your server's `plugins/` folder

3. Start your server

4. The plugin will create a `config.yml` file in `plugins/BoxPvPCore/`

### Step 3: Configure the Plugin

Edit `plugins/BoxPvPCore/config.yml` to customize:

- Spawn location
- Economy values (starting balance, rewards)
- Arena settings
- Shop items and prices
- Messages

### Step 4: Set Spawn Point and Warps

1. Join your server
2. Stand where you want spawn to be
3. Run `/setspawn` (requires op or `boxpvp.admin` permission)

### Step 5: Set Up Warp Locations

After your friend builds the map, set up warp points:

1. Go to the trading area → `/setwarp trade`
2. Go to the crate area → `/setwarp crate`
3. Go to the AFK zone → `/setwarp afk`

Players can then use `/warp trade`, `/warp crate`, or `/warp afk` to teleport!

## Configuration

The plugin creates a `config.yml` file with these settings:

```yaml
# Spawn settings
spawn:
  world: world
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0

# Economy settings
economy:
  starting-balance: 100
  kill-reward: 50
  death-penalty: 25

# Arena settings
arena:
  enabled: true
  min-players: 2
  max-players: 10
  countdown-time: 10

# Shop settings
shop:
  enabled: true
  gui-title: "§6§lBox PvP Shop"
```

## Permissions

- `boxpvp.admin` - Access to admin commands (/setspawn)
- `boxpvp.player` - Access to player commands (default: true)

## Player Data

Player data is stored in `plugins/BoxPvPCore/playerdata/` as YAML files.

Each player file contains:
- Kills
- Deaths
- Balance
- Last login timestamp

## Development

### Requirements
- Java 17+
- Maven
- Paper API 1.21.3

### Building from Source
```bash
mvn clean package
```

### Project Structure
```
src/main/java/com/boxpvp/core/
├── BoxPvPCore.java           # Main plugin class
├── commands/                 # Command handlers
│   ├── ArenaCommand.java
│   ├── SetSpawnCommand.java
│   ├── ShopCommand.java
│   ├── SpawnCommand.java
│   └── StatsCommand.java
├── data/                     # Data management
│   ├── PlayerData.java
│   └── PlayerDataManager.java
└── listeners/                # Event listeners
    └── PlayerListener.java
```

## Roadmap (Future Features)

### Phase 2: Mining System
- Public mines with regenerating blocks
- Mine block types: Coal, Iron, Gold, Diamond, Emerald
- Different block values for economy
- Private/ranked mines with vouchers

### Phase 3: Advanced Combat
- Combat stats tracking (combos, critical hits)
- Kill streaks with rewards
- Leaderboards (top killers, richest players)

### Phase 4: Arena System
- 1v1, 2v2, FFA match types
- Arena matchmaking queue
- Ranked matches with ELO system
- Arena rewards and betting

### Phase 5: Crates & Rewards
- Random loot crates
- Daily rewards
- Vote rewards
- Special items and perks

## Support

Need help? Check these resources:

1. **Minecraft Server Setup**: https://papermc.io/
2. **Plugin Installation Guide**: Place .jar in plugins/ folder and restart
3. **Common Issues**: Make sure you're using Paper (not Spigot or Bukkit)

## Version Compatibility

- Minecraft 1.21.x ✓ (Tested)
- Minecraft 1.20.x ✓
- Minecraft 1.19.x ✓
- Minecraft 1.18.x ✓
- Minecraft 1.17.x ✓
- Minecraft 1.16.x ✓

## Credits

Built for Box PvP servers using Paper API.

## License

This plugin is free to use for your Minecraft server!
