package rsocket.learning.prices.impl;

import java.util.concurrent.BlockingQueue;

import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.PriceDataDeserializer;

@RequiredArgsConstructor
@Slf4j
public class SinkAppendingMessageHandler implements MessageHandler {
	private final PriceDataDeserializer priceDataDeserializer;
	private final Sinks.Many<PriceData> sink;

	@Override
	public void handle(Context context, Message message) {
		try {
			var parsedPriceData = priceDataDeserializer.deserialize(message.getBodyAsBinary());
			log.info("Parsed price data: {}", parsedPriceData);
			sink.tryEmitNext(parsedPriceData);
		}
		catch (Exception error) {
			log.warn("Error occurred when parsing price data", error);
		}
	}
}
