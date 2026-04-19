package referee;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import common.Cards;
import common.Cards.PebbleColor;
import common.Equation;
import common.Equations;
import common.TurnState;

@DisplayName("GameState Unit Tests")
public class GameStateTest {

    private Equations standardEquations;
    private Map<PebbleColor, Integer> standardBank;
    private List<Cards> standardVisibleCards;
    private Queue<Cards> standardDeck;
    private List<PlayerState> standardPlayers;
    private int standardActiveIndex;

    @BeforeEach
    public void setUp() {
        // Create standard equations
        Equation eq1 = new Equation(
            List.of(PebbleColor.RED, PebbleColor.WHITE),
            List.of(PebbleColor.BLUE, PebbleColor.GREEN)
        );
        Equation eq2 = new Equation(
            List.of(PebbleColor.YELLOW),
            List.of(PebbleColor.RED, PebbleColor.BLUE)
        );
        standardEquations = Equations.createTable(List.of(eq1, eq2));

        // Create standard bank
        standardBank = new EnumMap<>(PebbleColor.class);
        standardBank.put(PebbleColor.RED, 3);
        standardBank.put(PebbleColor.WHITE, 5);
        standardBank.put(PebbleColor.BLUE, 2);
        standardBank.put(PebbleColor.GREEN, 4);
        standardBank.put(PebbleColor.YELLOW, 1);

        // Create standard visible cards
        standardVisibleCards = new ArrayList<>();
        standardVisibleCards.add(new Cards(
            List.of(PebbleColor.RED, PebbleColor.WHITE, PebbleColor.BLUE, 
                   PebbleColor.GREEN, PebbleColor.YELLOW),
            false
        ));
        standardVisibleCards.add(new Cards(
            List.of(PebbleColor.RED, PebbleColor.RED, PebbleColor.WHITE, 
                   PebbleColor.BLUE, PebbleColor.GREEN),
            true
        ));

        // Create standard deck
        standardDeck = new LinkedList<>();
        standardDeck.add(new Cards(
            List.of(PebbleColor.YELLOW, PebbleColor.YELLOW, PebbleColor.GREEN, 
                   PebbleColor.GREEN, PebbleColor.BLUE),
            false
        ));

        // Create standard players
        Map<PebbleColor, Integer> wallet1 = new EnumMap<>(PebbleColor.class);
        wallet1.put(PebbleColor.RED, 2);
        wallet1.put(PebbleColor.BLUE, 1);
        
        Map<PebbleColor, Integer> wallet2 = new EnumMap<>(PebbleColor.class);
        wallet2.put(PebbleColor.GREEN, 3);
        
        standardPlayers = new ArrayList<>();
        standardPlayers.add(new PlayerState("Player1", wallet1, 7, true));
        standardPlayers.add(new PlayerState("Player2", wallet2, 4, true));

        standardActiveIndex = 0;
    }

    // Constructor Tests

