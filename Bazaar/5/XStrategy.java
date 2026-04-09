import com.google.gson.*;
import common.Cards;
import common.Cards.PebbleColor;
import common.Equation;
import common.Equations;
import common.TurnState;
import player.Strategy;
import common.PlayerState;

import java.util.*;
import java.io.*;

/**
 * XStrategy: test harness for the Bazaar player strategy.
 *
 * Reads from STDIN:
 *   1. Equations  (JSON array of equations)
 *   2. Turn       (JSON object representing TurnState)
 *   3. Policy     (JSON string: "purchase-points" or "purchase-size")
 *
 * Writes to STDOUT:
 *   1. *Rules  — the exchanges the player wishes to perform
 *   2. *Cards  — the cards the player wishes to purchase
 *   3. points  — the points the player expects to receive
 *   4. *Pebbles — the player's pebbles after exchanges and purchases
 */
public class XStrategy {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        JsonStreamParser parser = new JsonStreamParser(sb.toString());

        // parse equations
        JsonArray equationsJson = parser.next().getAsJsonArray();
        Equations equations = parseEquations(equationsJson);

        // parse turn
        JsonObject turnJson = parser.next().getAsJsonObject();
        TurnState turnState = parseTurnState(turnJson);

        // parse policy
        String policy = parser.next().getAsString();
        boolean maximizePoints = policy.equals("purchase-points");

        // run the strat
        Strategy strategy = new Strategy(maximizePoints);
        Map<String, Object> result = strategy.chooseTurn(turnState, equations);

