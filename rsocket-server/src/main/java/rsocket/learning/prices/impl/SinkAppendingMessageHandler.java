package rsocket.learning.prices.impl;

import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;
import rsocket.learning.serialization.Deserializer;

@RequiredArgsConstructor
@Slf4j
public class SinkAppendingMessageHandler<T> implements MessageHandler {
	private final Deserializer deserializer;
	private final Sinks.Many<T> sink;
	private final Class<T> type;

	@Override
	public void handle(Context context, Message message) {
		try {
			var parsed = deserializer.deserialize(message.getBodyAsBinary(), type);
			log.info("Parsed {}: {}", type.getSimpleName(), parsed);
			sink.tryEmitNext(parsed);
		}
		catch (Exception error) {
			log.warn("Error occurred when parsing {}", type.getSimpleName(), error);
		}
	}
}
