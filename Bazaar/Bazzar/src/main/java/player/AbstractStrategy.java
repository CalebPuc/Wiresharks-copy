package player;
 
import common.*;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
/**
 * Shared implementation of the exchange-and-purchase search used by
 * both concrete strategies.
 *
 * The search explores all combinations of up to 4 exchange steps
 * followed by all possible card purchase sequences, collects the
 * candidates that maximize the strategy's goal, and applies tie
 * breaking rules to select a single best decision.
 *
 * Subclasses implement {@link #isBetter} to define what "better" means
 * for their particular maximization goal.
 */
public abstract class AbstractStrategy implements Strategy {
 
    // The spec imposes an artificial limit of 4 exchanges per turn.
    private static final int MAX_EXCHANGES = 4;
 
    // -------------------------------------------------------------------------
    // Strategy interface
    // -------------------------------------------------------------------------
 
    @Override
    public TurnDecision takeTurn(TurnState turn, Equations equations) {
        Pebbles wallet = turn.getActive().getWallet();
        Pebbles bank   = turn.getBank();
        Cards   cards  = turn.getVisibles();
 
        // Collect all legal exchange sequences up to MAX_EXCHANGES deep
        List<List<ExchangeStep>> allExchangeSeqs = new ArrayList<>();
        collectExchangeSequences(wallet, bank, equations,
            new ArrayList<>(), allExchangeSeqs);
 
        // For each exchange sequence, find the best card purchase sequence
        TurnDecision best = null;
        for (List<ExchangeStep> exchanges : allExchangeSeqs) {
            Pebbles walletAfter = applyExchanges(wallet, bank, exchanges).wallet;
            List<TurnDecision> purchaseCandidates =
                collectPurchaseCandidates(exchanges, walletAfter, cards);
            for (TurnDecision candidate : purchaseCandidates) {
                if (best == null || isBetter(candidate, best)) {
                    best = candidate;
                } else if (!isBetter(best, candidate)) {
                    // tie — apply tie-breaking
                    best = breakTie(best, candidate);
                }
            }
        }
 
        // Should never be null since empty exchanges + no purchases is valid
        if (best == null) {
            best = new TurnDecision(
                new ArrayList<>(), new ArrayList<>(), 0, wallet);
        }
        return best;
    }
 
    // -------------------------------------------------------------------------
    // Abstract method for subclasses
    // -------------------------------------------------------------------------
 
    /**
     * Returns true if {@code a} is strictly better than {@code b}
     * according to this strategy's maximization goal.
     *
     * Subclasses implement this to define what "better" means —
     * more points, or more cards purchased.
     */
    protected abstract boolean isBetter(TurnDecision a, TurnDecision b);
 
    // -------------------------------------------------------------------------
    // Exchange search
    // -------------------------------------------------------------------------
 
    /**
     * Recursively collects all legal exchange sequences up to
     * MAX_EXCHANGES steps deep, starting from the given wallet and bank.
     *
     * Each element of result is one complete exchange sequence.
     */
    private void collectExchangeSequences(Pebbles wallet,
                                          Pebbles bank,
                                          Equations equations,
                                          List<ExchangeStep> current,
                                          List<List<ExchangeStep>> result) {
        // Always include the current sequence (including the empty one)
        result.add(new ArrayList<>(current));
 
        if (current.size() >= MAX_EXCHANGES) {
            return;
        }
 
        // Try every equation in both directions
        for (Equation eq : equations.getEquations()) {
            if (eq.canApplyLeftToRight(wallet, bank)) {
                ExchangeStep step = new ExchangeStep(eq, true);
                Pebbles newWallet = wallet.remove(eq.getLeft())
                                          .add(eq.getRight());
                Pebbles newBank   = bank.remove(eq.getRight())
                                        .add(eq.getLeft());
                current.add(step);
                collectExchangeSequences(
                    newWallet, newBank, equations, current, result);
                current.remove(current.size() - 1);
            }
            if (eq.canApplyRightToLeft(wallet, bank)) {
                ExchangeStep step = new ExchangeStep(eq, false);
                Pebbles newWallet = wallet.remove(eq.getRight())
                                          .add(eq.getLeft());
                Pebbles newBank   = bank.remove(eq.getLeft())
                                        .add(eq.getRight());
                current.add(step);
                collectExchangeSequences(
                    newWallet, newBank, equations, current, result);
                current.remove(current.size() - 1);
            }
        }
    }
 
    // -------------------------------------------------------------------------
    // Card purchase search
    // -------------------------------------------------------------------------
 
    /**
     * Returns all TurnDecisions reachable by purchasing some subsequence
     * of the visible cards (in order) after the given exchange sequence,
     * starting with the given wallet.
     *
     * Always includes the decision of buying no cards.
     */
    private List<TurnDecision> collectPurchaseCandidates(
            List<ExchangeStep> exchanges,
            Pebbles startWallet,
            Cards visibles) {
        List<TurnDecision> result = new ArrayList<>();
        collectPurchaseSequences(
            exchanges, startWallet, visibles.getCards(),
            0, new ArrayList<>(), startWallet, 0, result);
        return result;
    }
 
    /**
     * Recursively tries purchasing each remaining affordable card in order,
     * accumulating points and updating the wallet.
     */
    private void collectPurchaseSequences(
            List<ExchangeStep> exchanges,
            Pebbles startWallet,
            List<Card> available,
            int index,
            List<Card> purchased,
            Pebbles currentWallet,
            int currentPoints,
            List<TurnDecision> result) {
 
        // Record this purchase sequence as a candidate
        result.add(new TurnDecision(
            exchanges,
            new ArrayList<>(purchased),
            currentPoints,
            currentWallet));
 
        // Try buying each remaining card in order
        for (int i = index; i < available.size(); i++) {
            Card card = available.get(i);
            if (currentWallet.hasAtLeast(card.getPebbles())) {
                Pebbles newWallet = currentWallet.remove(card.getPebbles());
                int pts = TurnDecision.score(card, newWallet.size());
                purchased.add(card);
                collectPurchaseSequences(
                    exchanges, startWallet, available, i + 1,
                    purchased, newWallet, currentPoints + pts, result);
                purchased.remove(purchased.size() - 1);
            }
        }
    }
 
