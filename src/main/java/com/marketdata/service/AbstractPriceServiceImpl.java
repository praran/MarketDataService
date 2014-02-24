package com.marketdata.service;

import static com.marketdata.constants.PriceServiceConstants.*;

import com.marketdata.data.Price;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Pradeep Muralidharan
 */
 abstract class AbstractPriceServiceImpl implements  PriceService{

    // Queue size of the queue holding data for each underlying
    private final int QUEUE_SIZE ;

    /**
     * Primanry data holder for storing prices
     * The key in the concurrent hashmap specifies the underlying
     * The Value of the concurrent hashmap is a blocking queue holding the prices for the relevant underlying
     * The idea is to reduce the thread contention and create lanes and each lane specific for the underlying
     * so all writers for a specific underlying writes into a specific lane and
     * all readers for a specific underlying reads from a specific lane
     */
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Price>> dataHolder;


    AbstractPriceServiceImpl(){
        this(DEFAULT_NO_OF_UNDERLYING_FOR_PRICE_SERVICE, DEFAULT_QUEUE_SIZE_HOLDING_PRICE_SEVICE);
    }

    /**
     * Constructor which sets the default values for capacity and queuesize
     */
    AbstractPriceServiceImpl(int noOfUnderlyings,int queueSize){
        QUEUE_SIZE = queueSize;
        dataHolder = new ConcurrentHashMap<String, LinkedBlockingQueue<Price>>(noOfUnderlyings);
    }


    /**
     * Adds the price to the specific queue for the underlying
     * if the underlying is not present creates a new entry
     * if the underlying is already present enqueues the price to the queue
     * @param price
     */
    protected void addPriceForUnderlying(Price price){
      String underlying = price.getUnderlying();
       if(!dataHolder.containsKey(underlying)){
          dataHolder.put(underlying,getNewBlockingQueueWithPrice(price));
      }else{
           try{
               dataHolder.get(underlying).put(price);
           }catch(InterruptedException ex){
               //
           }
      }
    }

    /**
     * Returns the price for the underlying from the queue
     * @param underlying
     * @return
     */
    protected Price getPriceForUnderlying(String underlying){
           return dataHolder.get(underlying).poll();
    }

    /**
     * Helper checks if price is valid
     * @param price
     * @return
     */
    protected boolean isValidPrice(Price price){
       return price != null && StringUtils.isNotBlank(price.getUnderlying());
    }

    /**
     * Checks if the underlying is present
     * @param underlying
     * @return
     */
    protected boolean isUnderlyingPresent(String underlying){
        return dataHolder.containsKey(underlying);
    }

    /**
     * Creates a new blocking queue with the price
     * @param price
     * @return
     */
    private LinkedBlockingQueue<Price> getNewBlockingQueueWithPrice(Price price){
        LinkedBlockingQueue<Price> lbq = new LinkedBlockingQueue<Price>(QUEUE_SIZE);
        lbq.add(price);
        return lbq;
    }
}
