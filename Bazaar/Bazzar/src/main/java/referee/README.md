# referee
 
This package contains the referee's private components — the data and
logic that only the referee owns and that players are never permitted
to access directly.
 
## Why Referee?
 
The referee is the arbiter of the game. It maintains the ground truth
of the game state, enforces the rules, and shares only what each player
is allowed to know. Keeping these components separate from `common`
enforces the information boundary: a player component can never import
from this package without violating the system's architectural rules.
 
No class in this package may be referenced by any class in the `common`
or `player` packages.
 
## Contents
 
- **`GameState.java`** — the referee's complete, private knowledge about a
  running game: the bank, the visible cards, the face-down deck, and all
  players' wallets and scores in turn order. Supports checking whether the
  game is over and deriving a TurnState to share with the active player.