        // make output
        JsonArray output = buildOutput(result);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(output));
    }

    /**
     * Parses a JSON array of equations.
     *
     * Each equation is a JSON array of two arrays of pebble color strings:
     *   [[left pebbles], [right pebbles]]
     * e.g. [["RED","WHITE"], ["BLUE","GREEN"]]
     */
    private static Equations parseEquations(JsonArray json) {
        List<Equation> equations = new ArrayList<>();
        for (JsonElement el : json) {
            JsonArray pair = el.getAsJsonArray();
            List<PebbleColor> left  = parsePebbleList(pair.get(0).getAsJsonArray());
            List<PebbleColor> right = parsePebbleList(pair.get(1).getAsJsonArray());
            equations.add(new Equation(left, right));
        }
        return new Equations(equations);
    }

    /**
     * Parses a JSON array of pebble color strings into a List<PebbleColor>.
     * e.g. ["RED", "WHITE", "BLUE"] -> [RED, WHITE, BLUE]
     */
    private static List<PebbleColor> parsePebbleList(JsonArray json) {
        List<PebbleColor> pebbles = new ArrayList<>();
        for (JsonElement el : json) {
            pebbles.add(PebbleColor.valueOf(el.getAsString().toUpperCase()));
        }
        return pebbles;
    }

    /**
     * Parses a TurnState from JSON.
     *
     * Expected format:
     * {
     *   "bank":        { "RED": 3, "BLUE": 2, ... },
     *   "wallet":      { "RED": 1, "WHITE": 2, ... },
     *   "score":       7,
     *   "otherScores": [4, 6],
     *   "cards":       [{ "pebbles": ["RED","WHITE","BLUE","GREEN","YELLOW"], "star": false }, ...]
     * }
     */
    private static TurnState parseTurnState(JsonObject json) {
        Map<PebbleColor, Integer> bank   = parsePebbleMap(json.getAsJsonObject("bank"));
        Map<PebbleColor, Integer> wallet = parsePebbleMap(json.getAsJsonObject("wallet"));
        int score = json.get("score").getAsInt();

        List<Integer> otherScores = new ArrayList<>();
        for (JsonElement el : json.getAsJsonArray("otherScores")) {
            otherScores.add(el.getAsInt());
        }

        List<Cards> visibleCards = new ArrayList<>();
        for (JsonElement el : json.getAsJsonArray("cards")) {
            visibleCards.add(parseCard(el.getAsJsonObject()));
        }

        PlayerState activePlayer = new PlayerState("active", wallet, score, true);
        return new TurnState(bank, activePlayer, otherScores, visibleCards);
    }

    /**
     * Parses a pebble count map from a JSON object.
     * e.g. { "RED": 3, "BLUE": 2 } -> { RED=3, BLUE=2 }
     */
    private static Map<PebbleColor, Integer> parsePebbleMap(JsonObject json) {
        Map<PebbleColor, Integer> map = new EnumMap<>(PebbleColor.class);
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(PebbleColor.valueOf(entry.getKey().toUpperCase()),
                    entry.getValue().getAsInt());
        }
        return map;
    }

    /**
     * Parses a single card from JSON.
     * e.g. { "pebbles": ["RED","WHITE","BLUE","GREEN","YELLOW"], "star": false }
     */
    private static Cards parseCard(JsonObject json) {
        List<PebbleColor> pebbles = parsePebbleList(json.getAsJsonArray("pebbles"));
        boolean hasStar = json.get("star").getAsBoolean();
        return new Cards(pebbles, hasStar);
    }

    /**
     * Builds the JSON output array from the strategy result.
     *
     * Output order per spec:
     *   [0] *Rules   — exchange sequence
     *   [1] *Cards   — cards purchased
     *   [2] points   — total points earned
     *   [3] *Pebbles — final wallet
     */
    private static JsonArray buildOutput(Map<String, Object> result) {
        JsonArray output = new JsonArray();

        // *Rules — list of exchange requests
        List<Map<String, Object>> exchanges =
            (List<Map<String, Object>>) result.get("exchanges");
        output.add(serializeExchanges(exchanges));

        // *Cards — list of cards purchased
        List<Cards> cards = (List<Cards>) result.get("cards");
        output.add(serializeCards(cards));

        // points — integer
        output.add((Integer) result.get("points"));

        // *Pebbles — final wallet
        Map<PebbleColor, Integer> wallet =
            (Map<PebbleColor, Integer>) result.get("wallet");
        output.add(serializeWallet(wallet));

        return output;
    }

    /**
     * Serializes an exchange sequence to JSON.
     *
     * Each exchange becomes a two-element array:
     *   [[left pebbles], [right pebbles]]
     * where left/right reflect the direction the player applied the equation.
     * Left-to-right: give left, receive right → output as [left, right]
     * Right-to-left: give right, receive left → output as [right, left]
     */
    private static JsonArray serializeExchanges(List<Map<String, Object>> exchanges) {
    JsonArray rules = new JsonArray();
    for (Map<String, Object> exchange : exchanges) {
        Equation eq = (Equation) exchange.get("equation");
        boolean leftToRight = (Boolean) exchange.get("leftToRight");

        // Give side
        JsonArray give = serializePebbleList(leftToRight ? eq.getLeft() : eq.getRight());
        // Receive side
        JsonArray receive = serializePebbleList(leftToRight ? eq.getRight() : eq.getLeft());

        JsonArray rule = new JsonArray();
        rule.add(give);
        rule.add(receive);
        rules.add(rule);
    }
    return rules;
}

    /**
     * Serializes a list of cards to JSON.
     *
     * Each card becomes:
     *   { "pebbles": [...], "star": bool }
     */
    private static JsonArray serializeCards(List<Cards> cards) {
        JsonArray result = new JsonArray();
        for (Cards card : cards) {
            JsonObject obj = new JsonObject();
            obj.add("pebbles", serializePebbleList(card.getPebbles()));
            obj.addProperty("star", card.hasStar());
            result.add(obj);
        }
        return result;
    }

    /**
     * Serializes a pebble list to a JSON array of color name strings.
     * e.g. [RED, WHITE] -> ["RED", "WHITE"]
     */
    private static JsonArray serializePebbleList(List<PebbleColor> pebbles) {
        JsonArray arr = new JsonArray();
        for (PebbleColor color : pebbles) {
            arr.add(color.name());
        }
        return arr;
    }

    /**
     * Serializes a wallet to a JSON object.
     * Only includes colors with count > 0.
     * e.g. { RED=2, BLUE=1 } -> { "RED": 2, "BLUE": 1 }
     */
    private static JsonObject serializeWallet(Map<PebbleColor, Integer> wallet) {
        JsonObject obj = new JsonObject();
        for (PebbleColor color : PebbleColor.values()) {
            int count = wallet.getOrDefault(color, 0);
            if (count > 0) {
                obj.addProperty(color.name(), count);
            }
        }
        return obj;
    }
}