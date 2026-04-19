package player;
 
/**
 * A strategy that maximizes the number of cards purchased in a single turn.
 *
 * Among all legal exchange-and-purchase combinations, this strategy
 * picks the one that results in the greatest number of card purchases.
 * Ties are broken using the rules defined in AbstractStrategy.
 */
public class PurchaseSizeStrategy extends AbstractStrategy {
 
    /**
     * Returns true if decision {@code a} purchases strictly more cards
     * than decision {@code b}.
     */
    @Override
    protected boolean isBetter(TurnDecision a, TurnDecision b) {
        return a.getPurchases().size() > b.getPurchases().size();
    }
}