package referee;
 
import common.*;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
/**
 * The referee's complete, private knowledge about a running Bazaar game.
 *
 * GameState is the ground truth of the game. It is never shared directly
 * with players — the referee derives a TurnState from it to share only
 * what the active player is permitted to know.
 *
 * Data representation:
 *   bank:    the pebbles currently available for trading
 *   visibles: the four face-up cards players may purchase
 *   deck:    the remaining face-down cards not yet revealed
 *   players: all active players in turn order, each with a wallet
 *            and score; the first player in the list is the active
 *            player whose turn it currently is
 *
 * Invariant: bank, visibles, deck, and players are non-null;
 *            players contains at least one PlayerState.
 */
public class GameState {
 
    private final Pebbles           bank;
    private final Cards             visibles;
    private final Cards             deck;
    private final List<PlayerState> players;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a GameState with the given bank, visible cards, deck,
     * and player list.
     *
     * @throws IllegalArgumentException if any argument is null or if
     *     the player list is empty
     */
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
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns the bank's current pebble supply.
     */
    public Pebbles getBank() {
        return bank;
    }
 
    /**
     * Returns the face-up cards available for purchase.
     */
    public Cards getVisibles() {
        return visibles;
    }
 
    /**
     * Returns the face-down card deck.
     */
    public Cards getDeck() {
        return deck;
    }
 
    /**
     * Returns all players in turn order. The first player is the
     * active player whose turn it currently is.
     */
    public List<PlayerState> getPlayers() {
        return players;
    }
 
    /**
     * Returns the active player — the first player in the turn order.
     */
    public PlayerState getActivePlayer() {
        return players.get(0);
    }
 
    // -------------------------------------------------------------------------
    // (1) Determining whether a game is over
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if the game has reached a terminal state.
     *
     * The game is over if any of the following conditions hold:
     *   - all players have been eliminated (player list is empty);
     *   - any player has 20 or more points;
     *   - no more cards are available (visibles and deck are both empty); or
     *   - the bank is empty and no player can purchase any visible card.
     */
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
 
    // -------------------------------------------------------------------------
    // (2) Extracting the turn state
    // -------------------------------------------------------------------------
 
    /**
     * Returns the TurnState derived from this GameState for the active
     * player — the information the referee will share with the player
     * whose turn it currently is.
     *
     * The TurnState includes the bank, the visible cards, the active
     * player's own state, and the scores (not wallets) of the remaining
     * players in turn order.
     */
    public TurnState toTurnState() {
        PlayerState active = players.get(0);
        List<Integer> otherScores = new ArrayList<>();
        for (int i = 1; i < players.size(); i++) {
            otherScores.add(players.get(i).getScore());
        }
        return new TurnState(bank, visibles, active, otherScores);
    }
 
    // -------------------------------------------------------------------------
    // (3) Rendering the game state graphically
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of the full game state, showing
     * the bank, visible cards, deck size, and all player states.
     *
     * Used by the referee to display or log the current state of the game.
     */
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
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this GameState and {@code that} have the same
     * bank, visible cards, deck, and player list.
     */
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
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        int result = bank.hashCode();
        result = 31 * result + visibles.hashCode();
        result = 31 * result + deck.hashCode();
        result = 31 * result + players.hashCode();
        return result;
    }
 
    /**
     * Returns a text representation of this game state.
     */
    @Override
    public String toString() {
        return render();
    }
 
    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if any player currently has enough pebbles to
     * purchase at least one visible card.
     *
     * Used as part of the game-over check.
     */
    private boolean anyPlayerCanBuyCard() {
        for (PlayerState p : players) {
            if (visibles.canAcquireAny(p.getWallet())) {
                return true;
            }
        }
        return false;
    }
}