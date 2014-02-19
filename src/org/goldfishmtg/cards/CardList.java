package org.goldfishmtg.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.goldfishmtg.util.MutableInt;

/**
 * A general purpose list of cards
 *
 * @author skaspersen
 *
 * @param <T>
 *            the type use to represent cards, must implement the
 *            {@link Comparable} interface
 */
public class CardList<T extends Comparable<T>> {

    private final Map<T, MutableInt> cards;

    public CardList() {
        this.cards = new TreeMap<T, MutableInt>();
    }

    public CardList(CardList<T> cardList) {
        this.cards = new TreeMap<T, MutableInt>();
        Set<Entry<T, MutableInt>> es = cardList.cards.entrySet();
        for (Entry<T, MutableInt> entry : es) {
            T card = entry.getKey();
            int amount = entry.getValue().get();
            addCards(card, amount);
        }

    }

    /**
     * Returns the number of the specified card contained in this card list
     */
    public int getCount(T card) {
        if (card != null && this.cards.containsKey(card)) {
            return this.cards.get(card).get();
        }
        return 0;
    }

    /**
     * Returns the combined total of the specified cards contained in this card
     * list. For example if {@link #addCards(Comparable, int) addCards(a, 1)},
     * and {@link #addCards(Comparable, int) addCards(b, 2)} have been called on
     * this list of cards, {@link #getCount(a,b)} would return <code>3</code>.
     *
     * @param cards
     *            the cards to count
     * @return the combined total of the specified cards contained in this card
     *         list.
     */
    @SafeVarargs
    public final int getCount(T... cards) {
        int count = 0;
        for (T card : cards) {
            count += getCount(card);
        }
        return count;
    }

    /**
     * Returns the total amount of cards in this card list
     */
    public int size() {
        int count = 0;
        for (Entry<T, MutableInt> entry : this.cards.entrySet()) {
            count += entry.getValue().get();
        }
        return count;
    }

    /**
     * Ensures that 1 of the specified card is added to this card list
     *
     * @param card
     *            the card to be added
     * @throws IllegalArgumentException
     *             if <code>card</code> is null
     */
    public void addCard(T card) {
        addCards(card, 1);
    }

    /**
     * Ensures that the specified amount of cards are added to this card list
     *
     * @param card
     *            the card to be added
     * @param amount
     *            The amount of cards to be added
     * @throws IllegalArgumentException
     *             if <code>amount</code> is negative, or <code>card</code> is
     *             null
     */
    public void addCards(T card, int amount) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        if (amount > 0) {
            if (!this.cards.containsKey(card)) {
                this.cards.put(card, new MutableInt(amount));
            } else {
                this.cards.get(card).add(amount);
            }
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
    }

    /**
     * Ensures that one of the specified card is removed from this card list.
     * Returns <code>true</code> if more than one of the specified card was in
     * this card list (or equivalently, if this card list changed as a result of
     * the call).
     *
     *
     * @param card
     *            the card to be removed from this card list
     * @param amount
     *            The amount of cards to be added to this card list
     * @return <code>true</code> if this card list changed as a result of this
     *         call
     * @throws IllegalArgumentException
     *             if <code>card</code> is null
     */
    public boolean removeCard(T card) {
        return removeCards(card, 1);
    }

    /**
     * Ensures that the specified amount of cards are removed from this card
     * list. Returns <code>true</code> if an amount greater than or equal to the
     * specified amount of cards was present in this card list (or equivalently,
     * if this card list changed as a result of the call).
     *
     *
     * @param card
     *            the card to be removed from this card list
     * @param amount
     *            The amount of cards to be removed
     * @return <code>true</code> if this card list changed as a result of this
     *         call
     * @throws IllegalArgumentException
     *             if <code>amount</code> is negative, or <code>card</code> is
     *             null
     */
    public boolean removeCards(T card, int amount) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        if (amount > 0 && getCount(card) >= amount) {
            this.cards.get(card).add(-amount);
            return true;
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        int total = 0;
        StringBuilder bld = new StringBuilder();
        for (Entry<T, MutableInt> entry : this.cards.entrySet()) {
            T card = entry.getKey();
            int cardCount = entry.getValue().get();

            bld.append(cardCount).append(" x ").append(card);
            bld.append("\n");

            total += cardCount;
        }
        bld.append("Total ").append(total).append(" cards");
        return bld.toString();
    }

    /**
     * Returns a new list containing all the cards in this card list. For
     * example if this list only contains 2 of one specific card this method
     * would return a list with 2 elements in it, both being the specific card.
     */
    public List<T> asList() {
        List<T> list = new ArrayList<T>(size());
        for (Entry<T, MutableInt> entry : this.cards.entrySet()) {
            T card = entry.getKey();
            int cardCount = entry.getValue().get();
            for (int i = 0; i < cardCount; i++) {
                list.add(card);
            }
        }
        return list;
    }

    /**
     * Removes all of the cards in this card list
     */
    public void clear() {
        this.cards.clear();
    }

}
