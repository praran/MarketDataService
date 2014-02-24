package com.marketdata.price;

/**
 * @author Pradeep Muralidharan
 * Dummy price source generates random price
 */
public class PriceSource {
    /**
     * Gets the price as double value
     * @return double
     */

    public static double getPrice() {
        return 1000d * Math.random();
    }
}
