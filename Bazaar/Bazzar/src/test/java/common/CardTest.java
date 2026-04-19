package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Card class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class CardTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Pebbles fivePebbles() {
        return new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW));
    }
 
    private static Pebbles fiveRed() {
        return new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED));
    }
 
    private static Card plainCard() {
        return new Card(fivePebbles(), false);
    }
 
    private static Card starCard() {
        return new Card(fivePebbles(), true);
    }
 
    // -------------------------------------------------------------------------
    // Constructor — valid inputs
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsExactlyFivePebbles() {
        assertDoesNotThrow(() -> plainCard());
    }
 
    @Test
    void constructorAcceptsStarCard() {
        assertDoesNotThrow(() -> starCard());
    }
 
    // -------------------------------------------------------------------------
    // Constructor — invalid inputs
    // -------------------------------------------------------------------------
 
    @Test
    void constructorRejectsFourPebbles() {
        Pebbles four = new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN));
        assertThrows(IllegalArgumentException.class, () -> new Card(four, false));
    }
 
    @Test
    void constructorRejectsSixPebbles() {
        Pebbles six = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.WHITE, Pebble.BLUE,
            Pebble.GREEN, Pebble.YELLOW));
        assertThrows(IllegalArgumentException.class, () -> new Card(six, false));
    }
 
    @Test
    void constructorRejectsEmptyPebbles() {
        assertThrows(IllegalArgumentException.class,
            () -> new Card(new Pebbles(), false));
    }
 
    // -------------------------------------------------------------------------
    // canAcquire()
    // -------------------------------------------------------------------------
 
    @Test
    void canAcquireReturnsTrueWhenWalletMatchesExactly() {
        Card    card   = plainCard();
        Pebbles wallet = fivePebbles();
        assertTrue(card.canAcquire(wallet));
    }
 
    @Test
    void canAcquireReturnsTrueWhenWalletHasMore() {
        Card    card   = new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)), false);
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED, Pebble.RED));
        assertTrue(card.canAcquire(wallet));
    }
 
    @Test
    void canAcquireReturnsFalseWhenWalletLacksPebbles() {
        Card    card   = new Card(fiveRed(), false);
        Pebbles wallet = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        assertFalse(card.canAcquire(wallet));
    }
 
    @Test
    void canAcquireReturnsFalseWhenWalletHasWrongColors() {
        Card    card   = new Card(fiveRed(), false);
        Pebbles wallet = new Pebbles(List.of(
            Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE));
        assertFalse(card.canAcquire(wallet));
    }
 
    @Test
    void canAcquireReturnsFalseForEmptyWallet() {
        assertFalse(plainCard().canAcquire(new Pebbles()));
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderContainsAllFivePebbleAbbreviations() {
        String rendered = plainCard().render();
        assertTrue(rendered.contains("R"));
        assertTrue(rendered.contains("W"));
        assertTrue(rendered.contains("B"));
        assertTrue(rendered.contains("G"));
        assertTrue(rendered.contains("Y"));
    }
 
    @Test
    void renderOfStarCardContainsStar() {
        assertTrue(starCard().render().contains("*"));
    }
 
    @Test
    void renderOfPlainCardDoesNotContainStar() {
        assertFalse(plainCard().render().contains("*"));
    }
 
    // -------------------------------------------------------------------------
    // getPebbles() and hasStar()
    // -------------------------------------------------------------------------
 
    @Test
    void getPebblesReturnsCardPebbles() {
        Pebbles p    = fivePebbles();
        Card    card = new Card(p, false);
        assertEquals(p, card.getPebbles());
    }
 
    @Test
    void hasStarReturnsTrueForStarCard() {
        assertTrue(starCard().hasStar());
    }
 
    @Test
    void hasStarReturnsFalseForPlainCard() {
        assertFalse(plainCard().hasStar());
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void cardsWithSamePebblesAndStarAreEqual() {
        assertEquals(plainCard(), plainCard());
    }
 
    @Test
    void cardsWithDifferentStarStatusAreNotEqual() {
        assertNotEquals(plainCard(), starCard());
    }
 
    @Test
    void cardsWithDifferentPebblesAreNotEqual() {
        Card c1 = new Card(fivePebbles(), false);
        Card c2 = new Card(fiveRed(), false);
        assertNotEquals(c1, c2);
    }
 
    @Test
    void equalCardsHaveEqualHashCodes() {
        assertEquals(plainCard().hashCode(), plainCard().hashCode());
    }
 
    @Test
    void cardIsEqualToItself() {
        Card c = plainCard();
        assertEquals(c, c);
    }
 
    @Test
    void cardIsNotEqualToNull() {
        assertNotEquals(null, plainCard());
    }
}