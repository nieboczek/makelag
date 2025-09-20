# MakeLag
*Ever wanted to feel that one friends lag? Wait no more.*  
With MakeLag you can artificially create lag!

Simply just run this one command on a hosted server, an [Essential Mod](https://modrinth.com/mod/essential) world, or an [e4mc](https://modrinth.com/mod/e4mc) world and enjoy yourself some lag: `/makelag start`.

The lag will increase gradually, if you ever feel like the need to speed it up a little, use the `/makelag progression tick <tick>` command and set the tick to something like `72000` for one hour of gameplay passed, `144000` for two hours passed, etc.

By default, the client-side ping display is disabled. Toggle it whenever you want using `/makelag togglePingDisplay`.

## Features
* Client-side ping display - displays ping above player's display name.
* Server-side artificial lag - delay, random teleports, dropped packets and surprises without needing client mods.
* Progression system - create "lag over time" effects instead of settings values manually ~~(TODO) with the ability to load progressions from JSON files~~.
* Lightweight and server-only - only needed on the server, optional client install adds ping display only.

## Commands
* `/makelag start` - Loads the default progression and starts making lag.
* `/makelag pause` - Pauses the currently loaded progression, removes the lag until resumed.
* `/makelag resume` - Resumes the currently loaded progression.
* `/makelag togglePingDisplay` - Toggle the above nameplate ping display for all clients with this mod.
* `/makelag progression load <id>` - Loads the progression with the passed in id.
* `/makelag progression start` - Starts the currently loaded progression.
* `/makelag progression tick <tick>` - Sets the tick of the progression to the passed in tick.
* `/makelag state <player> <module> <key> <value>` - Sets the passed in key of the specified module to the passed in value for the specified player.
* `/makelag config <key> <value>` - Sets the specified key to the passed in value for all players.

## Modules
A module affects gameplay in some way, which is described by the module id.
Every module has an `intensity` key which controls the chance of activation.
Some modules also have other keys that change how the module works.

## Progressions
Progressions can help managing module key values by automatically changing them.
A progression is simply a timeline of keyframes, that change the value of a key in a module.
~~(TODO) They can be loaded using JSON files~~

## Notes
* This mod probably won't be ported to any other version/loader unless somebody decides to spend their time to contribute to this project.

[<img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/available/modrinth_64h.png" alt="Avaliable on Modrinth">](https://modrinth.com/mod/makelag)

## Contributing
Contributions are welcome! Feel free to:
* Open issues for bugs or feature suggestions.
* Submit PRs for fixes or new features.
* Discuss ideas in the issues tab.

## FAQ
### Does this lag my entire computer?
no
### How much sanity have you lost while making this mod?
