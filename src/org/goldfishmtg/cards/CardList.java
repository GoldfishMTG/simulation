package org.goldfishmtg.cards;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.goldfishmtg.util.MutableInt;

/**
 * A general purpose list of cards
 *
 * @author skaspersen
 *
 * @param <T>
 *            the type of cards in this collection, must implement the
 *            {@link Comparable} interface
 */
public class CardList<T extends Comparable<T>> {

	private final Map<T, MutableInt> cards;

	public CardList() {
		cards = new LinkedHashMap<T, MutableInt>();
	}

	/**
	 * Returns the number of the specified card contained in this card list
	 */
	public int getCount(T card) {
		if (cards.containsKey(card)) {
			return cards.get(card).get();
		}
		return 0;
	}

	/**
	 * Returns the combined total of the specified cards contained in this card
	 * list. For example if {@link #addCards(Comparable, int) addCards(a, 1)},
	 * and {@link #addCards(Comparable, int) addCards(b, 2)} have been called on
	 * this list of cards, {@link #getCount(a,b)} would return <code>3</code>.
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
		for (Entry<T, MutableInt> entry : cards.entrySet()) {
			count += entry.getValue().get();
		}
		return count;
	}

	/**
	 * Ensures that the specified amount of cards are added to this card list
	 *
	 * @param card
	 *            the card to be added to this card list
	 * @param amount
	 *            The amount of cards to be added to this card list
	 * @throws IllegalArgumentException
	 *             if <code>amount</code> is negative
	 */
	public void addCards(T card, int amount) {
		if (amount > 0) {
			if (!cards.containsKey(card)) {
				cards.put(card, new MutableInt(amount));
			} else {
				cards.get(card).add(amount);
			}
		} else if (amount < 0) {
			throw new IllegalArgumentException("amount cannot be negative");
		}
	}

	/**
	 * Ensures that the specified amount of cards are removed from this card
	 * list. Returns true if an amount greater than or equal to the specified
	 * amount of cards was present in the card list (or equivalently, if this
	 * card list changed as a result of the call).
	 *
	 *
	 * @param card
	 *            the card to be added to this card list
	 * @param amount
	 *            The amount of cards to be added to this card list
	 * @return <code>true</code> if this card list changed as a result of this
	 *         call
	 * @throws IllegalArgumentException
	 *             if <code>amount</code> is negative
	 */
	public boolean removeCards(T card, int amount) {
		if (amount > 0 && getCount(card) >= amount) {
			cards.get(card).add(-amount);
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
		for (Entry<T, MutableInt> entry : cards.entrySet()) {
			T card = entry.getKey();
			int cardCount = entry.getValue().get();

			bld.append(cardCount).append(" x ").append(card);
			bld.append("\n");

			total += cardCount;
		}
		bld.append("Total ").append(total).append(" cards");
		return bld.toString();
	}

}
