package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the TurnState class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class TurnStateTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Pebbles someBank() {
        return new Pebbles(List.of(Pebble.RED, Pebble.BLUE, Pebble.GREEN));
    }
 
    private static Cards someVisibles() {
        return new Cards(List.of(
            new Card(new Pebbles(List.of(
                Pebble.RED, Pebble.RED, Pebble.BLUE, Pebble.GREEN, Pebble.WHITE)),
                false)
        ));
    }
 
    private static PlayerState someActive() {
        return new PlayerState(
            new Pebbles(List.of(Pebble.RED, Pebble.WHITE)), 3);
    }
 
    private static List<Integer> someScores() {
        return List.of(5, 2);
    }
 
    private static TurnState someTurnState() {
        return new TurnState(someBank(), someVisibles(), someActive(), someScores());
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsValidArguments() {
        assertDoesNotThrow(() -> someTurnState());
    }
 
    @Test
    void constructorAcceptsEmptyScoresList() {
        assertDoesNotThrow(() ->
            new TurnState(someBank(), someVisibles(), someActive(), List.of()));
    }
 
    @Test
    void constructorRejectsNullBank() {
        assertThrows(IllegalArgumentException.class, () ->
            new TurnState(null, someVisibles(), someActive(), someScores()));
    }
 
    @Test
    void constructorRejectsNullVisibles() {
        assertThrows(IllegalArgumentException.class, () ->
            new TurnState(someBank(), null, someActive(), someScores()));
    }
 
    @Test
    void constructorRejectsNullActive() {
        assertThrows(IllegalArgumentException.class, () ->
            new TurnState(someBank(), someVisibles(), null, someScores()));
    }
 
    @Test
    void constructorRejectsNullScores() {
        assertThrows(IllegalArgumentException.class, () ->
            new TurnState(someBank(), someVisibles(), someActive(), null));
    }
 
    // -------------------------------------------------------------------------
    // getBank()
    // -------------------------------------------------------------------------
 
    @Test
    void getBankReturnsCorrectBank() {
        assertEquals(someBank(), someTurnState().getBank());
    }
 
    // -------------------------------------------------------------------------
    // getVisibles()
    // -------------------------------------------------------------------------
 
    @Test
    void getVisiblesReturnsCorrectCards() {
        assertEquals(someVisibles(), someTurnState().getVisibles());
    }
 
    // -------------------------------------------------------------------------
    // getActive()
    // -------------------------------------------------------------------------
 
    @Test
    void getActiveReturnsCorrectPlayerState() {
        assertEquals(someActive(), someTurnState().getActive());
    }
 
    // -------------------------------------------------------------------------
    // getScores()
    // -------------------------------------------------------------------------
 
    @Test
    void getScoresReturnsCorrectScores() {
        assertEquals(someScores(), someTurnState().getScores());
    }
 
    @Test
    void getScoresIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> someTurnState().getScores().add(99));
    }
 
    @Test
    void getScoresDoesNotIncludeActivePlayerScore() {
        // The active player has score 3 (from someActive()).
        // The scores list should only contain other players' scores.
        TurnState ts = someTurnState();
        assertFalse(ts.getScores().contains(ts.getActive().getScore())
            && ts.getScores().size() == someScores().size()
            && !someScores().contains(3));
        // More directly: scores list matches what was passed in
        assertEquals(List.of(5, 2), ts.getScores());
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderContainsBankInfo() {
        assertTrue(someTurnState().render().contains("Bank"));
    }
 
    @Test
    void renderContainsCardsInfo() {
        assertTrue(someTurnState().render().contains("Cards"));
    }
 
    @Test
    void renderContainsActivePlayerInfo() {
        assertTrue(someTurnState().render().contains("Active"));
    }
 
    @Test
    void renderContainsScoresInfo() {
        assertTrue(someTurnState().render().contains("Scores"));
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void turnStatesWithSameContentsAreEqual() {
        assertEquals(someTurnState(), someTurnState());
    }
 
    @Test
    void turnStatesWithDifferentBanksAreNotEqual() {
        TurnState ts1 = someTurnState();
        TurnState ts2 = new TurnState(
            new Pebbles(List.of(Pebble.YELLOW)),
            someVisibles(), someActive(), someScores());
        assertNotEquals(ts1, ts2);
    }
 
    @Test
    void turnStatesWithDifferentScoresAreNotEqual() {
        TurnState ts1 = someTurnState();
        TurnState ts2 = new TurnState(
            someBank(), someVisibles(), someActive(), List.of(1, 2));
        assertNotEquals(ts1, ts2);
    }
 
    @Test
    void equalTurnStatesHaveEqualHashCodes() {
        assertEquals(someTurnState().hashCode(), someTurnState().hashCode());
    }
 
    @Test
    void turnStateIsEqualToItself() {
        TurnState ts = someTurnState();
        assertEquals(ts, ts);
    }
 
    @Test
    void turnStateIsNotEqualToNull() {
        assertNotEquals(null, someTurnState());
    }
}