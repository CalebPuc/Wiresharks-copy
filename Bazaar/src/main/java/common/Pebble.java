package common;
 
/**
 * One of the five pebble colors in the Bazaar game.
 *
 * Pebbles are the basic unit of currency. Players hold them in wallets,
 * the bank holds a supply of them, and equations specify trades in terms
 * of them.
 *
 * Data representation:
 *   An enumerated type with exactly five values: RED, WHITE, BLUE,
 *   GREEN, YELLOW. Each value represents one distinguishable pebble color.
 */
public enum Pebble {
    RED, WHITE, BLUE, GREEN, YELLOW;
 
    /**
     * Returns the single-character abbreviation for this pebble color,
     * used when rendering equations and cards in text form.
     */
    public String abbreviation() {
        return this.name().substring(0, 1);
    }
 
    /**
     * Returns the hex color string for this pebble color,
     * used when rendering pebbles graphically.
     */
    public String hexColor() {
        switch (this) {
            case RED:    return "#E74C3C";
            case WHITE:  return "#ECF0F1";
            case BLUE:   return "#3498DB";
            case GREEN:  return "#2ECC71";
            case YELLOW: return "#F1C40F";
            default:     throw new IllegalStateException("Unknown pebble: " + this);
        }
    }
}