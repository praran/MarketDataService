package com.marketdata.data;

import junit.framework.TestCase;

/**
 * @author Pradeep Muralidharan
 */
public class PricePairTests extends TestCase{

    // SUT
    PricePair pricePair;

    public void test_comparAndReturnAppropriate_when_null_supplied(){
        pricePair = new PricePair(new PriceImpl("FTSE",1000d),new PriceImpl("FTSE",200d),false);
        PricePair npp = pricePair.comparAndReturnAppropriate(null);
        assertTrue(npp.equals(pricePair));
    }

    public void test_comparAndReturnAppropriate_when_price_lower_than_second_price_supplied(){
        pricePair = new PricePair(new PriceImpl("FTSE",1000d),new PriceImpl("FTSE",200d),false);
        PricePair npp = pricePair.comparAndReturnAppropriate(new PriceImpl("FTSE",100d));
        assertTrue(npp.equals(pricePair));

    }

    public void test_comparAndReturnAppropriate_when_price_higher_than_highest_price_supplied(){
        pricePair = new PricePair(new PriceImpl("FTSE",1000d),new PriceImpl("FTSE",200d),false);
        PricePair npp = pricePair.comparAndReturnAppropriate(new PriceImpl("FTSE",1001d));
        assertFalse(npp.equals(pricePair));
        assertTrue(npp.getHighestPrice().getPrice() == 1001d);
        assertTrue(npp.getSecondHighestPrice().getPrice() == 1000d);
    }

    public void test_comparAndReturnAppropriate_when_price_higher_than_second_price_supplied(){
        pricePair = new PricePair(new PriceImpl("FTSE",1000d),new PriceImpl("FTSE",200d),false);
        PricePair npp = pricePair.comparAndReturnAppropriate(new PriceImpl("FTSE",201d));
        assertFalse(npp.equals(pricePair));
        assertTrue(npp.getHighestPrice().getPrice() == 1000d);
        assertTrue(npp.getSecondHighestPrice().getPrice() == 201d);
    }

    public void test_comparAndReturnAppropriate_immutability_of_price_pair(){
        pricePair = new PricePair(new PriceImpl("FTSE",1000d),new PriceImpl("FTSE",200d),false);
        PricePair npp = pricePair.comparAndReturnAppropriate(null);
        assertFalse(pricePair == npp);
    }
}
