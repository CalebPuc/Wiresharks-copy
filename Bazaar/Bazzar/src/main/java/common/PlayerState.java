package common;
 
/**
 * The visible state of one player in the Bazaar game: their wallet
 * and their current score.
 *
 * Used in both TurnState (as the active player's state, visible to
 * the player itself) and GameState (as part of the full player list
 * maintained by the referee).
 *
 * Data representation:
 *   wallet: the pebbles this player currently owns
 *   score:  this player's current point total (a non-negative integer)
 *
 * Invariant: score is non-negative.
 */
public class PlayerState {
 
    private final Pebbles wallet;
    private final int     score;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a PlayerState with the given wallet and score.
     *
     * @throws IllegalArgumentException if score is negative
     */
    public PlayerState(Pebbles wallet, int score) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet must not be null.");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score must be non-negative.");
        }
        this.wallet = wallet;
        this.score  = score;
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns this player's current pebble collection.
     */
    public Pebbles getWallet() {
        return wallet;
    }
 
    /**
     * Returns this player's current score.
     */
    public int getScore() {
        return score;
    }
 
    // -------------------------------------------------------------------------
    // Transformations
    // -------------------------------------------------------------------------
 
    /**
     * Returns a new PlayerState with the given wallet, keeping the same score.
     * Does not modify this PlayerState.
     *
     * Used when the referee updates a player's pebbles after a trade or draw.
     */
    public PlayerState withWallet(Pebbles newWallet) {
        return new PlayerState(newWallet, this.score);
    }
 
    /**
     * Returns a new PlayerState with the given score added to this one,
     * keeping the same wallet. Does not modify this PlayerState.
     *
     * Used when the referee awards points after a card purchase.
     */
    public PlayerState withAddedScore(int points) {
        return new PlayerState(this.wallet, this.score + points);
    }
 
    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of this player's state, showing
     * their wallet and score.
     *
     * Example: "Wallet: R R B  Score: 3"
     */
    public String render() {
        return "Wallet: " + wallet.toString() + "  Score: " + score;
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this PlayerState and {@code that} have the same
     * wallet and score.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerState)) return false;
        PlayerState that = (PlayerState) o;
        return this.score == that.score
            && this.wallet.equals(that.wallet);
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        return 31 * wallet.hashCode() + score;
    }
 
    /**
     * Returns a text representation of this player's state.
     */
    @Override
    public String toString() {
        return render();
    }
}