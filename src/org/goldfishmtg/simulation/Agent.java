package org.goldfishmtg.simulation;

import org.goldfishmtg.cards.CardList;
import org.goldfishmtg.cards.Library;

/**
 * The Agent acts as both an observer and controller for a {@link Goldfish}
 * instance
 * <p>
 * As a controller it is responsible for determining whether the opening hand
 * should be kept, whether another turn should be simulated, and can manipulate
 * the library and hand during each turn of the game.
 * <p>
 * As an observer it can perform inspections on the library, hand, and keep its
 * own internal game state to keep track of data points for analysis as each
 * turn is played
 * 
 * @author skaspersen
 * 
 * @param <T>
 *            the type use to represent cards, must implement the
 *            {@link Comparable} interface
 * 
 * @see Goldfish
 */
public interface Agent<T extends Comparable<T>> {
    /**
     * Returns <code>true</code> if the specified hand should be kept,
     * <code>false</code> if a mulligan should be taken instead
     * 
     * @param cardCount
     *            the number of cards drawn
     * @param hand
     *            the cards drawn
     * @return <code>true</code> if the specified hand should be kept
     */
    boolean keepOpeningHand(int cardCount, CardList<T> hand);

    /**
     * Returns <code>true</code> if the agent needs to simulate another turn
     */
    boolean simulateAnotherTurn();

    /**
     * Simulates the specified turn. The hand already contains the card drawn
     * from the specified library for that turn.
     * 
     * @param turn
     *            the number of the turn being played, the first turn is 1
     * @param library
     *            the library being played with, the agent can manipulate the
     *            library if needed using its public methods
     * @param hand
     *            the current hand with the card drawn for the turn added to it,
     *            the agent can manipulate the hand if needed using its public
     *            methods
     */
    void takeTurn(int turn, Library<T> library, CardList<T> hand);

    /**
     * A new game has started
     */
    void newGame();

    /**
     * The current game has ended either due to the agent declining a turn, or
     * the library running out of cards
     */
    void gameDone();
}
