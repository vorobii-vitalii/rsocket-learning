package rsocket.learning.prices.impl;

import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;

@Slf4j
public class QueueReadingFluxCreator {

	public <T> Flux<T> create(BlockingQueue<T> queue) {
//		return Flux.defer(() -> Flux.push(sink -> {
//			sink.onRequest(n -> {
//				for (long i = 0; i < n; i++) {
//					T element = queue.poll();
//					if (element == null) {
//						break;
//					}
//					sink.next(element);
//				}
//			});
//		}));
		return Flux.generate(sink -> {
			var element = queue.peek();
			if (element == null) {
				sink.complete();
			} else {
				sink.next(element);
			}
		});
	}

}
