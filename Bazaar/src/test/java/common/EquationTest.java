package common;

import common.Cards.PebbleColor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Equation.
 *
 * Test organization mirrors the public API:
 *   1. Construction — valid and invalid inputs
 *   2. canApplyLeftToRight
 *   3. canApplyRightToLeft
 *   4. canApply (either direction)
 *   5. render / toString
 *   6. equals / hashCode (symmetry)
 *   7. File-driven test cases from Tests/in and Tests/out
 */
class EquationTest {

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

    /** Parses a pebble map from a JsonObject. */
    private static Map<PebbleColor, Integer> parsePebbles(JsonObject obj) {
        Map<PebbleColor, Integer> map = new EnumMap<>(PebbleColor.class);
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(PebbleColor.valueOf(entry.getKey()), entry.getValue().getAsInt());
        }
        return map;
    }

    /** Parses an Equation from a JsonObject with "left" and "right" arrays. */
    private static Equation parseEquation(JsonObject obj) {
        List<PebbleColor> left  = new ArrayList<>();
        List<PebbleColor> right = new ArrayList<>();
        for (JsonElement e : obj.getAsJsonArray("left"))  left.add(PebbleColor.valueOf(e.getAsString()));
        for (JsonElement e : obj.getAsJsonArray("right")) right.add(PebbleColor.valueOf(e.getAsString()));
        return new Equation(left, right);
    }

    // -------------------------------------------------------------------------
    // 1. Construction
    // -------------------------------------------------------------------------

    @Test
    void construction_validSidesSucceeds() {
        assertDoesNotThrow(EquationTest::rwEqBg);
    }

    @Test
    void construction_singlePebbleEachSideSucceeds() {
        assertDoesNotThrow(EquationTest::yEqR);
    }

    @Test
    void construction_fourPebblesPerSideSucceeds() {
        assertDoesNotThrow(() -> new Equation(
                List.of(PebbleColor.RED, PebbleColor.RED,
                        PebbleColor.RED, PebbleColor.RED),
                List.of(PebbleColor.BLUE, PebbleColor.BLUE,
                        PebbleColor.BLUE, PebbleColor.BLUE)));
    }

    @Test
    void construction_repeatsWithinSideAllowed() {
        assertDoesNotThrow(() -> new Equation(
                List.of(PebbleColor.RED, PebbleColor.RED),
                List.of(PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsEmptyLeftSide() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(List.of(), List.of(PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsEmptyRightSide() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(List.of(PebbleColor.RED), List.of()));
    }

    @Test
    void construction_rejectsLeftSideWithFivePebbles() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(
                        List.of(PebbleColor.RED, PebbleColor.RED, PebbleColor.RED,
                                PebbleColor.RED, PebbleColor.RED),
                        List.of(PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsRightSideWithFivePebbles() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(
                        List.of(PebbleColor.RED),
                        List.of(PebbleColor.BLUE, PebbleColor.BLUE, PebbleColor.BLUE,
                                PebbleColor.BLUE, PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsSharedColorBetweenSides() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(
                        List.of(PebbleColor.RED, PebbleColor.WHITE),
                        List.of(PebbleColor.RED, PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsNullLeftSide() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(null, List.of(PebbleColor.BLUE)));
    }

    @Test
    void construction_rejectsNullRightSide() {
        assertThrows(IllegalArgumentException.class, () ->
                new Equation(List.of(PebbleColor.RED), null));
    }

    @Test
    void construction_getLeftIsImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                rwEqBg().getLeft().add(PebbleColor.YELLOW));
    }

    @Test
    void construction_getRightIsImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                rwEqBg().getRight().add(PebbleColor.YELLOW));
    }

    // -------------------------------------------------------------------------
    // 2. canApplyLeftToRight
    // -------------------------------------------------------------------------

    @Test
    void canApplyLeftToRight_trueWhenPlayerHasLeftAndBankHasRight() {
        assertTrue(rwEqBg().canApplyLeftToRight(
                pebbles(PebbleColor.RED, 1, PebbleColor.WHITE, 1),
                pebbles(PebbleColor.BLUE, 1, PebbleColor.GREEN, 1)));
    }

    @Test
    void canApplyLeftToRight_trueWithSurplusPebbles() {
        assertTrue(rwEqBg().canApplyLeftToRight(
                pebbles(PebbleColor.RED, 5, PebbleColor.WHITE, 5),
                pebbles(PebbleColor.BLUE, 5, PebbleColor.GREEN, 5)));
    }

    @Test
    void canApplyLeftToRight_falseWhenPlayerMissingLeftColor() {
        assertFalse(rwEqBg().canApplyLeftToRight(
                pebbles(PebbleColor.RED, 1),
                pebbles(PebbleColor.BLUE, 5, PebbleColor.GREEN, 5)));
    }

    @Test
    void canApplyLeftToRight_falseWhenBankMissingRightColor() {
        assertFalse(rwEqBg().canApplyLeftToRight(
                pebbles(PebbleColor.RED, 5, PebbleColor.WHITE, 5),
                pebbles(PebbleColor.BLUE, 1)));
    }

    @Test
    void canApplyLeftToRight_falseWhenBothEmpty() {
        assertFalse(rwEqBg().canApplyLeftToRight(Map.of(), Map.of()));
    }

    @Test
    void canApplyLeftToRight_falseWhenInsufficientMultiplicity() {
        // Needs 2 RED on left, player only has 1
        Equation eq = new Equation(
                List.of(PebbleColor.RED, PebbleColor.RED),
                List.of(PebbleColor.BLUE));
        assertFalse(eq.canApplyLeftToRight(
                pebbles(PebbleColor.RED, 1),
                pebbles(PebbleColor.BLUE, 5)));
    }

    @Test
    void canApplyLeftToRight_trueWhenExactMultiplicity() {
        // Needs 2 RED on left, player has exactly 2
        Equation eq = new Equation(
                List.of(PebbleColor.RED, PebbleColor.RED),
                List.of(PebbleColor.BLUE));
        assertTrue(eq.canApplyLeftToRight(
                pebbles(PebbleColor.RED, 2),
                pebbles(PebbleColor.BLUE, 5)));
    }

    // -------------------------------------------------------------------------
    // 3. canApplyRightToLeft
    // -------------------------------------------------------------------------

    @Test
    void canApplyRightToLeft_trueWhenPlayerHasRightAndBankHasLeft() {
        assertTrue(rwEqBg().canApplyRightToLeft(
                pebbles(PebbleColor.BLUE, 1, PebbleColor.GREEN, 1),
                pebbles(PebbleColor.RED, 1, PebbleColor.WHITE, 1)));
    }

    @Test
    void canApplyRightToLeft_falseWhenPlayerMissingRightColor() {
        assertFalse(rwEqBg().canApplyRightToLeft(
                pebbles(PebbleColor.BLUE, 1),
                pebbles(PebbleColor.RED, 5, PebbleColor.WHITE, 5)));
    }

    @Test
    void canApplyRightToLeft_falseWhenBankMissingLeftColor() {
        assertFalse(rwEqBg().canApplyRightToLeft(
                pebbles(PebbleColor.BLUE, 5, PebbleColor.GREEN, 5),
                pebbles(PebbleColor.RED, 1)));
    }

    @Test
    void canApplyRightToLeft_falseWhenBothEmpty() {
        assertFalse(rwEqBg().canApplyRightToLeft(Map.of(), Map.of()));
    }

    @Test
    void canApplyRightToLeft_falseWhenInsufficientMultiplicity() {
        // Needs 2 BLUE on right, player only has 1
        Equation eq = new Equation(
                List.of(PebbleColor.RED),
                List.of(PebbleColor.BLUE, PebbleColor.BLUE));
        assertFalse(eq.canApplyRightToLeft(
                pebbles(PebbleColor.BLUE, 1),
                pebbles(PebbleColor.RED, 5)));
    }

    // -------------------------------------------------------------------------
    // 4. canApply (either direction)
    // -------------------------------------------------------------------------

    @Test
    void canApply_trueWhenOnlyLeftToRightWorks() {
        assertTrue(rwEqBg().canApply(
                pebbles(PebbleColor.RED, 1, PebbleColor.WHITE, 1),
                pebbles(PebbleColor.BLUE, 1, PebbleColor.GREEN, 1)));
    }

    @Test
    void canApply_trueWhenOnlyRightToLeftWorks() {
        assertTrue(rwEqBg().canApply(
                pebbles(PebbleColor.BLUE, 1, PebbleColor.GREEN, 1),
                pebbles(PebbleColor.RED, 1, PebbleColor.WHITE, 1)));
    }

    @Test
    void canApply_trueWhenBothDirectionsWork() {
        Map<PebbleColor, Integer> rich = pebbles(
                PebbleColor.RED, 5, PebbleColor.WHITE, 5,
                PebbleColor.BLUE, 5, PebbleColor.GREEN, 5);
        assertTrue(rwEqBg().canApply(rich, rich));
    }

    @Test
    void canApply_falseWhenNeitherDirectionWorks() {
        Map<PebbleColor, Integer> yellow = pebbles(PebbleColor.YELLOW, 10);
        assertFalse(rwEqBg().canApply(yellow, yellow));
    }

    @Test
    void canApply_falseWhenBothEmpty() {
        assertFalse(rwEqBg().canApply(Map.of(), Map.of()));
    }

    // -------------------------------------------------------------------------
    // 5. render / toString
    // -------------------------------------------------------------------------

    @Test
    void render_containsEqualSign() {
        assertTrue(rwEqBg().render().contains("="));
    }

    @Test
    void render_leftSideBeforeEqualSign() {
        String rendered = rwEqBg().render();
        int eqIdx = rendered.indexOf("=");
        String left = rendered.substring(0, eqIdx);
        assertTrue(left.contains("R"));
        assertTrue(left.contains("W"));
    }

    @Test
    void render_rightSideAfterEqualSign() {
        String rendered = rwEqBg().render();
        int eqIdx = rendered.indexOf("=");
        String right = rendered.substring(eqIdx);
        assertTrue(right.contains("B"));
        assertTrue(right.contains("G"));
    }

    @Test
    void render_singlePebbleEachSide() {
        String rendered = yEqR().render();
        assertTrue(rendered.contains("Y"));
        assertTrue(rendered.contains("R"));
        assertTrue(rendered.contains("="));
    }

    @Test
    void render_repeatedColorOnLeftSide() {
        Equation eq = new Equation(
                List.of(PebbleColor.RED, PebbleColor.RED),
                List.of(PebbleColor.BLUE));
        assertEquals("R R = B", eq.render());
    }

    @Test
    void render_toStringDelegatesToRender() {
        Equation eq = rwEqBg();
        assertEquals(eq.render(), eq.toString());
    }

    // -------------------------------------------------------------------------
    // 6. equals / hashCode (symmetry)
    // -------------------------------------------------------------------------

    @Test
    void equals_identicalEquationsAreEqual() {
        assertEquals(rwEqBg(), rwEqBg());
    }

    @Test
    void equals_hashCodesMatchForEqualEquations() {
        assertEquals(rwEqBg().hashCode(), rwEqBg().hashCode());
    }

    @Test
    void equals_swappedSidesAreEqual() {
        Equation forward  = new Equation(
                List.of(PebbleColor.RED, PebbleColor.WHITE),
                List.of(PebbleColor.BLUE, PebbleColor.GREEN));
        Equation reversed = new Equation(
                List.of(PebbleColor.BLUE, PebbleColor.GREEN),
                List.of(PebbleColor.RED, PebbleColor.WHITE));
        assertEquals(forward, reversed);
        assertEquals(forward.hashCode(), reversed.hashCode());
    }

    @Test
    void equals_differentEquationsNotEqual() {
        assertNotEquals(rwEqBg(), yEqR());
    }

    @Test
    void equals_reflexive() {
        Equation eq = rwEqBg();
        assertEquals(eq, eq);
    }

    @Test
    void equals_notEqualToNull() {
        assertNotEquals(null, rwEqBg());
    }

    // -------------------------------------------------------------------------
    // 7. File-driven test cases
    // -------------------------------------------------------------------------

    @Test
    void fileDrivenTestCases() throws Exception {
        int i = 1;
        while (true) {
            File inFile  = new File("src/Tests/in/"  + i + "-in.json");
            File outFile = new File("src/Tests/out/" + i + "-out.json");
            if (!inFile.exists()) break;

            JsonObject input = JsonParser.parseReader(new FileReader(inFile)).getAsJsonObject();
            String testType  = input.get("test").getAsString();
            Equation eq      = parseEquation(input.getAsJsonObject("equation"));

            switch (testType) {
                case "canApplyLeftToRight": {
                    Map<PebbleColor, Integer> player = parsePebbles(input.getAsJsonObject("playerPebbles"));
                    Map<PebbleColor, Integer> bank   = parsePebbles(input.getAsJsonObject("bankPebbles"));
                    boolean expected = JsonParser.parseReader(new FileReader(outFile)).getAsBoolean();
                    assertEquals(expected, eq.canApplyLeftToRight(player, bank),
                            "canApplyLeftToRight failed for test case " + i);
                    break;
                }
                case "canApplyRightToLeft": {
                    Map<PebbleColor, Integer> player = parsePebbles(input.getAsJsonObject("playerPebbles"));
                    Map<PebbleColor, Integer> bank   = parsePebbles(input.getAsJsonObject("bankPebbles"));
                    boolean expected = JsonParser.parseReader(new FileReader(outFile)).getAsBoolean();
                    assertEquals(expected, eq.canApplyRightToLeft(player, bank),
                            "canApplyRightToLeft failed for test case " + i);
                    break;
                }
                case "canApply": {
                    Map<PebbleColor, Integer> player = parsePebbles(input.getAsJsonObject("playerPebbles"));
                    Map<PebbleColor, Integer> bank   = parsePebbles(input.getAsJsonObject("bankPebbles"));
                    boolean expected = JsonParser.parseReader(new FileReader(outFile)).getAsBoolean();
                    assertEquals(expected, eq.canApply(player, bank),
                            "canApply failed for test case " + i);
                    break;
                }
                case "render": {
                    String expected = JsonParser.parseReader(new FileReader(outFile)).getAsString();
                    assertEquals(expected, eq.render(),
                            "render failed for test case " + i);
                    break;
                }
                default:
                    fail("Unknown test type '" + testType + "' in test case " + i);
            }
            i++;
        }
    }
}
