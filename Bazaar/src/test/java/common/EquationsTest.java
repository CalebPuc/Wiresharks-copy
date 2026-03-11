package common;

import common.Cards.PebbleColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Equations (the equation table).
 *
 * Test organization mirrors the public API:
 *   1. Construction — Equations(list) and createTable
 *   2. createRandomTable
 *   3. filterApplicable
 *   4. render / toString
 *   5. getEquations / size
 */
class EquationsTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Map<PebbleColor, Integer> pebbles(Object... pairs) {
        Map<PebbleColor, Integer> map = new EnumMap<>(PebbleColor.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((PebbleColor) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }

    /** R W = B G */
    private static Equation rwEqBg() {
        return new Equation(
                List.of(PebbleColor.RED, PebbleColor.WHITE),
                List.of(PebbleColor.BLUE, PebbleColor.GREEN));
    }

    /** Y = R */
    private static Equation yEqR() {
        return new Equation(
                List.of(PebbleColor.YELLOW),
                List.of(PebbleColor.RED));
    }

    /** G = W */
    private static Equation gEqW() {
        return new Equation(
                List.of(PebbleColor.GREEN),
                List.of(PebbleColor.WHITE));
    }

    /** Builds the maximum allowed list of 10 distinct valid equations. */
    private static List<Equation> tenEquations() {
        return List.of(
                new Equation(List.of(PebbleColor.RED),    List.of(PebbleColor.BLUE)),
                new Equation(List.of(PebbleColor.RED),    List.of(PebbleColor.GREEN)),
                new Equation(List.of(PebbleColor.RED),    List.of(PebbleColor.YELLOW)),
                new Equation(List.of(PebbleColor.RED),    List.of(PebbleColor.WHITE)),
                new Equation(List.of(PebbleColor.BLUE),   List.of(PebbleColor.GREEN)),
                new Equation(List.of(PebbleColor.BLUE),   List.of(PebbleColor.YELLOW)),
                new Equation(List.of(PebbleColor.BLUE),   List.of(PebbleColor.WHITE)),
                new Equation(List.of(PebbleColor.GREEN),  List.of(PebbleColor.YELLOW)),
                new Equation(List.of(PebbleColor.GREEN),  List.of(PebbleColor.WHITE)),
                new Equation(List.of(PebbleColor.YELLOW), List.of(PebbleColor.WHITE)));
    }

    // -------------------------------------------------------------------------
    // 1. Construction
    // -------------------------------------------------------------------------

    @Test
    void construction_succeedsWithEmptyList() {
        assertDoesNotThrow(() -> new Equations(List.of()));
        assertEquals(0, new Equations(List.of()).size());
    }

    @Test
    void construction_succeedsWithOneEquation() {
        Equations table = new Equations(List.of(rwEqBg()));
        assertEquals(1, table.size());
    }

    @Test
    void construction_succeedsWithTenEquations() {
        Equations table = new Equations(tenEquations());
        assertEquals(10, table.size());
    }

    @Test
    void construction_rejectsElevenEquations() {
        List<Equation> eleven = new ArrayList<>(tenEquations());
        eleven.add(rwEqBg());
        assertThrows(IllegalArgumentException.class, () -> new Equations(eleven));
    }

    @Test
    void construction_rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Equations(null));
    }

    @Test
    void construction_createTableMatchesConstructor() {
        List<Equation> eqs = List.of(rwEqBg(), yEqR());
        assertEquals(
                new Equations(eqs).getEquations(),
                Equations.createTable(eqs).getEquations());
    }

    @Test
    void construction_getEquationsIsImmutable() {
        Equations table = Equations.createTable(List.of(rwEqBg()));
        assertThrows(UnsupportedOperationException.class, () ->
                table.getEquations().add(yEqR()));
    }

    @Test
    void construction_preservesInsertionOrder() {
        List<Equation> eqs = List.of(rwEqBg(), yEqR(), gEqW());
        Equations table = Equations.createTable(eqs);
        assertEquals(rwEqBg(), table.getEquations().get(0));
        assertEquals(yEqR(),   table.getEquations().get(1));
        assertEquals(gEqW(),   table.getEquations().get(2));
    }

    // -------------------------------------------------------------------------
    // 2. createRandomTable
    // -------------------------------------------------------------------------

    @Test
    void createRandomTable_producesExactlyTenEquations() {
        assertEquals(10, Equations.createRandomTable().size());
    }

    @Test
    void createRandomTable_allEquationsHaveValidSides() {
        Equations table = Equations.createRandomTable();
        for (Equation eq : table.getEquations()) {
            assertFalse(eq.getLeft().isEmpty(),  "Left side must not be empty");
            assertFalse(eq.getRight().isEmpty(), "Right side must not be empty");
            assertTrue(eq.getLeft().size()  <= 4, "Left side must have at most 4 pebbles");
            assertTrue(eq.getRight().size() <= 4, "Right side must have at most 4 pebbles");
            for (PebbleColor c : eq.getLeft()) assertNotNull(c);
            for (PebbleColor c : eq.getRight()) assertNotNull(c);
        }
    }

    @Test
    void createRandomTable_producesDifferentResultsAcrossInvocations() {
        // Three independent calls — all being identical is astronomically unlikely
        Equations t1 = Equations.createRandomTable();
        Equations t2 = Equations.createRandomTable();
        Equations t3 = Equations.createRandomTable();
        boolean allSame = t1.getEquations().equals(t2.getEquations())
                && t2.getEquations().equals(t3.getEquations());
        assertFalse(allSame, "createRandomTable should produce varied results");
    }

    // -------------------------------------------------------------------------
    // 3. filterApplicable
    // -------------------------------------------------------------------------

    @Test
    void filterApplicable_returnsAllWhenPlayerCanUseAll() {
        Equation eq1 = new Equation(List.of(PebbleColor.RED),   List.of(PebbleColor.BLUE));
        Equation eq2 = new Equation(List.of(PebbleColor.GREEN), List.of(PebbleColor.WHITE));
        Equations table = Equations.createTable(List.of(eq1, eq2));

        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 2, PebbleColor.GREEN, 2);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.BLUE, 2, PebbleColor.WHITE, 2);

        Equations filtered = table.filterApplicable(player, bank);
        assertEquals(2, filtered.size());
    }

    @Test
    void filterApplicable_returnsNoneWhenPlayerCanUseNone() {
        Equations table = Equations.createTable(List.of(rwEqBg(), yEqR()));

        // Player only has YELLOW; bank is empty so no direction works
        Map<PebbleColor, Integer> player = pebbles(PebbleColor.YELLOW, 1);
        Map<PebbleColor, Integer> bank   = Map.of();

        assertEquals(0, table.filterApplicable(player, bank).size());
    }

    @Test
    void filterApplicable_returnsSubsetWhenSomeApplicable() {
        Equation canUse    = new Equation(List.of(PebbleColor.RED),   List.of(PebbleColor.BLUE));
        Equation cannotUse = new Equation(List.of(PebbleColor.GREEN), List.of(PebbleColor.WHITE));
        Equations table = Equations.createTable(List.of(canUse, cannotUse));

        // Player has RED but not GREEN
        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 1);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.BLUE, 5, PebbleColor.WHITE, 5);

        Equations filtered = table.filterApplicable(player, bank);
        assertEquals(1, filtered.size());
        assertEquals(canUse, filtered.getEquations().get(0));
    }

    @Test
    void filterApplicable_respectsBankConstraint() {
        // eq: R = B — player has RED, but bank has no BLUE
        Equation eq = new Equation(List.of(PebbleColor.RED), List.of(PebbleColor.BLUE));
        Equations table = Equations.createTable(List.of(eq));

        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 5);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.GREEN, 5); // no BLUE

        assertEquals(0, table.filterApplicable(player, bank).size());
    }

    @Test
    void filterApplicable_includesReverseDirectionEquations() {
        // eq: R = B — player has BLUE (reverse), bank has RED
        Equation eq = new Equation(List.of(PebbleColor.RED), List.of(PebbleColor.BLUE));
        Equations table = Equations.createTable(List.of(eq));

        Map<PebbleColor, Integer> player = pebbles(PebbleColor.BLUE, 1);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.RED, 1);

        assertEquals(1, table.filterApplicable(player, bank).size());
    }

    @Test
    void filterApplicable_onEmptyTableReturnsEmptyTable() {
        Equations table = Equations.createTable(List.of());
        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 10);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.BLUE, 10);
        assertEquals(0, table.filterApplicable(player, bank).size());
    }

    @Test
    void filterApplicable_returnsNewTableNotSameReference() {
        Equations table = Equations.createTable(List.of(rwEqBg()));
        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 1, PebbleColor.WHITE, 1);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.BLUE, 1, PebbleColor.GREEN, 1);
        Equations filtered = table.filterApplicable(player, bank);
        assertNotSame(table, filtered);
    }

    @Test
    void filterApplicable_doesNotMutateOriginalTable() {
        Equation eq1 = new Equation(List.of(PebbleColor.RED),   List.of(PebbleColor.BLUE));
        Equation eq2 = new Equation(List.of(PebbleColor.GREEN), List.of(PebbleColor.WHITE));
        Equations table = Equations.createTable(List.of(eq1, eq2));

        // Filter to only eq1
        Map<PebbleColor, Integer> player = pebbles(PebbleColor.RED, 1);
        Map<PebbleColor, Integer> bank   = pebbles(PebbleColor.BLUE, 5, PebbleColor.WHITE, 5);
        table.filterApplicable(player, bank);

        // Original table should still have both equations
        assertEquals(2, table.size());
    }

    // -------------------------------------------------------------------------
    // 4. render / toString
    // -------------------------------------------------------------------------

    @Test
    void render_containsHeaderLine() {
        assertTrue(Equations.createTable(List.of(rwEqBg())).render().contains("Equations"));
    }

    @Test
    void render_containsOneEntryPerEquation() {
        Equations table = Equations.createTable(List.of(rwEqBg(), yEqR()));
        String rendered = table.render();
        // Each equation's render() output should appear in the table's render()
        assertTrue(rendered.contains(rwEqBg().render()));
        assertTrue(rendered.contains(yEqR().render()));
    }

    @Test
    void render_entriesAreNumbered() {
        Equations table = Equations.createTable(List.of(rwEqBg(), yEqR()));
        String rendered = table.render();
        assertTrue(rendered.contains("1."), "First entry should be numbered 1.");
        assertTrue(rendered.contains("2."), "Second entry should be numbered 2.");
    }

    @Test
    void render_emptyTableHasNoEqualSigns() {
        String rendered = Equations.createTable(List.of()).render();
        assertTrue(rendered.contains("Equations"), "Header should still appear");
        assertFalse(rendered.contains("="), "Empty table should have no equation entries");
    }

    @Test
    void render_tenEquationsNumberedCorrectly() {
        Equations table = Equations.createTable(tenEquations());
        String rendered = table.render();
        assertTrue(rendered.contains("10."), "Tenth entry should be numbered 10.");
    }

    @Test
    void render_toStringDelegatesToRender() {
        Equations table = Equations.createTable(List.of(rwEqBg()));
        assertEquals(table.render(), table.toString());
    }

    // -------------------------------------------------------------------------
    // 5. getEquations / size
    // -------------------------------------------------------------------------

    @Test
    void size_emptyTableIsZero() {
        assertEquals(0, Equations.createTable(List.of()).size());
    }

    @Test
    void size_reflectsNumberOfEquationsAdded() {
        assertEquals(3, Equations.createTable(List.of(rwEqBg(), yEqR(), gEqW())).size());
    }

    @Test
    void size_matchesGetEquationsSize() {
        Equations table = Equations.createTable(List.of(rwEqBg(), yEqR()));
        assertEquals(table.size(), table.getEquations().size());
    }

    @Test
    void getEquations_containsExactlyTheEquationsAdded() {
        List<Equation> eqs = List.of(rwEqBg(), yEqR());
        Equations table = Equations.createTable(eqs);
        assertTrue(table.getEquations().contains(rwEqBg()));
        assertTrue(table.getEquations().contains(yEqR()));
    }
}
