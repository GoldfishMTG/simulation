package org.goldfishmtg.simulation;

import java.util.concurrent.Callable;

import org.goldfishmtg.cards.CardList;
import org.goldfishmtg.cards.Library;

/**
 * Simulates games for a library using a supplied agent.
 * <p>
 * A Goldfish instance will iterate over a number of games performing the work
 * of `shuffling` the library before each game, drawing the opening hand, and
 * drawing the first card for each turn.
 * <p>
 * The supplied agent acts as both an observer and controller for the simulation
 *
 * @author skaspersen
 *
 * @param <T>
 *            the type use to represent cards, must implement the
 *            {@link Comparable} interface
 * @param <A>
 *            the agent used to make game decisions
 *
 * @see Agent
 */
public class Goldfish<T extends Comparable<T>, A extends Agent<T>> implements
        Callable<A> {

    private final Library<T> library;
    private final A agent;
    private int gameCount;
    private boolean skipFirstDrawStep;

    /**
     * Creates a new simulation for the specified library and agent. By default
     * the agent will not skip the first draw step and will simulate 1 game
     *
     * @param library
     *            the library for the simulation, if using threads make sure
     *            that this library is not shared by another goldfish instance
     * @param agent
     *            the agent used to affect the game, if using threads make sure
     *            that this agent is not shared by another goldfish instance
     *
     * @see #setSkipFirstDrawStep(boolean)
     * @see #setGames(int)
     */
    public Goldfish(Library<T> library, A agent) {
        this.agent = agent;
        this.library = library;
        this.skipFirstDrawStep = false;
        this.gameCount = 1;
    }

    /**
     * Changes whether the games are simulated on the draw or on the play. By
     * default this is set to false.
     *
     * @param skip
     *            <code>true</code> if the simulations are on the play and
     *            should skip the first draw step
     */
    public void setSkipFirstDrawStep(boolean skip) {
        this.skipFirstDrawStep = skip;
    }

    /**
     * Changes the number of games to simulate. The default value is 1.
     */
    public void setGames(int gameCount) {
        this.gameCount = gameCount;
    }

    @Override
    public A call() throws Exception {
        this.agent.simulationStarted();
        for (int i = 0; i < this.gameCount; i++) {
            this.agent.newGame();
            CardList<T> hand = drawOpeningHand();

            int turn = 1;
            if (!this.skipFirstDrawStep) {
                hand.addCard(this.library.draw());
            }
            this.agent.takeTurn(turn, this.library, hand);

            for (turn = 2; shouldPlayNextTurn(); turn++) {
                hand.addCard(this.library.draw());
                this.agent.takeTurn(turn, this.library, hand);
            }
            this.agent.gameDone();
        }
        this.agent.simulationDone();
        return this.agent;

    }

    private boolean shouldPlayNextTurn() {
        return this.library.cardsRemaining() > 0
                && this.agent.simulateAnotherTurn();
    }

    private CardList<T> drawOpeningHand() {
        CardList<T> drawn = new CardList<T>();
        this.library.reset();
        for (int cardCount = 7; cardCount > 0; cardCount--) {
            for (int i = 0; i < cardCount; i++) {
                drawn.addCard(this.library.draw());
            }
            if (this.agent.keepOpeningHand(cardCount, drawn)) {
                return drawn;
            } else {
                // Add the drawn cards back into the library
                drawn.clear();
                this.library.reset();
            }
        }
        return drawn;
    }

}
