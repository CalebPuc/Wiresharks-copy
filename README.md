# Bazaar — Ethan Pucylowski and Caleb Pucylowski
 
A Java implementation of the Bazaar trading card game across six milestones.
Players trade pebbles using equations and purchase cards to earn points.
The referee runs the game, enforces the rules, and eliminates misbehaving players.
 
---
 
## How to Build and Test
 
From inside `Bazaar/Bazzar/`:
 
```bash
./gradlew test                  # run all unit tests
./gradlew clean xeq xturn xstrategy xrules   # build all harness JARs
```
 
Copy harness JARs to their milestone directories:
 
```bash
cp build/libs/xeq.jar       ../3/xeq.jar
cp build/libs/xturn.jar     ../4/XTurn.jar
cp build/libs/xstrategy.jar ../5/xstrategy.jar
cp build/libs/xrules.jar    ../6/xrules.jar
```
 
---
 
## Source Files
 
### common/
Shared data representations used by both the referee and player components.
 
- **Pebble.java** — the five pebble colors (RED, WHITE, BLUE, GREEN, YELLOW)
- **Pebbles.java** — a multiset of pebbles; immutable, used for wallets and the bank
- **Equation.java** — a single bidirectional trade between two disjoint pebble groups
- **Equations.java** — the table of up to 10 equations for a game
- **Card.java** — one card displaying exactly 5 pebbles and an optional star
- **Cards.java** — an ordered collection of cards
- **PlayerState.java** — one player's wallet and score
- **TurnState.java** — the read-only snapshot the referee sends to the active player
- **RuleBook.java** — legality checks for exchanges, pebble requests, and card purchases
### referee/
The referee's private components.
 
- **GameState.java** — the full private game state; supports player elimination,
  state transitions, and game-over detection
- **Referee.java** — runs a complete game, grants turns, enforces rules, eliminates
  misbehaving players, and returns winners and misbehaved players
- **GameResult.java** — the result of a completed game (winners and misbehaved)
### player/
Strategy and mechanism components for a Bazaar player.
 
- **Strategy.java** — interface all strategies implement
- **PurchasePointsStrategy.java** — maximizes total points earned per turn
- **PurchaseSizeStrategy.java** — maximizes number of cards purchased per turn
- **TurnDecision.java** — the result returned by a strategy
- **ExchangeStep.java** — one step in an exchange sequence
- **Mechanism.java** — wraps a strategy and implements the referee-player protocol
---
 
## Test Harnesses
 
| Milestone | Harness | Tests in |
|-----------|---------|----------|
| 3 | xeq.jar | `3/Tests/` |
| 4 | XTurn.jar | `4/Tests/` |
| 5 | xstrategy.jar | `5/Tests/` |
| 6 | xrules.jar | `6/Tests/` |
 
Run a harness manually:
 
```bash
java -jar 3/xeq.jar < 3/Tests/Inputs/0-in.json
```
 
---
 
## Planning Documents
 
All design memos are in `Planning/`:
 
- **game-state.md** — data representation for the referee's game state (M2)
- **player-interface.md** — player method wish list (M3)
- **player-protocol.md** — referee-player interaction protocol with sequence diagram (M4)
- **referee.md** — full GameState interface and referee-GameState protocol (M5)
- **game-observer.md** — design for an interactive game observer component (M6)