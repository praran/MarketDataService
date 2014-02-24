package com.marketdata.reader;

import com.marketdata.data.PriceOutput;
import com.marketdata.data.PricePair;
import com.marketdata.output.PriceOutputWriter;
import com.marketdata.data.Price;
import com.marketdata.service.PriceService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author  Pradeep Muralidharan
 */
public abstract class AbstractPriceReader implements PriceReader {

    /**
     * Price service to read  prices from
     */
    protected final PriceService priceService;

    /**
     * specific underlying to read data for
     */
    protected final String underlying;

    /**
     * Poll interval to poll for data
     */
    protected final int pollInterval;

    /**
     * Time unit for polling
     */
    protected final TimeUnit timeUnit;

    /**
     * Price output writer to write output to
     */
    protected final PriceOutputWriter priceOutputWriter;

    /**
     * Fixed rate scheduler to schedule request
     */
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Hashmap which stores a pair of prices, the most recent highest and second higest price
     * for each underlying
     */
    private final ConcurrentHashMap<String, PricePair> pricePairHolder = new ConcurrentHashMap<String, PricePair>();

    /**
     * Variable holding the revision, specified to the writer
     */
    private final ThreadLocal<Integer> revision = new ThreadLocal<Integer>();


    protected AbstractPriceReader(PriceService priceService, String underlying, int pollInterval, TimeUnit timeUnit, PriceOutputWriter priceOutputWriter) {
        this.priceService = priceService;
        this.underlying = underlying;
        this.pollInterval = pollInterval;
        this.timeUnit = timeUnit;
        this.priceOutputWriter = priceOutputWriter;
    }

    protected Reader getReader(){
           return new Reader(this.priceService,this.underlying);
    }

    protected ReportWriter getReportWriter(){
        return new ReportWriter();
    }

    /**
     * Reader task to read prices from the price service.
     * The reader gets the price and when price is not null updates the
     * PairPriceholder with the most recent highest and second highest prices
     */

    private class Reader implements Runnable {

        private final String underlying;

        private final PriceService priceService;

        Reader(PriceService priceService, String underlying) {
            this.priceService = priceService;
            this.underlying = underlying;
        }

        @Override
        public void run() {
            Price price = priceService.getPrice(underlying);
            if (price != null) {
                PricePair pPair = pricePairHolder.putIfAbsent(underlying, new PricePair(price, null, true));
                if (pPair == null) return;
               /* while(!pricePairHolder.replace(underlying,pPair, pPair.comparAndReturnAppropriate(price))){
                    pPair = pricePairHolder.get(underlying);
                }*/
                pricePairHolder.put(underlying, pPair.comparAndReturnAppropriate(price));
            }
        }
    }



    /**
     * Report generator task, which runs at a fixed rate
     * retrieves the second highest price for the given underlying and passes to the output writer to
     * write the price information
     */
    private class ReportWriter implements Runnable {

        ReportWriter() {
        }

        @Override
        public void run() {
            try {
                PricePair pPair = pricePairHolder.get(underlying);
                if (pPair != null) {
                     if(revision.get() ==null){
                         revision.set(1);
                     }
                    int rev = revision.get();
                    // puts to output writer to output the prices
                    priceOutputWriter.write(new PriceOutput(pPair.getSecondHighestPrice(), rev));
                    // System.out.println(new PriceOutput(pPair.getSecondHighestPrice(),rev).toString());
                                        /*while(!pricePairHolder.replace(underlying,pPair, new PricePair(null,null,false))){
                        pPair = pricePairHolder.get(underlying);
                    }*/
                    revision.set(1 + rev);
                    pricePairHolder.put(underlying, new PricePair(null, null, false));

                } else {
                    System.out.println("Error for underlying : " + underlying + "Price not available");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