    // -------------------------------------------------------------------------
    // Tie breaking (spec-defined, shared by both strategies)
    // -------------------------------------------------------------------------
 
    /**
     * Breaks a tie between two equivalent decisions using the rules from
     * the spec:
     *   1. Fewer exchanges wins
     *   2. Smaller card sequence wins (by card comparison)
     *   3. Smaller exchange sequence wins (by exchange comparison)
     *
     * Returns whichever decision wins under these rules.
     */
    private TurnDecision breakTie(TurnDecision a, TurnDecision b) {
        // Rule 1: fewer exchanges
        int ea = a.getExchanges().size();
        int eb = b.getExchanges().size();
        if (ea != eb) return ea < eb ? a : b;
 
        // Rule 2: smaller card sequence
        int cardCmp = compareCardSequences(a.getPurchases(), b.getPurchases());
        if (cardCmp != 0) return cardCmp < 0 ? a : b;
 
        // Rule 3: smaller exchange sequence
        int exchCmp = compareExchangeSequences(
            a.getExchanges(), b.getExchanges());
        return exchCmp <= 0 ? a : b;
    }
 
    /**
     * Compares two card purchase sequences lexicographically using the
     * card comparison defined in the spec. Returns negative if a < b,
     * positive if a > b, 0 if equal.
     */
    private int compareCardSequences(List<Card> a, List<Card> b) {
        if (a.size() != b.size()) return a.size() - b.size();
        for (int i = 0; i < a.size(); i++) {
            int cmp = compareCards(a.get(i), b.get(i));
            if (cmp != 0) return cmp;
        }
        return 0;
    }
 
    /**
     * Compares two cards using the spec's ordering:
     *   - a non-starred card is less than a starred card
     *   - among same star status, compare pebble bags as wallets
     *
     * Returns negative if a < b, positive if a > b, 0 if equal.
     */
    private int compareCards(Card a, Card b) {
        // Non-starred < starred
        if (!a.hasStar() && b.hasStar()) return -1;
        if (a.hasStar() && !b.hasStar()) return 1;
        // Same star status — compare pebbles as wallets
        return comparePebbles(a.getPebbles(), b.getPebbles());
    }
 
    /**
     * Compares two pebble collections as wallets using the oracle's
     * string comparison: sort abbreviations alphabetically and compare
     * lexicographically; fall back to size if strings differ in length.
     *
     * Returns negative if a < b, positive if a > b, 0 if equal.
     */
    private int comparePebbles(Pebbles a, Pebbles b) {
        if (a.size() != b.size()) return a.size() - b.size();
        String sa = toSortedString(a);
        String sb = toSortedString(b);
        return sa.compareTo(sb);
    }
 
    /**
     * Converts a Pebbles collection to a sorted string of abbreviations,
     * as defined by the oracle implementation.
     *
     * Example: {RED:2, BLUE:1} -> "BRR"
     */
    private String toSortedString(Pebbles pebbles) {
        List<String> chars = new ArrayList<>();
        for (Pebble p : pebbles.toList()) {
            chars.add(p.abbreviation());
        }
        Collections.sort(chars);
        StringBuilder sb = new StringBuilder();
        for (String c : chars) sb.append(c);
        return sb.toString();
    }
 
    /**
     * Compares two exchange sequences lexicographically using the
     * spec's exchange comparison. Returns negative if a < b, positive
     * if a > b, 0 if equal.
     */
    private int compareExchangeSequences(
            List<ExchangeStep> a, List<ExchangeStep> b) {
        if (a.size() != b.size()) return a.size() - b.size();
        for (int i = 0; i < a.size(); i++) {
            int cmp = compareExchangeSteps(a.get(i), b.get(i));
            if (cmp != 0) return cmp;
        }
        return 0;
    }
 
    /**
     * Compares two exchange steps using the spec's ordering:
     * compare left-hand sides as wallets, then right-hand sides.
     *
     * Returns negative if a < b, positive if a > b, 0 if equal.
     */
    private int compareExchangeSteps(ExchangeStep a, ExchangeStep b) {
        int leftCmp = comparePebbles(a.getGiven(), b.getGiven());
        if (leftCmp != 0) return leftCmp;
        return comparePebbles(a.getReceived(), b.getReceived());
    }
 
    // -------------------------------------------------------------------------
    // Private helper
    // -------------------------------------------------------------------------
 
    /**
     * Applies a sequence of exchange steps to the given wallet and bank,
     * returning the resulting wallet and bank as a simple pair.
     */
    private WalletBankPair applyExchanges(Pebbles wallet,
                                          Pebbles bank,
                                          List<ExchangeStep> steps) {
        for (ExchangeStep step : steps) {
            wallet = wallet.remove(step.getGiven()).add(step.getReceived());
            bank   = bank.remove(step.getReceived()).add(step.getGiven());
        }
        return new WalletBankPair(wallet, bank);
    }
 
    /** Simple pair to return two values from applyExchanges. */
    private static class WalletBankPair {
        final Pebbles wallet;
        final Pebbles bank;
        WalletBankPair(Pebbles wallet, Pebbles bank) {
            this.wallet = wallet;
            this.bank   = bank;
        }
    }
}