# Minecraft Box PvP Plugin Development

## Project Overview
This is a Minecraft Paper plugin development environment for creating Box PvP gamemode features. Box PvP combines mining/economy systems with PvP combat in an arena setting.

## Current State
- **Version**: 1.0.0
- **Minecraft Compatibility**: 1.16 - 1.21.x
- **Server Type**: Paper/Spigot
- **Development Language**: Java 17

## Features Implemented
1. Custom command system (/spawn, /stats, /shop, /arena, /setspawn)
2. Economy system with kill rewards and death penalties
3. Player data management (kills, deaths, balance tracking)
4. Shop GUI with purchasable items
5. Basic arena command structure
6. Event listeners for player join/quit/death
7. Maven build system

## Project Structure
```
├── pom.xml                           # Maven configuration
├── src/main/
│   ├── java/com/boxpvp/core/
│   │   ├── BoxPvPCore.java          # Main plugin class
│   │   ├── commands/                # Command handlers
│   │   ├── data/                    # Player data management
│   │   └── listeners/               # Event listeners
│   └── resources/
│       ├── plugin.yml               # Plugin metadata
│       └── config.yml               # Default configuration
└── target/                          # Build output (BoxPvPCore-1.0.0.jar)
```

## How to Build
Run the workflow or execute:
```bash
mvn clean package
```

The compiled plugin will be in `target/BoxPvPCore-1.0.0.jar`

## How to Use
1. Build the plugin (see above)
2. Set up a Paper Minecraft server (download from papermc.io)
3. Copy the .jar file to your server's `plugins/` folder
4. Start the server
5. Configure settings in `plugins/BoxPvPCore/config.yml`
6. Set spawn point with `/setspawn`

## User Preferences
- Building Box PvP server (not hosting on Replit)
- Will build website for the server later (remember this!)
- Friend is building the map in Minecraft
- Focus on plugin development first

## Recent Changes
- 2025-11-05: Initial project setup with Maven and Paper API
- 2025-11-05: Implemented core commands and player data system
- 2025-11-05: Added shop GUI and economy system
- 2025-11-05: Created build workflow

## Next Steps (Future Phases)
1. Mining system with regenerating blocks
2. Advanced arena matchmaking (1v1, 2v2, FFA)
3. Leaderboards and statistics
4. Crate/reward system
5. Private mines with vouchers
6. **Build server website** (planned for later)
