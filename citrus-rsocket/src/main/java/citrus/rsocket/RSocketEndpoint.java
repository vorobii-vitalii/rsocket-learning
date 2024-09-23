package citrus.rsocket;

import java.util.Map;

import org.citrusframework.common.ShutdownPhase;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.endpoint.direct.DirectEndpointBuilder;
import org.citrusframework.message.DefaultMessageQueue;
import org.citrusframework.message.MessageQueue;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;
import org.citrusframework.messaging.SelectiveConsumer;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;

public class RSocketEndpoint extends AbstractEndpoint implements ShutdownPhase {
	private final MessageQueue messageQueue;
	private final RSocketEndpointConfiguration endpointConfiguration;
	private final SelectiveConsumer responsesQueueConsumer;
	private RSocketProducer rSocketProducer;

	public RSocketEndpoint(RSocketEndpointConfiguration endpointConfiguration) {
		super(endpointConfiguration);
		this.messageQueue = new DefaultMessageQueue(endpointConfiguration.getServerResponsesQueueName());
		this.endpointConfiguration = endpointConfiguration;
		this.responsesQueueConsumer = new DirectEndpointBuilder()
				.queue(messageQueue)
				.build()
				.createConsumer();
	}

	@Override
	public Producer createProducer() {
		if (rSocketProducer == null) {
			rSocketProducer = new RSocketProducer(
					endpointConfiguration.getName(),
					createRSocketConnector(endpointConfiguration),
					messageQueue,
					Map.of(
							RSocketRequestType.REQUEST_RESPONSE, RSocketClient::requestResponse,
							RSocketRequestType.REQUEST_STREAM, RSocketClient::requestStream
					));
		}
		return rSocketProducer;
	}

	@Override
	public Consumer createConsumer() {
		return responsesQueueConsumer;
	}

	@Override
	public void destroy() {
		if (rSocketProducer != null) {
			rSocketProducer.destroy();
		}
	}

	private RSocket createRSocketConnector(RSocketEndpointConfiguration endpointConfiguration) {
		return RSocketConnector.create()
				.payloadDecoder(PayloadDecoder.ZERO_COPY)
				.connect(TcpClientTransport.create(endpointConfiguration.getHost(), endpointConfiguration.getPort()))
				.block();
	}

}
