package io.questdb.orderbook;

import com.appsicle.orderbook.OrderBook;
import com.appsicle.orderbook.model.OrderSides;
import com.questdb.std.Rnd;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class OrderBookBenchmark {

    private static final long minPrice = 817520;
    private static final long maxPrice = 818536;
    private static final OrderBook orderBook = new OrderBook(
            minPrice, maxPrice, 200_000_000, l -> {
    });

    private static final Rnd rnd = new Rnd();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OrderBookBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .addProfiler("gc")
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Iteration)
    public void setup() {
        orderBook.clear();
    }

    @Benchmark
    public void testBuySellOrder() {
        orderBook.limitOrder(
                rnd.nextBoolean() ? OrderSides.SELL : OrderSides.BUY,
                rnd.nextPositiveLong() % (maxPrice - minPrice) + minPrice,
                rnd.nextPositiveInt() % 1024
        );
    }
}
