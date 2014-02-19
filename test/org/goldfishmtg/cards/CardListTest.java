package org.goldfishmtg.cards;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CardListTest {

    private CardList<String> test;

    @Before
    public void before() {
        this.test = new CardList<String>();
    }

    @Test
    public void testAddCards1() {
        for (int i = 0; i < 10; i++) {
            String cardName = "" + i;
            this.test.addCards(cardName, i);
            Assert.assertEquals(i, this.test.getCount(cardName));
        }
    }

    @Test
    public void testAddCards2() {
        for (int i = 0; i < 10; i++) {
            String cardName = "" + i;
            this.test.addCards(cardName, i);
            this.test.addCards(cardName, i + 1);
            Assert.assertEquals(i + i + 1, this.test.getCount(cardName));
        }
    }

    @Test
    public void testRemoveCards1() {
        int added = 10;
        String cardName = "A card";

        this.test.addCards(cardName, added);

        Assert.assertTrue(this.test.removeCards(cardName, added));
        Assert.assertEquals(0, this.test.getCount(cardName));
    }

    @Test
    public void testRemoveCards2() {
        int added = 10;
        String cardName = "A card";

        this.test.addCards(cardName, added);

        Assert.assertFalse(this.test.removeCards(cardName, added + 1));
        Assert.assertEquals(added, this.test.getCount(cardName));
    }

    @Test
    public void testRemoveCards3() {
        int added = 10;
        String cardName = "A card";

        this.test.addCards(cardName, added);

        for (int i = added - 1; i >= 0; i--) {
            Assert.assertTrue(this.test.removeCards(cardName, 1));
            Assert.assertEquals(i, this.test.getCount(cardName));
        }
    }

    @Test
    public void removeCards4() {
        String cardName = "Not A card";

        Assert.assertFalse(this.test.removeCards(cardName, 1));
        Assert.assertEquals(0, this.test.getCount(cardName));
    }

    @Test
    public void testGetCount1() {
        String cardName1 = "Card 1";
        String cardName2 = "Card 2";

        this.test.addCards(cardName1, 10);
        this.test.addCards(cardName1, 15);

        Assert.assertEquals(10 + 15, this.test.getCount(cardName1, cardName2));
    }

    @Test
    public void testGetCount2() {
        String cardName1 = "Card 1";
        String cardName2 = "Card 2";
        String cardName3 = "Not a card";

        this.test.addCards(cardName1, 10);
        this.test.addCards(cardName1, 15);

        Assert.assertEquals(10 + 15,
                this.test.getCount(cardName1, cardName2, cardName3, null));
    }
}
