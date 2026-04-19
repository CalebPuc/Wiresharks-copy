package common;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
 
/**
 * A multiset of pebbles — a collection where color matters but order does not,
 * and the same color may appear multiple times.
 *
 * Used to represent a player's wallet, the bank's supply, and each side
 * of an equation.
 *
 * Data representation:
 *   A map from Pebble to a positive integer count. Colors with zero
 *   pebbles are absent from the map entirely.
 *
 * Invariant: all counts are strictly positive.
 */
public class Pebbles {
 
    private final Map<Pebble, Integer> counts;
 
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
 
    /**
     * Creates an empty pebble collection.
     */
    public Pebbles() {
        this.counts = new EnumMap<>(Pebble.class);
    }
 
    /**
     * Creates a pebble collection from a flat list of colors.
     * Colors may repeat; order does not matter.
     */
    public Pebbles(List<Pebble> pebbles) {
        this.counts = new EnumMap<>(Pebble.class);
        for (Pebble p : pebbles) {
            counts.merge(p, 1, Integer::sum);
        }
    }
 
    /**
     * Creates a pebble collection from an existing count map.
     * Colors with a count of zero are ignored.
     */
    public Pebbles(Map<Pebble, Integer> counts) {
        this.counts = new EnumMap<>(Pebble.class);
        for (Map.Entry<Pebble, Integer> e : counts.entrySet()) {
            if (e.getValue() > 0) {
                this.counts.put(e.getKey(), e.getValue());
            }
        }
    }
 
    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this collection contains at least as many pebbles
     * of each color as {@code that}.
     *
     * Used to determine whether a player can afford a trade or card purchase.
     */
    public boolean hasAtLeast(Pebbles that) {
        for (Map.Entry<Pebble, Integer> e : that.counts.entrySet()) {
            if (this.counts.getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }
 
    /**
     * Returns the number of pebbles of the given color in this collection.
     * Returns 0 if the color is absent.
     */
    public int countOf(Pebble color) {
        return counts.getOrDefault(color, 0);
    }
 
    /**
     * Returns the total number of pebbles across all colors.
     */
    public int size() {
        int total = 0;
        for (int n : counts.values()) total += n;
        return total;
    }
 
    /**
     * Returns true if this collection contains no pebbles of any color.
     */
    public boolean isEmpty() {
        return counts.isEmpty();
    }
 
    /**
     * Returns true if this collection and {@code that} share no colors.
     *
     * Used to validate that the two sides of an equation are disjoint.
     */
    public boolean isDisjointFrom(Pebbles that) {
        for (Pebble p : that.counts.keySet()) {
            if (this.counts.containsKey(p)) return false;
        }
        return true;
    }
 
    // -------------------------------------------------------------------------
    // Transformations
    // -------------------------------------------------------------------------
 
    /**
     * Returns a new Pebbles containing all pebbles from both this collection
     * and {@code that}. Does not modify this collection.
     *
     * Used when a player receives pebbles from a trade or bank draw.
     */
    public Pebbles add(Pebbles that) {
        Map<Pebble, Integer> result = new EnumMap<>(this.counts);
        for (Map.Entry<Pebble, Integer> e : that.counts.entrySet()) {
            result.merge(e.getKey(), e.getValue(), Integer::sum);
        }
        return new Pebbles(result);
    }
 
    /**
     * Returns a new Pebbles with the pebbles in {@code that} removed from
     * this collection. Does not modify this collection.
     *
     * @throws IllegalArgumentException if this collection does not contain
     *     at least as many of each color as {@code that}
     *
     * Used when a player pays pebbles for a trade or card purchase.
     */
    public Pebbles remove(Pebbles that) {
        if (!this.hasAtLeast(that)) {
            throw new IllegalArgumentException(
                "Cannot remove pebbles not present in this collection.");
        }
        Map<Pebble, Integer> result = new EnumMap<>(this.counts);
        for (Map.Entry<Pebble, Integer> e : that.counts.entrySet()) {
            int remaining = result.get(e.getKey()) - e.getValue();
            if (remaining == 0) {
                result.remove(e.getKey());
            } else {
                result.put(e.getKey(), remaining);
            }
        }
        return new Pebbles(result);
    }
 
    // -------------------------------------------------------------------------
    // Conversion
    // -------------------------------------------------------------------------
 
    /**
     * Returns the pebbles in this collection as a flat list, with colors
     * appearing in the order RED, WHITE, BLUE, GREEN, YELLOW.
     *
     * Useful for rendering and for converting to the JSON format required
     * by the integration test harnesses.
     */
    public List<Pebble> toList() {
        List<Pebble> result = new ArrayList<>();
        for (Pebble p : Pebble.values()) {
            for (int i = 0; i < counts.getOrDefault(p, 0); i++) {
                result.add(p);
            }
        }
        return Collections.unmodifiableList(result);
    }
 
    /**
     * Returns a read-only view of the underlying color-to-count map.
     *
     * Prefer {@link #hasAtLeast}, {@link #countOf}, and {@link #toList}
     * for most uses. This method exists for cases where direct map access
     * is genuinely needed.
     */
    public Map<Pebble, Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this collection and {@code that} contain the same
     * counts of each pebble color.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pebbles)) return false;
        Pebbles that = (Pebbles) o;
        return this.counts.equals(that.counts);
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     */
    @Override
    public int hashCode() {
        return counts.hashCode();
    }
 
    /**
     * Returns a text representation of this collection, listing each pebble
     * by its abbreviation in the order RED, WHITE, BLUE, GREEN, YELLOW.
     *
     * Example: a collection of two red and one blue renders as "R R B".
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pebble p : Pebble.values()) {
            int n = counts.getOrDefault(p, 0);
            for (int i = 0; i < n; i++) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(p.abbreviation());
            }
        }
        return sb.toString();
    }
}