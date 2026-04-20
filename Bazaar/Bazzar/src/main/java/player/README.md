# player
 
The strategy and mechanism components for a Bazaar player. The referee
and common packages should never import from here -- the referee only
cares about the result of a turn, not how it was computed.
 
## Files
 
**Strategy.java** -- the interface both strategies implement. One method:
takeTurn(TurnState, Equations) -> TurnDecision. Having this as an interface
means the player mechanism in M6 can swap strategies without changing
anything else.
 
**PurchasePointsStrategy.java** -- picks the exchanges and purchases that
maximize total points this turn. Has the full search and tie-breaking logic
inlined. Almost identical to PurchaseSizeStrategy -- the only real difference
is how it compares two candidates. Should probably be refactored at some
point but it works for now.
 
**PurchaseSizeStrategy.java** -- picks the exchanges and purchases that
maximize the number of cards bought this turn. Same structure as
PurchasePointsStrategy, just a different comparison. The duplication is
a bit annoying but it's not worth abstracting until there's a third strategy.
 
**TurnDecision.java** -- the result a strategy returns: the chosen exchange
sequence, card purchases, total points, and the wallet state after all moves.
Also has the static score() helper which computes points from the scoring table.
 
**ExchangeStep.java** -- one step in an exchange sequence: an equation plus
a direction (left-to-right or right-to-left). Needed because an equation is
bidirectional so we have to track which way it was applied.
 
**Mechanism.java** -- wraps a strategy and implements the protocol the referee
uses to interact with a player. Has a name, delegates decisions to the strategy,
and exposes wantsPebble(), requestExchanges(), and requestPurchases() so the
referee can run a turn without knowing anything about how the strategy works.