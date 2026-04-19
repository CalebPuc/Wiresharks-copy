package common;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
/**
 * The information the referee shares with the active player at the
 * start of their turn.
 *
 * A TurnState contains only what the active player is permitted to
 * know about the game — it does not reveal other players' wallets
 * or the face-down card deck.
 *
 * Data representation:
 *   bank:    the pebbles currently available in the bank
 *   visibles: the face-up cards the active player may purchase
 *   active:  the active player's own wallet and score
 *   scores:  the scores of the remaining players in turn order
 *            (does not include the active player's score)
 *
 * Invariant: bank, visibles, and active are non-null; scores is
 *            non-null and contains only non-negative integers.
 */
public class TurnState {
 
    private final Pebbles       bank;
    private final Cards         visibles;
    private final PlayerState   active;
    private final List<Integer> scores;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a TurnState with the given bank, visible cards, active
     * player state, and remaining player scores.
     */
    public TurnState(Pebbles bank,
                     Cards visibles,
                     PlayerState active,
                     List<Integer> scores) {
        if (bank == null || visibles == null || active == null || scores == null) {
            throw new IllegalArgumentException(
                "TurnState fields must not be null.");
        }
        this.bank     = bank;
        this.visibles = visibles;
        this.active   = active;
        this.scores   = Collections.unmodifiableList(new ArrayList<>(scores));
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
     * Returns the active player's wallet and score.
     */
    public PlayerState getActive() {
        return active;
    }
 
    /**
     * Returns the scores of the remaining players in turn order.
     * Does not include the active player's score.
     */
    public List<Integer> getScores() {
        return scores;
    }
 
    // -------------------------------------------------------------------------
    // (4) Rendering the turn state graphically
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of this turn state, showing the
     * bank, visible cards, active player state, and other scores.
     *
     * Used to display what the active player knows about the game.
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Turn State ===\n");
        sb.append("Bank:    ").append(bank.toString()).append("\n");
        sb.append("Cards:   ").append(visibles.render()).append("\n");
        sb.append("Active:  ").append(active.render()).append("\n");
        sb.append("Scores:  ").append(scores.toString()).append("\n");
        return sb.toString();
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this TurnState and {@code that} have the same
     * bank, visible cards, active player state, and remaining scores.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TurnState)) return false;
        TurnState that = (TurnState) o;
        return this.bank.equals(that.bank)
            && this.visibles.equals(that.visibles)
            && this.active.equals(that.active)
            && this.scores.equals(that.scores);
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        int result = bank.hashCode();
        result = 31 * result + visibles.hashCode();
        result = 31 * result + active.hashCode();
        result = 31 * result + scores.hashCode();
        return result;
    }
 
    /**
     * Returns a text representation of this turn state.
     */
    @Override
    public String toString() {
        return render();
    }
}