package rsocket.learning.prices.impl;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import rsocket.learning.dto.PriceUpdate;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.StockPriceReader;

@RequiredArgsConstructor
@Slf4j
public class StockPriceReaderImpl implements StockPriceReader {
	private final Flux<PriceData> pricesStream;

	@Override
	public Flux<PriceUpdate> getPriceUpdates(String symbol) {
		return pricesStream
				.filter(v -> {
					var shouldProcessUpdate = Objects.equals(v.symbol(), symbol);
					log.debug("Received update = {}. Going to process it = {}", v, shouldProcessUpdate);
					return shouldProcessUpdate;
				})
				.map(v -> PriceUpdate.builder()
						.bidPrice(v.bidPrice())
						.askPrice(v.askPrice())
						.timestamp(v.timestamp())
						.build());
	}
}
