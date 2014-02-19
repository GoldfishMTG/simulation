package org.goldfishmtg.simulation;

import junit.framework.Assert;

import org.goldfishmtg.cards.CardList;
import org.goldfishmtg.cards.Library;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class GoldfishTest {

    private CardList<String> cards;
    private Agent<String> agent;
    private ArgumentCaptor<CardList> cardArg;

    @Before
    public void before() {
        this.cards = new CardList<String>();
        this.agent = Mockito.mock(Agent.class);
        // Always keep the offered opening hand
        Mockito.when(
                this.agent.keepOpeningHand(Mockito.anyInt(),
                        Mockito.any(CardList.class))).thenReturn(Boolean.TRUE);
        // Always say no to another turn
        Mockito.when(this.agent.simulateAnotherTurn())
                .thenReturn(Boolean.FALSE);

        this.cardArg = ArgumentCaptor.forClass(CardList.class);
    }

    /**
     * By default on the draw
     */
    @Test
    public void testSetSkipFirstDrawStep1() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        Library<String> library = new Library<>(this.cards);

        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);
        test.call();

        Mockito.verify(this.agent).takeTurn(Mockito.eq(1), Mockito.eq(library),
                this.cardArg.capture());
        Assert.assertEquals(8, this.cardArg.getValue().size());
    }

    /**
     * Setting on the play
     */
    @Test
    public void testSetSkipFirstDrawStep2() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        Library<String> library = new Library<>(this.cards);

        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);
        test.setSkipFirstDrawStep(true);
        test.call();

        Mockito.verify(this.agent).takeTurn(Mockito.eq(1), Mockito.eq(library),
                this.cardArg.capture());
        Assert.assertEquals(7, this.cardArg.getValue().size());
    }

    /**
     * Setting on the draw
     */
    @Test
    public void testSetSkipFirstDrawStep3() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        Library<String> library = new Library<>(this.cards);
        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);

        test.setSkipFirstDrawStep(true);
        test.setSkipFirstDrawStep(false);
        test.call();

        Mockito.verify(this.agent).takeTurn(Mockito.eq(1), Mockito.eq(library),
                this.cardArg.capture());
        Assert.assertEquals(8, this.cardArg.getValue().size());
    }

    /**
     * Default one game
     */
    @Test
    public void testSetGames1() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        this.cards.addCards("Card to innitate second turn", 1);
        Library<String> library = new Library<>(this.cards);

        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);
        test.call();

        Mockito.verify(this.agent).keepOpeningHand(Mockito.eq(7),
                Mockito.any(CardList.class));
        Mockito.verify(this.agent).newGame();
        Mockito.verify(this.agent).takeTurn(Mockito.eq(1), Mockito.eq(library),
                Mockito.any(CardList.class));
        Mockito.verify(this.agent).gameDone();
        Mockito.verify(this.agent).simulateAnotherTurn();
        Mockito.verifyNoMoreInteractions(this.agent);

    }

    /**
     * Changing the number of games
     */
    @Test
    public void testSetGames2() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        this.cards.addCards("Card to innitate second turn", 1);
        Library<String> library = new Library<>(this.cards);

        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);
        test.setGames(100);
        test.call();

        Mockito.verify(this.agent, Mockito.times(100)).keepOpeningHand(
                Mockito.eq(7), Mockito.any(CardList.class));
        Mockito.verify(this.agent, Mockito.times(100)).newGame();
        Mockito.verify(this.agent, Mockito.times(100)).takeTurn(Mockito.eq(1),
                Mockito.eq(library), Mockito.any(CardList.class));
        Mockito.verify(this.agent, Mockito.times(100)).gameDone();
        Mockito.verify(this.agent, Mockito.times(100)).simulateAnotherTurn();
        Mockito.verifyNoMoreInteractions(this.agent);
    }

    @Test
    public void testCall() throws Exception {
        this.cards.addCards("Enough cards for opening hand", 7);
        this.cards.addCards("Card for draw step", 1);
        this.cards.addCards("Card to innitate second turn", 1);
        Library<String> library = new Library<>(this.cards);

        Goldfish<String, Agent<String>> test = new Goldfish<String, Agent<String>>(
                library, this.agent);
        test.setGames(100);
        Agent<String> actualAgent = test.call();

        Assert.assertSame(this.agent, actualAgent);
    }

}
