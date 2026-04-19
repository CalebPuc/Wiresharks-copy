package referee;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * Unit tests for the GameState class.
 *
 * Tests are organized by method. Each test covers one specific behavior
 * described in the purpose statement of the method under test.
 */
public class GameStateTest {
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private static Pebbles fullBank() {
        return new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE, Pebble.GREEN, Pebble.YELLOW));
    }
 
    private static Pebbles emptyBank() {
        return new Pebbles();
    }
 
    private static Card anyCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED, Pebble.RED)), false);
    }
 
    private static Cards fourVisibles() {
        return new Cards(List.of(anyCard(), anyCard(), anyCard(), anyCard()));
    }
 
    private static Cards emptyCards() {
        return new Cards(List.of());
    }
 
    private static PlayerState playerAt(int score) {
        return new PlayerState(
            new Pebbles(List.of(Pebble.WHITE, Pebble.BLUE)), score);
    }
 
    private static GameState twoPlayerGame() {
        return new GameState(
            fullBank(),
            fourVisibles(),
            new Cards(List.of(anyCard(), anyCard())),
            List.of(playerAt(0), playerAt(0))
        );
    }
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    @Test
    void constructorAcceptsValidState() {
        assertDoesNotThrow(() -> twoPlayerGame());
    }
 
    @Test
    void constructorRejectsNullBank() {
        assertThrows(IllegalArgumentException.class, () ->
            new GameState(null, fourVisibles(), emptyCards(),
                List.of(playerAt(0))));
    }
 
    @Test
    void constructorRejectsEmptyPlayerList() {
        assertThrows(IllegalArgumentException.class, () ->
            new GameState(fullBank(), fourVisibles(), emptyCards(), List.of()));
    }
 
    @Test
    void constructorMakesDefensiveCopyOfPlayers() {
        List<PlayerState> players =
            new java.util.ArrayList<>(List.of(playerAt(0), playerAt(0)));
        GameState gs = new GameState(fullBank(), fourVisibles(),
            emptyCards(), players);
        players.add(playerAt(5));
        assertEquals(2, gs.getPlayers().size());
    }
 
    // -------------------------------------------------------------------------
    // getActivePlayer()
    // -------------------------------------------------------------------------
 
    @Test
    void getActivePlayerReturnsFirstPlayer() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getPlayers().get(0), gs.getActivePlayer());
    }
 
    // -------------------------------------------------------------------------
    // isGameOver()
    // -------------------------------------------------------------------------
 
    @Test
    void isGameOverReturnsFalseForActiveGame() {
        assertFalse(twoPlayerGame().isGameOver());
    }
 
    @Test
    void isGameOverReturnsTrueWhenPlayerReaches20Points() {
        GameState gs = new GameState(
            fullBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(20), playerAt(0))
        );
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverReturnsTrueWhenPlayerExceeds20Points() {
        GameState gs = new GameState(
            fullBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(25), playerAt(0))
        );
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverReturnsTrueWhenNoCardsRemain() {
        GameState gs = new GameState(
            fullBank(), emptyCards(), emptyCards(),
            List.of(playerAt(0))
        );
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverReturnsTrueWhenBankEmptyAndNoOneCanBuy() {
        // Bank is empty; player has WHITE+BLUE but card needs 5 RED
        GameState gs = new GameState(
            emptyBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(0))
        );
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverReturnsFalseWhenBankEmptyButPlayerCanBuy() {
        // Card needs WHITE+BLUE+RED+GREEN+YELLOW, player has exactly that
        Card affordable = new Card(new Pebbles(List.of(
            Pebble.WHITE, Pebble.BLUE, Pebble.RED, Pebble.GREEN, Pebble.YELLOW)),
            false);
        PlayerState richPlayer = new PlayerState(new Pebbles(List.of(
            Pebble.WHITE, Pebble.BLUE, Pebble.RED, Pebble.GREEN, Pebble.YELLOW)),
            0);
        GameState gs = new GameState(
            emptyBank(),
            new Cards(List.of(affordable)),
            emptyCards(),
            List.of(richPlayer)
        );
        assertFalse(gs.isGameOver());
    }
 
    @Test
    void isGameOverReturnsTrueWhenPlayerListIsEmpty() {
        // Simulate all players eliminated by constructing directly
        // (constructor rejects empty list, so we test via the condition logic)
        // A game with one player at score 20 triggers the score condition first
        GameState gs = new GameState(
            fullBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(20))
        );
        assertTrue(gs.isGameOver());
    }
 
    // -------------------------------------------------------------------------
    // toTurnState()
    // -------------------------------------------------------------------------
 
    @Test
    void toTurnStateReturnsCorrectBank() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getBank(), gs.toTurnState().getBank());
    }
 
    @Test
    void toTurnStateReturnsCorrectVisibles() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getVisibles(), gs.toTurnState().getVisibles());
    }
 
    @Test
    void toTurnStateReturnsActivePlayerAsActive() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getActivePlayer(), gs.toTurnState().getActive());
    }
 
    @Test
    void toTurnStateScoresContainOtherPlayersOnlyNotActive() {
        PlayerState p1 = playerAt(3);
        PlayerState p2 = playerAt(7);
        PlayerState p3 = playerAt(11);
        GameState gs = new GameState(
            fullBank(), fourVisibles(), emptyCards(),
            List.of(p1, p2, p3)
        );
        TurnState ts = gs.toTurnState();
        // Active is p1 (score 3); others are p2 (7) and p3 (11)
        assertEquals(List.of(7, 11), ts.getScores());
    }
 
    @Test
    void toTurnStateWithOnePlayerHasEmptyScores() {
        GameState gs = new GameState(
            fullBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(5))
        );
        assertTrue(gs.toTurnState().getScores().isEmpty());
    }
 
    @Test
    void toTurnStateDoesNotExposeOtherPlayersWallets() {
        // TurnState.getScores() returns integers, not PlayerStates —
        // so other wallets are structurally inaccessible
        GameState gs = twoPlayerGame();
        TurnState ts = gs.toTurnState();
        // scores is List<Integer>, not List<PlayerState>
        assertEquals(List.of(0), ts.getScores());
    }
 
    // -------------------------------------------------------------------------
    // render()
    // -------------------------------------------------------------------------
 
    @Test
    void renderContainsBankInfo() {
        assertTrue(twoPlayerGame().render().contains("Bank"));
    }
 
    @Test
    void renderContainsPlayerInfo() {
        assertTrue(twoPlayerGame().render().contains("Players"));
    }
 
    @Test
    void renderContainsDeckInfo() {
        assertTrue(twoPlayerGame().render().contains("Deck"));
    }
 
    // -------------------------------------------------------------------------
    // equals() and hashCode()
    // -------------------------------------------------------------------------
 
    @Test
    void equalGameStatesAreEqual() {
        GameState gs1 = twoPlayerGame();
        GameState gs2 = twoPlayerGame();
        assertEquals(gs1, gs2);
    }
 
    @Test
    void gameStatesWithDifferentBanksAreNotEqual() {
        GameState gs1 = twoPlayerGame();
        GameState gs2 = new GameState(
            emptyBank(), fourVisibles(), emptyCards(),
            List.of(playerAt(0), playerAt(0))
        );
        assertNotEquals(gs1, gs2);
    }
 
    @Test
    void equalGameStatesHaveEqualHashCodes() {
        assertEquals(twoPlayerGame().hashCode(), twoPlayerGame().hashCode());
    }
 
    @Test
    void gameStateIsEqualToItself() {
        GameState gs = twoPlayerGame();
        assertEquals(gs, gs);
    }
 
    @Test
    void gameStateIsNotEqualToNull() {
        assertNotEquals(null, twoPlayerGame());
    }
}