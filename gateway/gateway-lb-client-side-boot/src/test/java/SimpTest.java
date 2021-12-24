import cn.hutool.core.thread.ThreadUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 23:47
 */
public class SimpTest {
    @Test
    public void test_reactor() {
//        Flux<Integer> flux = Flux.ju
//                .publishOn(Schedulers.boundedElastic());

        Mono.just(Optional.empty())
                .flatMap(o -> {
                    Flux<Integer> flux = Mono.justOrEmpty("svc")
                            .flatMapMany(svc -> Flux.fromIterable(getPods(svc)))
                            .subscribeOn(Schedulers.boundedElastic());

//                    List<Integer> map = flux.toStream().collect(Collectors.toList());
                    List<Integer> map = flux.collectList().block();
                    return Mono.just(map);
                })
                .publishOn(Schedulers.boundedElastic())
                .subscribe(System.out::println);

    }

    private List<Integer> getPods(String svc) {
        ThreadUtil.sleep(1000);
        System.out.println("getPods");
        throw new RuntimeException();
//        return Arrays.asList(1, 2, 3);
    }

    @Test
    public void test_retry() {
        AtomicInteger errorCount = new AtomicInteger();
        Flux.defer(() -> Flux.fromIterable(getPods("svc")))
                .doOnError(e -> {
                    errorCount.incrementAndGet();
                    System.out.println(e + " at " + LocalTime.now());
                })
                .retryWhen(Retry
                        .backoff(3, Duration.ofMillis(10)).jitter(0d)
                        .doAfterRetry(rs -> System.out.println("retried at " + LocalTime.now() + ", attempt " + (rs.totalRetries() + 1)))
                        .onRetryExhaustedThrow((spec, rs) -> rs.failure())
                )
                .subscribe();
        ThreadUtil.sleep(3000L);
    }
}
