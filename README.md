# Overthinker

Oberthinker is a multiplayer game where one player takes the role of the Overthinker, while the others take on the roles of the Underthinkers. Their relationship is one of cooperation, the Overthinker has information, and a bit of power, and the Underthinkers have the ability to walk around in the world. The goal is for the two to work together to overcome the obstacles presented to them by the Labyrinth.

## The Overthinker

The Overthinker has access to information that the Underthinkers do not have access to. He coordinates the Underthinkers together in an effort for to overcome the Labyrinth and its obstacles, which include enemies, traps, and tricks. 

The catch is that the Overthinker must keep his calm or risk the failing his team mates. The EEG is connected to the Overthinker, measuring his stress levels. If the Overthinker stays stressed for too long the game ends in failure.

## The Underthinkers

The Underthinkers are trapped in the Labyrinth, and have to escape before the rising tide drowns them, or the Labyrinth monsters catch them. The Underthinkers will navigate the Labyrinth, solving each of its puzzles with help from the Overthinker. While following the directions of the Overthinker the Underthinkers will have to overcome their own obstacles.

## Conditions

If an Underthinker dies the Labyrinth gets harder.
If the Overthinker stresses out, the Labyrinth gets harder (or potentially ends the game).

## Game Design Stuff

The Underthinkers have limited information, but can affect things inside the Labyrinth.
The Overthinker has tons of information (Underthinker location, enemy location, Labyrinth objective, etc…), but doesn’t have the capacity to affect the Labyrinth.

Each game will include obstacles for the Underthinkers and the Overthinker.

---

# How to Run

1. One computer must run a server (overthinker.server.ServerMain), which will listen on a PORT number.

2. The other players (4 max) will then choose who they would like to play as. Make sure the player who is playing the Overthinker (limited to 1) obtains the eeg headset.

3. Run the Client (overthinker.client.ClientMain), and choose your respective roles.

4. Enjoy the game.

---

# NOTES ON SETUP

Server IP address is set in GamePlayAppState.initNetClient(), and can be set to localhost if all the clients will be run on the same machine.

The edk.dll file must be in the Windows build path--in our case, Windows/System32/edk.dll --if the headset is being used with the Overthinker.  Underthinker players do not need to set this
up, so Underthinkers can run on different OS's.  We tested on Windows 7 and Mac OSX.

The "jME3-utilities-assets.jar" archive must be added separately and individually--if asked, import it as 'classes.'  This include caused a lot of hiccups.

There is a boolean in the EEGMonitor.java file for enabling logging, it is on by default and saves to the Public Documents folder on Windows 7, or the current working
directory on any other Windows system.

Currently, there are three levels. These can be switched in by changing the "levelName" variable in GamePlayAppState to either "pentamaze", "radiomaze", or "circlemaze".

--

## Program Design Rubric

Each Underthinker will have their own 1st person view and movement.
Overthinker will have a top down view

## Completed:

### Overthinker **:: Peter, Derek, Sid**

* Read EEG data: **Peter, Sid**
* Map EEG data to environment (distorted view, faster rising tide, etc…) **Sid, Derek, Peter**
* Determine stress measurements: **Peter, Derek, Sid**
* GUI (birds eye top down, Overthinker info) **Derek**
* Navigation controls: **Peter**
* Gyroscopic controls: **Sid**

### Underthinker **:: Josh, Torran**

* 3rd person view : **Josh, Torran**
* Movement (jump, left, right, etc…) : **Josh, Torran**
* GUI (Other Underthinker deaths, etc...) : **Derek, Josh, Torran**
* Environment interaction (Open doors, collect resources, etc...) : **Josh, Torran**
* Physics (players react to changes in gravity created by the Overthinker) : **Torran**

### Game **:: Entire Group**

* Learn engine use : **Josh, Torran**
* Create Levels : **Torran, Josh**
* Objective design (keys, doors, gravity, fog) **Torran**
* Gravity change obstacles: **Torran**
* Environmental controls (response to EEG or Underthinker interaction) **Sid**
* Network communication: **Peter**
* Info feed implementation: **Peter**

### Other **:: Entire Group**

* Models, arts, music: **Peter,  Torran, Derek**
* Documentation, readme: **Josh**

### Extra

* Enemies (AI): **Josh**
* Export EEG data post game (record stress levels of Overthinker to file, possibly score how calm they stayed and such): **Sid**

---

## What Changed

The original idea had a lot of aspects that didn't work as well as we had hoped. We started off with wanting a first person view with a hud, and ended up with a 3rd person view and no hud. Also, throughout the development several ideas were added. The Labyrinth became tiered, gained an AI, and started producing fog.

The design aim that we used for the project, MVP, was a bit of a bust. It didn't help organize the code as much as we had hoped. It wasn't until late that we discovered some specific uses of the engine, specifically AppStates and the Entity design model. So some of the program has since been refactored to reflect these new ideas, while much of it remains in its previously mentioned form.