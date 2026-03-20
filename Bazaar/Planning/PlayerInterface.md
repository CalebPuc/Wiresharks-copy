**MEMORANDUM**

---

**TO: Afsaneh Rahbar**
**FROM: Ethan Pucylowski, Caleb Pucylowski**
**DATE:** March 19, 2026
**SUBJECT:** Player Interface Representation

---

# Player Interface
 
The player component is a state machine with two concerns: mechanics (this document) and strategy (out of scope). The referee calls these methods; the player never initiates contact.

## Setup
 
**`setup(Equations equations) -> void`**
Called once before the game begins. Provides the equations that will remain fixed for the entire game.
 
## Turn Mechanics
 
**`takeTurn(TurnState state) -> Action`**
Called at the start of the player's turn. The turn state provides the bank's pebbles, the player's own wallet and score, other players' scores, and the visible cards. The player returns one of three action types:
 
- `ExchangeAction(Equation eq, Direction dir)` - trade pebbles with the bank using the given equation in the given direction.
- `PurchaseAction(Cards card)` - buy one of the visible cards using pebbles from the wallet.
- `PassAction` - end the turn without trading or buying.
 
The referee validates the returned action against the ground-truth game state and executes it if legal.
 
## Notifications (Referee -> Player, no response required)
 
**`receiveNewBank(Map<PebbleColor, Integer> bank) -> void`**
Informs the player of the bank's updated pebble counts after any exchange or purchase on any turn.
 
**`receiveVisibleCards(List<Cards> cards) -> void`**
Informs the player when the visible card set changes (after a purchase or replenishment from the deck).
 
**`gameOver(Map<String, Integer> finalScores) -> void`**
Called once when the game ends. Provides the final scores of all players.