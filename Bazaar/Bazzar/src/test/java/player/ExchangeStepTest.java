package player;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the ExchangeStep class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class ExchangeStepTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Equation redForBlue() {
        return new Equation(
            new Pebbles(List.of(Pebble.RED)),
            new Pebbles(List.of(Pebble.BLUE)));
    }
 
    private static Equation twoWhiteForGreen() {
        return new Equation(
            new Pebbles(List.of(Pebble.WHITE, Pebble.WHITE)),
            new Pebbles(List.of(Pebble.GREEN)));
    }
 
    private static ExchangeStep leftToRight() {
        return new ExchangeStep(redForBlue(), true);
    }
 
    private static ExchangeStep rightToLeft() {
        return new ExchangeStep(redForBlue(), false);
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsLeftToRight() {
        assertDoesNotThrow(() -> new ExchangeStep(redForBlue(), true));
    }
 
    @Test
    void constructorAcceptsRightToLeft() {
        assertDoesNotThrow(() -> new ExchangeStep(redForBlue(), false));
    }
 
    @Test
    void constructorRejectsNullEquation() {
        assertThrows(IllegalArgumentException.class,
            () -> new ExchangeStep(null, true));
    }
 
    // -------------------------------------------------------------------------
    // getEquation()
    // -------------------------------------------------------------------------
 
    @Test
    void getEquationReturnsTheEquation() {
        Equation eq   = redForBlue();
        ExchangeStep step = new ExchangeStep(eq, true);
        assertEquals(eq, step.getEquation());
    }
 
    // -------------------------------------------------------------------------
    // isLeftToRight()
    // -------------------------------------------------------------------------
 
    @Test
    void isLeftToRightReturnsTrueForLeftToRightStep() {
        assertTrue(leftToRight().isLeftToRight());
    }
 
    @Test
    void isLeftToRightReturnsFalseForRightToLeftStep() {
        assertFalse(rightToLeft().isLeftToRight());
    }
 
    // -------------------------------------------------------------------------
    // getGiven()
    // -------------------------------------------------------------------------
 
    @Test
    void getGivenReturnsLeftSideWhenLeftToRight() {
        ExchangeStep step = leftToRight(); // RED = BLUE, left-to-right
        assertEquals(new Pebbles(List.of(Pebble.RED)), step.getGiven());
    }
 
    @Test
    void getGivenReturnsRightSideWhenRightToLeft() {
        ExchangeStep step = rightToLeft(); // RED = BLUE, right-to-left
        assertEquals(new Pebbles(List.of(Pebble.BLUE)), step.getGiven());
    }
 
    @Test
    void getGivenReturnsCorrectSideForMultiPebbleEquation() {
        // 2 WHITE = 1 GREEN, left-to-right: give 2 WHITE
        ExchangeStep step = new ExchangeStep(twoWhiteForGreen(), true);
        assertEquals(
            new Pebbles(List.of(Pebble.WHITE, Pebble.WHITE)),
            step.getGiven());
    }
 
    // -------------------------------------------------------------------------
    // getReceived()
    // -------------------------------------------------------------------------
 
    @Test
    void getReceivedReturnsRightSideWhenLeftToRight() {
        ExchangeStep step = leftToRight(); // RED = BLUE, left-to-right
        assertEquals(new Pebbles(List.of(Pebble.BLUE)), step.getReceived());
    }
 
    @Test
    void getReceivedReturnsLeftSideWhenRightToLeft() {
        ExchangeStep step = rightToLeft(); // RED = BLUE, right-to-left
        assertEquals(new Pebbles(List.of(Pebble.RED)), step.getReceived());
    }
 
    @Test
    void getReceivedReturnsCorrectSideForMultiPebbleEquation() {
        // 2 WHITE = 1 GREEN, right-to-left: receive 2 WHITE
        ExchangeStep step = new ExchangeStep(twoWhiteForGreen(), false);
        assertEquals(
            new Pebbles(List.of(Pebble.WHITE, Pebble.WHITE)),
            step.getReceived());
    }
 
    @Test
    void givenAndReceivedAreOpposites() {
        // For any step, what you give is the opposite of what you receive
        ExchangeStep ltr = leftToRight();
        ExchangeStep rtl = rightToLeft();
        assertEquals(ltr.getGiven(),    rtl.getReceived());
        assertEquals(ltr.getReceived(), rtl.getGiven());
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void stepsWithSameEquationAndDirectionAreEqual() {
        assertEquals(leftToRight(), leftToRight());
    }
 
    @Test
    void stepsWithSameEquationButDifferentDirectionAreNotEqual() {
        assertNotEquals(leftToRight(), rightToLeft());
    }
 
    @Test
    void stepsWithDifferentEquationsAreNotEqual() {
        ExchangeStep s1 = new ExchangeStep(redForBlue(), true);
        ExchangeStep s2 = new ExchangeStep(twoWhiteForGreen(), true);
        assertNotEquals(s1, s2);
    }
 
    @Test
    void equalStepsHaveEqualHashCodes() {
        assertEquals(leftToRight().hashCode(), leftToRight().hashCode());
    }
 
    @Test
    void stepIsEqualToItself() {
        ExchangeStep step = leftToRight();
        assertEquals(step, step);
    }
 
    @Test
    void stepIsNotEqualToNull() {
        assertNotEquals(null, leftToRight());
    }
}