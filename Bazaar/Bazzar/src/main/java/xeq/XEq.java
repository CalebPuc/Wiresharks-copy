package xeq;
 
import com.google.gson.*;
import common.*;
import util.JsonUtils;
 
import java.io.InputStreamReader;
 
/**
 * Integration test harness for the equation-filtering functionality.
 *
 * Reads three JSON values from STDIN in order:
 *   1. *Equations — an array of equations
 *   2. *Wallet   — the active player's pebbles
 *   3. *Bank     — the bank's pebbles
 *
 * Writes one JSON value to STDOUT:
 *   *Rules — the subset of equations the player can apply in at least
 *            one direction, given their wallet and the bank's supply.
 */
public class XEq {
 
    public static void main(String[] args) {
        JsonStreamParser parser =
            new JsonStreamParser(new InputStreamReader(System.in));
 
        Equations equations = JsonUtils.parseEquations(parser.next());
        Pebbles   wallet    = JsonUtils.parsePebbles(parser.next());
        Pebbles   bank      = JsonUtils.parsePebbles(parser.next());
 
        Equations applicable = equations.filterApplicable(wallet, bank);
 
        System.out.println(
            new Gson().toJson(JsonUtils.serializeEquations(applicable)));
    }
}