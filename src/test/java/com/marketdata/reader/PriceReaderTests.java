package com.marketdata.reader;

import com.marketdata.output.PriceFileWriter;
import com.marketdata.service.PriceService;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;

import java.util.concurrent.TimeUnit;

/**
 * @author Pradeep Muralidharan
 */
public class PriceReaderTests extends TestCase{

    // SUT
    private PriceReaderImpl priceReader;

    private PriceService priceService;
    private PriceFileWriter fileWriter;

    Mockery context = new JUnit4Mockery(){{ setThreadingPolicy(new Synchroniser());}};

    public void test_if_reader_can_Start_and_Stop(){
        priceService = context.mock(PriceService.class);
        priceReader = new PriceReaderImpl(priceService, ".FTSE", 1, TimeUnit.SECONDS, fileWriter);
        priceReader.start();
        try{
            Thread.sleep(1000 * 2);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        Assert.assertFalse(priceReader.isShutdown());
        priceReader.stop();
        try{
            Thread.sleep(1000 * 2);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        Assert.assertTrue(priceReader.isShutdown());
    }

    public void test_price_reader_should_read_from_price_service(){
        // init
        priceService = context.mock(PriceService.class);

        // set expectations
        context.checking( new Expectations(){{
            oneOf(priceService).getPrice(with(any(String.class)));
           }
        });
        // initialize SUT with behaviour, price service should send read requests
        // every 2 seconds
        priceReader = new PriceReaderImpl(priceService,".FTSE",2, TimeUnit.SECONDS,fileWriter);

        // Execute SUT
        try{
            priceReader.start();
            // Waiting for 5 secs, price service should record atleast 1 write request
            Thread.sleep(1000 * 5);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        // Assert
        context.assertIsSatisfied();
        // tear down
        priceReader.stop();
        Assert.assertTrue(priceReader.isShutdown());
    }

    public void test_price_reader_should_read_from_price_service_as_scheduled(){
        // init
        priceService = context.mock(PriceService.class);

        // set expectations
        context.checking( new Expectations(){{
            atLeast(5).of(priceService).getPrice(with(any(String.class)));
        }
        });
        // initialize SUT with behaviour, price service should send read requests
        // every 2 seconds
        priceReader = new PriceReaderImpl(priceService,".FTSE",2, TimeUnit.SECONDS,fileWriter);

        // Execute SUT
        try{
            priceReader.start();
            // Waiting for 10 secs, price service should record atleast 1 write request
            Thread.sleep(1000 * 10);
        }catch (InterruptedException ex){
            Assert.fail();
        }
        // Assert
        context.assertIsSatisfied();
        // tear down
        priceReader.stop();
        Assert.assertTrue(priceReader.isShutdown());
    }


}
