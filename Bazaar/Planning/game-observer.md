## What This Is
 
An observer watches a game as it runs and renders what it sees. The
referee tells it about each state change. The observer never participates
-- it just watches. This is useful for demos where someone wants to see
the game without reading code or log output.
 
## The Interface
 
An observer implements this interface:
 
```java
public interface GameObserver {
 
    // GameObserver GameState -> void
    // called after each legal state change during the game
    void update(GameState state);
 
    // GameObserver GameResult -> void
    // called once when the game ends
    void gameOver(GameResult result);
}
```
 
That's it. The referee calls update() after every successful action
(pebble draw, exchanges, card purchases) and gameOver() at the end.
The observer just receives information -- it never responds and the
referee never waits for it.
 
## How It Connects to the Referee
 
The referee gets an optional observer at construction:
 
```java
// existing constructor -- works as before
public Referee(Equations equations) { ... }
 
// new constructor -- same behavior plus observer notifications
public Referee(Equations equations, GameObserver observer) { ... }
```
 
If no observer is passed, the referee behaves exactly as it does now.
All existing tests continue to work.
 
Inside runGame(), the referee calls observer.update(state) after each
state change, wrapped in a try/catch. If the observer throws, the referee
ignores it and keeps going -- a crashing observer shouldn't take down
the game. Same policy as a crashing player.
 
## What a User Sees
 
A text-based observer implementation might print something like this
to the console after each update:
 
```
--- Turn 3: Alice ---
Bank:    R R W B B G
Cards:   [R R W B G]  [W W B B Y]  [R G G Y Y *]
Alice:   wallet=[R R B]  score=2
Bob:     score=5
 
Alice exchanges R -> B
Alice buys [R R W B G]  (+1 pt)
Alice:   wallet=[R B B]  score=3
```
 
A graphical observer would render the same data visually. The interface
is the same either way.
 
## A Few Design Notes
 
The observer receives GameState snapshots. Since GameState is immutable,
the observer can't accidentally corrupt the running game even if it does
something weird with the state it receives.
 
This design only supports one observer. If multiple observers were needed
later, the referee could hold a List<GameObserver> instead -- but one is
enough for the demo use case right now.
 
The observer interface lives in the referee package since the referee
is the one calling it. A separate observer implementation (like the
text-based one above) would live wherever the person building it wants
to put it, as long as it implements the interface.