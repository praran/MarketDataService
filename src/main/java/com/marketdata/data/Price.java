package com.marketdata.data;

/**
 * @author Pradeep Muralidharan
 */
public interface Price {
    /**
     * Returns the underlying
     * @return
     */

    String getUnderlying();

    /**
     *  Returns the price
     * @return double
     */
    double getPrice();

}
