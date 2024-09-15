package rsocket.learning.prices;

import reactor.core.publisher.Flux;
import rsocket.learning.dto.PriceUpdate;

public interface StockPriceReader {
	Flux<PriceUpdate> getPriceUpdates(String symbol);
}
