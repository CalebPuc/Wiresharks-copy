package common;
 
import java.util.Collections;
import java.util.List;
 
/**
 * A single card in the Bazaar game.
 *
 * A card displays five pebbles arranged in a circle, optionally decorated
 * with a star in the center. Players purchase cards to earn points; the
 * number of points depends on how many pebbles the player has left after
 * the purchase and whether the card has a star.
 *
 * Data representation:
 *   pebbles: a Pebbles with exactly 5 total pebbles
 *   hasStar: whether this card displays a star
 *
 * Invariant: pebbles contains exactly 5 pebbles.
 */
public class Card {
 
    private final Pebbles pebbles;
    private final boolean hasStar;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a card with the given pebbles and star status.
     *
     * @throws IllegalArgumentException if pebbles does not contain exactly
     *     5 pebbles
     */
    public Card(Pebbles pebbles, boolean hasStar) {
        if (pebbles == null || pebbles.size() != 5) {
            throw new IllegalArgumentException(
                "A card must display exactly 5 pebbles.");
        }
        this.pebbles = pebbles;
        this.hasStar = hasStar;
    }
 
    // -------------------------------------------------------------------------
    // (6) Determining whether a player can acquire this card
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if the given wallet contains at least the pebbles
     * displayed on this card.
     *
     * Used by a player to determine which visible cards it can purchase,
     * and by the referee to validate a card purchase request.
     */
    public boolean canAcquire(Pebbles wallet) {
        return wallet.hasAtLeast(pebbles);
    }
 
    // -------------------------------------------------------------------------
    // (7) Rendering this card graphically
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of this card, showing its pebbles
     * and whether it has a star.
     *
     * Example: "[ R W B G Y ]  *" for a starred card,
     *          "[ R W B G Y ]"   for a plain card.
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Pebble p : pebbles.toList()) {
            sb.append(" ").append(p.abbreviation());
        }
        sb.append(" ]");
        if (hasStar) {
            sb.append("  *");
        }
        return sb.toString();
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns the pebbles displayed on this card.
     */
    public Pebbles getPebbles() {
        return pebbles;
    }
 
    /**
     * Returns true if this card displays a star.
     */
    public boolean hasStar() {
        return hasStar;
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this card and {@code that} display the same pebbles
     * and have the same star status.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card that = (Card) o;
        return this.hasStar == that.hasStar
            && this.pebbles.equals(that.pebbles);
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        return 31 * pebbles.hashCode() + (hasStar ? 1 : 0);
    }
 
    /**
     * Returns a text representation of this card.
     */
    @Override
    public String toString() {
        return render();
    }
}