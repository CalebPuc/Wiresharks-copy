package common;
 
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the Pebble enum.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class PebbleTest {
 
    // -------------------------------------------------------------------------
    // abbreviation()
    // -------------------------------------------------------------------------
 
    @Test
    void redAbbreviationIsR() {
        assertEquals("R", Pebble.RED.abbreviation());
    }
 
    @Test
    void whiteAbbreviationIsW() {
        assertEquals("W", Pebble.WHITE.abbreviation());
    }
 
    @Test
    void blueAbbreviationIsB() {
        assertEquals("B", Pebble.BLUE.abbreviation());
    }
 
    @Test
    void greenAbbreviationIsG() {
        assertEquals("G", Pebble.GREEN.abbreviation());
    }
 
    @Test
    void yellowAbbreviationIsY() {
        assertEquals("Y", Pebble.YELLOW.abbreviation());
    }
 
    @Test
    void abbreviationIsSingleCharacter() {
        for (Pebble p : Pebble.values()) {
            assertEquals(1, p.abbreviation().length(),
                "Abbreviation for " + p + " should be exactly one character");
        }
    }
 
    // -------------------------------------------------------------------------
    // hexColor()
    // -------------------------------------------------------------------------
 
    @Test
    void redHexColorIsCorrect() {
        assertEquals("#E74C3C", Pebble.RED.hexColor());
    }
 
    @Test
    void whiteHexColorIsCorrect() {
        assertEquals("#ECF0F1", Pebble.WHITE.hexColor());
    }
 
    @Test
    void blueHexColorIsCorrect() {
        assertEquals("#3498DB", Pebble.BLUE.hexColor());
    }
 
    @Test
    void greenHexColorIsCorrect() {
        assertEquals("#2ECC71", Pebble.GREEN.hexColor());
    }
 
    @Test
    void yellowHexColorIsCorrect() {
        assertEquals("#F1C40F", Pebble.YELLOW.hexColor());
    }
 
    @Test
    void hexColorBeginsWithHash() {
        for (Pebble p : Pebble.values()) {
            assertTrue(p.hexColor().startsWith("#"),
                "Hex color for " + p + " should begin with #");
        }
    }
 
    @Test
    void hexColorIsSevenCharacters() {
        for (Pebble p : Pebble.values()) {
            assertEquals(7, p.hexColor().length(),
                "Hex color for " + p + " should be 7 characters");
        }
    }
 
    @Test
    void allFiveColorsExist() {
        assertEquals(5, Pebble.values().length);
    }
}