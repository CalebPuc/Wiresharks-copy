package player;
 
/**
 * A strategy that maximizes the total points earned in a single turn.
 *
 * Among all legal exchange-and-purchase combinations, this strategy
 * picks the one that yields the highest point total. Ties are broken
 * using the rules defined in AbstractStrategy.
 */
public class PurchasePointsStrategy extends AbstractStrategy {
 
    /**
     * Returns true if decision {@code a} earns strictly more points
     * than decision {@code b}.
     */
    @Override
    protected boolean isBetter(TurnDecision a, TurnDecision b) {
        return a.getPoints() > b.getPoints();
    }
}