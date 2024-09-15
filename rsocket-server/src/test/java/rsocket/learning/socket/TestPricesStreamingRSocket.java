package rsocket.learning.socket;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.rsocket.Payload;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import rsocket.learning.dto.PriceUpdate;
import rsocket.learning.dto.PricesStreamRequest;
import rsocket.learning.prices.StockPriceReader;
import rsocket.learning.serialization.PayloadDataDeserializer;
import rsocket.learning.serialization.PayloadDataSerializer;

@ExtendWith(MockitoExtension.class)
class TestPricesStreamingRSocket {
	private static final String SYMBOL = "ABBN";

	@Mock
	PayloadDataDeserializer payloadDataDeserializer;

	@Mock
	PayloadDataSerializer payloadDataSerializer;

	@Mock
	StockPriceReader stockPriceReader;

	@InjectMocks
	PricesStreamingRSocket pricesStreamingRSocket;

	@Mock
	Payload requestPayload;

	@Mock
	Payload priceUpdatePayload;

	@Test
	void requestStream() {
		var priceUpdate = PriceUpdate.builder()
				.bidPrice(BigDecimal.ONE)
				.askPrice(BigDecimal.valueOf(2.4))
				.timestamp(LocalDateTime.now())
				.build();
		when(payloadDataDeserializer.deserialize(requestPayload, PricesStreamRequest.class))
				.thenReturn(PricesStreamRequest.builder().symbol(SYMBOL).build());
		when(stockPriceReader.getPriceUpdates(SYMBOL)).thenReturn(Flux.just(priceUpdate));
		when(payloadDataSerializer.serializer(priceUpdate)).thenReturn(priceUpdatePayload);
		StepVerifier.create(pricesStreamingRSocket.requestStream(requestPayload))
				.expectNext(priceUpdatePayload)
				.expectComplete()
				.log()
				.verify();
	}
}
