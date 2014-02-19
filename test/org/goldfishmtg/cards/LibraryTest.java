package org.goldfishmtg.cards;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class LibraryTest {

    private CardList<String> cards;

    @Before
    public void before() {
        this.cards = new CardList<String>();
    }

    @Test
    public void testDraw1() {
        int librarySize = 10;
        this.cards.addCards("Card", librarySize);
        Library<String> test = new Library<>(this.cards);

        for (int i = librarySize - 1; i >= 0; i--) {
            Assert.assertEquals("Card", test.draw());
            Assert.assertEquals(i, test.cardsRemaining());
        }
        Assert.assertEquals(null, test.draw());
    }

    @Test
    public void testDraw2() {
        int librarySize = 10;
        int typeACount = 7;
        int typeBCount = librarySize - typeACount;
        this.cards.addCards("Type A", typeACount);
        this.cards.addCards("Type B", typeBCount);
        Library<String> test = new Library<>(this.cards);

        for (int i = librarySize - 1; i >= 0; i--) {
            String card = test.draw();
            if (card.equals("Type A")) {
                typeACount--;
            } else if (card.equals("Type B")) {
                typeBCount--;
            }
            Assert.assertEquals(i, test.cardsRemaining());
        }
        Assert.assertEquals(0, typeACount);
        Assert.assertEquals(0, typeBCount);
        Assert.assertEquals(null, test.draw());
    }

    @Test
    public void testReset1() {
        int librarySize = 10;
        this.cards.addCards("Card", librarySize);
        Library<String> test = new Library<>(this.cards);
        test.draw();

        test.reset();

        Assert.assertEquals(librarySize, test.cardsRemaining());
        for (int i = 0; i < librarySize; i++) {
            Assert.assertEquals("Card", test.draw());
        }
    }

    @Test
    public void testReset2() {
        int librarySize = 100;
        this.cards.addCards("Card", librarySize - 1);
        this.cards.addCard("Top");

        int i;
        for (i = 0; i < Short.MAX_VALUE; i++) {
            Library<String> test = new Library<>(this.cards);
            test.top(test.tutor("Top"));
            test.reset();
            if (!test.draw().equals("Top")) {
                break;
            }
        }
        /*
         * It is possible for Top to be the first card but Short.MAX_VALUE times
         * in a row is very unlikely
         */
        Assert.assertTrue(i < Short.MAX_VALUE);
    }

    @Test
    public void testReset3() {
        int librarySize = 100;
        this.cards.addCard("Bottom");
        this.cards.addCards("Card", librarySize - 1);

        int i;
        for (i = 0; i < Short.MAX_VALUE; i++) {
            Library<String> test = new Library<>(this.cards);
            test.bottom(test.tutor("Bottom"));
            test.reset();
            for (int draw = 1; draw < librarySize; draw++) {
                test.draw();
            }
            if (!test.draw().equals("Bottom")) {
                break;
            }
        }
        /*
         * It is possible for Bottom to be the last card but Short.MAX_VALUE
         * times in a row is very unlikely
         */
        Assert.assertTrue(i < Short.MAX_VALUE);
    }

    @Test
    public void testShuffle1() {
        int librarySize = 1000;
        this.cards.addCards("Card", librarySize - 1);
        this.cards.addCard("Top");

        int i;
        for (i = 0; i < Short.MAX_VALUE; i++) {
            Library<String> test = new Library<>(this.cards);
            test.top(test.tutor("Top"));
            test.shuffle();
            if (!test.draw().equals("Top")) {
                break;
            }
        }
        /*
         * It is possible for Top to be the first card but Short.MAX_VALUE times
         * in a row is very unlikely
         */
        Assert.assertTrue(i < Short.MAX_VALUE);
    }

    @Test
    public void testShuffle2() {
        int librarySize = 100;
        this.cards.addCard("Bottom");
        this.cards.addCards("Card", librarySize - 1);

        int i;
        for (i = 0; i < Short.MAX_VALUE; i++) {
            Library<String> test = new Library<>(this.cards);
            test.bottom(test.tutor("Bottom"));
            test.shuffle();
            for (int draw = 1; draw < librarySize; draw++) {
                test.draw();
            }
            if (!test.draw().equals("Bottom")) {
                break;
            }
        }
        /*
         * It is possible for Bottom to be the last card but Short.MAX_VALUE
         * times in a row is very unlikely
         */
        Assert.assertTrue(i < Short.MAX_VALUE);
    }

    @Test
    public void testTutor1() {
        int librarySize = 60;
        this.cards.addCards("Card", librarySize - 1);
        this.cards.addCard("Target");

        // Loop just in case the target was drawn at random
        for (int i = 0; i < 10; i++) {
            Library<String> test = new Library<>(this.cards);
            Assert.assertEquals("Target", test.tutor("Target"));
            Assert.assertEquals(librarySize - 1, test.cardsRemaining());
            while (test.cardsRemaining() > 0) {
                Assert.assertFalse("Target".equals(test.draw()));
            }
        }
    }

    @Test
    public void testTutor2() {
        int librarySize = 60;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card " + i);
        }

        for (int i = 0; i < 10; i++) {
            Library<String> test = new Library<>(this.cards);
            String target = test.draw();
            test.top(target);

            Assert.assertEquals(target, test.tutor(target));
            Assert.assertEquals(librarySize - 1, test.cardsRemaining());
            while (test.cardsRemaining() > 0) {
                Assert.assertFalse(target.equals(test.draw()));
            }
        }
    }

    @Test
    public void testTutor3() {
        int librarySize = 60;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card " + i);
        }

        for (int i = 0; i < 10; i++) {
            Library<String> test = new Library<>(this.cards);
            String target = test.draw();
            test.bottom(target);

            Assert.assertEquals(target, test.tutor(target));
            Assert.assertEquals(librarySize - 1, test.cardsRemaining());
            while (test.cardsRemaining() > 0) {
                Assert.assertFalse(target.equals(test.draw()));
            }
        }
    }

    @Test
    public void testTopT1() {
        int librarySize = 15;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }

        Library<String> test = new Library<>(this.cards);
        while (test.cardsRemaining() > 0) {
            String card = test.draw();
            test.top(card);
            Assert.assertEquals(card, test.draw());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTopT2() {
        int librarySize = 15;
        this.cards.addCards("Card", librarySize);

        Library<String> test = new Library<>(this.cards);
        test.top("Not a card");
    }

    @Test
    public void testTopTArray1() {
        int librarySize = 15;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }

        Library<String> test = new Library<>(this.cards);
        while (test.cardsRemaining() > 3) {
            String card1 = test.draw();
            String card2 = test.draw();
            String card3 = test.draw();
            test.top(card1, card2, card3);
            Assert.assertEquals(card1, test.draw());
            Assert.assertEquals(card2, test.draw());
            Assert.assertEquals(card3, test.draw());
        }
    }

    @Test
    public void testTopTArray2() {
        int librarySize = 15;
        this.cards.addCards("Card", librarySize);

        Library<String> test = new Library<>(this.cards);
        while (test.cardsRemaining() > 3) {
            String card1 = test.draw();
            String card2 = test.draw();
            String card3 = test.draw();
            test.top(card1, card2, card3);
            Assert.assertEquals(card1, test.draw());
            Assert.assertEquals(card2, test.draw());
            Assert.assertEquals(card3, test.draw());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTopTArray3() {
        int librarySize = 15;
        this.cards.addCards("Card", librarySize);

        Library<String> test = new Library<>(this.cards);
        String card1 = test.draw();
        String card2 = test.draw();
        String card3 = test.draw();
        int expectedSize = test.cardsRemaining();
        try {
            test.top(card1, card2, card3, "Not a Card");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(expectedSize, test.cardsRemaining());
            throw e;
        }

    }

    @Test
    public void testBottomT1() {
        int librarySize = 5;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }
        Library<String> test = new Library<>(this.cards);
        List<String> expectedOrder = new ArrayList<>(librarySize);
        for (int i = 0; i < librarySize; i++) {
            String card = test.draw();
            expectedOrder.add(i, card);
            test.bottom(card);
        }

        for (String expected : expectedOrder) {
            Assert.assertEquals(expected, test.draw());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBottomT2() {
        int librarySize = 5;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }

        Library<String> test = new Library<>(this.cards);

        test.bottom("Not a card");
    }

    @Test
    public void testBottomTArray1() {
        int librarySize = 15;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }
        Library<String> test = new Library<>(this.cards);
        List<String> expectedOrder = new ArrayList<>(librarySize);
        for (int i = 0; i < librarySize; i += 3) {
            String card1 = test.draw();
            String card2 = test.draw();
            String card3 = test.draw();

            expectedOrder.add(i, card1);
            expectedOrder.add(i + 1, card2);
            expectedOrder.add(i + 2, card3);

            test.bottom(card1, card2, card3);
        }

        for (String expected : expectedOrder) {
            Assert.assertEquals(expected, test.draw());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBottomTArray2() {
        int librarySize = 5;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }

        Library<String> test = new Library<>(this.cards);

        String card1 = test.draw();
        String card2 = test.draw();
        String card3 = test.draw();
        int expectedSize = test.cardsRemaining();
        try {
            test.bottom(card1, card2, card3, "Not a card");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(expectedSize, test.cardsRemaining());
            throw e;
        }
    }

    @Test
    public void testCardsRemaining() {
        int librarySize = 15;
        for (int i = 0; i < librarySize; i++) {
            this.cards.addCard("Card" + i);
        }

        Library<String> test = new Library<>(this.cards);
        for (int i = librarySize - 1; i > 0; i--) {
            test.draw();
            Assert.assertEquals(i, test.cardsRemaining());
        }
    }

}
