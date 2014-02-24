package com.marketdata.output;

import com.marketdata.data.PriceOutput;

import java.io.*;
import java.util.concurrent.*;

import static com.marketdata.constants.PriceServiceConstants.THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC;

/**
 * @author Pradeep Muralidharan
 */
public class PriceFileWriter implements PriceOutputWriter {

    /**
     * Blocking queue to put and pop prices to write
     */
    private final BlockingQueue<PriceOutput> queue = new ArrayBlockingQueue<PriceOutput>(500);

    /**
     * file to write to
     */
    private final File file;

    /**
     * Executor to start a writer thread
     */
    private final ExecutorService executor;

    /**
     * PrintWriter to write into file
     */
    private PrintWriter fWriter;


    public PriceFileWriter(File file) {
        this.file = file;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Populates the queue with the incoming price to write
     *
     * @param priceOutput
     */
    @Override
    public void write(PriceOutput priceOutput) {
        try {
            queue.put(priceOutput);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates the file writer and start the executor to execute the writer
     */
    @Override
    public void start() {

        try {
            fWriter = new PrintWriter(new BufferedWriter(new java.io.FileWriter(file)));
        } catch (IOException ex) {
            throw new RuntimeException("Invalid file specified");
        }
        executor.submit(new FWriter());


    }

    /**
     * Stops the executor gracefully, in two phases
     * first stop all incoming tasks
     * and then try and stop all active tasks
     */
    @Override
    public void stop() {
        executor.shutdown();
        fWriter.flush();
        fWriter.close();
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        } finally {
            fWriter.flush();
            fWriter.close();
        }
    }

    /**
     * Writer task which gets prices from the queue and writes to file,
     * if no prices found waits for the price to be populated.
     */
    private class FWriter implements Runnable {

        @Override
        public void run() {
            if (file == null) throw new RuntimeException("File is null");
            try {
                while (true) {
                    PriceOutput po = queue.take();
                    if (po != null) {
                        System.out.println(po.toString());
                        fWriter.println(po.toString());
                        fWriter.flush();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("File writer interuppted");
            }

        }
    }


}
