package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Pebbles class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class PebblesTest {
 
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
 
    @Test
    void emptyConstructorProducesEmptyCollection() {
        Pebbles p = new Pebbles();
        assertTrue(p.isEmpty());
    }
 
    @Test
    void listConstructorCountsColorsCorrectly() {
        Pebbles p = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        assertEquals(2, p.countOf(Pebble.RED));
        assertEquals(1, p.countOf(Pebble.BLUE));
    }
 
    @Test
    void listConstructorIgnoresOrder() {
        Pebbles p1 = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles p2 = new Pebbles(List.of(Pebble.BLUE, Pebble.RED));
        assertEquals(p1, p2);
    }
 
    @Test
    void mapConstructorIgnoresZeroCounts() {
        Pebbles p = new Pebbles(Map.of(Pebble.RED, 2, Pebble.BLUE, 0));
        assertEquals(2, p.countOf(Pebble.RED));
        assertEquals(0, p.countOf(Pebble.BLUE));
        assertEquals(1, p.getCounts().size());
    }
 
    @Test
    void mapConstructorIsDefensivelyCopied() {
        Map<Pebble, Integer> counts = new java.util.EnumMap<>(Pebble.class);
        counts.put(Pebble.RED, 1);
        Pebbles p = new Pebbles(counts);
        counts.put(Pebble.RED, 99);
        assertEquals(1, p.countOf(Pebble.RED));
    }
 
    // -------------------------------------------------------------------------
    // hasAtLeast()
    // -------------------------------------------------------------------------
 
    @Test
    void hasAtLeastReturnsTrueWhenExactMatch() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles cost   = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        assertTrue(wallet.hasAtLeast(cost));
    }
 
    @Test
    void hasAtLeastReturnsTrueWhenMoreThanEnough() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        Pebbles cost   = new Pebbles(List.of(Pebble.RED));
        assertTrue(wallet.hasAtLeast(cost));
    }
 
    @Test
    void hasAtLeastReturnsFalseWhenNotEnoughOfOneColor() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles cost   = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        assertFalse(wallet.hasAtLeast(cost));
    }
 
    @Test
    void hasAtLeastReturnsFalseWhenColorAbsent() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles cost   = new Pebbles(List.of(Pebble.BLUE));
        assertFalse(wallet.hasAtLeast(cost));
    }
 
    @Test
    void hasAtLeastReturnsTrueForEmptyCost() {
        Pebbles wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles cost   = new Pebbles();
        assertTrue(wallet.hasAtLeast(cost));
    }
 
    @Test
    void emptyCollectionHasAtLeastEmptyCollection() {
        assertTrue(new Pebbles().hasAtLeast(new Pebbles()));
    }
 
    // -------------------------------------------------------------------------
    // countOf()
    // -------------------------------------------------------------------------
 
    @Test
    void countOfReturnsZeroForAbsentColor() {
        Pebbles p = new Pebbles(List.of(Pebble.RED));
        assertEquals(0, p.countOf(Pebble.BLUE));
    }
 
    @Test
    void countOfReturnsCorrectCountForPresentColor() {
        Pebbles p = new Pebbles(List.of(Pebble.GREEN, Pebble.GREEN, Pebble.GREEN));
        assertEquals(3, p.countOf(Pebble.GREEN));
    }
 
    // -------------------------------------------------------------------------
    // size()
    // -------------------------------------------------------------------------
 
    @Test
    void sizeOfEmptyCollectionIsZero() {
        assertEquals(0, new Pebbles().size());
    }
 
    @Test
    void sizeCountsAllPebblesAcrossAllColors() {
        Pebbles p = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE, Pebble.GREEN));
        assertEquals(4, p.size());
    }
 
    // -------------------------------------------------------------------------
    // isEmpty()
    // -------------------------------------------------------------------------
 
    @Test
    void emptyConstructorIsEmpty() {
        assertTrue(new Pebbles().isEmpty());
    }
 
    @Test
    void nonEmptyCollectionIsNotEmpty() {
        assertFalse(new Pebbles(List.of(Pebble.RED)).isEmpty());
    }
 
    // -------------------------------------------------------------------------
    // isDisjointFrom()
    // -------------------------------------------------------------------------
 
    @Test
    void disjointCollectionsAreDisjoint() {
        Pebbles a = new Pebbles(List.of(Pebble.RED, Pebble.WHITE));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE, Pebble.GREEN));
        assertTrue(a.isDisjointFrom(b));
    }
 
    @Test
    void overlappingCollectionsAreNotDisjoint() {
        Pebbles a = new Pebbles(List.of(Pebble.RED, Pebble.WHITE));
        Pebbles b = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        assertFalse(a.isDisjointFrom(b));
    }
 
    @Test
    void emptyCollectionIsDisjointFromAnything() {
        Pebbles empty = new Pebbles();
        Pebbles full  = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        assertTrue(empty.isDisjointFrom(full));
        assertTrue(full.isDisjointFrom(empty));
    }
 
    @Test
    void isDisjointFromIsSymmetric() {
        Pebbles a = new Pebbles(List.of(Pebble.RED));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE));
        assertEquals(a.isDisjointFrom(b), b.isDisjointFrom(a));
    }
 
    // -------------------------------------------------------------------------
    // add()
    // -------------------------------------------------------------------------
 
    @Test
    void addCombinesCounts() {
        Pebbles a      = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        Pebbles b      = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles result = a.add(b);
        assertEquals(3, result.countOf(Pebble.RED));
        assertEquals(1, result.countOf(Pebble.BLUE));
    }
 
    @Test
    void addDoesNotModifyThis() {
        Pebbles a = new Pebbles(List.of(Pebble.RED));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE));
        a.add(b);
        assertEquals(1, a.size());
        assertEquals(0, a.countOf(Pebble.BLUE));
    }
 
    @Test
    void addingEmptyCollectionReturnsEqualCollection() {
        Pebbles a      = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles result = a.add(new Pebbles());
        assertEquals(a, result);
    }
 
    // -------------------------------------------------------------------------
    // remove()
    // -------------------------------------------------------------------------
 
    @Test
    void removeReducesCounts() {
        Pebbles start  = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        Pebbles cost   = new Pebbles(List.of(Pebble.RED));
        Pebbles result = start.remove(cost);
        assertEquals(1, result.countOf(Pebble.RED));
        assertEquals(1, result.countOf(Pebble.BLUE));
    }
 
    @Test
    void removeDoesNotModifyThis() {
        Pebbles start = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        Pebbles cost  = new Pebbles(List.of(Pebble.RED));
        start.remove(cost);
        assertEquals(2, start.countOf(Pebble.RED));
    }
 
    @Test
    void removeAllOfColorLeavesColorAbsent() {
        Pebbles start  = new Pebbles(List.of(Pebble.RED));
        Pebbles cost   = new Pebbles(List.of(Pebble.RED));
        Pebbles result = start.remove(cost);
        assertEquals(0, result.countOf(Pebble.RED));
        assertTrue(result.isEmpty());
    }
 
    @Test
    void removeThrowsWhenNotEnoughPebbles() {
        Pebbles start = new Pebbles(List.of(Pebble.RED));
        Pebbles cost  = new Pebbles(List.of(Pebble.RED, Pebble.RED));
        assertThrows(IllegalArgumentException.class, () -> start.remove(cost));
    }
 
    @Test
    void removeThrowsWhenColorAbsent() {
        Pebbles start = new Pebbles(List.of(Pebble.RED));
        Pebbles cost  = new Pebbles(List.of(Pebble.BLUE));
        assertThrows(IllegalArgumentException.class, () -> start.remove(cost));
    }
 
    // -------------------------------------------------------------------------
    // toList()
    // -------------------------------------------------------------------------
 
    @Test
    void toListReturnsCorrectSize() {
        Pebbles p = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        assertEquals(3, p.toList().size());
    }
 
    @Test
    void toListIsInCanonicalColorOrder() {
        Pebbles p    = new Pebbles(List.of(Pebble.YELLOW, Pebble.RED, Pebble.BLUE));
        List<Pebble> list = p.toList();
        assertEquals(Pebble.RED,    list.get(0));
        assertEquals(Pebble.BLUE,   list.get(1));
        assertEquals(Pebble.YELLOW, list.get(2));
    }
 
    @Test
    void toListOfEmptyCollectionIsEmpty() {
        assertTrue(new Pebbles().toList().isEmpty());
    }
 
    @Test
    void toListIsUnmodifiable() {
        Pebbles p = new Pebbles(List.of(Pebble.RED));
        assertThrows(UnsupportedOperationException.class, () -> p.toList().add(Pebble.BLUE));
    }
 
    // -------------------------------------------------------------------------
    // getCounts()
    // -------------------------------------------------------------------------
 
    @Test
    void getCountsReturnsCorrectMap() {
        Pebbles p = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        Map<Pebble, Integer> counts = p.getCounts();
        assertEquals(2, counts.get(Pebble.RED));
        assertEquals(1, counts.get(Pebble.BLUE));
    }
 
    @Test
    void getCountsIsUnmodifiable() {
        Pebbles p = new Pebbles(List.of(Pebble.RED));
        assertThrows(UnsupportedOperationException.class,
            () -> p.getCounts().put(Pebble.BLUE, 1));
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void equalCollectionsAreEqual() {
        Pebbles a = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE, Pebble.RED));
        assertEquals(a, b);
    }
 
    @Test
    void unequalCollectionsAreNotEqual() {
        Pebbles a = new Pebbles(List.of(Pebble.RED));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE));
        assertNotEquals(a, b);
    }
 
    @Test
    void equalCollectionsHaveEqualHashCodes() {
        Pebbles a = new Pebbles(List.of(Pebble.RED, Pebble.BLUE));
        Pebbles b = new Pebbles(List.of(Pebble.BLUE, Pebble.RED));
        assertEquals(a.hashCode(), b.hashCode());
    }
 
    @Test
    void collectionIsEqualToItself() {
        Pebbles a = new Pebbles(List.of(Pebble.RED));
        assertEquals(a, a);
    }
 
    @Test
    void collectionIsNotEqualToNull() {
        Pebbles a = new Pebbles(List.of(Pebble.RED));
        assertNotEquals(null, a);
    }
 
    // -------------------------------------------------------------------------
    // toString()
    // -------------------------------------------------------------------------
 
    @Test
    void toStringOfEmptyCollectionIsEmptyString() {
        assertEquals("", new Pebbles().toString());
    }
 
    @Test
    void toStringListsColorsInCanonicalOrder() {
        Pebbles p = new Pebbles(List.of(Pebble.BLUE, Pebble.RED));
        assertEquals("R B", p.toString());
    }
 
    @Test
    void toStringRepeatsColorsForMultipleCounts() {
        Pebbles p = new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.BLUE));
        assertEquals("R R B", p.toString());
    }
}