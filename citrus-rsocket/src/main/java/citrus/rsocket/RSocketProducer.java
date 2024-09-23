package citrus.rsocket;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiFunction;

import org.citrusframework.common.ShutdownPhase;
import org.citrusframework.context.TestContext;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.Message;
import org.citrusframework.message.MessageQueue;
import org.citrusframework.messaging.Producer;
import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class RSocketProducer implements Producer, ShutdownPhase {
	private final String producerName;
	private final RSocket socket;
	private final MessageQueue responsesQueue;
	private final Map<RSocketRequestType, BiFunction<RSocketClient, Mono<Payload>, ? extends Publisher<Payload>>> callFunctionByRequestType;

	public RSocketProducer(
			String producerName,
			RSocket socket,
			MessageQueue responsesQueue,
			Map<RSocketRequestType, BiFunction<RSocketClient, Mono<Payload>, ? extends Publisher<Payload>>> callFunctionByRequestType
	) {
		this.producerName = producerName;
		this.socket = socket;
		this.responsesQueue = responsesQueue;
		this.callFunctionByRequestType = callFunctionByRequestType;
	}

	@Override
	public void send(Message message, TestContext context) {
		var request = new RSocketRequest(message);
		log.info("Sending message {}", request);
		var requestMono = Mono.just(DefaultPayload.create(request.getPayload(String.class), StandardCharsets.UTF_8));
		var callFunction = callFunctionByRequestType.get(request.getRequestType());
		if (callFunction == null) {
			log.warn("Call function by request type = {} not found!", request.getRequestType());
			throw new IllegalStateException();
		}
		Flux.from(callFunction.apply(RSocketClient.from(socket), requestMono))
				.map(Payload::getDataUtf8)
				.subscribe(v -> responsesQueue.send(new DefaultMessage(v)));
	}

	@Override
	public String getName() {
		return "producer:" + producerName;
	}

	@Override
	public void destroy() {
		socket.dispose();
	}
}
