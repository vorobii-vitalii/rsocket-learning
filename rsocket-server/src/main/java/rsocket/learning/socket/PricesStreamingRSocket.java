package rsocket.learning.socket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import rsocket.learning.dto.PricesStreamRequest;
import rsocket.learning.prices.StockPriceReader;
import rsocket.learning.serialization.PayloadDataDeserializer;
import rsocket.learning.serialization.PayloadDataSerializer;

@Slf4j
@RequiredArgsConstructor
public class PricesStreamingRSocket implements RSocket {
	private final PayloadDataDeserializer payloadDataDeserializer;
	private final PayloadDataSerializer payloadDataSerializer;
	private final StockPriceReader stockPriceReader;

	@Override
	public Flux<Payload> requestStream(Payload payload) {
		var pricesStreamRequest = payloadDataDeserializer.deserialize(payload, PricesStreamRequest.class);
		log.info("Received prices stream request: {}", pricesStreamRequest);
		return stockPriceReader.getPriceUpdates(pricesStreamRequest.symbol()).map(payloadDataSerializer::serializer);
	}
}
