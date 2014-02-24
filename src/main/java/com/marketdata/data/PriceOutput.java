package com.marketdata.data;

/**
 * @author Pradeep Muralidharan
 *
 * Immutable object representing the price output to be printed
 */
public class PriceOutput {

    private final Price price;

    private final int revision;

    public PriceOutput(Price price, int revision) {
        this.price = price;
        this.revision = revision;
    }

    public int getRevision(){
        return this.revision;
    }

    public Price getPrice(){
        return this.price;
    }

    @Override
    public String toString(){
        return revision+" "+this.price.getUnderlying()+" : "+this.getPrice().getPrice();
    }
}
