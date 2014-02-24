package com.marketdata.service;

import com.marketdata.data.Price;

/**
 * @author Pradeep Muralidharan
 * Specifications for the price service
 */
public interface PriceService {

    /**
     * Should be able to set price
     * @param price
     */
    void setPrice(Price price);

    /**
     * should be able to return price for the underlying
     * @param underlying
     * @return
     */
    Price getPrice(String underlying);
}
