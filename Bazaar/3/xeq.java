import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import java.io.InputStreamReader;
import java.util.*;
/**
 * xeq - Integration test harness for Milestone 3 .
 *
 * Reads from STDIN three JSON values in sequence:
 *   1. *Equations - array of [[pebble,...], [pebble,...]] pairs
 *   2. *Wallet    - array of pebble strings (the active player's wallet)
 *   3. *Bank      - array of pebble strings (the bank's pebbles)
 *
 * Outputs to STDOUT one JSON value:
 *   *Rules - the subset of equations that the player can apply LEFT-TO-RIGHT
 *            given their wallet and the bank's pebbles.
 *
 * Pebble string values: "red", "white", "blue", "green", "yellow"
 */
public class xeq {

    public static void main(String[] args) {
        Gson gson = new Gson();
        JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

        //Read the three JSON values from STDIN
        if (!parser.hasNext()) { System.err.println("Missing equations"); System.exit(1); }
        JsonArray equations = parser.next().getAsJsonArray();

        if (!parser.hasNext()) { System.err.println("Missing wallet"); System.exit(1); }
        JsonArray walletArr = parser.next().getAsJsonArray();

        if (!parser.hasNext()) { System.err.println("Missing bank"); System.exit(1); }
        JsonArray bankArr = parser.next().getAsJsonArray();

        //Convert wallet and bank arrays to count maps
        Map<String, Integer> wallet = toCountMap(walletArr);
        Map<String, Integer> bank   = toCountMap(bankArr);

        //Filter equations to those applicable left-to-right
        JsonArray rules = new JsonArray();
        for (JsonElement el : equations) {
            JsonArray eq = el.getAsJsonArray();
            if (canApplyLTR(eq, wallet, bank)) {
                rules.add(eq);
            }
        }

        System.out.println(gson.toJson(rules));
    }

    /**
     * Returns true if the equation can be applied left-to-right:
     *   - the wallet contains all pebbles on the left side, AND
     *   - the bank contains all pebbles on the right side.
     */
    private static boolean canApplyLTR(
            JsonArray eq,
            Map<String, Integer> wallet,
            Map<String, Integer> bank) {
        Map<String, Integer> leftNeeded  = toCountMap(eq.get(0).getAsJsonArray());
        Map<String, Integer> rightNeeded = toCountMap(eq.get(1).getAsJsonArray());
        return hasEnough(wallet, leftNeeded) && hasEnough(bank, rightNeeded);
    }

    /**
     * Returns true if the available map contains at least as many of each
     * pebble color as specified in the needed map.
     */
    private static boolean hasEnough(
            Map<String, Integer> available,
            Map<String, Integer> needed) {
        for (Map.Entry<String, Integer> entry : needed.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a JSON array of pebble strings into a color -> count map.
     * e.g. ["red", "red", "blue"] -> {"red": 2, "blue": 1}
     */
    private static Map<String, Integer> toCountMap(JsonArray arr) {
        Map<String, Integer> map = new HashMap<>();
        for (JsonElement el : arr) {
            String color = el.getAsString();
            map.merge(color, 1, Integer::sum);
        }
        return map;
    }
}
