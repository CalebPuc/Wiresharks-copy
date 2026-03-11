package common;

import common.Cards.PebbleColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents the table of equations in the Bazaar game and a single equation.
 *
 * An equation shows two collections of pebbles on the sides of an "=" sign.
 * Each side has 1–4 pebbles, and the two sides must not share any pebble color.
 * Equations may be used in either direction (they are symmetric trades).
 *
 * The EquationTable is the full set of 10 equations visible to all players.
 *
 * Data representation:
 *   Equation:
 *     - left:  a non-empty list of 1–4 PebbleColor values (may repeat within side)
 *     - right: a non-empty list of 1–4 PebbleColor values, disjoint colors from left
 *
 *   EquationTable:
 *     - equations: a list of up to 10 Equation values
 */
public class Equations {

    // =========================================================================
    // EquationTable — the table of up to 10 equations shown during a game
    // =========================================================================

    private final List<Equation> equations;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates an EquationTable from an existing list of equations.
     *
     * @param equations list of Equation objects (at most 10)
     */
    public Equations(List<Equation> equations) {
        if (equations == null || equations.size() > 10) {
            throw new IllegalArgumentException(
                    "An equation table must contain 0–10 equations.");
        }
        this.equations = Collections.unmodifiableList(new ArrayList<>(equations));
    }

    // -------------------------------------------------------------------------
    // (1) Creating a table of equations
    // -------------------------------------------------------------------------

    /**
     * (1a) Creates a table from a provided list of equations.
     */
    public static Equations createTable(List<Equation> equations) {
        return new Equations(equations);
    }

    /**
     * (1b) Creates a table of exactly 10 randomly generated equations.
     *
     * Generation randomly partitions the 5 colors into two non-empty,
     * non-overlapping groups and assigns random pebble counts (1–4) per side.
     */
    public static Equations createRandomTable() {
        Random rng = new Random();
        List<Equation> result = new ArrayList<>();
        while (result.size() < 10) {
            try {
                result.add(randomEquation(rng));
            } catch (IllegalArgumentException ignored) {
                // retry if random generation violates constraints
            }
        }
        return new Equations(result);
    }

    // -------------------------------------------------------------------------
    // (2) Filtering equations a player can use
    // -------------------------------------------------------------------------

    /**
     * (2) Returns the subset of equations that the player can apply
     * (in at least one direction) given their pebbles and the bank's pebbles.
     *
     * @param playerPebbles map from PebbleColor to count for the player
     * @param bankPebbles   map from PebbleColor to count for the bank
     * @return a new EquationTable containing only the applicable equations
     */
    public Equations filterApplicable(
            Map<PebbleColor, Integer> playerPebbles,
            Map<PebbleColor, Integer> bankPebbles) {
        List<Equation> applicable = new ArrayList<>();
        for (Equation eq : equations) {
            if (eq.canApply(playerPebbles, bankPebbles)) {
                applicable.add(eq);
            }
        }
        return new Equations(applicable);
    }

    // -------------------------------------------------------------------------
    // (4) Rendering the table graphically
    // -------------------------------------------------------------------------

    /**
     * (4) Renders the full table of equations as a multi-line text display.
     *
     * Example:
     *   Equations:
     *   1.  R W = B G
     *   2.  Y = R W B
     *   ...
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equations:\n");
        for (int i = 0; i < equations.size(); i++) {
            sb.append(String.format("  %2d.  %s%n", i + 1, equations.get(i).render()));
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public List<Equation> getEquations() {
        return equations;
    }

    public int size() {
        return equations.size();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Equation randomEquation(Random rng) {
        PebbleColor[] colors = PebbleColor.values(); // 5 colors
        // Shuffle colors and split into two non-empty groups
        List<PebbleColor> shuffled = new ArrayList<>(List.of(colors));
        Collections.shuffle(shuffled, rng);

        // Split point: 1–4 for left (leaving at least 1 for right)
        int splitPoint = 1 + rng.nextInt(shuffled.size() - 1);
        List<PebbleColor> leftColors  = shuffled.subList(0, splitPoint);
        List<PebbleColor> rightColors = shuffled.subList(splitPoint, shuffled.size());

        // Build left side: pick 1–4 pebbles from leftColors (with repetition allowed)
        List<PebbleColor> left  = randomSide(rng, leftColors);
        List<PebbleColor> right = randomSide(rng, rightColors);
        return new Equation(left, right);
    }

    private static List<PebbleColor> randomSide(Random rng, List<PebbleColor> availableColors) {
        int count = 1 + rng.nextInt(4); // 1–4 pebbles
        List<PebbleColor> side = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            side.add(availableColors.get(rng.nextInt(availableColors.size())));
        }
        return side;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return render();
    }
}

