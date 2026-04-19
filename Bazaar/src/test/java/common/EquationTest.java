package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Equation class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class EquationTest {
 
    // -------------------------------------------------------------------------
    // Helpers — shared equation instances
    // -------------------------------------------------------------------------
 
    // left = 1 RED, right = 1 BLUE
    private static Equation redBlue() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE))
        );
    }
 
    // left = 2 RED 1 WHITE, right = 1 GREEN 1 YELLOW
    private static Equation multiPebble() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.WHITE)),
            new Pebbles(List.of(Pebble.GREEN, Pebble.YELLOW))
        );
    }
 
    // -------------------------------------------------------------------------
    // Constructor — valid inputs
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsValidEquation() {
        assertDoesNotThrow(() -> redBlue());
    }
 
    @Test
    void constructorAcceptsMaximumSizeSides() {
        assertDoesNotThrow(() -> new Equation(
            new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE, Pebble.BLUE))
        ));
    }
 
    // -------------------------------------------------------------------------
    // Constructor — invalid inputs
    // -------------------------------------------------------------------------
 
    @Test
    void constructorRejectsEmptyLeftSide() {
        assertThrows(IllegalArgumentException.class, () ->
            new Equation(new Pebbles(), new Pebbles(List.of(Pebble.BLUE)))
        );
    }
 
    @Test
    void constructorRejectsEmptyRightSide() {
        assertThrows(IllegalArgumentException.class, () ->
            new Equation(new Pebbles(List.of(Pebble.RED)), new Pebbles())
        );
    }
 
    @Test
    void constructorRejectsLeftSideExceedingFourPebbles() {
        assertThrows(IllegalArgumentException.class, () ->
            new Equation(
                new Pebbles(List.of(Pebble.RED, Pebble.RED, Pebble.RED,
                                    Pebble.RED, Pebble.RED)),
                new Pebbles(List.of(Pebble.BLUE))
            )
        );
    }
 
    @Test
    void constructorRejectsRightSideExceedingFourPebbles() {
        assertThrows(IllegalArgumentException.class, () ->
            new Equation(
                new Pebbles(List.of(Pebble.RED)),
                new Pebbles(List.of(Pebble.BLUE, Pebble.BLUE, Pebble.BLUE,
                                    Pebble.BLUE, Pebble.BLUE))
            )
        );
    }
 
    @Test
    void constructorRejectsOverlappingColors() {
        assertThrows(IllegalArgumentException.class, () ->
            new Equation(
                new Pebbles(List.of(Pebble.RED, Pebble.BLUE)),
                new Pebbles(List.of(Pebble.BLUE, Pebble.GREEN))
            )
        );
    }
 
    // -------------------------------------------------------------------------
    // getLeft() and getRight()
    // -------------------------------------------------------------------------
 
    @Test
    void getLeftReturnsLeftSide() {
        Pebbles left = new Pebbles(List.of(Pebble.RED));
        Equation eq  = new Equation(left, new Pebbles(List.of(Pebble.BLUE)));
        assertEquals(left, eq.getLeft());
    }
 
    @Test
    void getRightReturnsRightSide() {
        Pebbles right = new Pebbles(List.of(Pebble.BLUE));
        Equation eq   = new Equation(new Pebbles(List.of(Pebble.RED)), right);
        assertEquals(right, eq.getRight());
    }
 
    // -------------------------------------------------------------------------
    // canApplyLeftToRight()
    // -------------------------------------------------------------------------
 
    @Test
    void canApplyLeftToRightReturnsTrueWhenAffordable() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.RED, Pebble.GREEN));
        Pebbles  bank   = new Pebbles(List.of(Pebble.BLUE, Pebble.WHITE));
        assertTrue(eq.canApplyLeftToRight(wallet, bank));
    }
 
    @Test
    void canApplyLeftToRightReturnsFalseWhenPlayerLacksLeftSide() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.GREEN));
        Pebbles  bank   = new Pebbles(List.of(Pebble.BLUE));
        assertFalse(eq.canApplyLeftToRight(wallet, bank));
    }
 
    @Test
    void canApplyLeftToRightReturnsFalseWhenBankLacksRightSide() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles  bank   = new Pebbles(List.of(Pebble.GREEN));
        assertFalse(eq.canApplyLeftToRight(wallet, bank));
    }
 
    // -------------------------------------------------------------------------
    // canApplyRightToLeft()
    // -------------------------------------------------------------------------
 
    @Test
    void canApplyRightToLeftReturnsTrueWhenAffordable() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.BLUE));
        Pebbles  bank   = new Pebbles(List.of(Pebble.RED));
        assertTrue(eq.canApplyRightToLeft(wallet, bank));
    }
 
    @Test
    void canApplyRightToLeftReturnsFalseWhenPlayerLacksRightSide() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.GREEN));
        Pebbles  bank   = new Pebbles(List.of(Pebble.RED));
        assertFalse(eq.canApplyRightToLeft(wallet, bank));
    }
 
    @Test
    void canApplyRightToLeftReturnsFalseWhenBankLacksLeftSide() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.BLUE));
        Pebbles  bank   = new Pebbles(List.of(Pebble.GREEN));
        assertFalse(eq.canApplyRightToLeft(wallet, bank));
    }
 
    // -------------------------------------------------------------------------
    // canApply()
    // -------------------------------------------------------------------------
 
    @Test
    void canApplyReturnsTrueWhenLeftToRightIsPossible() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.RED));
        Pebbles  bank   = new Pebbles(List.of(Pebble.BLUE));
        assertTrue(eq.canApply(wallet, bank));
    }
 
    @Test
    void canApplyReturnsTrueWhenRightToLeftIsPossible() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.BLUE));
        Pebbles  bank   = new Pebbles(List.of(Pebble.RED));
        assertTrue(eq.canApply(wallet, bank));
    }
 
    @Test
    void canApplyReturnsFalseWhenNeitherDirectionIsPossible() {
        Equation eq     = redBlue();
        Pebbles  wallet = new Pebbles(List.of(Pebble.GREEN));
        Pebbles  bank   = new Pebbles(List.of(Pebble.YELLOW));
        assertFalse(eq.canApply(wallet, bank));
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderProducesCorrectFormat() {
        Equation eq = redBlue();
        assertEquals("R = B", eq.render());
    }
 
    @Test
    void renderShowsBothSidesSeparatedByEquals() {
        assertTrue(redBlue().render().contains(" = "));
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void sameOrientationEquationsAreEqual() {
        Equation eq1 = redBlue();
        Equation eq2 = redBlue();
        assertEquals(eq1, eq2);
    }
 
    @Test
    void reversedOrientationEquationsAreEqual() {
        Equation eq1 = new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE))
        );
        Equation eq2 = new Equation(
            new Pebbles(List.of(Pebble.BLUE)),
            new Pebbles(List.of(Pebble.RED))
        );
        assertEquals(eq1, eq2);
    }
 
    @Test
    void differentEquationsAreNotEqual() {
        Equation eq1 = redBlue();
        Equation eq2 = new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.GREEN))
        );
        assertNotEquals(eq1, eq2);
    }
 
    @Test
    void equalEquationsHaveEqualHashCodes() {
        Equation eq1 = new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE))
        );
        Equation eq2 = new Equation(
            new Pebbles(List.of(Pebble.BLUE)),
            new Pebbles(List.of(Pebble.RED))
        );
        assertEquals(eq1.hashCode(), eq2.hashCode());
    }
 
    @Test
    void equationIsEqualToItself() {
        Equation eq = redBlue();
        assertEquals(eq, eq);
    }
 
    @Test
    void equationIsNotEqualToNull() {
        assertNotEquals(null, redBlue());
    }
}