# Sagrada

<img src="https://ksr-ugc.imgix.net/assets/013/393/383/88f9cae91e41ef71ac2b06fb2fa564de_original.jpg?crop=faces&w=1552&h=873&fit=crop&v=1473272732&auto=format&q=92&s=49635f0025d51f0ffe4d3b820b04c854" width="700" height="350"></img>

## What is Sagrada?
> A Game of Dice Drafting and Window Crafting for 1 to 4 players that plays in about 30 minutes.

## Where could you find more information about this game?
[Here], on the official KickStarter webpage.

## Index of the README

  - How to run the game
  - External libraries used
  - Specification covered

## Usage

The game requires [Java 8] or later versions to run.

#### Client

```sh
$ java -jar sagrada-client.jar [cli/gui] [unicode/ascii] [ip-address]
```

##### Options
- `[cli/gui]` - type `cli` or `gui` to chose from Command Line Interface and Graphical User Interface (default: `cli`)
- `[unicode/ascii]` - type `ascii` to prevent the application from using unicode characters. In Windows you need to use `bash` to correctly display unicode characters. Keep in mind that your terminal needs to have a font that supports unicode boxes. (default: `unicode`)
- `[ip-address]` - you can specify the ip address of the server (default: `localhost`)

#### Server
```sh
$ java -jar sagrada-server.jar
```
You can specify parameters in a file that you need to call `"config.properties"`, which needs to be located in the same folder as the jar.
The parameters you can specify are:
- `socketPort` - Specify the socket server port
- `rmiPort` - Specify the rmi server port
- `queueTimer` - Specify the queue timer (milliseconds)
- `inGameTimer` - Specify the in-game timer (seconds)
- `patternPath` - Specify the (relative) path to a json file where you can define custom board patterns

### External libraries used

The external libraries we used to implement some game's features are linked below.

| Library | Link | Use |
| ------ | ------ | ------ |
| GSON | https://github.com/google/gson | We use this library to load the patterns from file |

## Specification covered

**Game-specific met requirements:**

- Complete rules (toolcards from 1 to 12)

**Game-agnostic met requirements:**

- Server side
    - Implemented rules' game with JavaSE
    - Instantiated only once
    - It supports matches with Socket and RMI simultaneously
    
- Client side
    - Implemented with JavaSE
    - GUI implemented with JavaFX
    - On startup the player can choose the type of connection (Socket or RMI)
    - On startup the player can choose the type of user interface (CLI or GUI)

- Match start
    - If there are no matches in start-up phase, a new match is created, otherwise the player automatically gets into the match
    - The match starts once there are four logged players. When two players connect to the match, a timer is initialized, loaded from a configuration file located in the server-side. If the timer expires and the number of players isn't reached, the match starts with the logged players, if they are >= 2. Furthermore, if in the meantime the number of players falls below 2, the time is reset
    
- During the match
    - The players must follow the game's rules
    - The disconnection is handled both when it happens automatically and manually
    - The game continues, skipping the disconnected player
    - A player can reconnect to the match
    - All players are notified when a player disconnects
    - The players must do a move within a timer's duration
    - If at some point the player in the match is only one, the match ends with the remaining player's victory

- Advanced functionalities
    - **Dynamic patterns**: the patterns are loaded via file
    - **Multiple matches**: the server can handle more matches at a time (players are handled with queues and rooms)

Developers
----

```
$ Michele Lambertucci (https://github.com/MicheleLambertucci)
$ Mattia Mancassola (https://github.com/mett29)
$ Andrea Losavio (https://github.com/ontech7)
```

License
----

MIT

[//]: #

   [HERE]: <https://www.kickstarter.com/projects/floodgategames/sagrada-a-game-of-dice-drafting-and-window-craftin>
   [Java 8]: <https://www.java.com/it/download/>

