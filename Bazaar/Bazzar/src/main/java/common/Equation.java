package common;
 
import java.util.Collections;
import java.util.List;
 
/**
 * A single equation in the Bazaar game: a bidirectional trade between
 * two collections of pebbles.
 *
 * An equation specifies that the pebbles on one side may be exchanged
 * for the pebbles on the other side, in either direction, subject to
 * what the player and bank each possess.
 *
 * Data representation:
 *   left:  a Pebbles with 1–4 total pebbles
 *   right: a Pebbles with 1–4 total pebbles, whose colors are
 *          disjoint from those of left
 *
 * Invariant: left and right are non-null, each contains 1–4 pebbles,
 *            and they share no pebble colors.
 */
public class Equation {
 
    private final Pebbles left;
    private final Pebbles right;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates an equation with the given two sides.
     *
     * @throws IllegalArgumentException if either side is empty, exceeds 4
     *     pebbles, or the two sides share a pebble color
     */
    public Equation(Pebbles left, Pebbles right) {
        validateSide(left, "left");
        validateSide(right, "right");
        if (!left.isDisjointFrom(right)) {
            throw new IllegalArgumentException(
                "Both sides of an equation must not share any pebble color.");
        }
        this.left  = left;
        this.right = right;
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns the left side of this equation.
     */
    public Pebbles getLeft() {
        return left;
    }
 
    /**
     * Returns the right side of this equation.
     */
    public Pebbles getRight() {
        return right;
    }
 
    // -------------------------------------------------------------------------
    // Applicability
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if the player can apply this equation left-to-right —
     * giving the left side to the bank and receiving the right side —
     * given their current wallet and the bank's current supply.
     */
    public boolean canApplyLeftToRight(Pebbles wallet, Pebbles bank) {
        return wallet.hasAtLeast(left) && bank.hasAtLeast(right);
    }
 
    /**
     * Returns true if the player can apply this equation right-to-left —
     * giving the right side to the bank and receiving the left side —
     * given their current wallet and the bank's current supply.
     */
    public boolean canApplyRightToLeft(Pebbles wallet, Pebbles bank) {
        return wallet.hasAtLeast(right) && bank.hasAtLeast(left);
    }
 
    /**
     * Returns true if the player can apply this equation in at least
     * one direction given their current wallet and the bank's current supply.
     */
    public boolean canApply(Pebbles wallet, Pebbles bank) {
        return canApplyLeftToRight(wallet, bank)
            || canApplyRightToLeft(wallet, bank);
    }
 
    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------
 
    /**
     * Returns a text representation of this equation, showing the two sides
     * separated by " = ".
     *
     * Example: "R W = B G"
     */
    public String render() {
        return left.toString() + " = " + right.toString();
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if this equation and {@code that} represent the same trade.
     *
     * Two equations are equal if their sides match in either orientation,
     * since an equation is bidirectional.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equation)) return false;
        Equation that = (Equation) o;
        return (this.left.equals(that.left)  && this.right.equals(that.right))
            || (this.left.equals(that.right) && this.right.equals(that.left));
    }
 
    /**
     * Returns a hash code consistent with {@link #equals}.
     *
     * Uses addition so that both orientations of the same equation
     * produce the same hash code.
     */
    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }
 
    /**
     * Returns a text representation of this equation.
     *
     * Example: "R W = B G"
     */
    @Override
    public String toString() {
        return render();
    }
 
    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------
 
    /**
     * Validates that the given side contains between 1 and 4 pebbles.
     *
     * @throws IllegalArgumentException if the constraint is violated
     */
    private static void validateSide(Pebbles side, String name) {
        if (side == null || side.isEmpty() || side.size() > 4) {
            throw new IllegalArgumentException(
                "The " + name + " side must have 1–4 pebbles.");
        }
    }
}