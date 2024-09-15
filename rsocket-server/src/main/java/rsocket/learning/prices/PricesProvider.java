package rsocket.learning.prices;

import reactor.core.publisher.Flux;

public interface PricesProvider {
	Flux<PriceData> subscribeToPrices();
}
