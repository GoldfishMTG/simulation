package org.goldfishmtg.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Acts as a library to draw cards from. Also provides mechanisms for tutoring
 * and placing cards on the top or bottom of the library
 *
 * @author skaspersen
 *
 * @param <T>
 *            the type use to represent cards, must implement the
 *            {@link Comparable} interface
 */
public class Library<T extends Comparable<T>> {

    private final CardList<T> cards;
    private final Random rng;
    private List<T> list;
    private List<T> drawn;
    private LinkedList<T> top;
    private LinkedList<T> bottom;

    /**
     * Creates a new library initially containing all the cards in the specified
     * card list
     *
     * @param cards
     *            the initial list of cards
     */
    public Library(CardList<T> cards) {
        this(new Random(), cards);
    }

    /**
     * Creates a new library initially containing all the cards in the specified
     * card list
     *
     *
     *
     * @param rng
     *            the random number generator to use
     * @param cards
     *            the initial list of cards
     */
    public Library(Random rng, CardList<T> cards) {
        if (rng == null) {
            throw new IllegalArgumentException("random cannot be null");
        }
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }
        this.cards = cards;
        this.rng = rng;

        int totalCards = this.cards.size();

        this.list = new LinkedList<T>();
        this.drawn = new ArrayList<T>(totalCards);
        this.top = new LinkedList<T>();
        this.bottom = new LinkedList<T>();

        reset();
    }

    /**
     * Resets this library to its original state, this equates to adding all
     * cards back into this library and shuffling it
     */
    public void reset() {
        this.list.clear();
        this.top.clear();
        this.bottom.clear();
        this.drawn.clear();
        this.list.addAll(this.cards.asList());
    }

    /**
     * Shuffles this library. Cards that were placed on the top or bottom of the
     * deck are no longer guaranteed to be drawn in a predictable order. This
     * does not add drawn cards back to this library.
     */
    public void shuffle() {
        this.list.addAll(this.top);
        this.top.clear();

        this.list.addAll(this.bottom);
        this.bottom.clear();
    }

    /**
     * Draws a card from this library. Returns in order of preference the last
     * card to be placed on top, a card at random that has not been placed on
     * the bottom, or the first card that was placed on the bottom.
     *
     * @return the card on top of this library, <code>null</code> if there were
     *         no cards left
     */
    public T draw() {
        T card;
        if (this.top.size() > 0) {
            card = this.top.removeFirst();
        } else if (this.list.size() > 0) {
            int cardIndex = this.rng.nextInt(this.list.size());
            card = this.list.remove(cardIndex);
        } else if (this.bottom.size() > 0) {
            card = this.bottom.removeFirst();
        } else {
            card = null;
        }
        if (card != null) {
            this.drawn.add(card);
        }
        return card;
    }

    /**
     * Searches for the specified card in this library and shuffles it. Returns
     * the specified card if it was present in this library.
     *
     * @param card
     *            the card to tutor for
     * @return the specified card if it was present in this library, otherwise
     *         <code>null</code>
     */
    public T tutor(T card) {
        shuffle();
        if (this.list.remove(card)) {
            this.drawn.add(card);
            return card;
        } else {
            return null;
        }
    }

    /**
     * Adds the specified card to the top of this library. The next card drawn
     * will be the specified card
     *
     * @param card
     *            the card to place on top of this library
     * @throws IllegalArgumentException
     *             if the card was never drawn from this library
     */
    public final void top(T card) throws IllegalArgumentException {
        if (this.drawn.remove(card)) {
            this.top.addFirst(card);
        } else {
            throw cardNotDrawn(card);
        }
    }

    /**
     * Adds the specified cards to the top of this library. The first card drawn
     * will be the first card specified, the second card drawn will be the
     * second card specified, and so on
     *
     * @param cards
     *            the cards to put on the top of this library, the first card
     *            specified will be the card on top of this library
     * @throws IllegalArgumentException
     *             if any of the cards were never drawn from this library
     */
    @SafeVarargs
    public final void top(T... cards) throws IllegalArgumentException {
        for (T card : cards) {
            if (!this.drawn.contains(card)) {
                throw cardNotDrawn(card);
            }
        }
        for (int i = cards.length - 1; i >= 0; i--) {
            T card = cards[i];
            top(card);
        }
    }

    /**
     * Adds the specified card to the bottom of this library. The last card
     * drawn from this library will be the specified card
     *
     * @param card
     *            the card to place at the bottom of this library
     * @throws IllegalArgumentException
     *             if the card was never drawn from this library
     */
    public void bottom(T card) {
        if (this.drawn.remove(card)) {
            this.bottom.addLast(card);
        } else {
            throw cardNotDrawn(card);
        }
    }

    /**
     * Adds the specified card to the bottom of this library. The last card
     * drawn from this library will be the last card specified, the second last
     * card drawn from this library will be the second last card specified, and
     * so on
     *
     * @param card
     *            the card to place at the bottom of this library
     * @throws IllegalArgumentException
     *             if any of the cards were never drawn from this library
     */
    @SafeVarargs
    public final void bottom(T... cards) {
        for (T card : cards) {
            if (!this.drawn.contains(card)) {
                throw cardNotDrawn(card);
            }
        }
        for (T card : cards) {
            bottom(card);
        }
    }

    /**
     * Returns the number of cards left to draw from this library
     */
    public int cardsRemaining() {
        return this.cards.size() - this.drawn.size();
    }

    private IllegalArgumentException cardNotDrawn(T card) {
        String message = "Card '" + card + "' was never drawn";
        throw new IllegalArgumentException(message);
    }

    public CardList<T> getCards() {
        return this.cards;
    }

}
