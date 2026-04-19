package common;
 
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the PlayerState class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class PlayerStateTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Pebbles twoRed() {
        return new Pebbles(List.of(Pebble.RED, Pebble.RED));
    }
 
    private static Pebbles oneBlue() {
        return new Pebbles(List.of(Pebble.BLUE));
    }
 
    private static PlayerState playerWithScore(int score) {
        return new PlayerState(twoRed(), score);
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsZeroScore() {
        assertDoesNotThrow(() -> new PlayerState(twoRed(), 0));
    }
 
    @Test
    void constructorAcceptsPositiveScore() {
        assertDoesNotThrow(() -> new PlayerState(twoRed(), 15));
    }
 
    @Test
    void constructorRejectsNegativeScore() {
        assertThrows(IllegalArgumentException.class,
            () -> new PlayerState(twoRed(), -1));
    }
 
    @Test
    void constructorRejectsNullWallet() {
        assertThrows(IllegalArgumentException.class,
            () -> new PlayerState(null, 0));
    }
 
    // -------------------------------------------------------------------------
    // getWallet()
    // -------------------------------------------------------------------------
 
    @Test
    void getWalletReturnsCorrectWallet() {
        Pebbles wallet = twoRed();
        PlayerState ps = new PlayerState(wallet, 0);
        assertEquals(wallet, ps.getWallet());
    }
 
    // -------------------------------------------------------------------------
    // getScore()
    // -------------------------------------------------------------------------
 
    @Test
    void getScoreReturnsCorrectScore() {
        assertEquals(7, playerWithScore(7).getScore());
    }
 
    @Test
    void getScoreReturnsZeroForNewPlayer() {
        assertEquals(0, playerWithScore(0).getScore());
    }
 
    // -------------------------------------------------------------------------
    // withWallet()
    // -------------------------------------------------------------------------
 
    @Test
    void withWalletReturnsNewStateWithUpdatedWallet() {
        PlayerState original = new PlayerState(twoRed(), 5);
        PlayerState updated  = original.withWallet(oneBlue());
        assertEquals(oneBlue(), updated.getWallet());
    }
 
    @Test
    void withWalletPreservesScore() {
        PlayerState original = new PlayerState(twoRed(), 5);
        PlayerState updated  = original.withWallet(oneBlue());
        assertEquals(5, updated.getScore());
    }
 
    @Test
    void withWalletDoesNotModifyOriginal() {
        PlayerState original = new PlayerState(twoRed(), 5);
        original.withWallet(oneBlue());
        assertEquals(twoRed(), original.getWallet());
    }
 
    // -------------------------------------------------------------------------
    // withAddedScore()
    // -------------------------------------------------------------------------
 
    @Test
    void withAddedScoreReturnsNewStateWithIncreasedScore() {
        PlayerState original = new PlayerState(twoRed(), 5);
        PlayerState updated  = original.withAddedScore(3);
        assertEquals(8, updated.getScore());
    }
 
    @Test
    void withAddedScorePreservesWallet() {
        PlayerState original = new PlayerState(twoRed(), 5);
        PlayerState updated  = original.withAddedScore(3);
        assertEquals(twoRed(), updated.getWallet());
    }
 
    @Test
    void withAddedScoreDoesNotModifyOriginal() {
        PlayerState original = new PlayerState(twoRed(), 5);
        original.withAddedScore(3);
        assertEquals(5, original.getScore());
    }
 
    @Test
    void withAddedScoreOfZeroReturnsEquivalentState() {
        PlayerState original = new PlayerState(twoRed(), 5);
        PlayerState updated  = original.withAddedScore(0);
        assertEquals(5, updated.getScore());
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderContainsWalletInfo() {
        PlayerState ps = new PlayerState(twoRed(), 3);
        assertTrue(ps.render().contains("R"));
    }
 
    @Test
    void renderContainsScoreInfo() {
        PlayerState ps = new PlayerState(twoRed(), 3);
        assertTrue(ps.render().contains("3"));
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void statesWithSameWalletAndScoreAreEqual() {
        PlayerState ps1 = new PlayerState(twoRed(), 5);
        PlayerState ps2 = new PlayerState(twoRed(), 5);
        assertEquals(ps1, ps2);
    }
 
    @Test
    void statesWithDifferentScoresAreNotEqual() {
        assertNotEquals(playerWithScore(3), playerWithScore(5));
    }
 
    @Test
    void statesWithDifferentWalletsAreNotEqual() {
        PlayerState ps1 = new PlayerState(twoRed(), 5);
        PlayerState ps2 = new PlayerState(oneBlue(), 5);
        assertNotEquals(ps1, ps2);
    }
 
    @Test
    void equalStatesHaveEqualHashCodes() {
        PlayerState ps1 = new PlayerState(twoRed(), 5);
        PlayerState ps2 = new PlayerState(twoRed(), 5);
        assertEquals(ps1.hashCode(), ps2.hashCode());
    }
 
    @Test
    void stateIsEqualToItself() {
        PlayerState ps = playerWithScore(5);
        assertEquals(ps, ps);
    }
 
    @Test
    void stateIsNotEqualToNull() {
        assertNotEquals(null, playerWithScore(5));
    }
}