package player;
 
import common.Equations;
import common.TurnState;
 
/**
 * The strategy interface for a Bazaar player.
 *
 * A strategy takes a turn state and the table of equations and returns
 * a decision describing which exchanges to perform and which cards to
 * purchase. Different strategies pursue different maximization goals.
 *
 * Implementations must be deterministic — given the same inputs, a
 * strategy must always return the same decision. The referee relies on
 * this property to compare player behavior across runs.
 */
public interface Strategy {
 
    /**
     * Returns a TurnDecision describing the exchanges and card purchases
     * that this strategy recommends for the given turn state and equations.
     *
     * The decision respects the following constraints from the rules:
     *   - at most 4 exchange steps total
     *   - a pebble draw is requested only if no exchanges are possible
     *     and the bank is non-empty (this is handled by the Mechanism,
     *     not the Strategy)
     *   - all exchanges must be legal given the wallet and bank at each step
     *   - all card purchases must be affordable given the wallet after
     *     exchanges
     */
    TurnDecision takeTurn(TurnState turn, Equations equations);
}