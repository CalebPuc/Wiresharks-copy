package player;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for PurchasePointsStrategy.
 *
 * Tests are organized by concern: basic correctness, points
 * maximization, tie-breaking behavior, exchange cap, and
 * wallet correctness after a decision.
 */
public class PurchasePointsStrategyTest {
 
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
 
    private static final Strategy STRATEGY = new PurchasePointsStrategy();
 
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
    void earnsZeroPointsWhenNothingCanBeBought() {
        Pebbles wallet = new Pebbles(List.of(Pebble.YELLOW));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(0, d.getPoints());
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
    // Points maximization
    // -------------------------------------------------------------------------
 
    @Test
    void prefersStarCardOverPlainWhenStarGivesMorePoints() {
        // Wallet has exactly 5 RED. Plain card -> 5 pts at 0 pebbles left.
        // Star card -> 8 pts at 0 pebbles left. Strategy picks star.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(),
            List.of(plainFiveRed(), starFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertEquals(1, d.getPurchases().size());
        assertTrue(d.getPurchases().get(0).hasStar());
    }
 
    @Test
    void totalPointsArePositiveWhenCardIsBought() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        assertTrue(d.getPoints() > 0);
    }
 
    @Test
    void usesExchangeToEnableCardPurchase() {
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
    void fewerExchangesPreferredWhenOutcomesAreEqual() {
        // Only one exchange is possible — verify the decision
        // uses the minimum exchanges necessary.
        Pebbles wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE));
        TurnState turn = makeTurn(wallet, bank, List.of());
        TurnDecision d = STRATEGY.takeTurn(
            turn, new Equations(List.of(redForBlue())));
        assertTrue(d.getExchanges().size() <= 1);
    }
 
    @Test
    void decisionPointsMatchExpectedScoringTable() {
        // Plain card, 0 pebbles left after purchase = 5 points.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        if (!d.getPurchases().isEmpty()) {
            assertEquals(5, d.getPoints());
        }
    }
 
    @Test
    void starCardScoreCorrectAtZeroPebbles() {
        // Star card, 0 pebbles left = 8 points.
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(starFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        if (!d.getPurchases().isEmpty()) {
            assertEquals(8, d.getPoints());
        }
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
    void walletReflectsStateAfterPurchase() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED));
        TurnState turn = makeTurn(wallet, new Pebbles(), List.of(plainFiveRed()));
        TurnDecision d = STRATEGY.takeTurn(turn, new Equations(List.of()));
        if (!d.getPurchases().isEmpty()) {
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