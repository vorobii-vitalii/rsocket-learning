package rsocket.learning;

import reactor.core.publisher.Flux;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.impl.DemoPricesProvider;

public class DemoReactive {

	public static void main(String[] args) throws InterruptedException {
		var pricesProvider = new DemoPricesProvider();
		final Flux<PriceData> flux = pricesProvider.subscribeToPrices().share();
		flux.subscribe(v -> {
			System.out.println("1 = " + v);
		});
		Thread.sleep(5_000);
		flux.subscribe(v -> {
			System.out.println("2 = " + v);
		});
		Thread.sleep(5_000);
	}

}
