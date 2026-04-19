package util;
 
import com.google.gson.*;
import common.*;
import referee.GameState;
 
import java.util.ArrayList;
import java.util.List;
 
/**
 * Shared JSON serialization and deserialization helpers used by all
 * integration test harnesses.
 *
 * Each method converts between a domain object and its JSON
 * representation as specified by the milestone test harness formats.
 *
 * JSON formats:
 *   *Pebble    = "red" | "white" | "blue" | "green" | "yellow"
 *   *Pebbles   = [*Pebble, ...]
 *   *Equation  = [*Pebbles, *Pebbles]
 *   *Equations = [*Equation, ...]
 *   *Card      = {"pebbles": *Pebbles, "face?": Boolean}
 *   *Cards     = [*Card, ...]
 *   *Player    = {"wallet": *Pebbles, "score": Natural}
 *   *Game      = {"bank": *Pebbles, "visibles": *Cards,
 *                 "cards": *Cards, "players": [*Player, ...]}
 *   *Turn      = {"bank": *Pebbles, "cards": *Cards,
 *                 "active": *Player, "scores": [Natural, ...]}
 */
public class JsonUtils {
 
    // -------------------------------------------------------------------------
    // Pebble / Pebbles
    // -------------------------------------------------------------------------
 
    /**
     * Parses a lowercase pebble color string into a Pebble.
     */
    public static Pebble parsePebble(String s) {
        switch (s) {
            case "red":    return Pebble.RED;
            case "white":  return Pebble.WHITE;
            case "blue":   return Pebble.BLUE;
            case "green":  return Pebble.GREEN;
            case "yellow": return Pebble.YELLOW;
            default: throw new IllegalArgumentException(
                "Unknown pebble color: " + s);
        }
    }
 
    /**
     * Parses a JSON array of pebble strings into a Pebbles collection.
     */
    public static Pebbles parsePebbles(JsonElement json) {
        List<Pebble> list = new ArrayList<>();
        for (JsonElement p : json.getAsJsonArray()) {
            list.add(parsePebble(p.getAsString()));
        }
        return new Pebbles(list);
    }
 
    /**
     * Serializes a Pebbles collection to a JSON array of lowercase
     * color strings in canonical order: red, white, blue, green, yellow.
     */
    public static JsonArray serializePebbles(Pebbles pebbles) {
        JsonArray arr = new JsonArray();
        for (Pebble p : pebbles.toList()) {
            arr.add(p.name().toLowerCase());
        }
        return arr;
    }
 
    // -------------------------------------------------------------------------
    // Equation / Equations
    // -------------------------------------------------------------------------
 
    /**
     * Parses a JSON array of equations into an Equations table.
     */
    public static Equations parseEquations(JsonElement json) {
        List<Equation> result = new ArrayList<>();
        for (JsonElement eqJson : json.getAsJsonArray()) {
            result.add(parseEquation(eqJson));
        }
        return new Equations(result);
    }
 
    /**
     * Parses a two-element JSON array into a single Equation.
     */
    public static Equation parseEquation(JsonElement json) {
        JsonArray arr   = json.getAsJsonArray();
        Pebbles   left  = parsePebbles(arr.get(0));
        Pebbles   right = parsePebbles(arr.get(1));
        return new Equation(left, right);
    }
 
    /**
     * Serializes an Equations table to a JSON array of equation arrays.
     */
    public static JsonArray serializeEquations(Equations equations) {
        JsonArray result = new JsonArray();
        for (Equation eq : equations.getEquations()) {
            result.add(serializeEquation(eq));
        }
        return result;
    }
 
    /**
     * Serializes one Equation to a two-element JSON array.
     */
    public static JsonArray serializeEquation(Equation eq) {
        JsonArray arr = new JsonArray();
        arr.add(serializePebbles(eq.getLeft()));
        arr.add(serializePebbles(eq.getRight()));
        return arr;
    }
 
    // -------------------------------------------------------------------------
    // Card / Cards
    // -------------------------------------------------------------------------
 
    /**
     * Parses a JSON object with "pebbles" and "face?" fields into a Card.
     */
    public static Card parseCard(JsonElement json) {
        JsonObject obj    = json.getAsJsonObject();
        Pebbles    pebbles = parsePebbles(obj.get("pebbles"));
        boolean    hasStar = obj.get("face?").getAsBoolean();
        return new Card(pebbles, hasStar);
    }
 
    /**
     * Parses a JSON array of card objects into a Cards collection.
     */
    public static Cards parseCards(JsonElement json) {
        List<Card> list = new ArrayList<>();
        for (JsonElement c : json.getAsJsonArray()) {
            list.add(parseCard(c));
        }
        return new Cards(list);
    }
 
    /**
     * Serializes a Card to a JSON object with "pebbles" and "face?" fields.
     */
    public static JsonObject serializeCard(Card card) {
        JsonObject obj = new JsonObject();
        obj.add("pebbles", serializePebbles(card.getPebbles()));
        obj.addProperty("face?", card.hasStar());
        return obj;
    }
 
    /**
     * Serializes a Cards collection to a JSON array of card objects.
     */
    public static JsonArray serializeCards(Cards cards) {
        JsonArray arr = new JsonArray();
        for (Card c : cards.getCards()) {
            arr.add(serializeCard(c));
        }
        return arr;
    }
 
    // -------------------------------------------------------------------------
    // PlayerState
    // -------------------------------------------------------------------------
 
    /**
     * Parses a JSON object with "wallet" and "score" fields into a
     * PlayerState.
     */
    public static PlayerState parsePlayer(JsonElement json) {
        JsonObject obj    = json.getAsJsonObject();
        Pebbles    wallet = parsePebbles(obj.get("wallet"));
        int        score  = obj.get("score").getAsInt();
        return new PlayerState(wallet, score);
    }
 
    /**
     * Serializes a PlayerState to a JSON object with "wallet" and
     * "score" fields.
     */
    public static JsonObject serializePlayer(PlayerState player) {
        JsonObject obj = new JsonObject();
        obj.add("wallet", serializePebbles(player.getWallet()));
        obj.addProperty("score", player.getScore());
        return obj;
    }
 
    // -------------------------------------------------------------------------
    // GameState
    // -------------------------------------------------------------------------
 
    /**
     * Parses a JSON object with "bank", "visibles", "cards", and
     * "players" fields into a GameState.
     */
    public static GameState parseGame(JsonElement json) {
        JsonObject        obj      = json.getAsJsonObject();
        Pebbles           bank     = parsePebbles(obj.get("bank"));
        Cards             visibles = parseCards(obj.get("visibles"));
        Cards             deck     = parseCards(obj.get("cards"));
        List<PlayerState> players  = new ArrayList<>();
        for (JsonElement p : obj.get("players").getAsJsonArray()) {
            players.add(parsePlayer(p));
        }
        return new GameState(bank, visibles, deck, players);
    }
 
    // -------------------------------------------------------------------------
    // TurnState
    // -------------------------------------------------------------------------
 
    /**
     * Serializes a TurnState to a JSON object with "bank", "cards",
     * "active", and "scores" fields.
     */
    public static JsonObject serializeTurnState(TurnState turn) {
        JsonObject obj = new JsonObject();
        obj.add("bank",   serializePebbles(turn.getBank()));
        obj.add("cards",  serializeCards(turn.getVisibles()));
        obj.add("active", serializePlayer(turn.getActive()));
 
        JsonArray scores = new JsonArray();
        for (int score : turn.getScores()) {
            scores.add(score);
        }
        obj.add("scores", scores);
 
        return obj;
    }
}