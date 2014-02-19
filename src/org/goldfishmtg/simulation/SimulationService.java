package org.goldfishmtg.simulation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.goldfishmtg.cards.CardList;
import org.goldfishmtg.cards.Library;

/**
 * A service for submitting agents and decks to goldfish them against. The
 * service is backed by a thread pool having one thread per available processor
 * on the machine
 *
 * @author skaspersen
 *
 * @param <T>
 *            the type use to represent cards, must implement the
 *            {@link Comparable} interface
 */
public class SimulationService<T extends Comparable<T>> {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final ExecutorService pool;
    private final ExecutorCompletionService<Agent<T>> completionService;

    private int defaultNumberOfGames;
    private boolean defaultSkipDrawStep;

    private int pending;

    /**
     * Creates a new simulation service backed by a thread per available
     * processors. The default number of games will be 1, and the first draw
     * step will not be skipped.
     */
    public SimulationService() {
        int cores = Runtime.getRuntime().availableProcessors();
        this.pool = Executors.newFixedThreadPool(cores);
        this.completionService = new ExecutorCompletionService<>(this.pool);
        this.pending = 0;
        this.logger.log(Level.INFO, "New Simulation Service[threadCount="
                + cores + "]");
        this.defaultNumberOfGames = 1;
        this.defaultSkipDrawStep = false;
    }

    /**
     * The number of games that will be simulated if the number of games has not
     * been specified. The default number of games is 1.
     *
     * @return the number of games that will be simulated by default
     */
    public int getDefaultNumberOfGames() {
        return this.defaultNumberOfGames;
    }

    /**
     * The number of games that will be simulated if the number of games has not
     * been specified. The default number of games is 1.
     *
     */
    public void setDefaultNumberOfGames(int defaultTurns) {
        this.defaultNumberOfGames = defaultTurns;
    }

    /**
     * Whether or not the draw step will be skipped for each game if not
     * specified. The default behaviour is to use the first draw step
     *
     * @return <code>true</code> if the draw step will be skipped for
     *         simulations
     */
    public boolean isDefaultSkipDrawStep() {
        return this.defaultSkipDrawStep;
    }

    /**
     * Whether or not the draw step will be skipped for each game if not
     * specified. The default behaviour is to use the first draw step
     *
     */
    public void setDefaultSkipDrawStep(boolean defaultSkipDrawStep) {
        this.defaultSkipDrawStep = defaultSkipDrawStep;
    }

    /**
     * Simulates the default number of games, skipping the first draw step if
     * {@link #isDefaultSkipDrawStep()} returns true
     *
     * @param cardList
     *            the list of cards that makes up the library
     * @param agent
     *            the agent observing and controlling the game
     *
     * @see #getDefaultNumberOfGames()
     * @see #isDefaultSkipDrawStep()
     */
    public void simulate(CardList<T> cardList, Agent<T> agent) {
        simulate(cardList, agent, this.defaultNumberOfGames,
                this.defaultSkipDrawStep);
    }

    /**
     * Simulates the specified number of games, skipping the first draw step if
     * {@link #isDefaultSkipDrawStep()} returns true
     *
     * @param cardList
     *            the list of cards that makes up the library
     * @param agent
     *            the agent observing and controlling the game
     * @param numberOfGames
     *            the number of games to simulate
     *
     * @see #isDefaultSkipDrawStep()
     */
    public void simulate(CardList<T> cardList, Agent<T> agent, int numberOfGames) {
        simulate(cardList, agent, numberOfGames, this.defaultSkipDrawStep);
    }

    /**
     * Simulates the default number of games, skipping the draw step as
     * specified
     *
     * @param cardList
     *            the list of cards that makes up the library
     * @param agent
     *            the agent observing and controlling the game
     * @param skipFirstDrawStep
     *            <code>true</code> if the first draw step is to be skipped
     *
     * @see #getDefaultNumberOfGames()
     */

    public void simulate(CardList<T> cardList, Agent<T> agent,
            boolean skipFirstDrawStep) {
        simulate(cardList, agent, this.defaultNumberOfGames, skipFirstDrawStep);
    }

    /**
     * Simulates the specified number of games, skipping the draw step as
     * specified
     *
     * @param cardList
     *            the list of cards that makes up the library
     * @param agent
     *            the agent observing and controlling the game
     * @param numberOfGames
     *            the number of games to simulate
     * @param skipFirstDrawStep
     *            <code>true</code> if the first draw step is to be skipped
     */
    public void simulate(CardList<T> cardList, Agent<T> agent,
            int numberOfGames, boolean skipFirstDrawStep) {

        Library<T> library = new Library<>(cardList);

        Goldfish<T, Agent<T>> goldfish = new Goldfish<>(library, agent);
        goldfish.setGames(numberOfGames);
        goldfish.setSkipFirstDrawStep(skipFirstDrawStep);

        this.completionService.submit(goldfish);
        this.pending++;

        this.logger.log(Level.INFO, "New simulation added[games="
                + numberOfGames + ", skipFirstDrawstep=" + skipFirstDrawStep
                + "], There are " + this.pending + " pending simulations.");
    }

    /**
     * The number of agents that can be retrieved from this service
     *
     * @see #retrieveNextCompleted()
     */
    public int getRemaining() {
        return this.pending;
    }

    /**
     * Returns the next agent that has completed simulation. If there are no
     * agents that have had their simulation completed this method will wait
     * until one is completed. If there are no simulations running, and no
     * agents waiting to be retrieved returns null
     *
     * @return the next agent that has completed simulation
     * @throws ExecutionException
     *             if interrupted while waiting
     * @see #getRemaining()
     */
    public Agent<T> retrieveNextCompleted() throws InterruptedException,
            ExecutionException {
        if (this.pending > 0) {
            try {
                return this.completionService.take().get();
            } finally {
                this.pending--;
            }
        } else {
            return null;
        }
    }

    /**
     * Shuts down the service
     */
    public void shutdown() {
        this.pool.shutdown();
    }
}
