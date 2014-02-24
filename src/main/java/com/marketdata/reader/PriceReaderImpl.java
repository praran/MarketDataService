package com.marketdata.reader;

import com.marketdata.output.PriceOutputWriter;
import com.marketdata.service.PriceService;

import java.io.Reader;
import java.util.concurrent.TimeUnit;
import static com.marketdata.constants.PriceServiceConstants.THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC;
import static com.marketdata.constants.PriceServiceConstants.REPORT_GENERATION_INTERVAL_IN_SEC;


/**
 * @author Pradeep Muralidharan
 */
public class PriceReaderImpl extends  AbstractPriceReader {



    public PriceReaderImpl(PriceService priceService, String underlying, int pollInterval, TimeUnit timeUnit, PriceOutputWriter priceOutputWriter) {
        super(priceService, underlying, pollInterval, timeUnit, priceOutputWriter);
    }

    /**
     * Starts the reader to read data at a specific poll interval
     * Starts a Report generator to generate a report
     */
    @Override
    public void start() {
        // Scheduler to read data at a fixed rate
        scheduler.scheduleAtFixedRate(getReader(), pollInterval, pollInterval, timeUnit);
        // Report generator task executes every 1 minute
        scheduler.scheduleAtFixedRate(getReportWriter(), REPORT_GENERATION_INTERVAL_IN_SEC, REPORT_GENERATION_INTERVAL_IN_SEC, timeUnit);

    }

    public boolean isShutdown(){
        return scheduler.isShutdown();
    }

    /**
     * Stops the executor gracefully, in two phases
     * first stop all incoming tasks
     * and then try and stop all active tasks
     */
    @Override
    public void stop() {
        scheduler.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!scheduler.awaitTermination(THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            scheduler.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }



}
