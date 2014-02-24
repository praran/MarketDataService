package com.marketdata.service;

import com.marketdata.Exception.InvalidRequestException;
import com.marketdata.data.Price;

/**
 * @author Pradeep Muralidharan
 */
public final class PriceServiceImpl extends AbstractPriceServiceImpl {

    /**
     * Default constructor
     */
    public PriceServiceImpl(){
        super();
    }

    /**
     * Constructor with no of undelryings and queuesize specified
     * @param noOfUnderlyings
     * @param queueSize
     */
    public PriceServiceImpl(int noOfUnderlyings,int queueSize){
       super(noOfUnderlyings,queueSize);
    }

    /**
     * Checks if the price is valid else throw exception
     * Sets the price in the implementation in the super class
     * @param price
     */
    @Override
    public void setPrice(Price price) {
        if(isValidPrice(price)){
              addPriceForUnderlying(price);
        }else{
            throw new InvalidRequestException("Invalid price supplied !!!");
        }
    }

    /**
     * Checks if the price is present for the underlying else returns null
     * if underlying present returns the price
     * @param underlying
     * @return
     */
    @Override
    public Price getPrice(String underlying) {
       if( !isUnderlyingPresent(underlying))
           return null;
        return  getPriceForUnderlying(underlying);

    }
}
