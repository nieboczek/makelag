# Configuration
You can configure death messages, progressions and modules.

# Death messages
Custom death messages will be displayed on a death instead of the vanilla ones if a chance succeeds.
These messages are stored in `config/makelag/death_messages.json` in your Minecraft instance.
You can open the file and edit them easily, `{}` will be replaced with the player's name.
After editing just use `/makelag reload` to use your edited messages.

# Progressions
Progressions can help managing module keys by automatically changing their value.
A progression is simply a timeline of keyframes, that change the value of a key in a module.
They can be loaded using JSON files.

# Progression Format
## Root structure
```json
{
  "timeline": [ /* array of keyframes */ ]
}
```
## Keyframe object
Each entry in `timeline` has the following fields:
<table>
    <tr>
        <th>Field</th>
        <th>Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>module</code></td>
        <td><code>string</code></td>
        <td>Id of the module</td>
    </tr>
    <tr>
        <td><code>key</code></td>
        <td><code>string</code></td>
        <td>Id of the key inside the module</td>
    </tr>
    <tr>
        <td><code>startTick</code></td>
        <td><code>int</code></td>
        <td>Tick at which this keyframe starts (inclusive)</td>
    </tr>
    <tr>
        <td><code>endTick</code></td>
        <td><code>int</code></td>
        <td>Tick at which this keyframe ends (inclusive)</td>
    </tr>
    <tr>
        <td><code>startValue</code></td>
        <td><code>float</code></td>
        <td>Starting numeric value at <code>startTick</code></td>
    </tr>
    <tr>
        <td><code>endValue</code></td>
        <td><code>float</code></td>
        <td>Target numeric value at <code>endTick</code></td>
    </tr>
</table>

## Behavior
* Between `startTick` and `endTick`, the progression system linearly interpolates between `startValue` to `endValue`.
* Before `startTick`, the keyframe does not affect the value.
* After `endTick`, the key will retain the last `endValue` until another keyframe overrides it.
* Keyframes for the same `module` and `key` should not overlap in time. Overlapping `startTick` with `endTick` is allowed.

## An example of a progression
```json
{
  "timeline": [
    { "module": "packet", "key": "delay",     "startTick": 0,     "endTick": 36000,  "startValue": 0,   "endValue": 400    },
    { "module": "packet", "key": "delay",     "startTick": 36000, "endTick": 72000,  "startValue": 400, "endValue": 1200   },
    { "module": "death",  "key": "intensity", "startTick": 72000, "endTick": 216000, "startValue": 0,   "endValue": 0.0001 }
  ]
}
```
In this example:
* For ticks 0 to 36,000 (0-30 minutes), `delay` key of module `packet` ramps up from 0 to 400.
* For ticks 36,000 to 72,000 (30-60 minutes), it ramps up from 400 to 1200.
* For ticks 72,000 to 216,000 (60-180 minutes) `intensity` of `death` goes from 0 to 0.0001.

## Loading progressions
Put your progression JSON in `config/makelag/progression/` inside your Minecraft instance.  
Use `/makelag progression load your_progression` where `your_progression` should be replaced
with the file name of your progression file minus the `.json` file extension.  
If your progression has been already loaded before in the same world you can use `/makelag reload`,
both commands will work the same.


# Modules
A module affects gameplay in some way. Modules can be applied per player. You can change their behavior
using `/makelag state <player> <module> <key> <value>`.


# All modules
## `advancementUnlock`
Grants an advancement for a player. Revokes the advancement and then grants it if it was granted before.  
Possible advancements:
* Getting an Upgrade
* Acquire Hardware
* Stone Age
* Isn't It Iron Pick
* Diamonds!
* Not Today, Thank You
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `changeDeathMessage`
Changes the death message of a player to a custom one.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every death. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `death`
Instantly makes the player die with a custom message.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `disappearShiftPlacedBlocks`
Starts a timer after `blocksToStartTimer` blocks have been shift-placed for `timerLength` ± `timerLengthDelta` milliseconds.  
After the timer finishes, the shift-placed blocks will disappear.

<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every shift-placed block. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
    <tr>
        <td><code>blocksToStartTimer</code></td>
        <td>not negative <code>integer</code></td>
        <td>Count of shift-placed blocks to start timer.</td>
    </tr>
    <tr>
        <td><code>timerLength</code></td>
        <td>not negative <code>integer</code></td>
        <td>Timer length in milliseconds.</td>
    </tr>
    <tr>
        <td><code>timerLengthDelta</code></td>
        <td>not negative <code>integer</code></td>
        <td>Delta added to timer. Min: <code>-timerLengthDelta</code>. Max: <code>+timerLengthDelta</code>.</td>
    </tr>
</table>

## `dismount`
Dismounts the player from a mount.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `packet`
Manipulates various packet-related things:
* Adds packet delay calculated by `delay` ± `delayDelta`.
* Drops packets with chance `dropChance`.
* Creates lag spikes if chance `lagSpikeChance` succeeds, and multiples the delay by `lagSpikeMultiplier`.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>delay</code></td>
        <td>not negative <code>integer</code></td>
        <td>Added packet delay.</td>
    </tr>
    <tr>
        <td><code>delayDelta</code></td>
        <td>not negative <code>integer</code></td>
        <td>Delta added to delay. Min: <code>-delayDelta</code>. Max: <code>+delayDelta</code>.</td>
    </tr>
    <tr>
        <td><code>dropChance</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to drop packet. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
    <tr>
        <td><code>lagSpikeChance</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to create a lag spike. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
    <tr>
        <td><code>lagSpikeMultiplier</code></td>
        <td>not negative <code>float</code></td>
        <td>Multiplies the packet delay if a lag spike has been created.</td>
    </tr>
</table>

## `sendStats`
Sends statistics like dropped packets, modules ran, fake lag spikes and lag spikes.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every minute. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `stopSprinting`
Stops the player from sprinting.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

## `teleport`
Teleports the player away to a place previously been in for two seconds and then teleports them back.
<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>intensity</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>

# Config module
The keys under this module affects everyone. You can configure them using `/makelag config <key> <value>`.  
Possible actions:
* If chance `fakeLagSpikeChance` succeeds, executes the following set of commands:
  * `/tick freeze` 
  * wait 2 seconds
  * `/tick unfreeze`
  * `/tick rate 60`
  * wait 1 second
  * `/tick rate 20`

<table>
    <tr>
        <th>Key</th>
        <th>Key type</th>
        <th>Key description</th>
    </tr>
    <tr>
        <td><code>fakeLagSpikeChance</code></td>
        <td><code>float</code> (0 to 1)</td>
        <td>Chance to trigger a fake lag spike every tick. (e.g. 0.1 = 10%, 0.01 = 1%)</td>
    </tr>
</table>
