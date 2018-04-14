This repo contains sample games built with <a href="https://github.com/AlmasB/FXGL">FXGL</a> Game Library.
Each game focuses on one or two aspects of FXGL, e.g. Drop focuses on bare minimums, Pac-man focuses on AI, etc.

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