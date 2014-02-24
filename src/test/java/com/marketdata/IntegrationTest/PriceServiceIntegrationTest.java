package com.marketdata.IntegrationTest;

import com.marketdata.output.PriceFileWriter;
import com.marketdata.output.PriceOutputWriter;
import com.marketdata.reader.PriceReader;
import com.marketdata.reader.PriceReaderImpl;
import com.marketdata.service.PriceServiceImpl;
import com.marketdata.writer.PriceWriter;
import com.marketdata.writer.PriceWriterImpl;
import junit.framework.TestCase;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author  Pradeep Muralidharan
 */
public class PriceServiceIntegrationTest extends TestCase {

    public void test_run_full_reader_writer_service(){
        // instantiate the priceserivce
        PriceServiceImpl priceService = new PriceServiceImpl();
        // instantiate the writer service
        File file = new File("C:\\data\\git-repo\\MarketDataService\\Output\\TestOutput.txt");
        PriceOutputWriter fileWriter = new PriceFileWriter(file);

                // instantiate price writers
        PriceWriter priceWriter = new PriceWriterImpl(".FTSE", priceService, 10, TimeUnit.SECONDS);
        PriceWriter priceWriter2 = new PriceWriterImpl(".DJI", priceService, 5, TimeUnit.SECONDS);
        PriceWriter priceWriter3 = new PriceWriterImpl(".NDX", priceService, 5, TimeUnit.SECONDS);
        PriceWriter priceWriter4 = new PriceWriterImpl(".GSPC", priceService, 7, TimeUnit.SECONDS);

        // instantiate price readers
        PriceReader priceReader = new PriceReaderImpl(priceService, ".FTSE", 11, TimeUnit.SECONDS, fileWriter);
        PriceReader priceReader2 = new PriceReaderImpl(priceService, ".DJI", 6, TimeUnit.SECONDS, fileWriter);
        PriceReader priceReader3 = new PriceReaderImpl(priceService, ".NDX", 6, TimeUnit.SECONDS, fileWriter);
        PriceReader priceReader4 = new PriceReaderImpl(priceService, ".GSPC", 8, TimeUnit.SECONDS, fileWriter);

        // start file writer
        fileWriter.start();

        // start the price writers
        priceWriter.start();
        priceWriter2.start();
        priceWriter3.start();
        priceWriter4.start();

        // start the price readers
        priceReader.start();
        priceReader2.start();
        priceReader3.start();
        priceReader4.start();

        // sleep for 1 minute
        try {
            Thread.sleep(60000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping all tasks !!!!!");
        // stop price writers
        priceWriter.stop();
        priceWriter2.stop();
        priceWriter3.stop();
        priceWriter4.stop();

        // stop price readers
        priceReader.stop();
        priceReader2.stop();
        priceReader3.stop();
        priceReader4.stop();
        // stop filewriter
        fileWriter.stop();

    }
}
