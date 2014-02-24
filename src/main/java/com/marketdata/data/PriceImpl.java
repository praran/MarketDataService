package com.marketdata.data;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Pradeep Muralidharan
 *
 * Immutable object of price
 */
public class PriceImpl implements Price {

    private final String underlying;

    private final double price;

    public PriceImpl(String underlying, double price){
        this.underlying = underlying;
        this.price = price;
    }

    @Override
    public String getUnderlying() {
        return this.underlying;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public String toString(){
        return underlying+" : "+ price;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PriceImpl) || obj == null ) return false;
        PriceImpl p = (PriceImpl) obj;
       return StringUtils.equals(p.getUnderlying(), this.getUnderlying()) && (p.getPrice() == this.getPrice());
    }
}
