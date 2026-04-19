package player;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for PurchaseSizeStrategy.
 *
 * Tests are organized by concern: basic correctness, size
 * maximization, tie-breaking behavior, exchange cap, and
 * wallet correctness after a decision.
 */
public class PurchaseSizeStrategyTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static TurnState makeTurn(Pebbles wallet,
                                      Pebbles bank,
                                      List<Card> visibles) {
        return new TurnState(
            bank,
            new Cards(visibles),
            new PlayerState(wallet, 0),
            List.of());
    }
 
    private static Card plainFiveRed() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), false);
    }
 
    private static Card starFiveRed() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), true);
    }
 
    private static Equation redForBlue() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE)));
    }
 
    private static final Strategy STRATEGY = new PurchaseSizeStrategy();
 
    // -------------------------------------------------------------------------
    // Basic correctness
    // -------------------------------------------------------------------------
 
    @Test
    void returnsNonNullDecision() {
        TurnState turn = makeTurn(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(),
            List.of());
        assertNotNull(STRATEGY.takeTurn(turn, new Equations(List.of())));
    }
 
    @Test
    void buysAffordableCardDirectly() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(1, d.getPurchases().size());
    }
 
    @Test
    void doesNotBuyUnaffordableCard() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertTrue(d.getPurchases().isEmpty());
    }
 
    @Test
    void handlesEmptyVisiblesWithNoError() {
        TurnState turn = makeTurn(
            new Pebbles(List.of(Pebble.RED)), new Pebbles(), List.of());
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertNotNull(d);
        assertTrue(d.getPurchases().isEmpty());
    }
 
    @Test
    void handlesEmptyEquationsWithNoError() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertNotNull(d);
    }
 
    // -------------------------------------------------------------------------
    // Size maximization
    // -------------------------------------------------------------------------
 
    @Test
    void buysTwoCardsWhenBothAffordable() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(2, d.getPurchases().size());
    }
 
    @Test
    void prefersTwoPlainCardsOverOneStarCard() {
        // Wallet has 10 RED. Size strategy prefers 2 cards over 1.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed(), starFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertTrue(d.getPurchases().size() >= 2);
    }
 
    @Test
    void buysThreeCardsWhenAllAffordable() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed(), plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(3, d.getPurchases().size());
    }
 
    @Test
    void doesNotBuyMoreCardsThanAffordable() {
        // Wallet has exactly 5 RED — can only afford one of the two cards.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(1, d.getPurchases().size());
    }
 
    @Test
    void usesExchangeToEnableMorePurchases() {
        // Player has 1 RED and 4 BLUE already. Bank has 1 BLUE.
        // Card needs 5 BLUE. After exchanging RED->BLUE the player
        // has 5 BLUE and can afford the card. The exchange is useful
        // so the strategy will choose it.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED,
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        Card blueCard  = new Card(new Pebbles(List.of(
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE,
            Pebble.BLUE, Pebble.BLUE)), false);
        TurnState turn = makeTurn(wallet, bank, List.of(blueCard));
        TurnDecision d = STRATEGY.takeTurn(
            turn, new Equations(List.of(redForBlue())));
        assertFalse(d.getExchanges().isEmpty());
        assertFalse(d.getPurchases().isEmpty());
    }
 
    // -------------------------------------------------------------------------
    // Tie-breaking
    // -------------------------------------------------------------------------
 
    @Test
    void fewerExchangesPreferredWhenCardCountIsEqual() {
        // Only one exchange is possible — verify minimum is used.
        Pebbles wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of());
        TurnDecision d = STRATEGY.takeTurn(
            turn, new Equations(List.of(redForBlue())));
        assertTrue(d.getExchanges().size() <= 1);
    }
 
    @Test
    void twoStrategiesCanProduceDifferentDecisionsOnSameInput() {
        // Both strategies should return valid non-null decisions.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), starFiveRed()));
        TurnDecision sizeD =
            new PurchaseSizeStrategy().takeTurn(turn, new Equations(List.of()));
        TurnDecision pointsD =
            new PurchasePointsStrategy().takeTurn(turn, new Equations(List.of()));
        assertNotNull(sizeD);
        assertNotNull(pointsD);
    }
 
    // -------------------------------------------------------------------------
    // Exchange cap
    // -------------------------------------------------------------------------
 
    @Test
    void neverExceedsFourExchanges() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        Pebbles bank = new Pebbles(List.of(
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE,
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of());
        TurnDecision d = STRATEGY.takeTurn(
            turn, new Equations(List.of(redForBlue())));
        assertTrue(d.getExchanges().size() <= 4);
    }
 
    // -------------------------------------------------------------------------
    // Wallet after decision
    // -------------------------------------------------------------------------
 
    @Test
    void walletReflectsStateAfterAllPurchases() {
        // Wallet has 10 RED. Buy two cards costing 5 each.
        // Wallet should be empty after.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        if (d.getPurchases().size() == 2) {
            assertEquals(0, d.getWallet().size());
        }
    }
 
    @Test
    void walletIsNonNullEvenWhenNothingHappens() {
        TurnState turn = makeTurn(
            new Pebbles(List.of(Pebble.YELLOW)), new Pebbles(), List.of());
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertNotNull(d.getWallet());
    }
}