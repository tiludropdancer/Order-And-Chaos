# Order and Chaos Game

This is a game similar to tic tac toe, where opponents race to reach order or wreak havoc in the game.

## How to Build

```
./gradlew clean build
```

## How to Run

```
./gradlew run
```

## How to Play

You can choose to play as either Order or Chaos. Depending on the side you choose, you're objective will differ.

Order player: aims to build a chain of 5 X's or O's in any direction (vertical, horizontal, or diagonal).

Chaos player: aims to prevent Order player from its mission, by inserting X's or O's anywhere on the board. Chaos wins when the board is filled and Order has not won.

Both players can use X's and O's throughout the game, this isn't tic-tac-toe where each player has a designated symbol.

## Modes of Play

There are three modes of play, which you can choose from the main menu:

1) Player vs. AI

2) Player vs. Player - which is played on one computer.

3) Network Play - which requires two instances of the game to be launched (possibly on two different machines) and players must follow on screen directions to connect and play each other.

## Features

*Ability to save state of current game, exit, and return to restore saved game session and continue playing at a later time.

*Track achievements via leaderboard reached by different players during different instances of the game.

*Ability to undo player's last move, an unlimited amount of times.