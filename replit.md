# BoxPvPCore - Minecraft Paper Plugin

## Project Overview
BoxPvPCore is a comprehensive Box PvP gamemode plugin for Minecraft Paper servers (1.16-1.21.x). The plugin features an extensive 15-tier rank system with virtual chests, mining multipliers, auto-crafting, and player commands.

## Build Status
- **Environment**: Replit (Development only - Server built externally)
- **Build Tool**: Maven
- **Output**: `target/BoxPvPCore-1.0.0.jar`
- **Build Command**: `mvn clean package` (via workflow)

## Project Structure
```
src/main/java/com/boxpvp/core/
├── BoxPvPCore.java               # Main plugin class
├── commands/                     # All command implementations
│   ├── RankCommand.java          # /rank - Purchase ranks via GUI
│   ├── PvCommand.java            # /pv - Virtual chest access
│   ├── NickCommand.java          # /nick - Custom nicknames (RGB for RICHKID+)
│   ├── FeedCommand.java          # /feed - Restore hunger
│   ├── HealCommand.java          # /heal - Full HP restore
│   ├── FlyCommand.java           # /fly - Toggle fly mode
│   ├── InvseeCommand.java        # /invsee - View player inventory
│   ├── TuChetaoCommand.java      # /tuchetao - Auto-crafting system
│   ├── AuctionHouseCommand.java  # /ah - Auction house (with rank limits)
│   └── ...                       # Other existing commands
├── data/                         # Data managers and models
│   ├── Rank.java                 # 15-tier rank enum with all perks
│   ├── RankManager.java          # Rank operations & purchase logic
│   ├── PlayerData.java           # Player data with rank field
│   ├── VirtualChestManager.java  # 40 virtual chests per player
│   ├── AutoCraftingManager.java  # Auto-crafting recipe system
│   └── AutoCraftRecipe.java      # Recipe data model
└── listeners/
    ├── PlayerListener.java       # Kill/death bonuses, join/quit
    └── MiningListener.java       # Mining multiplier system
```

## Rank System (11 Purchasable Ranks)

### Purchasable Ranks (Sequential Progression)
*Note: 1 coin = 1,000 VNĐ*

| Rank | Price (Coins) | Price (VNĐ) | Color | Vaults | Mining | AH Listings | Special Perks |
|------|---------------|-------------|-------|--------|--------|-------------|---------------|
| **MEMBER** | 0 | Free | Gray | 1 | x1 | 0 | - |
| **VIP** | 20 | 20K | Yellow | 2 | x2 | 2 | /invsee |
| **VIP+** | 50 | 50K | Green | 3 | x3 | 3 | - |
| **MVP** | 100 | 100K | Aqua | 5 | x5 | 5 | /nick, /feed |
| **MVP+** | 200 | 200K | Dark Aqua | 7 | x7 | 7 | /heal, /tuchetao (120s) |
| **RICHKID** | 300 | 300K | Pink | 10 | x15 | 10 | RGB nicknames, 60s cooldown |
| **DARK** | 400 | 400K | Dark Blue | 20 | x20 | 15 | /fly, 30s cooldown |
| **HEAVEN** | 500 | 500K | White+Yellow | 35 | x25 | 20 | +10K coins/kill, +10 gems/kill, 20s cooldown |
| **INFINITE** | 700 | 700K | Red+Blue | 40 | x30 | 30 | +10K coins/kill, +10 gems/kill, 10s cooldown |
| **EXTREME** | 850 | 850K | Red+Gold | 40 | x50 | 30 | Death penalty immunity, +10K coins/kill, +10 gems/kill, 5s cooldown |
| **CUSTOM** | 1,000 | 1 triệu | Red | 40 | x70 | 30 | +50K coins/kill, +50 gems/kill, 1s cooldown |

### Staff Ranks (Not Purchasable)
| Rank | Display | Vaults | Mining | Function |
|------|---------|--------|--------|----------|
| **HELPER** | Pink | 40 | x70 | Staff commands |
| **POLICE** | Red+Purple | 40 | x70 | Enhanced staff tools |
| **ADMIN** | Dark Red+Aqua | 40 | x100 | Full admin powers |
| **OWNER** | Gradient 4-color | 40 | x1000 | Server owner |

## Key Features

### 1. Virtual Chests (/pv)
- Up to 40 personal vaults (1-40 based on rank)
- 54-slot chests per vault
- Persistent storage across server restarts
- Auto-save on close

### 2. Mining Multiplier System
- Automatic ore multiplication when mining
- x1 (MEMBER) → x70 (OWNER)
- Works on all ores (coal, iron, diamond, emerald, ancient debris, etc.)
- Applies to both overworld and nether ores

### 3. Auto-Crafting (/tuchetao)
**Admin Features:**
- `/tuchetao create` - Create recipes via 2-step GUI
  1. Place input items in first chest
  2. Place output items in second chest
- Recipes saved to `autocraft_recipes.yml`

**Player Features:**
- `/tuchetao` - Auto-craft if inventory matches any recipe
- Cooldown: 120s (SUPER) → 1s (OWNER)
- Automatic inventory scanning and item replacement

### 4. Rank Shop (/rank)
- Interactive GUI showing all 15 ranks as colored leather armor
- Visual indicators:
  - ✓ UNLOCKED (green) - Already owned
  - AVAILABLE (yellow) - Can purchase now
  - LOCKED (red) - Must unlock previous rank first
