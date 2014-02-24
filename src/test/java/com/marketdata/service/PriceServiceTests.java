package com.marketdata.service;


import com.google.common.collect.Lists;
import com.marketdata.Exception.InvalidRequestException;
import com.marketdata.data.Price;
import com.marketdata.data.PriceImpl;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.marketdata.helper.TestUtils.*;

/**
 * @author Pradeep Muralidharan
 */
public class PriceServiceTests extends TestCase {

    // SUT
    private PriceService priceService;

    public void test_price_service_throw_exception_if_invalid_price() {
        priceService = new PriceServiceImpl();
        try {
            priceService.setPrice(null);
            fail();
        } catch (InvalidRequestException ex) {
            assertTrue(ex.getMessage().equalsIgnoreCase("Invalid price supplied !!!"));
        }
    }

    public void test_price_service_should_return_null_when_underlying_not_present() {
        priceService = new PriceServiceImpl();
        assertNull(priceService.getPrice(""));
    }

    public void test_price_service_getting_price_from_multiple_readers_for_single_underlying() {
        // init
        final PriceService ps = new PriceServiceImpl();
        final Price price = new PriceImpl("FTSE", 100d);

        List<Future<Price>> futures = new ArrayList<Future<Price>>();
        Callable<Price> cal = getCallable(ps, "FTSE");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // SUT
        ps.setPrice(price);
        for(int i =0 ; i <2;i++){
             Future<Price> future = executor.submit(cal);
             futures.add(future);
        }

        // Assert
        try{
         assertTrue(futures.size() ==2);
         assertTrue(futures.get(0).get().equals(price));
         assertNull(futures.get(1).get());
        }catch(Exception ex){
            fail();
        }
    }

    public void test_price_service_getting_price_for_multiple_readers_for_multiple_underlying() {
        // init
        final PriceService ps = new PriceServiceImpl();
        final Price price = new PriceImpl("FTSE", 100d);
        final Price price2 = new PriceImpl("DOW", 100d);

        List<Future<Price>> futures = Lists.<Future<Price>>newArrayList();
        Callable<Price> cal = getCallable(ps,"FTSE");
        Callable<Price> cal2 = getCallable(ps,"DOW");

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // SUT
        ps.setPrice(price);
        ps.setPrice(price2);
        for(int i =0 ; i <2;i++){
            futures.add(executor.submit(cal));
            futures.add(executor.submit(cal2));
        }

        // Assert
          assertTrue(futures.size() ==4);
          assertTrue(isPricePresentInListOfFutures(futures,price));
          assertTrue(isPricePresentInListOfFutures(futures,price2));
    }

    public void test_price_service_blocking_write_when_price_service_is_full(){
         // creating price service that supports 1 underlying and queue size as 2
        final PriceService ps = new PriceServiceImpl(1,2);
         Price price = new PriceImpl("FTSE",100d);
         final Price price2 = new PriceImpl("FTSE",200d);

        // filling the queue to full
         ps.setPrice(price);
         ps.setPrice(price);

         // create a writer thread
         Thread writer = new Thread(){
             public void run(){
                     ps.setPrice(price2);
             }
         };

        try{
            writer.start();
            Thread.currentThread().sleep(1000 * 10);
            assertTrue("Writer should still be alive as expected to be blocked",writer.isAlive());
            writer.interrupt();
            writer.join(1000 * 2);
            assertFalse("Writer should not be alive as its been interrupted",writer.isAlive());
        }catch (Exception ex){
            fail();
        }
    }

    public void test_price_service_when_full_should_block_and_unblock_after_read(){
        // creating price service that supports 1 underlying and queue size as 2
        final PriceService ps = new PriceServiceImpl(1,2);
        Price price = new PriceImpl("FTSE",100d);
        final Price price2 = new PriceImpl("FTSE",200d);

        // filling the queue to full
        ps.setPrice(price);
        ps.setPrice(price);

        // create a writer thread
        Thread writer = new Thread(){
            public void run(){
                ps.setPrice(price2);
            }
        };
        try{
            writer.start();
            Thread.currentThread().sleep(1000 * 5);
            assertTrue("Writer should still be alive as expected to be blocked",writer.isAlive());
            Price p = ps.getPrice("FTSE");
            Thread.currentThread().sleep(1000 * 5);
            assertFalse("Writer should not be alive as its been interrupted",writer.isAlive());
            ps.getPrice("FTSE");
            Price blokPrice = ps.getPrice("FTSE");
            assertTrue(blokPrice.equals(price2));
        }catch (Exception ex){
            fail();
        }
    }
}
