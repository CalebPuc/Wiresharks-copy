package referee;
 
import common.*;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
/*
 * The referee's complete private knowledge about a running game.
 *
 * Never shared directly with players -- the referee derives a
 * TurnState from it containing only what the active player is
 * allowed to know.
 *
 * Data representation:
 *   bank:    pebbles available for trading
 *   visibles: the four face-up cards players may purchase
 *   deck:    remaining face-down cards not yet revealed
 *   players: all active players in turn order, each with a wallet
 *            and score; the first player is the active player
 *
 * Invariant: bank, visibles, deck, players are non-null;
 *            players has at least one entry.
 */
public class GameState {
 
    private final Pebbles           bank;
    private final Cards             visibles;
    private final Cards             deck;
    private final List<PlayerState> players;
 
    // Pebbles Cards Cards List<PlayerState> -> GameState
    // creates a GameState with the given bank, visible cards, deck, and players
    // throws IllegalArgumentException if any argument is null or players is empty
    public GameState(Pebbles bank,
                     Cards visibles,
                     Cards deck,
                     List<PlayerState> players) {
        if (bank == null || visibles == null || deck == null || players == null) {
            throw new IllegalArgumentException(
                "GameState fields must not be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException(
                "GameState must have at least one player.");
        }
        this.bank     = bank;
        this.visibles = visibles;
        this.deck     = deck;
        this.players  = Collections.unmodifiableList(new ArrayList<>(players));
    }
 
    // GameState -> Pebbles
    // returns the bank's current pebble supply
    public Pebbles getBank() {
        return bank;
    }
 
    // GameState -> Cards
    // returns the face-up cards available for purchase
    public Cards getVisibles() {
        return visibles;
    }
 
    // GameState -> Cards
    // returns the face-down card deck
    public Cards getDeck() {
        return deck;
    }
 
    // GameState -> List<PlayerState>
    // returns all players in turn order -- first player is the active one
    public List<PlayerState> getPlayers() {
        return players;
    }
 
    // GameState -> PlayerState
    // returns the active player -- the first in turn order
    public PlayerState getActivePlayer() {
        return players.get(0);
    }
 
    // GameState -> boolean
    // true if the game has reached a terminal state
    // conditions: all players eliminated; a player has 20+ points;
    //             no cards remain; or bank is empty and no one can buy
    public boolean isGameOver() {
        if (players.isEmpty()) {
            return true;
        }
        for (PlayerState p : players) {
            if (p.getScore() >= 20) {
                return true;
            }
        }
        if (visibles.isEmpty() && deck.isEmpty()) {
            return true;
        }
        if (bank.isEmpty() && !anyPlayerCanBuyCard()) {
            return true;
        }
        return false;
    }
 
    // GameState -> TurnState
    // returns the TurnState for the active player -- only what they
    // are allowed to know: bank, visible cards, their own state,
    // and the scores (not wallets) of remaining players
    public TurnState toTurnState() {
        PlayerState active = players.get(0);
        List<Integer> otherScores = new ArrayList<>();
        for (int i = 1; i < players.size(); i++) {
            otherScores.add(players.get(i).getScore());
        }
        return new TurnState(bank, visibles, active, otherScores);
    }
 
    // GameState -> String
    // text representation of the full game state
    // used by the referee to display or log the current state
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Game State ===\n");
        sb.append("Bank:      ").append(bank.toString()).append("\n");
        sb.append("Visibles:  ").append(visibles.render());
        sb.append("Deck size: ").append(deck.size()).append(" cards\n");
        sb.append("Players:\n");
        for (int i = 0; i < players.size(); i++) {
            String label = (i == 0) ? " (active)" : "";
            sb.append(String.format("  %d%s: %s%n",
                i + 1, label, players.get(i).render()));
        }
        return sb.toString();
    }
 
    // GameState Object -> boolean
    // true if this and 'that' have the same bank, cards, deck, and players
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;
        GameState that = (GameState) o;
        return this.bank.equals(that.bank)
            && this.visibles.equals(that.visibles)
            && this.deck.equals(that.deck)
            && this.players.equals(that.players);
    }
 
    // GameState -> int
    // hash code consistent with equals
    @Override
    public int hashCode() {
        int result = bank.hashCode();
        result = 31 * result + visibles.hashCode();
        result = 31 * result + deck.hashCode();
        result = 31 * result + players.hashCode();
        return result;
    }
 
    // GameState -> String
    @Override
    public String toString() {
        return render();
    }
 
    // GameState -> boolean
    // true if any player currently has enough pebbles to buy at least
    // one visible card -- used as part of the game-over check
    private boolean anyPlayerCanBuyCard() {
        for (PlayerState p : players) {
            if (visibles.canAcquireAny(p.getWallet())) {
                return true;
            }
        }
        return false;
    }
}