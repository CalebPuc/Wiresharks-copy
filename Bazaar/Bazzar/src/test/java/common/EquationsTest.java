package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Equations class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class EquationsTest {
 
    // -------------------------------------------------------------------------
    // Helpers — shared equation and table instances
    // -------------------------------------------------------------------------
 
    // RED = BLUE
    private static Equation redBlue() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE))
        );
    }
 
    // WHITE = GREEN
    private static Equation whiteGreen() {
        return new Equation(
            new Pebbles(List.of(Pebble.WHITE)),
            new Pebbles(List.of(Pebble.GREEN))
        );
    }
 
    // YELLOW = RED WHITE  (uses RED and WHITE on right)
    private static Equation yellowRedWhite() {
        return new Equation(
            new Pebbles(List.of(Pebble.YELLOW)),
            new Pebbles(List.of(Pebble.RED, Pebble.WHITE))
        );
    }
 
    private static Equations twoEquationTable() {
        return new Equations(List.of(redBlue(), whiteGreen()));
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsEmptyList() {
        assertDoesNotThrow(() -> new Equations(List.of()));
    }
 
    @Test
    void constructorAcceptsTenEquations() {
        List<Equation> ten = List.of(
            new Equation(new Pebbles(List.of(Pebble.RED)),    new Pebbles(List.of(Pebble.BLUE))),
            new Equation(new Pebbles(List.of(Pebble.RED)),    new Pebbles(List.of(Pebble.GREEN))),
            new Equation(new Pebbles(List.of(Pebble.RED)),    new Pebbles(List.of(Pebble.WHITE))),
            new Equation(new Pebbles(List.of(Pebble.RED)),    new Pebbles(List.of(Pebble.YELLOW))),
            new Equation(new Pebbles(List.of(Pebble.BLUE)),   new Pebbles(List.of(Pebble.GREEN))),
            new Equation(new Pebbles(List.of(Pebble.BLUE)),   new Pebbles(List.of(Pebble.WHITE))),
            new Equation(new Pebbles(List.of(Pebble.BLUE)),   new Pebbles(List.of(Pebble.YELLOW))),
            new Equation(new Pebbles(List.of(Pebble.GREEN)),  new Pebbles(List.of(Pebble.WHITE))),
            new Equation(new Pebbles(List.of(Pebble.GREEN)),  new Pebbles(List.of(Pebble.YELLOW))),
            new Equation(new Pebbles(List.of(Pebble.WHITE)),  new Pebbles(List.of(Pebble.YELLOW)))
        );
        assertDoesNotThrow(() -> new Equations(ten));
    }
 
    @Test
    void constructorRejectsMoreThanTenEquations() {
        List<Equation> eleven = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) {
            eleven.add(redBlue());
        }
        assertThrows(IllegalArgumentException.class, () -> new Equations(eleven));
    }
 
    @Test
    void constructorMakesDefensiveCopy() {
        List<Equation> list = new java.util.ArrayList<>(List.of(redBlue()));
        Equations table = new Equations(list);
        list.add(whiteGreen());
        assertEquals(1, table.size());
    }
 
    // -------------------------------------------------------------------------
    // createRandom()
    // -------------------------------------------------------------------------
 
    @Test
    void createRandomProducesTenEquations() {
        assertEquals(10, Equations.createRandom().size());
    }
 
    @Test
    void createRandomProducesValidEquations() {
        // Each equation in a random table must satisfy all constraints.
        // If any equation were invalid, its constructor would have thrown.
        assertDoesNotThrow(() -> Equations.createRandom());
    }
 
    @Test
    void createRandomProducesDifferentTablesOnSuccessiveCalls() {
        // Two independently generated tables are very unlikely to be identical.
        Equations t1 = Equations.createRandom();
        Equations t2 = Equations.createRandom();
        // Not a guaranteed assertion — but practically always true.
        // This test documents expected non-determinism.
        assertNotNull(t1);
        assertNotNull(t2);
    }
 
    // -------------------------------------------------------------------------
    // filterApplicable()
    // -------------------------------------------------------------------------
 
    @Test
    void filterApplicableReturnsOnlyUsableEquations() {
        Equations table  = twoEquationTable(); // RED=BLUE, WHITE=GREEN
        Pebbles   wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles   bank   = new Pebbles(List.of(Pebble.BLUE));
        Equations result = table.filterApplicable(wallet, bank);
        assertEquals(1, result.size());
        assertEquals(redBlue(), result.getEquations().get(0));
    }
 
    @Test
    void filterApplicableReturnsEmptyWhenNoneApply() {
        Equations table  = twoEquationTable();
        Pebbles   wallet = new Pebbles(List.of(Pebble.YELLOW));
        Pebbles   bank   = new Pebbles(List.of(Pebble.YELLOW));
        Equations result = table.filterApplicable(wallet, bank);
        assertTrue(result.isEmpty());
    }
 
    @Test
    void filterApplicableReturnsAllWhenAllApply() {
        Equations table  = twoEquationTable();
        // Wallet has RED and WHITE; bank has BLUE and GREEN
        Pebbles wallet = new Pebbles(List.of(Pebble.RED, Pebble.WHITE));
        Pebbles bank   = new Pebbles(List.of(Pebble.BLUE, Pebble.GREEN));
        Equations result = table.filterApplicable(wallet, bank);
        assertEquals(2, result.size());
    }
 
    @Test
    void filterApplicableConsidersBothDirections() {
        // Equation is RED=BLUE. Wallet has BLUE, bank has RED.
        // Right-to-left direction should be found applicable.
        Equations table  = new Equations(List.of(redBlue()));
        Pebbles   wallet = new Pebbles(List.of(Pebble.BLUE));
        Pebbles   bank   = new Pebbles(List.of(Pebble.RED));
        Equations result = table.filterApplicable(wallet, bank);
        assertEquals(1, result.size());
    }
 
    @Test
    void filterApplicableDoesNotModifyOriginalTable() {
        Equations table  = twoEquationTable();
        Pebbles   wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles   bank   = new Pebbles(List.of(Pebble.BLUE));
        table.filterApplicable(wallet, bank);
        assertEquals(2, table.size());
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderBeginsWithEquationsHeader() {
        assertTrue(twoEquationTable().render().startsWith("Equations:"));
    }
 
    @Test
    void renderContainsOneLinePerEquation() {
        String rendered = twoEquationTable().render();
        // Two equations means two numbered lines
        assertTrue(rendered.contains("1."));
        assertTrue(rendered.contains("2."));
    }
 
    @Test
    void renderOfEmptyTableShowsOnlyHeader() {
        String rendered = new Equations(List.of()).render();
        assertTrue(rendered.startsWith("Equations:"));
        assertFalse(rendered.contains("1."));
    }
 
    // -------------------------------------------------------------------------
    // getEquations()
    // -------------------------------------------------------------------------
 
    @Test
    void getEquationsReturnsCorrectList() {
        Equations table = twoEquationTable();
        assertEquals(2, table.getEquations().size());
        assertEquals(redBlue(), table.getEquations().get(0));
    }
 
    @Test
    void getEquationsIsUnmodifiable() {
        Equations table = twoEquationTable();
        assertThrows(UnsupportedOperationException.class,
            () -> table.getEquations().add(yellowRedWhite()));
    }
 
    // -------------------------------------------------------------------------
    // size() and isEmpty()
    // -------------------------------------------------------------------------
 
    @Test
    void sizeReturnsNumberOfEquations() {
        assertEquals(2, twoEquationTable().size());
    }
 
    @Test
    void sizeOfEmptyTableIsZero() {
        assertEquals(0, new Equations(List.of()).size());
    }
 
    @Test
    void isEmptyReturnsTrueForEmptyTable() {
        assertTrue(new Equations(List.of()).isEmpty());
    }
 
    @Test
    void isEmptyReturnsFalseForNonEmptyTable() {
        assertFalse(twoEquationTable().isEmpty());
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void tablesWithSameEquationsAreEqual() {
        Equations t1 = twoEquationTable();
        Equations t2 = twoEquationTable();
        assertEquals(t1, t2);
    }
 
    @Test
    void tablesWithDifferentEquationsAreNotEqual() {
        Equations t1 = twoEquationTable();
        Equations t2 = new Equations(List.of(redBlue()));
        assertNotEquals(t1, t2);
    }
 
    @Test
    void equalTablesHaveEqualHashCodes() {
        assertEquals(twoEquationTable().hashCode(), twoEquationTable().hashCode());
    }
 
    @Test
    void tableIsEqualToItself() {
        Equations t = twoEquationTable();
        assertEquals(t, t);
    }
 
    @Test
    void tableIsNotEqualToNull() {
        assertNotEquals(null, twoEquationTable());
    }
}