    @Test
    @DisplayName("Constructor succeeds with valid inputs")
    public void testConstructor_ValidInputs() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertNotNull(gameState);
        assertEquals(standardEquations, gameState.getEquations());
        assertEquals(2, gameState.getPlayers().size());
        assertEquals(0, gameState.getActiveIndex());
    }

    @Test
    @DisplayName("Constructor throws when equations is null")
    public void testConstructor_NullEquations() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(null, standardBank, standardVisibleCards, 
                               standardDeck, standardPlayers, standardActiveIndex)
        );
        assertEquals("Equations must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor throws when bank is null")
    public void testConstructor_NullBank() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(standardEquations, null, standardVisibleCards, 
                               standardDeck, standardPlayers, standardActiveIndex)
        );
        assertEquals("Bank must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor throws when players is null")
    public void testConstructor_NullPlayers() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(standardEquations, standardBank, standardVisibleCards, 
                               standardDeck, null, standardActiveIndex)
        );
        assertEquals("There must be at least one player.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor throws when players list is empty")
    public void testConstructor_EmptyPlayers() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(standardEquations, standardBank, standardVisibleCards, 
                               standardDeck, List.of(), standardActiveIndex)
        );
        assertEquals("There must be at least one player.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor throws when activeIndex is negative")
    public void testConstructor_NegativeActiveIndex() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(standardEquations, standardBank, standardVisibleCards, 
                               standardDeck, standardPlayers, -1)
        );
        assertEquals("activeIndex out of range.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor throws when activeIndex is too large")
    public void testConstructor_ActiveIndexTooLarge() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new GameState(standardEquations, standardBank, standardVisibleCards, 
                               standardDeck, standardPlayers, 2)
        );
        assertEquals("activeIndex out of range.", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor accepts null visibleCards")
    public void testConstructor_NullVisibleCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            null,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertNotNull(gameState);
        assertTrue(gameState.getVisibleCards().isEmpty());
    }

    @Test
    @DisplayName("Constructor accepts null deck")
    public void testConstructor_NullDeck() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            null,
            standardPlayers,
            standardActiveIndex
        );

        assertNotNull(gameState);
        assertTrue(gameState.getDeck().isEmpty());
    }

    @Test
    @DisplayName("Constructor accepts empty bank")
    public void testConstructor_EmptyBank() {
        Map<PebbleColor, Integer> emptyBank = new EnumMap<>(PebbleColor.class);
        
        GameState gameState = new GameState(
            standardEquations,
            emptyBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertNotNull(gameState);
        assertTrue(gameState.getBank().isEmpty());
    }

    // isGameOver Tests

    @Test
    @DisplayName("isGameOver returns false when game is ongoing")
    public void testIsGameOver_GameOngoing() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertFalse(gameState.isGameOver());
    }

    @Test
    @DisplayName("isGameOver returns true when no cards left")
    public void testIsGameOver_NoCardsLeft() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            List.of(),  // empty visible cards
            new LinkedList<>(),  // empty deck
            standardPlayers,
            standardActiveIndex
        );

        assertTrue(gameState.isGameOver());
    }

    @Test
    @DisplayName("isGameOver returns true when no active players")
    public void testIsGameOver_NoActivePlayers() {
        List<PlayerState> inactivePlayers = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        
        inactivePlayers.add(new PlayerState("Player1", wallet, 5, false));
        inactivePlayers.add(new PlayerState("Player2", wallet, 3, false));

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            inactivePlayers,
            0
        );

        assertTrue(gameState.isGameOver());
    }

    @Test
    @DisplayName("isGameOver returns false when visible cards exist but deck is empty")
    public void testIsGameOver_VisibleCardsRemain() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            new LinkedList<>(),  // empty deck
            standardPlayers,
            standardActiveIndex
        );

        assertFalse(gameState.isGameOver());
    }

    @Test
    @DisplayName("isGameOver returns false when deck has cards but visible is empty")
    public void testIsGameOver_DeckHasCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            List.of(),  // empty visible
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertFalse(gameState.isGameOver());
    }

    // extractTurnState Tests

    @Test
    @DisplayName("extractTurnState returns correct TurnState")
    public void testExtractTurnState_Standard() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            0  // Player1 is active
        );

        TurnState turnState = gameState.extractTurnState();

        assertNotNull(turnState);
        assertEquals("Player1", turnState.getActivePlayer().getName());
        assertEquals(7, turnState.getActivePlayer().getScore());
        assertEquals(1, turnState.getOtherScores().size());
        assertEquals(4, turnState.getOtherScores().get(0));  // Player2's score
        assertEquals(2, turnState.getVisibleCards().size());
    }

    @Test
    @DisplayName("extractTurnState includes correct bank")
    public void testExtractTurnState_Bank() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        TurnState turnState = gameState.extractTurnState();

        Map<PebbleColor, Integer> bank = turnState.getBank();
        assertEquals(3, bank.get(PebbleColor.RED));
        assertEquals(5, bank.get(PebbleColor.WHITE));
        assertEquals(2, bank.get(PebbleColor.BLUE));
    }

    @Test
    @DisplayName("extractTurnState excludes active player from otherScores")
    public void testExtractTurnState_ExcludesActivePlayer() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            1  // Player2 is active
        );

        TurnState turnState = gameState.extractTurnState();

        assertEquals("Player2", turnState.getActivePlayer().getName());
        assertEquals(1, turnState.getOtherScores().size());
        assertEquals(7, turnState.getOtherScores().get(0));  // Player1's score
    }

    @Test
    @DisplayName("extractTurnState excludes inactive players from otherScores")
    public void testExtractTurnState_ExcludesInactivePlayers() {
        List<PlayerState> mixedPlayers = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        
        mixedPlayers.add(new PlayerState("Player1", wallet, 10, true));
        mixedPlayers.add(new PlayerState("Player2", wallet, 8, false));  // inactive
        mixedPlayers.add(new PlayerState("Player3", wallet, 6, true));

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            mixedPlayers,
            0  // Player1 is active
        );

        TurnState turnState = gameState.extractTurnState();

        assertEquals(1, turnState.getOtherScores().size());
        assertEquals(6, turnState.getOtherScores().get(0));  // Only Player3
    }

    @Test
    @DisplayName("extractTurnState with single player has empty otherScores")
    public void testExtractTurnState_SinglePlayer() {
        List<PlayerState> singlePlayer = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        singlePlayer.add(new PlayerState("SoloPlayer", wallet, 10, true));

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            singlePlayer,
            0
        );

        TurnState turnState = gameState.extractTurnState();

        assertTrue(turnState.getOtherScores().isEmpty());
    }

    // Accessor Tests

    @Test
    @DisplayName("getEquations returns correct equations")
    public void testGetEquations() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertEquals(standardEquations, gameState.getEquations());
    }

    @Test
    @DisplayName("getBank returns correct bank")
    public void testGetBank() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        Map<PebbleColor, Integer> bank = gameState.getBank();
        assertEquals(3, bank.get(PebbleColor.RED));
        assertEquals(5, bank.get(PebbleColor.WHITE));
    }

    @Test
    @DisplayName("getVisibleCards returns correct cards")
    public void testGetVisibleCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        List<Cards> cards = gameState.getVisibleCards();
        assertEquals(2, cards.size());
        assertFalse(cards.get(0).hasStar());
        assertTrue(cards.get(1).hasStar());
    }

    @Test
    @DisplayName("getDeck returns correct deck")
    public void testGetDeck() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        Queue<Cards> deck = gameState.getDeck();
        assertEquals(1, deck.size());
    }

    @Test
    @DisplayName("getPlayers returns correct players")
    public void testGetPlayers() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        List<PlayerState> players = gameState.getPlayers();
        assertEquals(2, players.size());
        assertEquals("Player1", players.get(0).getName());
        assertEquals("Player2", players.get(1).getName());
    }

    @Test
    @DisplayName("getActiveIndex returns correct index")
    public void testGetActiveIndex() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            1
        );

        assertEquals(1, gameState.getActiveIndex());
    }

    @Test
    @DisplayName("getActivePlayer returns correct player")
    public void testGetActivePlayer() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            1
        );

        PlayerState activePlayer = gameState.getActivePlayer();
        assertEquals("Player2", activePlayer.getName());
        assertEquals(4, activePlayer.getScore());
    }

    // Immutability Tests

    @Test
    @DisplayName("Bank returned by getBank is immutable")
    public void testImmutability_Bank() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        Map<PebbleColor, Integer> bank = gameState.getBank();

        assertThrows(UnsupportedOperationException.class, () -> {
            bank.put(PebbleColor.YELLOW, 100);
        });
    }

    @Test
    @DisplayName("VisibleCards returned by getVisibleCards is immutable")
    public void testImmutability_VisibleCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        List<Cards> cards = gameState.getVisibleCards();

        assertThrows(UnsupportedOperationException.class, () -> {
            cards.add(new Cards(
                List.of(PebbleColor.RED, PebbleColor.RED, PebbleColor.RED, 
                       PebbleColor.RED, PebbleColor.RED),
                false
            ));
        });
    }

    @Test
    @DisplayName("Players returned by getPlayers is immutable")
    public void testImmutability_Players() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        List<PlayerState> players = gameState.getPlayers();

        assertThrows(UnsupportedOperationException.class, () -> {
            Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
            players.add(new PlayerState("Player3", wallet, 0, true));
        });
    }

    @Test
    @DisplayName("Modifications to original bank don't affect GameState")
    public void testImmutability_OriginalBankModification() {
        Map<PebbleColor, Integer> mutableBank = new EnumMap<>(standardBank);
        
        GameState gameState = new GameState(
            standardEquations,
            mutableBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        mutableBank.put(PebbleColor.YELLOW, 999);

        assertEquals(1, gameState.getBank().get(PebbleColor.YELLOW));
    }

    @Test
    @DisplayName("Modifications to original players don't affect GameState")
    public void testImmutability_OriginalPlayersModification() {
        List<PlayerState> mutablePlayers = new ArrayList<>(standardPlayers);
        
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            mutablePlayers,
            standardActiveIndex
        );

        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        mutablePlayers.add(new PlayerState("Player3", wallet, 0, true));

        assertEquals(2, gameState.getPlayers().size());
    }

    // Render Tests

    @Test
    @DisplayName("render includes header")
    public void testRender_ContainsHeader() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("=== Game State ==="));
    }

    @Test
    @DisplayName("render includes active player name and score")
    public void testRender_ContainsActivePlayer() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            0
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Active Player: Player1"));
        assertTrue(rendered.contains("score: 7"));
    }

    @Test
    @DisplayName("render includes bank info")
    public void testRender_ContainsBank() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Bank:"));
        assertTrue(rendered.contains("RED=3"));
        assertTrue(rendered.contains("WHITE=5"));
    }

    @Test
    @DisplayName("render includes visible cards")
    public void testRender_ContainsVisibleCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Visible Cards:"));
        assertTrue(rendered.contains("★"));  // happy card
    }

    @Test
    @DisplayName("render shows (none) for empty visible cards")
    public void testRender_EmptyVisibleCards() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            List.of(),
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Visible Cards:"));
        assertTrue(rendered.contains("(none)"));
    }

    @Test
    @DisplayName("render includes deck size")
    public void testRender_ContainsDeckSize() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Deck: 1 card(s) remaining"));
    }

    @Test
    @DisplayName("render includes all players")
    public void testRender_ContainsPlayers() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("Players:"));
        assertTrue(rendered.contains("Player1"));
        assertTrue(rendered.contains("Player2"));
        assertTrue(rendered.contains("[*]"));  // active marker
        assertTrue(rendered.contains("[ ]"));  // inactive marker
    }

    @Test
    @DisplayName("render marks eliminated players")
    public void testRender_MarksEliminatedPlayers() {
        List<PlayerState> players = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        
        players.add(new PlayerState("Player1", wallet, 10, true));
        players.add(new PlayerState("Player2", wallet, 5, false));

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            players,
            0
        );

        String rendered = gameState.render();

        assertTrue(rendered.contains("(eliminated)"));
    }

    @Test
    @DisplayName("toString delegates to render")
    public void testToString_DelegatesToRender() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            standardPlayers,
            standardActiveIndex
        );

        String toString = gameState.toString();
        String render = gameState.render();

        assertEquals(render, toString);
    }

    // Edge Case Tests

    @Test
    @DisplayName("Handles single player game")
    public void testEdgeCase_SinglePlayer() {
        List<PlayerState> singlePlayer = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        singlePlayer.add(new PlayerState("SoloPlayer", wallet, 10, true));

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            singlePlayer,
            0
        );

        assertEquals(1, gameState.getPlayers().size());
        assertEquals("SoloPlayer", gameState.getActivePlayer().getName());
    }

    @Test
    @DisplayName("Handles many players")
    public void testEdgeCase_ManyPlayers() {
        List<PlayerState> manyPlayers = new ArrayList<>();
        Map<PebbleColor, Integer> wallet = new EnumMap<>(PebbleColor.class);
        wallet.put(PebbleColor.RED, 1);
        
        for (int i = 1; i <= 6; i++) {
            manyPlayers.add(new PlayerState("Player" + i, wallet, i * 2, true));
        }

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            standardDeck,
            manyPlayers,
            0
        );

        assertEquals(6, gameState.getPlayers().size());
    }

    @Test
    @DisplayName("Handles empty deck")
    public void testEdgeCase_EmptyDeck() {
        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            new LinkedList<>(),
            standardPlayers,
            standardActiveIndex
        );

        assertTrue(gameState.getDeck().isEmpty());
    }

    @Test
    @DisplayName("Handles large deck")
    public void testEdgeCase_LargeDeck() {
        Queue<Cards> largeDeck = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            largeDeck.add(new Cards(
                List.of(PebbleColor.RED, PebbleColor.WHITE, PebbleColor.BLUE, 
                       PebbleColor.GREEN, PebbleColor.YELLOW),
                i % 2 == 0
            ));
        }

        GameState gameState = new GameState(
            standardEquations,
            standardBank,
            standardVisibleCards,
            largeDeck,
            standardPlayers,
            standardActiveIndex
        );

        assertEquals(20, gameState.getDeck().size());
    }
}