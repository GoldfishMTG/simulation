package org.goldfishmtg.util;

/**
 * An int value that may be updated.
 *
 * @author skaspersen
 *
 */
public class MutableInt {
    private int value;

    /**
     * Creates a new MutableInt with an initial value of 0
     *
     */
    public MutableInt() {
        this(0);
    }

    /**
     * Creates a new MutableInt with the given initial value.
     *
     * @param initialValue
     *            the initial value
     */
    public MutableInt(int intitialValue) {
        this.value = intitialValue;
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public int get() {
        return this.value;
    }

    /**
     * Adds the given value to the current value.
     *
     * @param delta
     *            the value to add
     */
    public void add(int delta) {
        this.value += delta;
    }

    /**
     * Sets to the given value
     *
     * @param newValue
     *            the new value
     */
    public void set(int newValue) {
        this.value = newValue;
    }

}
