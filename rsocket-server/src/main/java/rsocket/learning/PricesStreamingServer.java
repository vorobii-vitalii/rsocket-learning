package rsocket.learning;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.gson.GsonBuilder;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.impl.SinkAppendingMessageHandler;
import rsocket.learning.prices.impl.StockPriceReaderImpl;
import rsocket.learning.serialization.json.JsonDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataSerializer;
import rsocket.learning.socket.PricesStreamingRSocket;
import rsocket.learning.utils.LocalDateTimeTypeAdapter;

@Slf4j
public class PricesStreamingServer {
	private static final String PRICES_STREAM = "prices_stream";
	private static final int PRICE_DATA_QUEUE_CAPACITY = 5000;
	private static final int SERVER_PORT = 7878;
	private static final String FALLBACK_RABBIT_URI = "rabbitmq-stream://rabbit:rabbit@localhost:5552/%2f";

	private static String getRabbitURI() {
		return Optional.ofNullable(System.getenv("RABBITMQ_URI")).orElse(FALLBACK_RABBIT_URI);
	}

	public static void main(String[] args) {
		try {
			var gson = new GsonBuilder().setPrettyPrinting()
					.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
					.create();
			var serializer = new JsonPayloadDataSerializer(gson);
			var deserializer = new JsonPayloadDataDeserializer(gson);

			var environment = Environment.builder().uri(getRabbitURI()).build();
			log.info("URI = {}", getRabbitURI());
			var pricesUpdatesQueue = new ArrayBlockingQueue<PriceData>(PRICE_DATA_QUEUE_CAPACITY);
			Sinks.Many<PriceData> taskSink = Sinks.many().unicast().onBackpressureBuffer(pricesUpdatesQueue);
			var pricesConsumer = environment.consumerBuilder()
					.stream(PRICES_STREAM)
					.offset(OffsetSpecification.next())
					.noTrackingStrategy()
					.messageHandler(new SinkAppendingMessageHandler<>(new JsonDeserializer(gson), taskSink, PriceData.class))
					.build();
			var pricesStream = taskSink.asFlux()
					.doOnError(ignoredError -> pricesConsumer.close())
					.share();
			log.info("Starting server");
			SocketAcceptor socketAcceptor = (payload, socket) -> {
				var stockPriceReader = new StockPriceReaderImpl(pricesStream);
				return Mono.just(new PricesStreamingRSocket(deserializer, serializer, stockPriceReader));
			};
			Objects.requireNonNull(
							RSocketServer.create(socketAcceptor)
									.payloadDecoder(PayloadDecoder.ZERO_COPY)
									.bind(TcpServerTransport.create(SERVER_PORT))
									.block())
					.onClose()
					.block();
		}
		catch (Exception e) {
			log.error("Error", e);
			System.exit(1);
		}
	}

}
