TigerClaim %version%

TigerClaim is a add-on for WorldGuard.
It is a simple solution to create Regions for Player.
It allows Player to create their own Region with predefined Flags like greeting, farewell, ice-melt and so on.
TigerClaim supports all WorldGuard flags, also all WorldGuard flag add-ons.
You can find more information about this plugin in the wiki on github:
https://github.com/Bergtiger/TigerClaim/wiki

Configuration

Once you have run your server with TigerClaim installed, you will find the configuration file inside the plugins/TigerClaim folder: config.yml
You can set your config in game with the /claim set command.

Settings
Setting         Default                     Description

Time.Pattern:   dd-MM-yyyy                  Time pattern.
ExpandVert:     true                        Regions will be automatically expanded to full height
Overlapping:    false                       Claims can overlap existing Regions
Radius:         39                          Claims without WorldEdit selection will be a square with this radius around the player
Pattern:        -player-_-counter-_-time-   Claims will be named with this pattern
Gap:            10                          Claims need a gap between each other.
Flags:          -                           Default flags that will be added when a claim is made.

Constant
Constants are placeholders in the language and configuration file.
If you change constants you have to manually change their occurrence in these files.

Commands

All commands begin with /claim [cmd] followed by parameters.

Command Parameters                  Explanation

claim   -                           Creates a Region.
plugin  -                           Shows plugin version and configuration.
reload  -                           Reloads plugin and configuration.
set     expandvert [true/false]     Set if regions should be automatically expanded vertical.
        overlapping [true/false]    Set if claims are allowed to overlap existing regions.
        flag [flag] [value]         Set a flag [flag] to choose witch flag you want to set. If no value is given the flag will be removed.
        gap [number]                Set the number of Blocks that have to be between the new region and old regions. If gap equals 0 the gap will be ignored.
        time [value]                Set the time-pattern used in the pattern for naming regions.
        pattern [value]             Set the pattern for naming regions. -player- will be replaced with the player-name and -time- will be replaced with time-pattern
        radius [number]             Set radius of Blocks around the player for default regions.

Examples

/claim claim

If you have the WorldEdit permission, you can select a WorldEdit selection. Cuboid and Polygon selection is supported.
If you do not have permission or have no WorldEdit selection your position is the centre and in the defined radius around you will be created a cuboid region.
You have to confirm the command. After you confirmed with /yes, it will be checked if there are conflicts with exiting region names or overlapping regions.
The counter pattern is only calculated correctly after confirmation.

/claim set

claim set flag greeting Welcome to @p

Permissions

By default no one can use TigerClaim. In order to work properly for yourself, moderators and players you must provide the proper permissions.
Players normally will only need tclaim.claim and tclaim.limit.[number] to create their own region.

Permission                      Explanation

tclaim.admin                    Be able to use all commands
tclaim.set                      Be able to use /claim set
tclaim.claim                    Be able to use /claim claim
tclaim.nolimit                  Be able to make limitless regions
tbuttons.plugin                 Be able to use /claim plugin
tbuttons.reload                 Be able to use /claim reload
tbuttons.worldedit              Be able to use WorldEdit regions for /claim claim
tclaim.limit.[world].[number]   Amount of allowed claims in a world