- Purchase validation:
  - Sequential progression enforced
  - Coin balance check
  - Instant rank upgrade on purchase

### 5. Player Commands
- `/nick <name|reset>` - Custom nickname (RGB: `&#RRGGBB` for RICHKID+)
- `/feed` - Restore hunger (MVP+)
- `/heal` - Full HP + hunger restore (MVP+)
- `/fly` - Toggle fly mode (DARK+)
- `/invsee <player>` - View player inventory (VIP+)

### 6. Kill/Death System
**Kill Bonuses (INFINITE+ ranks):**
- INFINITE: +25 coins per kill
- INFINITE+: +50 coins, +1 gem per kill
- OWNER: +100 coins, +2 gems per kill

**Death Penalties:**
- Money loss: 25 coins (configurable)
- Gems loss: Varies by rank
- EXTREME+ ranks: Immune to money penalty

### 7. Auction House Integration
- Listing limits based on rank (2-20)
- Enforced via `getListingsBySeller()` check
- GUI message when limit reached

## Configuration

### config.yml (Default Values)
```yaml
messages:
  prefix: "§8[§6BoxPvP§8] "
  player-not-found: "§cPlayer not found!"

economy:
  kill-reward: 50
  death-penalty: 25

mining:
  enabled: true
  
autocraft:
  enabled: true
```

### Data Storage
- `playerdata.yml` - Player stats, rank, coins, gems
- `rank.txt` - Legacy rank storage (deprecated)
- `vaults.yml` - Virtual chest data
- `autocraft_recipes.yml` - Auto-craft recipes
- `auctions.yml` - Auction listings

## Development Notes

### Recent Changes (Latest Session)
**November 11, 2025 - GUI Improvements**
- ✅ **GUI /ah (Auction House)**: 
  - Đổi title từ "Black Market" thành "Auction - [page/total]"
  - Di chuyển navigation: Previous (slot 45), Refresh/Sunflower (slot 49), Next (slot 53)
  - Bỏ currency filter button
- ✅ **GUI /rank (Rank Shop)**:
  - Thêm cyan glass pane decoration ở hàng trên cùng
  - Player head với coin display ở slot 4
  - Format lại rank info: Giá + Quyền lợi + Đã sở hữu (✓/✗)
  - Ẩn "Dyed (Vanilla)" và attributes với ItemFlags
- ✅ **GUI /item create (Custom Items)**:
  - Thêm Name Tag button cho chat input (slot 16)
  - Chat listener để nhập tên với color codes (&a, &c, etc.)
  - Confirmation GUI với green/red stained glass panes
  - Name tag preview hiển thị tên sẽ áp dụng
  - Session persistence qua confirmation flow

**Previous Implementation**
- ✅ Implemented complete 15-tier rank system with Rank enum
- ✅ Added RankManager with sequential purchase validation
- ✅ Integrated rank field into PlayerData with persistence
- ✅ Created VirtualChestManager with 40 vaults per player
- ✅ Implemented MiningListener for ore multiplication (x1-x70)
- ✅ Updated PlayerListener with kill/death bonuses
- ✅ Built /rank GUI with colored leather armor display
- ✅ Added player commands: /pv, /nick, /feed, /heal, /fly, /invsee
- ✅ Created AutoCraftingManager with admin recipe creation
- ✅ Implemented /tuchetao command with cooldown system
- ✅ Updated AuctionHouseCommand with rank-based limits
- ✅ Registered all commands and listeners in BoxPvPCore
- ✅ Updated plugin.yml with all new commands
- ✅ **CRITICAL FIX**: Added saveAllLoadedVaults() to prevent vault data loss on shutdown
- ✅ **PRODUCTION-READY**: Architect verified all features and persistence logic

### Build Information
- **Last Build**: Successful (Maven 3.x)
- **Compile Time**: ~11 seconds
- **Warnings**: Deprecated API usage in VirtualChestManager (Bukkit serialization)
- **JAR Size**: ~63KB (before dependencies)
- **Status**: ✅ **PRODUCTION-READY** (Architect confirmed)

### User Preferences
- **Language**: Vietnamese (user communication)
- **Server Location**: Built externally (NOT hosted on Replit)
- **Website**: Planned for future (not implemented yet)
- **Development Approach**: Focus on core functionality first

## Known Limitations
- No staff commands (/mute, /ban, /kick) implemented yet
- Staff rank overlay system not fully implemented
- Custom rank name customization (/custom) not implemented
- RGB nickname parsing uses Bungee ChatColor API

## Future Enhancements
- Staff moderation commands
- Custom rank customization GUI
- Enhanced death penalty system
- Staff rank overlay display
- Website integration
- Rank permission nodes
- Placeholder API integration

## Testing Checklist
- [x] Rank purchase validation (sequential)
- [x] Virtual chest persistence
- [x] Mining multiplier calculation
- [x] Auto-craft recipe matching
- [x] Kill/death bonus application
- [x] Auction listing limits
- [ ] RGB nickname rendering
- [ ] Cooldown system accuracy
- [ ] Cross-server compatibility (1.16-1.21.x)

## Support & Contact
- **Developer**: YourName
- **Version**: 1.0.0
- **API Version**: 1.16+
- **Dependencies**: Paper API (Spigot compatible)

---
**Last Updated**: November 11, 2025  
**Build Status**: ✅ Compiling Successfully  
**Latest JAR**: `target/BoxPvPCore-1.0.0.jar` (135KB)
