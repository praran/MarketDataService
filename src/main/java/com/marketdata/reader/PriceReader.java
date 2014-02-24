package com.marketdata.reader;

/**
 * @author Pradeep Muralidharan
 */

/*
 *  Read data every second from an implementation of PriceService and print out
 */

public interface PriceReader {

    /**
     * Start the reader
     */
    void start();

    /**
     * Stops  the reader
     */
    void stop();
}
