package com.marketdata.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Pradeep Muralidharan
 */
public  class PriceServiceConstants {

    public static final TimeUnit TIME_UNITS = TimeUnit.SECONDS;

    public static final int REPORT_GENERATION_INTERVAL_IN_SEC =  60;

    public static final int THREAD_EXECUTOR_SHUTDOWN_AWAIT_TIME_IN_SEC = 60;

    // default queue size of each blocking queue for underlying type
    public static final int DEFAULT_QUEUE_SIZE_HOLDING_PRICE_SEVICE = 500;

    // default no of underlyings
    public static final int DEFAULT_NO_OF_UNDERLYING_FOR_PRICE_SERVICE = 20;
}
