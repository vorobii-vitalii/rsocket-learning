package rsocket.learning;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.gson.GsonBuilder;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.impl.JsonPriceDataDeserializer;
import rsocket.learning.prices.impl.SinkAppendingMessageHandler;
import rsocket.learning.prices.impl.QueueReadingFluxCreator;
import rsocket.learning.prices.impl.StockPriceReaderImpl;
import rsocket.learning.serialization.json.JsonPayloadDataDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataSerializer;
import rsocket.learning.socket.PricesStreamingRSocket;
import rsocket.learning.utils.LocalDateTimeTypeAdapter;

public class PricesStreamingServer {
	public static final String PRICES_STREAM = "prices_stream";
	public static final int PRICE_DATA_QUEUE_CAPACITY = 5000;
	private static final int SERVER_PORT = 7878;

	public static void main(String[] args) {
		var gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		var serializer = new JsonPayloadDataSerializer(gson);
		var deserializer = new JsonPayloadDataDeserializer(gson);

		var environment = Environment.builder()
				.uri("rabbitmq-stream://rabbit:rabbit@localhost:5552/%2f")
				.build();
		var pricesUpdatesQueue = new ArrayBlockingQueue<PriceData>(PRICE_DATA_QUEUE_CAPACITY);
		Sinks.Many<PriceData> taskSink = Sinks.many().unicast().onBackpressureBuffer(pricesUpdatesQueue);
		var pricesConsumer = environment.consumerBuilder()
				.stream(PRICES_STREAM)
				.offset(OffsetSpecification.next())
				.noTrackingStrategy()
				.messageHandler(new SinkAppendingMessageHandler(new JsonPriceDataDeserializer(gson), taskSink))
				.build();

		var pricesStream = taskSink.asFlux()
				.doOnError(err -> pricesConsumer.close())
				.share();

		Objects.requireNonNull(
						RSocketServer.create(
										(payload, socket) -> {
											var stockPriceReader = new StockPriceReaderImpl(pricesStream);
											return Mono.just(new PricesStreamingRSocket(deserializer, serializer, stockPriceReader));
										})
								.payloadDecoder(PayloadDecoder.ZERO_COPY)
								.bind(TcpServerTransport.create(SERVER_PORT))
								.block())
				.onClose()
				.block();
	}

}
