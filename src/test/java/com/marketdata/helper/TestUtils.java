package com.marketdata.helper;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.marketdata.data.Price;
import com.marketdata.service.PriceService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Pradeep Muralidharan
 */
public class TestUtils {

    /**
     * Helper method to create callable
     * @param ps
     * @param underlying
     * @return
     */
    public  static Callable<Price> getCallable(final PriceService ps , final String underlying){
        return  new Callable<Price>() {
            @Override
            public Price call() throws Exception {
                return ps.getPrice(underlying);
            }
        };
    }

    public static boolean isPricePresentInListOfFutures(List<Future<Price>> futurePrices, Price price){
        List<Price> ps = filterNullFromPrices(getPriceFromFutures(Lists.newArrayList(futurePrices)));
        return ps.contains(price);
    }

    public static List<Price> getPriceFromFutures(List<Future<Price>> futures){
        return Lists.transform(Lists.newArrayList(futures), new Function<Future<Price>, Price>() {
            @Override
            public Price apply(Future<Price> future) {
                try {
                    if (future.isDone()) {
                        return future.get();
                    } else {
                        return future.get(5, TimeUnit.SECONDS);
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
        });
    }

    public static List<Price> filterNullFromPrices(List<Price> prices){
        int i =1;
        return Lists.newArrayList( Collections2.filter(prices, new Predicate<Price>() {
            @Override
            public boolean apply(Price price) {
                return price != null;
            }
        }));
    }
}
