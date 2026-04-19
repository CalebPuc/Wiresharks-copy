package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Cards class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class CardsTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Card plainCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW)),
            false);
    }
 
    private static Card starCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW)),
            true);
    }
 
    private static Card redCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)),
            false);
    }
 
    private static Cards twoCards() {
        return new Cards(List.of(plainCard(), starCard()));
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsEmptyList() {
        assertDoesNotThrow(() -> new Cards(List.of()));
    }
 
    @Test
    void constructorAcceptsNonEmptyList() {
        assertDoesNotThrow(() -> twoCards());
    }
 
    @Test
    void constructorMakesDefensiveCopy() {
        List<Card> list = new java.util.ArrayList<>(List.of(plainCard()));
        Cards cards = new Cards(list);
        list.add(starCard());
        assertEquals(1, cards.size());
    }
 
    // -------------------------------------------------------------------------
    // createRandom()
    // -------------------------------------------------------------------------
 
    @Test
    void createRandomProducesTwentyCards() {
        assertEquals(20, Cards.createRandom().size());
    }
 
    @Test
    void createRandomProducesValidCards() {
        assertDoesNotThrow(() -> Cards.createRandom());
    }
 
    // -------------------------------------------------------------------------
    // canAcquireAny()
    // -------------------------------------------------------------------------
 
    @Test
    void canAcquireAnyReturnsTrueWhenOneCardIsAffordable() {
        // plainCard and starCard both need R W B G Y
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW));
        assertTrue(twoCards().canAcquireAny(wallet));
    }
 
    @Test
    void canAcquireAnyReturnsFalseWhenNoCardIsAffordable() {
        // wallet has only yellow, cards need R W B G Y
        Pebbles wallet = new Pebbles(List.of(Pebble.YELLOW));
        assertFalse(twoCards().canAcquireAny(wallet));
    }
 
    @Test
    void canAcquireAnyReturnsFalseForEmptyCollection() {
        Pebbles wallet = new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW));
        assertFalse(new Cards(List.of()).canAcquireAny(wallet));
    }
 
    @Test
    void canAcquireAnyReturnsFalseForEmptyWallet() {
        assertFalse(twoCards().canAcquireAny(new Pebbles()));
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderBeginsWithCardsHeader() {
        assertTrue(twoCards().render().startsWith("Cards:"));
    }
 
    @Test
    void renderContainsOneLinePerCard() {
        String rendered = twoCards().render();
        assertTrue(rendered.contains("1."));
        assertTrue(rendered.contains("2."));
    }
 
    @Test
    void renderOfEmptyCollectionShowsOnlyHeader() {
        String rendered = new Cards(List.of()).render();
        assertTrue(rendered.startsWith("Cards:"));
        assertFalse(rendered.contains("1."));
    }
 
    // -------------------------------------------------------------------------
    // getCards()
    // -------------------------------------------------------------------------
 
    @Test
    void getCardsReturnsCorrectList() {
        Cards cards = twoCards();
        assertEquals(2, cards.getCards().size());
        assertEquals(plainCard(), cards.getCards().get(0));
    }
 
    @Test
    void getCardsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> twoCards().getCards().add(redCard()));
    }
 
    // -------------------------------------------------------------------------
    // size() and isEmpty()
    // -------------------------------------------------------------------------
 
    @Test
    void sizeReturnsNumberOfCards() {
        assertEquals(2, twoCards().size());
    }
 
    @Test
    void sizeOfEmptyCollectionIsZero() {
        assertEquals(0, new Cards(List.of()).size());
    }
 
    @Test
    void isEmptyReturnsTrueForEmptyCollection() {
        assertTrue(new Cards(List.of()).isEmpty());
    }
 
    @Test
    void isEmptyReturnsFalseForNonEmptyCollection() {
        assertFalse(twoCards().isEmpty());
    }
 
    // -------------------------------------------------------------------------
    // getFirst()
    // -------------------------------------------------------------------------
 
    @Test
    void getFirstReturnsFirstCard() {
        assertEquals(plainCard(), twoCards().getFirst());
    }
 
    @Test
    void getFirstThrowsOnEmptyCollection() {
        assertThrows(IllegalStateException.class,
            () -> new Cards(List.of()).getFirst());
    }
 
    // -------------------------------------------------------------------------
    // removeFirst()
    // -------------------------------------------------------------------------
 
    @Test
    void removeFirstReturnsSmallerCollection() {
        Cards result = twoCards().removeFirst();
        assertEquals(1, result.size());
    }
 
    @Test
    void removeFirstRemovesCorrectCard() {
        Cards result = twoCards().removeFirst();
        assertEquals(starCard(), result.getFirst());
    }
 
    @Test
    void removeFirstDoesNotModifyOriginal() {
        Cards original = twoCards();
        original.removeFirst();
        assertEquals(2, original.size());
    }
 
    @Test
    void removeFirstThrowsOnEmptyCollection() {
        assertThrows(IllegalStateException.class,
            () -> new Cards(List.of()).removeFirst());
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void collectionsWithSameCardsInSameOrderAreEqual() {
        assertEquals(twoCards(), twoCards());
    }
 
    @Test
    void collectionsWithDifferentCardsAreNotEqual() {
        Cards c1 = twoCards();
        Cards c2 = new Cards(List.of(plainCard()));
        assertNotEquals(c1, c2);
    }
 
    @Test
    void collectionsWithSameCardsInDifferentOrderAreNotEqual() {
        Cards c1 = new Cards(List.of(plainCard(), starCard()));
        Cards c2 = new Cards(List.of(starCard(), plainCard()));
        assertNotEquals(c1, c2);
    }
 
    @Test
    void equalCollectionsHaveEqualHashCodes() {
        assertEquals(twoCards().hashCode(), twoCards().hashCode());
    }
 
    @Test
    void collectionIsEqualToItself() {
        Cards c = twoCards();
        assertEquals(c, c);
    }
 
    @Test
    void collectionIsNotEqualToNull() {
        assertNotEquals(null, twoCards());
    }
}