package referee;
 
import common.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
 
/*
 * Unit tests for the GameState class.
 */
public class GameStateTest {
 
    // helpers
 
    private static Pebbles fullBank() {
        return new Pebbles(List.of(
            Pebble.RED, Pebble.WHITE, Pebble.BLUE,
            Pebble.GREEN, Pebble.YELLOW));
    }
 
    private static Card anyCard() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), false);
    }
 
    private static Cards fourVisibles() {
        return new Cards(List.of(anyCard(), anyCard(), anyCard(), anyCard()));
    }
 
    private static PlayerState playerAt(int score) {
        return new PlayerState(
            new Pebbles(List.of(Pebble.WHITE, Pebble.BLUE)), score);
    }
 
    private static GameState twoPlayerGame() {
        return new GameState(fullBank(), fourVisibles(),
            new Cards(List.of(anyCard())),
            List.of(playerAt(0), playerAt(0)));
    }
 
    // constructor
 
    @Test
    void constructorAcceptsValidState() {
        assertDoesNotThrow(() -> twoPlayerGame());
    }
 
    @Test
    void constructorRejectsEmptyPlayerList() {
        assertThrows(IllegalArgumentException.class, () ->
            new GameState(fullBank(), fourVisibles(),
                new Cards(List.of()), List.of()));
    }
 
    @Test
    void constructorMakesDefensiveCopyOfPlayers() {
        List<PlayerState> players = new ArrayList<>(
            List.of(playerAt(0), playerAt(0)));
        GameState gs = new GameState(fullBank(), fourVisibles(),
            new Cards(List.of()), players);
        players.add(playerAt(5));
        assertEquals(2, gs.getPlayers().size());
    }
 
    // getActivePlayer
 
    @Test
    void getActivePlayerReturnsFirstPlayer() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getPlayers().get(0), gs.getActivePlayer());
    }
 
    // isGameOver
 
    @Test
    void isGameOverFalseForActiveGame() {
        assertFalse(twoPlayerGame().isGameOver());
    }
 
    @Test
    void isGameOverTrueWhenPlayerReaches20Points() {
        GameState gs = new GameState(fullBank(), fourVisibles(),
            new Cards(List.of()), List.of(playerAt(20), playerAt(0)));
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverTrueWhenNoCardsRemain() {
        GameState gs = new GameState(fullBank(),
            new Cards(List.of()), new Cards(List.of()),
            List.of(playerAt(0)));
        assertTrue(gs.isGameOver());
    }
 
    @Test
    void isGameOverTrueWhenBankEmptyAndNoOnCanBuy() {
        GameState gs = new GameState(new Pebbles(), fourVisibles(),
            new Cards(List.of()), List.of(playerAt(0)));
        assertTrue(gs.isGameOver());
    }
 
    // toTurnState
 
    @Test
    void toTurnStateBankMatchesGameBank() {
        GameState gs = twoPlayerGame();
        assertEquals(gs.getBank(), gs.toTurnState().getBank());
    }
 
    @Test
    void toTurnStateScoresExcludesActivePlayer() {
        GameState gs = new GameState(fullBank(), fourVisibles(),
            new Cards(List.of()),
            List.of(playerAt(3), playerAt(7), playerAt(11)));
        assertEquals(List.of(7, 11), gs.toTurnState().getScores());
    }
 
    @Test
    void toTurnStateScoresEmptyForSinglePlayer() {
        GameState gs = new GameState(fullBank(), fourVisibles(),
            new Cards(List.of()), List.of(playerAt(5)));
        assertTrue(gs.toTurnState().getScores().isEmpty());
    }
 
    // equals and hashCode
 
    @Test
    void equalGameStatesAreEqual() {
        assertEquals(twoPlayerGame(), twoPlayerGame());
    }
 
    @Test
    void equalGameStatesHaveSameHashCode() {
        assertEquals(twoPlayerGame().hashCode(), twoPlayerGame().hashCode());
    }
}