package com.marketdata.writer;

/**
 * @author Pradeep Muralidharan
 */

/*
 *  Get data from the PriceSource and put it into an implementation of PriceService
 */
public interface PriceWriter {

    /**
     * Starts the Price Writer
     */
    void start();

    /**
     * Stops the Price Writer
     */
    void stop();
}
