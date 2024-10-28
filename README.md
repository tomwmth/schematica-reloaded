<p align="center">
  <img src="https://i.imgur.com/wrzXhK4.png" width="200px">
</p>

A fork of Schematica for 1.8.9 to bring it up to speed with modern requirements.

> Because not everyone should have to use a client

## Download
Releases are available on [Modrinth](https://modrinth.com/mod/schematica).

## Ethos
This fork aims to bridge the gap between the original mod and the versions often found built into modern clients.

This is achieved by adding many of the same features and improvements while still being distributed as a standalone mod.

## Changes
- New "Change State" option
  - When enabled, printer will automatically interact with certain blocks to change their state if necessary
  - This works for: Repeaters, Comparators, Levers, Doors, Trap Doors, Fence Gates
- New "Arrow Key Move" option
  - When enabled, the arrow keys can be pressed to horizontally move the current schematic
  - While sneaking, the up and down arrow will move the schematic vertically
- New "Persist Schematic" option
  - When enabled, disconnecting will not unload the current schematic
- New custom configuration GUI
  - Data is now stored as `.json` instead of `.cfg`
- New `/schematicaConfig` command
  - Opens the configuration GUI without needing to go through the FML mod list
- All changes up until 1.12.2 (the final version) backported to 1.8.9
- `LunatriusCore` is now built-in
- The no longer functional version checking has been removed
