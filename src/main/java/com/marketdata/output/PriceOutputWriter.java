package com.marketdata.output;

import com.marketdata.data.PriceOutput;

/**
 * @author Pradeep Muralidharan
 */
public interface PriceOutputWriter {

    public void write(PriceOutput outputObject);

    public void start();

    public void stop();

}
