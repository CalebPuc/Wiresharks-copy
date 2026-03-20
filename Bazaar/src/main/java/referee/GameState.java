package referee;

import common.Cards;
import common.Cards.PebbleColor;
import common.Equations;
import common.TurnState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Represents the complete game state that the referee maintains throughout a
 * Bazaar game.
 *
 * Data representation:
 *   - equations:     the fixed set of up to 10 equations for this game
 *   - bank:          a map from PebbleColor to the number of pebbles the bank holds
 *   - visibleCards:  the cards available for purchase
 *   - deck:          the remaining cards not yet vsible
 *   - players:       the ordered list of all player states (index 0 = first player)
 *   - activeIndex:   index into players indicating whose turn it is
 */
public class GameState {

    // Fields
    private final Equations equations;                         // global constant
    private final Map<PebbleColor, Integer> bank;              // mutable bank
    private final List<Cards> visibleCards;                    // up to 4
    private final Queue<Cards> deck;                           // remaining cards
    private final List<PlayerState> players;                   // all players, in turn order
    private int activeIndex;                                   // index of active player

    /**
     * Creates a new GameState.
     *
     * @param equations    the equations chosen for this game (fixed for the entire game)
     * @param bank         the initial pebble counts in the bank
     * @param visibleCards the initially visible cards
     * @param deck         the remaining cards in the deck
     * @param players      all players in turn order
     * @param activeIndex  index of the player whose turn it is first
     */
    public GameState(
            Equations equations,
            Map<PebbleColor, Integer> bank,
            List<Cards> visibleCards,
            Queue<Cards> deck,
            List<PlayerState> players,
            int activeIndex) {
        if (equations == null) throw new IllegalArgumentException("Equations must not be null.");
        if (bank == null)      throw new IllegalArgumentException("Bank must not be null.");
        if (players == null || players.isEmpty())
            throw new IllegalArgumentException("There must be at least one player.");
        if (activeIndex < 0 || activeIndex >= players.size())
            throw new IllegalArgumentException("activeIndex out of range.");

        this.equations    = equations;
        this.bank         = new EnumMap<>(bank);
        this.visibleCards = new ArrayList<>(visibleCards != null ? visibleCards : List.of());
        this.deck         = new LinkedList<>(deck != null ? deck : List.of());
        this.players      = new ArrayList<>(players);
        this.activeIndex  = activeIndex;
    }

    /**
     * Returns true if the game is over.
     *
     * The game ends when:
     *   (a) all visible cards have been purchased and the deck is empty, OR
     *   (b) no active (non-eliminated) players remain.
     *
     * @return true if the game is over
     */
    public boolean isGameOver() {
        boolean noCardsLeft = visibleCards.isEmpty() && deck.isEmpty();
        boolean noActivePlayers = players.stream().noneMatch(PlayerState::isActive);
        return noCardsLeft || noActivePlayers;
    }

    /**
     * Extracts the TurnState, the info the referee transmits to the
     * active player at the start of their turn.
     *
     * The TurnState includes:
     *   - the bank's current pebble counts
     *   - the active player's own state 
     *   - the current scores of all other active players
     *   - the visible cards
     *
     * @return the TurnState for the active player
     */
    public TurnState extractTurnState() {
        PlayerState active = players.get(activeIndex);

        // Collect scores of all other active players (not the active player itself)
        List<Integer> otherScores = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (i != activeIndex && players.get(i).isActive()) {
                otherScores.add(players.get(i).getScore());
            }
        }

        return new TurnState(
                new EnumMap<>(bank),
                active,
                Collections.unmodifiableList(otherScores),
                Collections.unmodifiableList(new ArrayList<>(visibleCards)));
    }

    /**
     * Renders a readable summary of the full game state for debugging
     * and display purposes.
     *
     * Example output:
     *   === Game State ===
     *   Active Player: Player 2 (score: 4)
     *   Bank: RED=3 WHITE=5 BLUE=2 GREEN=4 YELLOW=1
     *   Visible Cards:
     *     [ R W B G Y ]
     *     [ R R W B G ]  ★
     *   Deck: 14 card(s) remaining
     *   Players:
     *     [*] Player 1 | wallet: RED=2 BLUE=1 | score: 7
     *     [ ] Player 2 | wallet: GREEN=3      | score: 4  (active)
     *   Equations:
     *     1.  R W = B G
     *     (more down here but i dont feel like writing allat fir a comment)
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Game State ===\n");

        // Active player
        PlayerState active = players.get(activeIndex);
        sb.append("Active Player: ").append(active.getName())
          .append(" (score: ").append(active.getScore()).append(")\n");

        // Bank
        sb.append("Bank: ").append(renderPebbleMap(bank)).append("\n");

        // Visible cards
        sb.append("Visible Cards:\n");
        if (visibleCards.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (Cards c : visibleCards) {
                sb.append("  ").append(c.render()).append("\n");
            }
        }

        // Deck
        sb.append("Deck: ").append(deck.size()).append(" card(s) remaining\n");

        // Players
        sb.append("Players:\n");
        for (int i = 0; i < players.size(); i++) {
            PlayerState ps = players.get(i);
            String marker = (i == activeIndex) ? "[*]" : "[ ]";
            sb.append("  ").append(marker).append(" ")
              .append(ps.getName())
              .append(" | wallet: ").append(renderPebbleMap(ps.getWallet()))
              .append(" | score: ").append(ps.getScore());
            if (!ps.isActive()) sb.append("  (eliminated)");
            if (i == activeIndex) sb.append("  (active)");
            sb.append("\n");
        }

        sb.append(equations.render());

        return sb.toString();
    }

    // Accessors

    public Equations getEquations()                  { return equations; }
    public Map<PebbleColor, Integer> getBank()       { return Collections.unmodifiableMap(bank); }
    public List<Cards> getVisibleCards()             { return Collections.unmodifiableList(visibleCards); }
    public Queue<Cards> getDeck()                    { return new LinkedList<>(deck); }
    public List<PlayerState> getPlayers()            { return Collections.unmodifiableList(players); }
    public int getActiveIndex()                      { return activeIndex; }
    public PlayerState getActivePlayer()             { return players.get(activeIndex); }

    // Private helpers
    private static String renderPebbleMap(Map<PebbleColor, Integer> map) {
        if (map.isEmpty()) return "(empty)";
        StringBuilder sb = new StringBuilder();
        for (PebbleColor c : PebbleColor.values()) {
            int count = map.getOrDefault(c, 0);
            if (count > 0) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(c.name()).append("=").append(count);
            }
        }
        return sb.length() == 0 ? "(empty)" : sb.toString();
    }
    
    @Override
    public String toString() {
        return render();
    }
}
