import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

public class FluxTest {

    @Test
    public void fluxJustTest() throws InterruptedException {
        Flux.just("1", "A", 3).subscribe(System.out::print);
        System.out.println();

        Flux.range(1, 10).subscribe(System.out::print);
        System.out.println();

        Flux.just("1", "a", 2)
                .interval(Duration.ofSeconds(1))
                .subscribe(System.out::print);

        // CountDownLatch latch = new CountDownLatch(10);
        // latch.await();

        Flux.error(new Exception("a wo,something is wrong!")).subscribe(System.out::println);


    }

    @Test
    public void fluxGenerateTest() {

        Flux.generate(i -> {
            i.next("aaaa");
            i.complete();
        }).subscribe(System.out::println);

        final Random rnd = new Random();

        Flux<Object> t1 = Flux.generate(ArrayList::new, (list, item) -> {
            Integer value = rnd.nextInt(100);
            list.add(value);
            item.next(value);
            if (list.size() > 10) {
                item.complete();
            }

            return list;
        });

        t1.subscribe(System.out::println);

    }

    @Test
    public void fluxBufferTest() throws InterruptedException {

        Flux.range(0, 10).buffer(3).subscribe(System.out::println);
        System.out.println("--------------");

        Flux<List<Long>> t1 = Flux
                .interval(Duration.of(1, ChronoUnit.SECONDS))
                .bufferTimeout(2, Duration.of(2, ChronoUnit.SECONDS));

        t1.subscribe(System.out::println);

        //防止程序过早退出，放一个CountDownLatch拦住
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();

    }


    @Test
    public void fluxWindowTest() throws InterruptedException {

        Flux<Flux<Long>> t2 = Flux
                .interval(Duration.of(1, ChronoUnit.SECONDS))
                .windowTimeout(3, Duration.of(2, ChronoUnit.SECONDS));
        Flux<Long> t3 = t2.flatMap(x -> x);

        t3.subscribe(System.out::println);

        Flux<Tuple2<String, String>> t4 = Flux.just("A", "B").zipWith(Flux.just("1", "2", "3"));
        t4.subscribe(System.out::println);


        //防止程序过早退出，放一个CountDownLatch拦住
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();


    }

    @Test
    public void fluxTakeTest() {
        Flux.range(1, 10).take(3).subscribe(System.out::println);
        System.out.println("--------------");

        Flux<Integer> t1 = Flux.range(1, 10).takeLast(3);
        t1.subscribe(System.out::println);
        System.out.println("--------------");
        Flux.range(1, 10).takeWhile(c -> c > 1 && c < 5).subscribe(System.out::println);
        System.out.println("--------------");
        Flux.range(1, 10).takeUntil(c -> c > 1 && c < 5).subscribe(System.out::println);
        System.out.println("--------------");
        Flux.range(1, 4).takeUntilOther(Flux.never()).subscribe(System.out::println);
    }

    @Test
    public void mergeTest() {
        Flux<Long> t1 = Flux.merge(Flux.interval(Duration.of(500, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(600, ChronoUnit.MILLIS), Duration.of(500, ChronoUnit.MILLIS)).take(5));
        t1.toStream().forEach(System.out::println);

        System.out.println("-----------------------------");

        Flux.mergeSequential(Flux.interval(Duration.of(500, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(600, ChronoUnit.MILLIS), Duration.of(500, ChronoUnit.MILLIS)).take(5))
                .toStream().forEach(System.out::println);
    }


    @Test
    public void subscribeTest1() {
        Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IndexOutOfBoundsException("下标越界啦！")))
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void subscribeTest2() {
        Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IndexOutOfBoundsException("下标越界啦！")))
                .onErrorReturn("x")
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void subscribeTest4() {
        Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IndexOutOfBoundsException("下标越界啦！")))
                .retry(1)
                .subscribe(System.out::println, System.err::println);
    }



}