package com.marketdata.data;

/**
 * @author  Pradeep Muralidharan
 * Immutable object representing a pair of highest and second highest price
 */
public final class PricePair {

    private final Price highestPrice;

    private final Price secondHighestPrice;

    private final boolean updated;

    public PricePair(Price highestPrice, Price secondHighestPrice, boolean updated){
        this.highestPrice = highestPrice;
        this.secondHighestPrice = secondHighestPrice;
        this.updated = updated;
    }

    public Price getHighestPrice(){
        return this.highestPrice;
    }

    public Price getSecondHighestPrice(){
        return this.secondHighestPrice;
    }

    /**
     * rules to check if the given price is higher than current highest price and current second highest price
     * and returns a new immutable object of the current highest and lowest price.
     * @param price
     * @return
     */
    public PricePair comparAndReturnAppropriate(Price price){
        if(isValidPrice(price)){
            if(getHighestPrice() == null) return new PricePair(price, null,true);
            else if(getHighestPrice().getPrice() <= price.getPrice()) return new PricePair(price, getHighestPrice(),true);
            else if(getSecondHighestPrice() == null) return new PricePair(getHighestPrice(),price,true);
            else if(getSecondHighestPrice().getPrice() <= price.getPrice()) return new PricePair(getHighestPrice(), price,true);
            else return new PricePair(getHighestPrice(),getSecondHighestPrice(),false);
        }
        return  new PricePair(getHighestPrice(),getSecondHighestPrice(),false);
    }

    /**
     * Checks if the price is valid
     * @param price
     * @return
     */
    private boolean isValidPrice(Price price){
        return (price == null) ? false : true;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof PricePair) || obj == null ) return false;
        PricePair p = (PricePair) obj;
        return p.getHighestPrice().equals(this.getHighestPrice()) && p.getSecondHighestPrice().equals(this.getSecondHighestPrice());
    }
    }