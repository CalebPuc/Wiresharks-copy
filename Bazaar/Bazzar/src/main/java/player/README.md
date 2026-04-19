# player
 
This package contains the strategy components used by a Bazaar player
to decide what to do on its turn.
 
## Why Player?
 
The strategy is the player's private decision-making logic. It depends
on the common data representations (TurnState, Equations, Cards) but
nothing in common or referee should depend on it. Keeping these
components here enforces that boundary — the referee never needs to
know how a player makes decisions, only what decision it returns.
 
No class in this package may be referenced by any class in the
`common` or `referee` packages.
 
## Contents
 
- **`Strategy.java`** — the interface all strategies implement. Takes a
  TurnState and an Equations table and returns a TurnDecision. Abstracting
  over this interface allows the player mechanism to swap strategies
  without any other component changing.
- **`AbstractStrategy.java`** — shared search logic used by both concrete
  strategies. Explores all legal combinations of up to 4 exchange steps
  followed by card purchase sequences, collects the candidates, and
  applies the spec's tie-breaking rules to select a single best decision.
- **`PurchasePointsStrategy.java`** — a Strategy that maximizes the total
  points earned in a single turn.
- **`PurchaseSizeStrategy.java`** — a Strategy that maximizes the number
  of cards purchased in a single turn.
- **`TurnDecision.java`** — the result returned by a strategy: the chosen
  exchange sequence, card purchase sequence, total points earned, and the
  player's wallet after all moves are applied. Also provides the scoring
  table as a static helper used during the search.
- **`ExchangeStep.java`** — one step in an exchange sequence: an equation
  applied in a specific direction. Records what the player gives and
  receives so the search can compute hypothetical wallet states correctly.