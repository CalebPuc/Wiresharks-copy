package player;
 
import common.Equation;
import common.Pebbles;
 
/**
 * One step in a sequence of pebble exchanges: an equation applied
 * in a specific direction.
 *
 * An ExchangeStep records which equation was used and which direction
 * it was applied — left-to-right (give left, receive right) or
 * right-to-left (give right, receive left).
 *
 * Data representation:
 *   equation:    the equation that was applied
 *   leftToRight: true if applied left-to-right, false if right-to-left
 */
public class ExchangeStep {
 
    private final Equation equation;
    private final boolean  leftToRight;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates an ExchangeStep for the given equation applied in the
     * given direction.
     */
    public ExchangeStep(Equation equation, boolean leftToRight) {
        if (equation == null) {
            throw new IllegalArgumentException("Equation must not be null.");
        }
        this.equation    = equation;
        this.leftToRight = leftToRight;
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns the equation used in this exchange step.
     */
    public Equation getEquation() {
        return equation;
    }
 
    /**
     * Returns true if this step applies the equation left-to-right,
     * false if it applies it right-to-left.
     */
    public boolean isLeftToRight() {
        return leftToRight;
    }
 
    /**
     * Returns the pebbles the player gives to the bank in this step.
     */
    public Pebbles getGiven() {
        return leftToRight ? equation.getLeft() : equation.getRight();
    }
 
    /**
     * Returns the pebbles the player receives from the bank in this step.
     */
    public Pebbles getReceived() {
        return leftToRight ? equation.getRight() : equation.getLeft();
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeStep)) return false;
        ExchangeStep that = (ExchangeStep) o;
        return this.leftToRight == that.leftToRight
            && this.equation.equals(that.equation);
    }
 
    @Override
    public int hashCode() {
        return 31 * equation.hashCode() + (leftToRight ? 1 : 0);
    }
 
    @Override
    public String toString() {
        String arrow = leftToRight ? "->" : "<-";
        return equation.getLeft() + " " + arrow + " " + equation.getRight();
    }
}