package rsocket.learning.dagger.modules;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.MessageHandler;
import com.rabbitmq.stream.OffsetSpecification;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.impl.SinkAppendingMessageHandler;
import rsocket.learning.serialization.Deserializer;

@Module
public class RabbitModule {
	private static final int PRICE_DATA_QUEUE_CAPACITY = 5000;
	private static final String PRICES_STREAM = "prices_stream";
	private static final String FALLBACK_RABBIT_URI = "rabbitmq-stream://rabbit:rabbit@localhost:5552/%2f";

	@Provides
	Environment environment() {
		return Environment.builder().uri(getRabbitURI()).build();
	}

	@Singleton
	@Provides
	Sinks.Many<PriceData> priceDataSink() {
		var pricesUpdatesQueue = new ArrayBlockingQueue<PriceData>(PRICE_DATA_QUEUE_CAPACITY);
		return Sinks.many().unicast().onBackpressureBuffer(pricesUpdatesQueue);
	}

	@Provides
	MessageHandler messageHandler(Deserializer deserializer, Sinks.Many<PriceData> priceDataSink) {
		return new SinkAppendingMessageHandler<>(deserializer, priceDataSink, PriceData.class);
	}

	@Singleton
	@Provides
	Flux<PriceData> priceDataStream(
			Environment environment,
			Sinks.Many<PriceData> priceDataSink,
			MessageHandler messageHandler
	) {
		var pricesConsumer = environment.consumerBuilder()
				.stream(PRICES_STREAM)
				.offset(OffsetSpecification.next())
				.noTrackingStrategy()
				.messageHandler(messageHandler)
				.build();
		return priceDataSink.asFlux()
				.doOnError(ignoredError -> pricesConsumer.close())
				.share();
	}

	private String getRabbitURI() {
		return Optional.ofNullable(System.getenv("RABBITMQ_URI")).orElse(FALLBACK_RABBIT_URI);
	}

}
