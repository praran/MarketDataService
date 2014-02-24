package com.marketdata.writer;

import com.marketdata.data.Price;
import com.marketdata.service.PriceService;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;

import java.util.concurrent.TimeUnit;

/**
 * @author  Pradeep Muralidharan
 */


public class PriceWriterTests extends  TestCase{
    // SUT
    private PriceWriterImpl priceWriter;

    private PriceService priceService;

    Mockery context = new JUnit4Mockery(){{ setThreadingPolicy(new Synchroniser());}};


    public void test_if_writer_can_Start_and_Stop(){
        priceService = context.mock(PriceService.class);
        priceWriter = new PriceWriterImpl(".FTSE",priceService, 2, TimeUnit.SECONDS);
        priceWriter.start();
        try{
            Thread.sleep(1000 * 2);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        Assert.assertFalse(priceWriter.isShutdown());
        priceWriter.stop();
        try{
            Thread.sleep(1000 * 2);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        Assert.assertTrue(priceWriter.isShutdown());
    }

    public void test_if_writer_sends_write_requests(){
        // init
        priceService = context.mock(PriceService.class);

        // set expectations
        context.checking( new Expectations(){{
            oneOf(priceService).setPrice(with(any(Price.class)));
        }
        });

        // initialize SUT with behaviour, price service should send write requests
        // every 2 seconds
        priceWriter = new PriceWriterImpl(".FTSE",priceService, 2, TimeUnit.SECONDS);

        // Execute SUT
        try{
            priceWriter.start();
            // Waiting for 5 secs, price service should record atleast 1 write request
            Thread.sleep(1000 * 5);
        }catch (InterruptedException ex){
            Assert.fail();
        }
       // Assert
        context.assertIsSatisfied();
        // tear down
        priceWriter.stop();
        Assert.assertTrue(priceWriter.isShutdown());
    }

    public void test_if_writer_sends_write_requests_as_scheduled(){
        // init
        priceService = context.mock(PriceService.class);

        // set expectations
        context.checking( new Expectations(){{
            atLeast(5).of(priceService).setPrice(with(any(Price.class)));
        }
        });

        // initialize SUT with behaviour, price service should send write requests
        // every 2 seconds
        priceWriter = new PriceWriterImpl(".FTSE",priceService, 2, TimeUnit.SECONDS);

        // Execute SUT
        try{
            priceWriter.start();
            // waiting for 10 secs, the price service should
            // record atleast 5 requests
            Thread.sleep(1000 * 10);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        // Assert
        context.assertIsSatisfied();
        // tear down
        priceWriter.stop();
        Assert.assertTrue(priceWriter.isShutdown());
    }


}
