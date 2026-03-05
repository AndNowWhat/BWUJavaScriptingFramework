# JBotWithUsV2

A modular Java 21 game scripting framework that communicates with a game server via Windows named pipes using MessagePack-encoded JSON-RPC. Scripts are dynamically discovered at runtime via Java's ServiceLoader SPI and execute on virtual threads.

## Requirements

- Java 21+
- Windows (named pipe transport)
- Gradle 8.14+ (included via wrapper)

## Quick Start

```bash
# Build all modules
./gradlew build

# Run the CLI/GUI application
./gradlew :cli:run
```

The GUI provides a command-based interface for connecting to the game server, managing scripts, and viewing logs.

## Module Architecture

Four Gradle subprojects with strict dependency layering:

```
api                 (no deps)        — Public interfaces, models, query builders
  ↑ required by
core                (api + msgpack)  — RPC client, pipe transport, script runtime
  ↑ required by
cli                 (api + core)     — Interactive CLI/GUI, command system
example-script      (api only)       — Example BotScript implementations
```

### api

Pure interface module with zero dependencies. Contains `BotScript` (the SPI), `GameAPI` (100+ methods for game interaction), fluent entity query builders (`Npcs`, `Players`, `SceneObjects`, `GroundItems`), inventory wrappers (`Backpack`, `Bank`, `Equipment`), an event bus, and inter-script communication via `MessageBus`.

### core

Runtime and communication layer. Handles Windows named pipe I/O (`PipeClient`), synchronous JSON-RPC with MessagePack serialization (`RpcClient`), script discovery from JAR files (`ScriptLoader`), and script lifecycle management on virtual threads (`ScriptRuntime`, `ScriptRunner`).

### cli

Interactive Swing-based GUI with ANSI color support and a command system. Commands include `connect`, `disconnect`, `scripts`, `screenshot`, `logs`, `mount`/`unmount`, `reload`, `ping`, `help`, `clear`, and `exit`. Supports multiple simultaneous pipe connections.

### example-script

Reference implementations (`ExampleScript`, `WoodcuttingFletcherScript`). Building this module automatically installs the JAR to the `scripts/` directory.

## Writing a Script

Scripts implement the `BotScript` SPI and are packaged as Java modules.

```java
@ScriptManifest(
    name = "My Script",
    version = "1.0",
    author = "You",
    description = "Does something useful"
)
public class MyScript implements BotScript {

    private ScriptContext ctx;

    @Override
    public void onStart(ScriptContext ctx) {
        this.ctx = ctx;
        // Initialize state, subscribe to events
    }

    @Override
    public int onLoop() {
        GameAPI api = ctx.getGameAPI();
        // Query entities, interact with the game
        Npcs npcs = new Npcs(api);
        // ...
        return 1000; // delay in ms before next loop, or -1 to stop
    }

    @Override
    public void onStop() {
        // Clean up resources
    }
}
```

Your `module-info.java` must declare the service provider:

```java
module my.script {
    requires com.botwithus.bot.api;
    provides com.botwithus.bot.api.BotScript with my.script.MyScript;
}
```

Place the compiled JAR in the `scripts/` directory. The runtime discovers and loads it automatically.

## Build Commands

```bash
./gradlew build                    # Build all modules (installs example-script to scripts/)
./gradlew clean build              # Clean and rebuild
./gradlew :cli:run                 # Run the CLI/GUI application
./gradlew :example-script:build    # Build and install example script only
./gradlew test                     # Run all tests
```

## Communication Flow

```
BotScript → GameAPI → RpcClient → PipeClient → Game Server (named pipe)
```

The pipe transport uses length-prefixed MessagePack frames over `\\.\pipe\BotWithUs`. The RPC client provides synchronous request/response semantics with async event dispatch.

## License

ISC
