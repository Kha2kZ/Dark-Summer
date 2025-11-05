# Minecraft Box PvP Plugin Development

## Project Overview
This is a Minecraft Paper plugin development environment for creating Box PvP gamemode features. Box PvP combines mining/economy systems with PvP combat in an arena setting.

## Current State
- **Version**: 1.0.0
- **Minecraft Compatibility**: 1.16 - 1.21.x
- **Server Type**: Paper/Spigot
- **Development Language**: Java 17

## Features Implemented
1. **Multi-Currency Economy System**
   - Three currencies: Money, Gems, and Coins
   - Full persistence in player YAML files
   - Kill rewards and death penalties (money-based)

2. **Player-to-Player Trading** (/trade)
   - Request/confirmation system with 60-second timeout
   - Interactive trade GUI where both players can add items
   - Proximity requirement: 10 blocks, same world
   - Protected against cross-world trades

3. **Auction House** (/ah)
   - List items with `/ah sell <price> <money|gems|coins>`
   - Browse GUI with pagination (arrows for navigation)
   - Currency filter button (cycles through all/money/gems/coins)
   - Item details on hover (name, price, seller, date)
   - Purchase confirmation GUI with buy/cancel options
   - Listings persist in auctions.yml

4. **Gift Code System** (/giftcode, /creategiftcode) [NEW!]
   - Admins create codes with `/creategiftcode <code> <maxuses>`
   - Interactive GUI to add custom items (enchanted, named, ANY items)
   - Click currency icons to set money/gems/coins rewards
   - Players redeem with `/giftcode <code>`
   - Tracks usage per player (prevents double-claiming)
   - Supports one-time or multi-use codes (-1 for unlimited)
   - Full item serialization preserves enchantments, names, lore

5. **Player Data Management**
   - Tracks kills, deaths, and all three currency balances
   - Automatic save/load from YAML files

6. **Command System**
   - /spawn, /stats, /arena, /setspawn
   - /warp, /setwarp (trade, crate, afk zones)
   - /trade <player>
   - /ah (main GUI), /ah sell <price> <currency>
   - /giftcode <code> (redeem gift codes)
   - /creategiftcode <code> <maxuses> (admin only)

7. **Event Listeners**
   - Player join/quit handling
   - Death handling with currency penalties

8. **Maven Build System**
   - Clean compilation of 22 source files
   - Shaded jar output for easy deployment

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
- 2025-11-05: Fixed api-version compatibility (1.16-1.21.x)
- 2025-11-05: Fixed shop armor sets to give full sets instead of just chestplate
- 2025-11-05: Added warp system with /warp and /setwarp commands
- 2025-11-05: **Removed /shop command** (will be replaced with NPC-based shop later)
- 2025-11-05: **Implemented multi-currency economy** (money, gems, coins) in PlayerData
- 2025-11-05: **Created player-to-player trading system** with GUI and proximity checks
- 2025-11-05: **Built comprehensive auction house** with currency filters and pagination
- 2025-11-05: **Fixed critical bug** in trade command: added world validation before distance calculation
- 2025-11-05: **Implemented gift code system** with GUI for admins to create codes with custom items and currency rewards
- 2025-11-05: **Fixed gift code slot mapping bug** - changed from List to Map for proper item-to-slot tracking

## Next Steps (Future Phases)
1. Mining system with regenerating blocks
2. Advanced arena matchmaking (1v1, 2v2, FFA)
3. Leaderboards and statistics
4. Crate/reward system
5. Private mines with vouchers
6. **Build server website** (planned for later)
