package referee;
 
import common.*;
import player.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
 
/*
 * Unit tests for the Referee class.
 *
 * Tests cover both cooperative scenarios (normal game play) and
 * non-cooperative ones (players that misbehave or throw exceptions).
 */
public class RefereeTest {
 
    // helpers
 
    private static Equations redForBlue() {
        return new Equations(List.of(
            new Equation(
                new Pebbles(List.of(Pebble.RED)),
                new Pebbles(List.of(Pebble.BLUE)))));
    }
 
    private static Card fiveRed() {
        return new Card(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), false);
    }
 
    // -> GameState
    // one player with 5 RED, one visible card costing 5 RED, empty bank
    // player can buy the card immediately with no exchanges needed
    private static GameState onePlayerCanWin() {
        PlayerState p = new PlayerState(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), 0);
        return new GameState(
            new Pebbles(),
            new Cards(List.of(fiveRed())),
            new Cards(List.of()),
            List.of(p));
    }
 
    // -> GameState
    // two players, first has 5 RED and can buy immediately, bank is empty
    private static GameState twoPlayersFirstCanBuy() {
        PlayerState p1 = new PlayerState(new Pebbles(List.of(
            Pebble.RED, Pebble.RED, Pebble.RED,
            Pebble.RED, Pebble.RED)), 0);
        PlayerState p2 = new PlayerState(new Pebbles(), 0);
        return new GameState(
            new Pebbles(),
            new Cards(List.of(fiveRed())),
            new Cards(List.of()),
            List.of(p1, p2));
    }
 
    // -> GameState
    // two players both with RED, bank has BLUE, equation RED=BLUE exists
    // this gives the throwing strategy a chance to actually throw on
    // requestExchanges rather than being eliminated for a pebble request
    private static GameState twoPlayersWithExchanges() {
        PlayerState p1 = new PlayerState(
            new Pebbles(List.of(Pebble.RED, Pebble.RED)), 0);
        PlayerState p2 = new PlayerState(
            new Pebbles(List.of(Pebble.RED, Pebble.RED)), 0);
        return new GameState(
            new Pebbles(List.of(Pebble.BLUE, Pebble.BLUE)),
            new Cards(List.of(fiveRed())),
            new Cards(List.of()),
            List.of(p1, p2));
    }
 
    private static Mechanism pointsMech(String name) {
        return new Mechanism(name, new PurchasePointsStrategy());
    }
 
    // throws on any call to takeTurn -- simulates a completely broken player
    private static Strategy throwingStrategy() {
        return (turn, equations) -> {
            throw new RuntimeException("player broke");
        };
    }
 
    // cooperative play
 
    @Test
    void gameWithOnePlayerProducesWinner() {
        Referee ref = new Referee(new Equations(List.of()));
        GameResult result = ref.runGame(
            List.of(pointsMech("Alice")), onePlayerCanWin());
        assertFalse(result.getWinners().isEmpty());
    }
 
    @Test
    void winnerNameMatchesMechanism() {
        Referee ref = new Referee(new Equations(List.of()));
        GameResult result = ref.runGame(
            List.of(pointsMech("Alice")), onePlayerCanWin());
        assertTrue(result.getWinners().contains("Alice"));
    }
 
    @Test
    void noMisbehavedPlayersInNormalGame() {
        Referee ref = new Referee(new Equations(List.of()));
        GameResult result = ref.runGame(
            List.of(pointsMech("Alice")), onePlayerCanWin());
        assertTrue(result.getMisbehaved().isEmpty());
    }
 
    @Test
    void twoPlayerGameProducesAtLeastOneWinner() {
        Referee ref = new Referee(new Equations(List.of()));
        List<Mechanism> players = List.of(
            pointsMech("Alice"), pointsMech("Bob"));
        GameResult result = ref.runGame(players, twoPlayersFirstCanBuy());
        assertFalse(result.getWinners().isEmpty());
    }
 
    // non-cooperative: throwing player
 
    @Test
    void throwingPlayerIsEliminated() {
        // use a state where exchanges are possible so the strategy fires
        Referee ref = new Referee(redForBlue());
        Mechanism bad  = new Mechanism("Bad",  throwingStrategy());
        Mechanism good = new Mechanism("Good", new PurchasePointsStrategy());
        GameResult result = ref.runGame(
            List.of(bad, good), twoPlayersWithExchanges());
        assertTrue(result.getMisbehaved().contains("Bad"));
    }
 
    @Test
    void gameontinuesAfterBadPlayerEliminated() {
        Referee ref = new Referee(redForBlue());
        Mechanism bad  = new Mechanism("Bad",  throwingStrategy());
        Mechanism good = new Mechanism("Good", new PurchasePointsStrategy());
        GameResult result = ref.runGame(
            List.of(bad, good), twoPlayersWithExchanges());
        assertFalse(result.getWinners().isEmpty());
    }
 
    @Test
    void allMisbehavingPlayersProducesNoWinners() {
        // both players have exchanges available so the strategy fires and throws
        Referee ref = new Referee(redForBlue());
        Mechanism bad1 = new Mechanism("Bad1", throwingStrategy());
        Mechanism bad2 = new Mechanism("Bad2", throwingStrategy());
        GameResult result = ref.runGame(
            List.of(bad1, bad2), twoPlayersWithExchanges());
        assertTrue(result.getWinners().isEmpty());
        assertFalse(result.getMisbehaved().isEmpty());
    }
 
    // result structure
 
    @Test
    void gameResultWinnersIsNonNull() {
        Referee ref = new Referee(new Equations(List.of()));
        GameResult result = ref.runGame(
            List.of(pointsMech("Alice")), onePlayerCanWin());
        assertNotNull(result.getWinners());
    }
 
    @Test
    void gameResultMisbehavedIsNonNull() {
        Referee ref = new Referee(new Equations(List.of()));
        GameResult result = ref.runGame(
            List.of(pointsMech("Alice")), onePlayerCanWin());
        assertNotNull(result.getMisbehaved());
    }
}