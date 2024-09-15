package rsocket.learning.prices.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Flux;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.PricesProvider;

public class DemoPricesProvider implements PricesProvider {
	private static final Duration PERIOD_BETWEEN_PRICE_UPDATES = Duration.ofMillis(1);
	private static final String[] SYMBOLS = new String[] {
			"ABBN",
			"APPL",
			"XBT/USD",
			"TSL"
	};
	private final AtomicInteger index = new AtomicInteger(0);
	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	public Flux<PriceData> subscribeToPrices() {
		return Flux.interval(PERIOD_BETWEEN_PRICE_UPDATES)
				.map(ignoredTimestamp -> {
					int symbolIndex = index.getAndIncrement() % SYMBOLS.length;
					return new PriceData(
							SYMBOLS[symbolIndex],
							randomPrice(),
							randomPrice(),
							LocalDateTime.now()
					);
				});
	}

	private BigDecimal randomPrice() {
		return BigDecimal.valueOf(secureRandom.nextDouble());
	}

}
