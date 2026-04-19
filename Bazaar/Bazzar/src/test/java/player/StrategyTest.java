package player;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Broad end-to-end tests covering both strategies across common
 * scenarios. Each test verifies that the strategies produce sensible
 * decisions without focusing on a specific strategy's details.
 */
public class StrategyTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Equation redForBlue() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE)));
    }
 
    private static Card cardNeedingFiveBlue() {
        return new Card(new Pebbles(List.of(
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE,
            Pebble.BLUE, Pebble.BLUE)), false);
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
 
    private static TurnState makeTurn(Pebbles wallet,
                                      Pebbles bank,
                                      List<Card> visibles) {
        return new TurnState(
            bank,
            new Cards(visibles),
            new PlayerState(wallet, 0),
            List.of());
    }
 
    // -------------------------------------------------------------------------
    // PurchasePointsStrategy
    // -------------------------------------------------------------------------
 
    @Test
    void pointsStrategyBuysAffordableCard() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of(plainFiveRed()));
        Equations eqs  = new Equations(List.of(redForBlue()));
 
        TurnDecision d =
            new PurchasePointsStrategy().takeTurn(turn, eqs);
 
        assertEquals(1, d.getPurchases().size());
        assertTrue(d.getPoints() > 0);
    }
 
    @Test
    void pointsStrategyPrefersStarCardOverPlainWhenMorePoints() {
        // Wallet has exactly 5 RED. Star card gives more points than plain.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), starFiveRed()));
        Equations eqs = new Equations(List.of());
 
        TurnDecision d =
            new PurchasePointsStrategy().takeTurn(turn, eqs);
 
        assertEquals(1, d.getPurchases().size());
        assertTrue(d.getPurchases().get(0).hasStar());
    }
 
    @Test
    void pointsStrategyReturnsEmptyDecisionWhenNothingAffordable() {
        Pebbles wallet = new Pebbles(List.of(Pebble.YELLOW));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of(plainFiveRed()));
        Equations eqs  = new Equations(List.of(redForBlue()));
 
        TurnDecision d =
            new PurchasePointsStrategy().takeTurn(turn, eqs);
 
        assertEquals(0, d.getPurchases().size());
        assertEquals(0, d.getPoints());
    }
 
    @Test
    void pointsStrategyUsesExchangeToAffordCard() {
        // Player has 1 RED and 4 BLUE. Bank has 1 BLUE.
        // Card needs 5 BLUE. After exchanging RED->BLUE the player
        // has 5 BLUE and can afford the card.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED,
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of(cardNeedingFiveBlue()));
        Equations eqs  = new Equations(List.of(redForBlue()));
 
        TurnDecision d =
            new PurchasePointsStrategy().takeTurn(turn, eqs);
 
        assertFalse(d.getExchanges().isEmpty());
        assertFalse(d.getPurchases().isEmpty());
    }
 
    // -------------------------------------------------------------------------
    // PurchaseSizeStrategy
    // -------------------------------------------------------------------------
 
    @Test
    void sizeStrategyBuysMultipleCardsIfAffordable() {
        // Wallet has enough for two cards.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed()));
        Equations eqs = new Equations(List.of());
 
        TurnDecision d =
            new PurchaseSizeStrategy().takeTurn(turn, eqs);
 
        assertEquals(2, d.getPurchases().size());
    }
 
    @Test
    void sizeStrategyPrefersMoreCardsOverMorePoints() {
        // Two plain red cards vs one star red card.
        // Size strategy should prefer buying two cards over one.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), plainFiveRed(), starFiveRed()));
        Equations eqs = new Equations(List.of());
 
        TurnDecision d =
            new PurchaseSizeStrategy().takeTurn(turn, eqs);
 
        assertTrue(d.getPurchases().size() >= 2);
    }
 
    @Test
    void sizeStrategyReturnsEmptyWhenNothingAffordable() {
        Pebbles wallet = new Pebbles(List.of(Pebble.YELLOW));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        Equations eqs  = new Equations(List.of());
 
        TurnDecision d =
            new PurchaseSizeStrategy().takeTurn(turn, eqs);
 
        assertEquals(0, d.getPurchases().size());
    }
 
    // -------------------------------------------------------------------------
    // Exchange limit
    // -------------------------------------------------------------------------
 
    @Test
    void strategyNeverExceedsFourExchanges() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        Pebbles bank = new Pebbles(List.of(
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE,
            Pebble.BLUE, Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of());
        Equations eqs  = new Equations(List.of(redForBlue()));
 
        TurnDecision d =
            new PurchasePointsStrategy().takeTurn(turn, eqs);
 
        assertTrue(d.getExchanges().size() <= 4);
    }
}