package player;
 
import common.Card;
import common.Pebbles;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
/**
 * The decision a strategy makes for one turn: a sequence of pebble
 * exchanges to perform, followed by a sequence of cards to purchase.
 *
 * Both sequences are ordered — the exchanges are applied in the order
 * listed, and the cards are purchased in the order listed.
 *
 * Data representation:
 *   exchanges: an ordered list of equations to apply, each represented
 *              as a pair of Pebbles [give, receive]
 *   purchases: an ordered list of cards to buy
 *   points:    the total points earned from the purchases
 *   wallet:    the player's pebbles after all exchanges and purchases
 *
 * Invariant: points is non-negative; all fields are non-null.
 */
public class TurnDecision {
 
    private final List<ExchangeStep> exchanges;
    private final List<Card>         purchases;
    private final int                points;
    private final Pebbles            wallet;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Creates a TurnDecision with the given exchanges, purchases,
     * points earned, and resulting wallet.
     */
    public TurnDecision(List<ExchangeStep> exchanges,
                        List<Card> purchases,
                        int points,
                        Pebbles wallet) {
        this.exchanges = Collections.unmodifiableList(new ArrayList<>(exchanges));
        this.purchases = Collections.unmodifiableList(new ArrayList<>(purchases));
        this.points    = points;
        this.wallet    = wallet;
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    /**
     * Returns the ordered sequence of exchanges this decision makes.
     */
    public List<ExchangeStep> getExchanges() {
        return exchanges;
    }
 
    /**
     * Returns the ordered sequence of cards this decision purchases.
     */
    public List<Card> getPurchases() {
        return purchases;
    }
 
    /**
     * Returns the total points earned by this decision's purchases.
     */
    public int getPoints() {
        return points;
    }
 
    /**
     * Returns the player's wallet after all exchanges and purchases
     * in this decision are applied.
     */
    public Pebbles getWallet() {
        return wallet;
    }
 
    // -------------------------------------------------------------------------
    // Scoring helper
    // -------------------------------------------------------------------------
 
    /**
     * Computes the points earned when purchasing the given card with
     * the given number of pebbles remaining in the wallet after the
     * purchase.
     *
     * Scoring table from the Bazaar rules:
     *   pebbles left | plain card | starred card
     *   3 or more    |     1      |      2
     *   2            |     2      |      3
     *   1            |     3      |      5
     *   0            |     5      |      8
     */
    public static int score(Card card, int pebblesRemaining) {
        if (card.hasStar()) {
            if (pebblesRemaining >= 3) return 2;
            if (pebblesRemaining == 2) return 3;
            if (pebblesRemaining == 1) return 5;
            return 8;
        } else {
            if (pebblesRemaining >= 3) return 1;
            if (pebblesRemaining == 2) return 2;
            if (pebblesRemaining == 1) return 3;
            return 5;
        }
    }
 
    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
 
    @Override
    public String toString() {
        return "TurnDecision{exchanges=" + exchanges.size()
            + ", purchases=" + purchases.size()
            + ", points=" + points + "}";
    }
}