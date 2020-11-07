[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FAlmasB%2FFXGLGames&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=PAGE+VIEWS&edge_flat=false)](https://hits.seeyoufarm.com)

This repo contains sample games built with <a href="https://github.com/AlmasB/FXGL">FXGL</a> Game Library.
Each game focuses on one or two aspects of FXGL, e.g. Drop focuses on bare minimums, Pac-man focuses on AI, etc.

Checkout [this commit](https://github.com/AlmasB/FXGLGames/commit/a0821c76ba4a7a64dba4f9ec6f182827d909561c) for Java 8 code for all projects (FXGL 0.5.4).
All projects will eventually be upgraded to Java 11 code (FXGL 11.0+).

## Build
```bash
cd PROJECT_NAME
mvn package
```
This will produce a standalone executable with that project in <code>target/</code>.
Most games are pre-built and can be downloaded from [binaries](binaries).

## Run
```bash
cd target/
java -jar PROJECT_NAME-VERSION.jar
```
OR simply double-click the jar file if the extensions are correctly set on your machine.

## Build for Mobile

Follow latest instructions from [client-samples](https://github.com/gluonhq/client-samples#build-and-run-the-samples). A relevant extract is copied below.

For example, to build Breakout for Android (can only build from Linux):

1. Download [GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.2.0) zip: `graalvm-ce-java11-linux-amd64-20.2.0.tar.gz`

2. Set `GRAALVM_HOME` and `JAVA_HOME` environment variables to the GraalVM installation directory. For example:

```
export GRAALVM_HOME=/opt/graalvm-ce-java11-20.2.0
export JAVA_HOME=$GRAALVM_HOME
```   

3.

```
cd Breakout
mvn clean client:build
mvn client:package

// connect your Android device and allow it installation over USB

mvn client:install
```

You can now run the game from Android, or you can run with logging from Linux: `mvn client:run`.

## Contribute

These game demos are constantly upgraded, so feel free to fork and add something of your own.

## Projects by difficulty

### Beginner

* [Cannon](Cannon)
* [Drop](Drop)
* [Tower Defense](TowerDefense)
* [Bomberman](Bomberman)
* [OutRun](OutRun) (in Kotlin)
* [Shooter](Shooter)
* [Space Runner](SpaceRunner)

### Intermediate

* [GeoJumper](GeoJumper) (in Kotlin)
* [Pong](Pong)
* [Breakout](Breakout)
* [TicTacToe](TicTacToe)
* [Battle Tanks](BattleTanks)
* [Flappy Bird](FlappyBird)
* [Slot Machine](SlotMachine)

### Advanced

* [Space Invaders](SpaceInvaders)
* [Geometry Wars](GeometryWars)
* [Mario](Mario)
* [Pac-man](Pacman)

## Community

You are very welcome to contribute to any of these games, or link to your own games.
List of community developed games / demos: (Please add links below)

* [FXGL based Idle Game](https://github.com/softknk/softknk.io) by Daniel Künkel.
