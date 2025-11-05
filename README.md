# AttackIndicator

A modern, lightweight damage indicator plugin for Paper/Spigot servers. Display beautiful animated floating damage numbers when entities take damage.

## Version Support

The plugin automatically detects your server version and uses the best available method:

| Version | Method | Features |
|---------|--------|----------|
| **1.19.4+** | Display Entities | Smooth animations, scalable, best performance |
| **1.16.5-1.19.3** | Armor Stands | MiniMessage support (hex colors, gradients) |
| **1.8-1.16.4** | Armor Stands | Legacy ChatColor (basic colors: &c, &6, &a, etc.) |

## Key Features

- **Smooth Animations** - Floating damage indicators with configurable speed and duration
- **Rich Text Formatting** - MiniMessage support on 1.16.5+ (hex colors, gradients, styles)
- **Highly Customizable** - Control appearance, size, position, speed, and behavior
- **Smart Filtering** - Show indicators based on damage source (all/player-only/no-self)
- **Player Damage Support** - Optional indicators on players
- **Entity Filtering** - Whitelist/blacklist specific entity types
- **Random Positioning** - Natural look with random offsets to prevent stacking
- **Auto-Update Checker** - Get notified when new versions are available
- **World Management** - Disable indicators in specific worlds
- **Performance Optimized** - Minimal server impact on all versions

## Commands

- `/ai reload` - Reload plugin configuration
- Aliases: `/attackindicator`

## Configuration

All settings are in `config.yml` with detailed comments explaining version compatibility.

### Quick Examples

```yaml
# Works on all versions
indicator-format: "<#ffffff>-{damage}<#ff5555>♥"  # Hex colors on 1.16.5+
indicator-format: "&f-{damage}&c♥"                # Legacy format for 1.8-1.16.4

# Scale (1.19.4+ only)
indicator-scale: 1.5

# Display mode (all versions)
display-mode: PLAYER_ONLY
```

### Attack indicator >2.0 stats
![bstats](https://bstats.org/signatures/bukkit/Attack%20indicator.svg)
