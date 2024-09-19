package rsocket.learning.prices.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import rsocket.learning.dto.PriceUpdate;
import rsocket.learning.prices.PriceData;

class TestStockPriceReaderImpl {

	@Test
	void getPriceUpdates() {
		var refDate = LocalDateTime.now();
		var priceDataAbbn1 = PriceData.builder()
				.bidPrice(BigDecimal.ONE)
				.askPrice(BigDecimal.ONE)
				.symbol("ABBN")
				.timestamp(refDate.minusSeconds(5))
				.build();
		var priceDataAbbn2 = PriceData.builder()
				.bidPrice(BigDecimal.valueOf(2))
				.askPrice(BigDecimal.ONE)
				.symbol("ABBN")
				.timestamp(refDate.minusSeconds(3))
				.build();

		var stockPricesReader = new StockPriceReaderImpl(Flux.just(
				priceDataAbbn1,
				PriceData.builder()
						.bidPrice(BigDecimal.valueOf(2))
						.askPrice(BigDecimal.ONE)
						.symbol("XBT/USD")
						.timestamp(refDate.minusSeconds(4))
						.build(),
				priceDataAbbn2
		));
		StepVerifier.create(stockPricesReader.getPriceUpdates("ABBN"))
				.expectNext(PriceUpdate.builder()
						.bidPrice(BigDecimal.ONE)
						.askPrice(BigDecimal.ONE)
						.timestamp(refDate.minusSeconds(5))
						.build())
				.expectNext(PriceUpdate.builder()
						.bidPrice(BigDecimal.valueOf(2))
						.askPrice(BigDecimal.ONE)
						.timestamp(refDate.minusSeconds(3))
						.build())
				.expectComplete()
				.log()
				.verify();
	}
}