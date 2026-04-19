package player;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for TurnDecision, focused on the scoring helper and
 * basic data representation correctness.
 */
public class TurnDecisionTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Card plainCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)), false);
    }
 
    private static Card starCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)), true);
    }
 
    private static TurnDecision emptyDecision() {
        return new TurnDecision(
            List.of(), List.of(), 0, new Pebbles());
    }
 
    // -------------------------------------------------------------------------
    // score() — plain card
    // -------------------------------------------------------------------------
 
    @Test
    void plainCardScoreWith3OrMorePebblesIs1() {
        assertEquals(1, TurnDecision.score(plainCard(), 3));
        assertEquals(1, TurnDecision.score(plainCard(), 5));
    }
 
    @Test
    void plainCardScoreWith2PebblesIs2() {
        assertEquals(2, TurnDecision.score(plainCard(), 2));
    }
 
    @Test
    void plainCardScoreWith1PebbleIs3() {
        assertEquals(3, TurnDecision.score(plainCard(), 1));
    }
 
    @Test
    void plainCardScoreWith0PebblesIs5() {
        assertEquals(5, TurnDecision.score(plainCard(), 0));
    }
 
    // -------------------------------------------------------------------------
    // score() — starred card
    // -------------------------------------------------------------------------
 
    @Test
    void starCardScoreWith3OrMorePebblesIs2() {
        assertEquals(2, TurnDecision.score(starCard(), 3));
        assertEquals(2, TurnDecision.score(starCard(), 10));
    }
 
    @Test
    void starCardScoreWith2PebblesIs3() {
        assertEquals(3, TurnDecision.score(starCard(), 2));
    }
 
    @Test
    void starCardScoreWith1PebbleIs5() {
        assertEquals(5, TurnDecision.score(starCard(), 1));
    }
 
    @Test
    void starCardScoreWith0PebblesIs8() {
        assertEquals(8, TurnDecision.score(starCard(), 0));
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    @Test
    void getPointsReturnsCorrectPoints() {
        TurnDecision d = new TurnDecision(
            List.of(), List.of(plainCard()), 3,
            new Pebbles(List.of(Pebble.RED)));
        assertEquals(3, d.getPoints());
    }
 
    @Test
    void getWalletReturnsCorrectWallet() {
        Pebbles wallet = new Pebbles(List.of(Pebble.BLUE, Pebble.GREEN));
        TurnDecision d = new TurnDecision(List.of(), List.of(), 0, wallet);
        assertEquals(wallet, d.getWallet());
    }
 
    @Test
    void getExchangesIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> emptyDecision().getExchanges().add(
                new ExchangeStep(
                    new Equation(
                        new Pebbles(List.of(Pebble.RED)),
                        new Pebbles(List.of(Pebble.BLUE))),
                    true)));
    }
 
    @Test
    void getPurchasesIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> emptyDecision().getPurchases().add(plainCard()));
    }
}