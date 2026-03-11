package common;

import common.Cards.PebbleColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* A single equation: a bidirectional trade between two pebble collections.
*/
public class Equation {

    private final List<PebbleColor> left;
    private final List<PebbleColor> right;

    /**
     * Creates an equation with the given two sides.
     *
     * @param left  1–4 pebbles; must not be empty
     * @param right 1–4 pebbles; must not share any color with left
     * @throws IllegalArgumentException if constraints are violated
     */
    public Equation(List<PebbleColor> left, List<PebbleColor> right) {
        validateSide(left, "left");
        validateSide(right, "right");
        if (!disjointColors(left, right)) {
            throw new IllegalArgumentException(
                    "Both sides of an equation must not share any pebble color.");
        }
        this.left  = Collections.unmodifiableList(new ArrayList<>(left));
        this.right = Collections.unmodifiableList(new ArrayList<>(right));
    }

    // ------------------------------------------------------------------
    // Public accessors
    // ------------------------------------------------------------------

    public List<PebbleColor> getLeft()  { return left; }
    public List<PebbleColor> getRight() { return right; }

    // ------------------------------------------------------------------
    // Functionality helpers (used by EquationTable filtering)
    // ------------------------------------------------------------------

    /**
     * Returns true if the player can apply this equation left→right
     * (give left, receive right) given their pebbles and the bank's pebbles.
     */
    public boolean canApplyLeftToRight(
            Map<PebbleColor, Integer> playerPebbles,
            Map<PebbleColor, Integer> bankPebbles) {
        return playerHas(playerPebbles, left) && bankHas(bankPebbles, right);
    }

    /**
     * Returns true if the player can apply this equation right→left
     * (give right, receive left) given their pebbles and the bank's pebbles.
     */
    public boolean canApplyRightToLeft(
            Map<PebbleColor, Integer> playerPebbles,
            Map<PebbleColor, Integer> bankPebbles) {
        return playerHas(playerPebbles, right) && bankHas(bankPebbles, left);
    }

    /**
     * Returns true if the equation is usable in at least one direction.
     */
    public boolean canApply(
            Map<PebbleColor, Integer> playerPebbles,
            Map<PebbleColor, Integer> bankPebbles) {
        return canApplyLeftToRight(playerPebbles, bankPebbles)
                || canApplyRightToLeft(playerPebbles, bankPebbles);
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Renders this equation as a text string, e.g.  "R W = B G"
     */
    public String render() {
        return renderSide(left) + " = " + renderSide(right);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private static void validateSide(List<PebbleColor> side, String name) {
        if (side == null || side.isEmpty() || side.size() > 4) {
            throw new IllegalArgumentException(
                    "The " + name + " side must have 1–4 pebbles.");
        }
    }

    private static boolean disjointColors(
            List<PebbleColor> a, List<PebbleColor> b) {
        Set<PebbleColor> setA = EnumSet.copyOf(a);
        for (PebbleColor p : b) {
            if (setA.contains(p)) return false;
        }
        return true;
    }

    private static boolean playerHas(
            Map<PebbleColor, Integer> playerPebbles,
            List<PebbleColor> needed) {
        Map<PebbleColor, Integer> neededMap = toCountMap(needed);
        for (Map.Entry<PebbleColor, Integer> e : neededMap.entrySet()) {
            if (playerPebbles.getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static boolean bankHas(
            Map<PebbleColor, Integer> bankPebbles,
            List<PebbleColor> needed) {
        return playerHas(bankPebbles, needed); // same logic
    }

    private static Map<PebbleColor, Integer> toCountMap(List<PebbleColor> pebbles) {
        Map<PebbleColor, Integer> map = new EnumMap<>(PebbleColor.class);
        for (PebbleColor p : pebbles) {
            map.merge(p, 1, Integer::sum);
        }
        return map;
    }

    private static String renderSide(List<PebbleColor> side) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < side.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(side.get(i).abbreviation());
        }
        return sb.toString();
    }

    @Override
    public String toString() { return render(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equation)) return false;
        Equation eq = (Equation) o;
        // Equations are symmetric: {left=right} == {right=left}
        return (left.equals(eq.left) && right.equals(eq.right))
                || (left.equals(eq.right) && right.equals(eq.left));
    }

    @Override
    public int hashCode() {
        // symmetric hash
        return left.hashCode() + right.hashCode();
    }
}
