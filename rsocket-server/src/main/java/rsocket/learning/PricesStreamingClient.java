package rsocket.learning;

import java.time.LocalDateTime;

import com.google.gson.GsonBuilder;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rsocket.learning.dto.PriceUpdate;
import rsocket.learning.dto.PricesStreamRequest;
import rsocket.learning.serialization.json.JsonPayloadDataDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataSerializer;
import rsocket.learning.utils.LocalDateTimeTypeAdapter;

@Slf4j
public class PricesStreamingClient {

	@SneakyThrows
	public static void main(String[] args) {
		var gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
		var serializer = new JsonPayloadDataSerializer(gson);
		var deserializer = new JsonPayloadDataDeserializer(gson);

		RSocket clientRSocket =
				RSocketConnector.create()
						.payloadDecoder(PayloadDecoder.ZERO_COPY)
						.connect(TcpClientTransport.create(32770))
						.block();

		var pricesStreamRequest = PricesStreamRequest.builder().symbol("ABBN").build();

		Payload serializedRequest = serializer.serializer(pricesStreamRequest);
		RSocketClient.from(clientRSocket)
				.requestStream(Mono.just(serializedRequest))
				.doOnSubscribe(s -> log.info("Executing Request {}", serializedRequest.getDataUtf8()))
				.map(v -> deserializer.deserialize(v, PriceUpdate.class))
				.subscribe(v -> log.info("Price update: {}", v));
		System.in.read();
	}

}
