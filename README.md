# Overthinker

This will be a 4 player game, with one player (the “Overthinker”) wearing an EEG helmet and the other three on separate desktops.  Underthinkers (players without the EEG) may cooperate or compete towards a goal (depending on game mode), with the help from the Overthinker.

## The Overthinker

The Overthinker has access to information that the Underthinkers do not have access to. He coordinates the Underthinkers (the “Underthinkers”) together in an effort for the Underthinkers to overcome obstacles, avoid enemies, and escape from the maze.

The catch is that the Overthinker must keep his calm or risk the failing his team mates. The EEG is connected to the Overthinker, measuring his stress levels. If the Overthinker stays stressed for too long the game ends in failure.

## The Underthinkers

The Underthinkers are trapped in the maze, and have to escape before time runs out, monsters catch them. The Underthinkers will navigate a maze, solving each of its puzzles with help from the Overthinker. While following the directions of the Overthinker the Underthinkers will have to overcome their own obstacles.

## Conditions

If an Underthinker fails the maze gets harder.
If the Overthinker stresses out, the maze gets harder (or potentially ends the game).

## Game Design Stuff

The Underthinkers have limited information, but can affect things inside the maze.
The overthinker has tons of information (Underthinker location, enemy location, maze objective, etc…), but doesn’t have the capacity to affect the maze.

Each game will include obstacles for the Underthinkers and the Overthinker.

## Program Design Rubric

Each Underthinker will have their own 1st person view and movement.
Overthinker will have a top down view

---

## TODO:

### Overthinker **:: Peter, Derek, Sid**

* Read EEG data: **Peter, Derek, Sid**
* Map EEG data to environment (distorted view, faster health drop, etc…) **Derek, Sid**
* Determine stress measurements: **Peter, Derek, Sid**
* GUI (birds eye top down, Overthinker info) **Derek**
* Navigation controls: **Peter,**

### Underthinker **:: Josh, Torran**

* 1st person view : **Josh, Torran**
* Movement (jump, left, right, etc…) : **Josh, Torran**
* GUI (info feed? Other Underthinker deaths, emotes, Overthinker messages, etc...) : **Josh, Torran**
* Environment interaction? (Open doors and such) : **Josh, Torran**

### Game **:: Entire Group**

* Learn engine use : **Josh, Torran**
* Level layout is random? (Level design at least) **Torran**
* Objective design (go here, collect this, avoid that, etc…) **Derek**
* Environmental controls (response to EEG or Underthinker interaction)
* Network communication: **Peter,**
* Info feed implementation: **Peter,**

### Other **:: Entire Group**

* Models, arts, music, etc… (probably extra’s, but imperative for a game to feel complete imo): **Peter,  Torran**
* Documentation, readme, etc…
* Unit testing

### Extra

* Enemies (AI)
* Export EEG data post game (record stress levels of Overthinker to file, possibly score how calm they stayed and such)
* Overthinker adversarial mode