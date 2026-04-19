package xeq;
 
import com.google.gson.*;
import common.*;
 
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 
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
 *
 * JSON format:
 *   *Pebble    = "red" | "white" | "blue" | "green" | "yellow"
 *   *Pebbles   = [*Pebble, ...]
 *   *Equation  = [*Pebbles, *Pebbles]
 *   *Equations = [*Equation, ...]
 *   *Rules     = [*Equation, ...]
 */
public class XEq {
 
    public static void main(String[] args) {
        JsonStreamParser parser =
            new JsonStreamParser(new InputStreamReader(System.in));
 
        // Read the three JSON values in order
        JsonElement equationsJson = parser.next();
        JsonElement walletJson    = parser.next();
        JsonElement bankJson      = parser.next();
 
        // Deserialize
        Equations equations = parseEquations(equationsJson);
        Pebbles   wallet    = parsePebbles(walletJson);
        Pebbles   bank      = parsePebbles(bankJson);
 
        // Filter
        Equations applicable = equations.filterApplicable(wallet, bank);
 
        // Serialize and print
        System.out.println(serializeEquations(applicable));
    }
 
    // -------------------------------------------------------------------------
    // Deserialization — JSON -> domain objects
    // -------------------------------------------------------------------------
 
    /**
     * Parses a JSON array of equations into an Equations table.
     *
     * Each equation is a two-element array of pebble arrays:
     *   [[pebble, ...], [pebble, ...]]
     */
    static Equations parseEquations(JsonElement json) {
        List<Equation> result = new ArrayList<>();
        for (JsonElement eqJson : json.getAsJsonArray()) {
            result.add(parseEquation(eqJson));
        }
        return new Equations(result);
    }
 
    /**
     * Parses a two-element JSON array into a single Equation.
     */
    static Equation parseEquation(JsonElement json) {
        JsonArray arr   = json.getAsJsonArray();
        Pebbles   left  = parsePebbles(arr.get(0));
        Pebbles   right = parsePebbles(arr.get(1));
        return new Equation(left, right);
    }
 
    /**
     * Parses a JSON array of pebble strings into a Pebbles collection.
     *
     * Example: ["red", "red", "blue"] -> Pebbles with 2 RED and 1 BLUE
     */
    static Pebbles parsePebbles(JsonElement json) {
        List<Pebble> list = new ArrayList<>();
        for (JsonElement p : json.getAsJsonArray()) {
            list.add(parsePebble(p.getAsString()));
        }
        return new Pebbles(list);
    }
 
    /**
     * Parses a lowercase pebble color string into a Pebble.
     */
    static Pebble parsePebble(String s) {
        switch (s) {
            case "red":    return Pebble.RED;
            case "white":  return Pebble.WHITE;
            case "blue":   return Pebble.BLUE;
            case "green":  return Pebble.GREEN;
            case "yellow": return Pebble.YELLOW;
            default: throw new IllegalArgumentException("Unknown pebble color: " + s);
        }
    }
 
    // -------------------------------------------------------------------------
    // Serialization — domain objects -> JSON
    // -------------------------------------------------------------------------
 
    /**
     * Serializes an Equations table to a JSON array of equation arrays.
     */
    static String serializeEquations(Equations equations) {
        JsonArray result = new JsonArray();
        for (Equation eq : equations.getEquations()) {
            result.add(serializeEquation(eq));
        }
        return new Gson().toJson(result);
    }
 
    /**
     * Serializes one Equation to a two-element JSON array.
     */
    static JsonArray serializeEquation(Equation eq) {
        JsonArray arr = new JsonArray();
        arr.add(serializePebbles(eq.getLeft()));
        arr.add(serializePebbles(eq.getRight()));
        return arr;
    }
 
    /**
     * Serializes a Pebbles collection to a JSON array of lowercase strings,
     * in canonical color order: red, white, blue, green, yellow.
     */
    static JsonArray serializePebbles(Pebbles pebbles) {
        JsonArray arr = new JsonArray();
        for (Pebble p : pebbles.toList()) {
            arr.add(p.name().toLowerCase());
        }
        return arr;
    }
}