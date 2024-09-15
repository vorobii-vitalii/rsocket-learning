package rsocket.learning;

import java.time.LocalDateTime;

import com.google.gson.GsonBuilder;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;
import rsocket.learning.prices.impl.DemoPricesProvider;
import rsocket.learning.serialization.json.JsonPayloadDataDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataSerializer;
import rsocket.learning.socket.PricesStreamingRSocket;
import rsocket.learning.utils.LocalDateTimeTypeAdapter;

public class PricesStreamingServer {
	private static final int SERVER_PORT = 7878;

	public static void main(String[] args) {
		var pricesProvider = new DemoPricesProvider();
		var gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		var serializer = new JsonPayloadDataSerializer(gson);
		var deserializer = new JsonPayloadDataDeserializer(gson);

		var pricesStream = pricesProvider.subscribeToPrices().share();
		RSocketServer.create((payload, socket) -> Mono.just(new PricesStreamingRSocket(deserializer, serializer, pricesStream)))
				.payloadDecoder(PayloadDecoder.ZERO_COPY)
				.bind(TcpServerTransport.create(SERVER_PORT))
				.block()
				.onClose()
				.block();
	}

}
