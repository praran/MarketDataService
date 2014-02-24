package com.marketdata.writer;

import com.marketdata.data.Price;
import com.marketdata.data.PriceImpl;
import com.marketdata.price.PriceSource;
import com.marketdata.service.PriceService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.marketdata.constants.PriceServiceConstants.THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC;


/**
 * @author Pradeep Muralidharan
 */
public class PriceWriterImpl implements PriceWriter{

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final String underlying;

    private final PriceService priceService;

    private final int  pollInterval;

    private final TimeUnit timeUnit;

    public PriceWriterImpl(String underlying, PriceService priceService, int pollInterval, TimeUnit timeUnit){
        this.underlying = underlying;
        this.priceService = priceService;
        this.pollInterval = pollInterval;
        this.timeUnit = timeUnit;
    }

    /**
     * Starts the Scheduler at fixed rated and adds data as per the specified poll interval
     */
    @Override
    public void start() {
        final Runnable writer = new Writer(new PriceSource(),priceService, underlying);
        scheduler.scheduleAtFixedRate(writer,0 ,pollInterval , timeUnit);
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

    public boolean isShutdown(){
        return scheduler.isShutdown();
    }

    /**
     * Runnable task to update the price at a fixed rate
     * updates the price from the price source for the given underlying
     */
    private class Writer implements  Runnable{
        private final PriceSource priceSource;

        private final String underlying;

        private final PriceService priceService;

        Writer(PriceSource priceSource, PriceService priceService, String underlying){
            this.priceSource = priceSource;
            this.priceService = priceService;
            this.underlying = underlying;
        }

        @Override
        public void run() {
            final Price price = new PriceImpl(underlying, PriceSource.getPrice());
            priceService.setPrice(price);
        }
    }

}
