# evasion

This is the implementation of the architecture for the [evasion game](https://cs.nyu.edu/courses/fall20/CSCI-GA.2965-001/evasion.html). 
Any questions, requests, or bug reporting, please contact me at chenye.xu@nyu.edu or xiaoshi.wang@nyu.edu via class mailing list!

# Requirements

* Java 8 SDK
* Apache Maven
* Node.js (optional, client-side, for display) Tested on v5.5.0

# Clone and build
```
git clone git@github.com:mipademiao/hps-evasionArchitecture.git
cd hps-evasionArchitecture
mvn clean package
```


# Run
```
java -jar ./target/evasion-1.0-SNAPSHOT.jar [player 1 port] [player 2 port] [max walls] [wall placement delay] ([display host] [display port])
```

As an example if running with display using random players:

```
java -jar ./target/evasion-1.0-SNAPSHOT.jar 9000 9001 100 10 127.0.0.1 8999
node web.js 8999 8998
python ./players/random_player.py 9000
python ./players/random_player.py 9001
```
The display will be shown in your web browser here: http://127.0.0.1:8998/

# Details

Players should connect on the ports specified as arguments to the jar after it has started executing. Each message sent in either direction (to client or to server) should/will end with a newline.

Upon connecting, players will be prompted with `sendname`; they should respond with their team name. The game won't start until both players have done so.

Multiple games will be played during a single session. At the start of each game, the player will receive the message `hunter` or `prey`, which identifies their role in the upcoming match. Players should not respond to this message.

Immediately after that, the game will commence and the server will begin sending messages containing the full game state at each iteration to each player. These messages look like:

```
[playerTimeLeft] [gameNum] [tickNum] [maxWalls] [wallPlacementDelay] [boardsizeX] [boardsizeY] [currentWallTimer] [hunterXPos] [hunterYPos] [hunterXVel] [hunterYVel] [preyXPos] [preyYPos] [numWalls] {wall1 info} {wall2 info} ... 
```

Each "{wall info}" above is just a set of numbers describing a wall on the playing field. There will be `[numWalls]` such sets.

A horizontal wall is identified by: `0 [y] [x1] [x2]` where `y` is its y location, `x1` is the x location of its left-most pixel (i.e. smaller x value), and `x2` is the x location of its right-most pixel (i.e. larger x value). 

A vertical wall is identified by: `1 [x] [y1] [y2]` where `x` is its x location, `y1` is the y location of its bottom-most pixel (i.e. smaller y value), and `y2` is the y location of its top-most pixel (i.e. larger y value).

A diagonal wall is identified by: `2 [x1] [x2] [y1] [y2] [builddirection]` where the line from (x1,y1) to (x2,y2) represents the diagonal wall and builddirection represents whether the line began by incrementing in the x or y direction from its starting point. The value  `builddirection` will be a 0 if the line was begun by building in the x direction from its starting point (x1,y1), or a 1 if the line was begun by building in the y direction.

As an example, given `2 0 1 0 1 0`, the line would be represented by the points: (0,0), (1,0), (1,1). Given `2 0 1 0 1 1`, the line would be represented by the points: (0,0), (0,1), (1,1).

A counterdiagonal wall is identified by: `3 [x1] [x2] [y1] [y2] [builddirection]` where the line from (x1,y1) to (x2,y2) represents the counterdiagonal wall and builddirection represents whether the line began by incrementing in the x or y direction from its starting point. The value  `builddirection` will be a 0 if the line was begun by building in the x direction from its starting point (x1,y1), or a 1 if the line was begun by building in the y direction.

As an example, given `3 0 1 1 0 0`, the line would be represented by the points: (0,1), (1,1), (1,0). Given `3 0 1 1 0 1`, the line would be represented by the points: (0,1), (0,0), (1,0).

The order of the `{wall info}` sets is relevent; when the hunter references a wall to delete it should do so using the wall's place in this list, starting at 0.

Note that both players get the same game state information each tick, EXCEPT for playerTimeLeft which is specific to the given player (and given in milliseconds). (Players are not permitted to know how close their opponents are to exhausting their time limits.)

# Hunter

In response to each received game state message, the hunter should send the following:

```
[gameNum] [tickNum] [wall type to add] [wall index to delete] [wall index to delete] [wall index to delete] ...
```

`[gameNum]` and `[tickNum]` should be relayed directly back to the server based on which game state message this action is in response to. 

`[wall type to add]` should be 0 for no wall, 1 for horizontal wall, 2 for vertical wall, 3 for diagonal wall, 4 for counterdiagonal wall. There is no penalty for asking for a wall that can't be built for whatever reason.

Note about diagonals:

Diagonal is when x and y are both increasing or both are decreasing. Currently given a diagonal will always be created where Y is incremented first to start off the diagonal. EX: given (i,j) the diagonal (i-1,j-1), (i-1,j), (i,j), (i,j+1), (i+1,j+1) is created.

Counterdiagonal is where if x is increasing y is decreasing. The Counterdiagonal will always be created where Y is decremented first. EX: given(i,j) the diagonal (i-1,j+1) (i-1,j) (i,j), (i,j-1), (i+1,j-1) is created.

Each `[wall index to delete]` specifies a wall to be deleted, based on its position (starting at 0) in the game state message. There can be any number of these, or none. Asking to delete a wall index that doesn't exist has no effect.

The hunter bounce behavior is as follows (* = wall or arena boundary, arrow = hunter moving in the direction it points):

```
First the pixels adjacent to both the hunter and the one being hit are considered...

+-+-+    +-+-+
|*| |    |*|↗|
+-+-+ -> +-+-+
|*|↖|    |*| |  
+-+-+    +-+-+   (1)

+-+-+    +-+-+
|*|*|    |*|*|
+-+-+ -> +-+-+
| |↖|    |↙| |
+-+-+    +-+-+   (2)

+-+-+    +-+-+
|*|*|    |*|*|
+-+-+ -> +-+-+
|*|↖|    |*|↘|
+-+-+    +-+-+   (3)


+-+-+
|*| |
+-+-+  = search is expanded, now including the other pixels adjacent to the one being hit...
| |↖|
+-+-+


  +-+        +-+
  | |        | |
+-+-+-+    +-+-+-+
|*|*| | -> |*|*| |
+-+-+-+    +-+-+-+
  | |↖|      |↙| |
  +-+-+      +-+-+   (4)

  +-+        +-+
  |*|        |*|
+-+-+-+    +-+-+-+
| |*| | -> | |*|↗|
+-+-+-+    +-+-+-+
  | |↖|      | | |
  +-+-+      +-+-+   (5)

  +-+        +-+
  |*|        |*|
+-+-+-+    +-+-+-+
|*|*| | -> |*|*| |
+-+-+-+    +-+-+-+
  | |↖|      | |↘|
  +-+-+      +-+-+   (6)

  +-+        +-+
  | |        | |
+-+-+-+    +-+-+-+
| |*| | -> | |*| |
+-+-+-+    +-+-+-+
  | |↖|      | |↘|
  +-+-+      +-+-+   (7)
```

(The same calculations are mirrored about the relevant axes for the other three directions in which the hunter can be traveling.)

# Prey

In response to each received game state message, the prey should send the following:

```
[gameNum] [tickNum] [x movement] [y movement]
```

`[gameNum]` and `[tickNum]` should be relayed directly back to the server based on which game state message this action is in response to. 

The x and y movement specifies the direction in which the prey wishes to travel. This should be 1, 0, or -1 for each -- values outside this range will be clamped. On ticks in which the prey can't move (`tickNum % 2 == 0`), these fields will have no effect, but placeholder values should still be sent.

# Capture

As noted in the class site game overview, the game is over when the Euclidean distance between hunter and prey is within four, and there is no wall between them. Specifically, this implementation calculates whether a wall is between them by using [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm) to find the points approximately on the line segment connecting them, and testing whether a wall occupies any of those points. 

Specifically, this [helpful reference](http://www.roguebasin.com/index.php?title=Bresenham%27s_Line_Algorithm#Ruby) was used as a guideline for implementing the algorithm. The Ruby version specifically was translated into a Java implementation for use in this project.

# Display

To enable the display, on your local machine, first run `node web.js [display port] [local webserver port]`, and go to `localhost:[local webserver port]` in your browser. You'll be accessing the port given by `[local webserver port]` locally, but if using energon you'll need a connection from energon to your local port given by `[display port]`, so router/firewall port forwarding for that port might be required. The `[display port]` is where the application sends the data to node.js. The `[local webserver port]` is where node.js sends the data to browser.

Now run the main java jar, supplying the display host and display port parameters as listed in the run instructions above. If running locally, host should be "localhost". If running on energon, it should be the ip address of your local machine. (You can try using 'python display/getLocalIp.py' but if that fails any "what is my ip" web service should suffice.) The display port should match what you supplied to the node server.

From this point everything should just work. You should not need to reset your browser tab or the node server -- not even in between restarts of the java jar.

Note about diagonals: in the HTML for the display, the Y axis is flipped so the diagonal will look like a counterdiagonal.


# Other notes

Each player has two minutes of "thinking" time, which can be distributed across ticks as they see fit. If a player's total elapsed time reaches zero on any turn, the game is over. If the prey times out, the score for the current game is recorded as the current tick number. If the hunter times out, the score for the current game is recorded as (effectively) infinity. (Note: this is basically the opposite of what was described in class -- the realtime, 1/60th of a second per tick, server-ignoring-old-moves system has been scrapped.)

The current game is over when the server sends the message `hunter` or `prey`, meaning a new game is about to start, or `done`, meaning the session is over and the player should disconnect.

# Player code

Sample player code can be found in the "players" directory (random_player.py).

To run: `python random_player.py [port on which to connect]`

# Acknowledgements

Original implementation provided by https://github.com/danielrotar/evasion
