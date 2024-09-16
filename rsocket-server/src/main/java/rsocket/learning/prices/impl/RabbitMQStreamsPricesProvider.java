package rsocket.learning.prices.impl;

import reactor.core.publisher.Flux;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.PricesProvider;

public class RabbitMQStreamsPricesProvider implements PricesProvider {


	@Override
	public Flux<PriceData> subscribeToPrices() {
		return null;
	}
}
