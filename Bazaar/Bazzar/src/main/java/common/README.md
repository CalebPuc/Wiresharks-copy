# common
 
This package contains the data representations shared by both the referee
and the player components of the Bazaar game system.
 
## Why Common?
 
The referee and player are independent components that may be developed
by different teams. Both need to reason about the same game pieces —
pebbles, equations, and cards — without either depending on the other.
Placing these shared representations here ensures that neither component
owns what both need, and that no circular dependencies arise between them.
 
No class in this package may reference any class in the `referee` or
`player` packages.
 
## Contents
 
- **`Pebble.java`** — the five pebble colors: RED, WHITE, BLUE, GREEN, YELLOW.
  The most fundamental type in the system; everything else is built on it.
- **`Pebbles.java`** — a multiset of pebbles, where color matters but order
  does not. Used to represent a player's wallet, the bank's supply, and
  each side of an equation.
- **`Equation.java`** — a single bidirectional trade between two disjoint
  pebble collections (1–4 pebbles per side). Either side may be given to
  receive the other, subject to what the player and bank each possess.
- **`Equations.java`** — the table of up to 10 equations fixed at the start
  of a game and visible to all players. Supports filtering to the subset
  applicable to a given wallet and bank.
- **`Card.java`** — a single purchasable card displaying exactly 5 pebbles
  and an optional star. A player may purchase a card if their wallet
  contains at least the pebbles shown.
- **`Cards.java`** — an ordered collection of cards. Used to represent both
  the face-down draw deck and the visible cards on the table.