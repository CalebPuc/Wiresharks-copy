**MEMORANDUM**

---

**TO: Afsaneh Rahbar**
**FROM: Ethan Pucylowski, Caleb Pucylowski**
**DATE:** March 10, 2026
**SUBJECT:** Game State Data Representation

---

The game state is a snapshot of all information the referee needs to manage a game. It consists of the following fields:

```
GameState {
  List<Equation>                equations;       // the 10 fixed equations for this game
  List<Card>                    visibleCards;    // up to 4 cards currently on the field
  Queue<Card>                   deck;            // remaining face-down cards
  Map<PebbleColor, Integer>     bank;            // pebble counts held by the bank
  List<PlayerState>             players;         // ordered list of players by turn
  int                           currentPlayer;   // index into players
  Map<Player, Integer>          scores;          // total score per player
}

PlayerState {
  Player                        player;          // reference to the player
  Map<PebbleColor, Integer>     pebbles;         // pebbles held (hidden from other players)
  boolean                       active;          // false if the player has been eliminated
}
```

All pebble collections are represented as `Map<PebbleColor, Integer>`, mapping each of the five colors to a non-negative count. A missing key is equivalent to a count of zero.

---

The following functionality must be available to the referee to run a game:

1. **Setup:** initialize the game state from a set of players: generate 10 random equations, deal 4 cards to the field, fill the bank with 20 pebbles of each color, and assign each player an empty pebble collection.

2. **Grant a turn:** return the `PlayerState` of the current player and advance the turn index afterward.

3. **Draw a pebble:** remove one random pebble from the bank and add it to the current player's pebbles; return the color drawn.

4. **Apply an exchange:** given a player's requested equation and direction, confirm that (a) the equation is in the game's table, (b) the player owns the pebbles required on the "give" side, and (c) the bank owns the pebbles required on the "receive" side; if valid, transfer pebbles between player and bank and remove the bottom-most deck card (or all visible cards if the deck is empty).

5. **Apply a card purchase:** confirm the player owns the pebbles shown on the requested card and that the card is currently visible; if valid, remove pebbles from the player, return them to the bank, remove the card from the field, and compute and record the score earned using the player's remaining pebble count.

6. **Replenish cards:** after a purchase, draw cards from the deck to refill the field to four (or as many as remain).

7. **Eliminate a player:** mark a `PlayerState` as inactive and return all of its pebbles to the bank.

8. **Show scores:** return the current score mapping for all active (and eliminated) players; scores are always visible to all participants.

9. **Detect end conditions:** return true if any of the following hold: all players are eliminated; a player has reached 20 or more points; no visible cards remain and the deck is empty; or the bank is empty and no active player can purchase any visible card.

10. **Announce outcome:** produce a ranking of surviving players by score, breaking ties arbitrarily, for delivery to all participants.