package common;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
 
/**
 * An ordered collection of cards in the Bazaar game.
 *
 * Used to represent both the deck of face-down cards and the visible
 * cards on the table. Order matters: the first card in the list is the
 * next one to be drawn or purchased.
 *
 * Data representation:
 *   A list of Card values. The list is ordered and may contain duplicates.
 *
 * Invariant: the list is non-null.
 */
public class Cards {
 
    private final List<Card> cards;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a Cards collection from the given list.
     */
    public Cards(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Card list must not be null.");
        }
        this.cards = Collections.unmodifiableList(new ArrayList<>(cards));
    }
 
    // -------------------------------------------------------------------------
    // (5) Creating cards
    // -------------------------------------------------------------------------
 
    /**
     * Returns a new collection of 20 randomly generated cards, as used
     * by the referee at game setup.
     *
     * Each card is assigned 5 randomly chosen pebbles and a randomly
     * assigned star status.
     */
    public static Cards createRandom() {
        Random rng = new Random();
        Pebble[] colors = Pebble.values();
        List<Card> result = new ArrayList<>();
        while (result.size() < 20) {
            List<Pebble> pebbleList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                pebbleList.add(colors[rng.nextInt(colors.length)]);
            }
            boolean star = rng.nextBoolean();
            result.add(new Card(new Pebbles(pebbleList), star));
        }
        return new Cards(result);
    }
 
    // -------------------------------------------------------------------------
    // (6) Determining whether a player can acquire any card
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if the given wallet is sufficient to purchase at least
     * one card in this collection.
     *
     * Used by the referee to determine whether a player can buy anything
     * on its turn.
     */
    public boolean canAcquireAny(Pebbles wallet) {
        for (Card card : cards) {
            if (card.canAcquire(wallet)) {
                return true;
            }
        }
        return false;
    }
 
    // -------------------------------------------------------------------------
    // (7) Rendering the collection graphically
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of all cards in this collection,
     * listing each card on its own line with a one-based index.
     *
     * Example:
     *   Cards:
     *     1.  [ R W B G Y ]  *
     *     2.  [ R R B G Y ]
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cards:\n");
        for (int i = 0; i < cards.size(); i++) {
            sb.append(String.format("  %2d.  %s%n", i + 1, cards.get(i).render()));
        }
        return sb.toString();
    }
 
    // -------------------------------------------------------------------------
    // Accessors and transformations
    // -------------------------------------------------------------------------
 
    /**
     * Returns a read-only view of the cards in this collection.
     */
    public List<Card> getCards() {
        return cards;
    }
 
    /**
     * Returns the number of cards in this collection.
     */
    public int size() {
        return cards.size();
    }
 
    /**
     * Returns true if this collection contains no cards.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
 
    /**
     * Returns the first card in this collection.
     *
     * @throws IllegalStateException if this collection is empty
     */
    public Card getFirst() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot get first card of an empty collection.");
        }
        return cards.get(0);
    }
 
    /**
     * Returns a new Cards with the first card removed. Does not modify
     * this collection.
     *
     * Used when the referee draws the next card from the deck or removes
     * a purchased card from the visible cards.
     *
     * @throws IllegalStateException if this collection is empty
     */
    public Cards removeFirst() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot remove from an empty collection.");
        }
        return new Cards(cards.subList(1, cards.size()));
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this collection and {@code that} contain the same
     * cards in the same order.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cards)) return false;
        Cards that = (Cards) o;
        return this.cards.equals(that.cards);
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        return cards.hashCode();
    }
 
    /**
     * Returns a text representation of this card collection.
     */
    @Override
    public String toString() {
        return render();
    }
}