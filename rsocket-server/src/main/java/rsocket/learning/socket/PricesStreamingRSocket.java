package rsocket.learning.socket;

import java.util.Objects;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import rsocket.learning.dto.PricesStreamRequest;
import rsocket.learning.prices.PriceData;
import rsocket.learning.serialization.PayloadDataDeserializer;
import rsocket.learning.serialization.PayloadDataSerializer;

@Slf4j
@RequiredArgsConstructor
public class PricesStreamingRSocket implements RSocket {
	private final PayloadDataDeserializer payloadDataDeserializer;
	private final PayloadDataSerializer payloadDataSerializer;
	private final Flux<PriceData> pricesStream;

	@Override
	public Flux<Payload> requestStream(Payload payload) {
		var pricesStreamRequest = payloadDataDeserializer.deserialize(payload, PricesStreamRequest.class);
		log.info("Received prices stream request: {}", pricesStreamRequest);
		return pricesStream
				.filter(v -> {
					var shouldProcessUpdate = Objects.equals(v.symbol(), pricesStreamRequest.symbol());
					log.info("Received update = {}. Going to process it = {}", v, shouldProcessUpdate);
					return shouldProcessUpdate;
				})
				.map(v -> PriceData.builder()
						.bidPrice(v.bidPrice())
						.askPrice(v.askPrice())
						.timestamp(v.timestamp())
						.build())
				.map(payloadDataSerializer::serializer);
	}
}
