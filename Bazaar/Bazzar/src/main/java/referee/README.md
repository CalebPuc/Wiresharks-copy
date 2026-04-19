# referee
 
The referee's private game state. Players never see this directly --
the referee uses toTurnState() to derive a read-only snapshot of just
what the active player is allowed to know.
 
Nothing in common or player should import from here. If it does,
there's a dependency cycle and the whole build order breaks.
 
## Files
 
**GameState.java** -- the full private state of a running game: bank,
visible cards, face-down deck, and all players in turn order. Supports
isGameOver() and toTurnState(). The referee constructs a new GameState
after each legal action rather than mutating the existing one.