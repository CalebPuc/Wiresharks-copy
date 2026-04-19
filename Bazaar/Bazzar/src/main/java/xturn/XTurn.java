package xturn;
 
import com.google.gson.*;
import referee.GameState;
import common.TurnState;
import util.JsonUtils;
 
import java.io.InputStreamReader;
 
/**
 * Integration test harness for the GameState-to-TurnState transformation.
 *
 * Reads one JSON value from STDIN:
 *   *Game — the referee's full game state
 *
 * Writes one JSON value to STDOUT:
 *   *Turn — the turn state derived for the active player
 *
 * JSON format:
 *   *Game = {"bank": *Pebbles, "visibles": *Cards,
 *            "cards": *Cards, "players": [*Player, ...]}
 *
 *   *Turn = {"bank": *Pebbles, "cards": *Cards,
 *            "active": *Player, "scores": [Natural, ...]}
 *
 * The active player is the first player in the "players" array.
 * The "scores" array contains only the remaining players' scores,
 * not the active player's score.
 */
public class XTurn {
 
    public static void main(String[] args) {
        JsonStreamParser parser =
            new JsonStreamParser(new InputStreamReader(System.in));
 
        // Parse the Game JSON into a GameState
        GameState game = JsonUtils.parseGame(parser.next());
 
        // Derive the TurnState for the active player
        TurnState turn = game.toTurnState();
 
        // Serialize and print
        System.out.println(
            new Gson().toJson(JsonUtils.serializeTurnState(turn)));
    }
}