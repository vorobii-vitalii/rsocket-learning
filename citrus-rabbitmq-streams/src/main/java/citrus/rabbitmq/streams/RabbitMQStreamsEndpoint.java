package citrus.rabbitmq.streams;

import java.nio.charset.StandardCharsets;

import org.citrusframework.common.ShutdownPhase;
import org.citrusframework.endpoint.AbstractEndpoint;
import org.citrusframework.endpoint.direct.DirectEndpointBuilder;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.DefaultMessageQueue;
import org.citrusframework.messaging.Consumer;
import org.citrusframework.messaging.Producer;

import com.rabbitmq.stream.OffsetSpecification;

public class RabbitMQStreamsEndpoint extends AbstractEndpoint implements ShutdownPhase {
	private static final String MESSAGES_SUFFIX = "-messages";

	private final RabbitMQStreamsEndpointConfiguration endpointConfiguration;
	private RabbitMQStreamsProducer rabbitMQStreamsProducer;
	private com.rabbitmq.stream.Consumer rabbitStreamConsumer;
	private Consumer consumer;

	public RabbitMQStreamsEndpoint(RabbitMQStreamsEndpointConfiguration endpointConfiguration) {
		super(endpointConfiguration);
		this.endpointConfiguration = endpointConfiguration;
	}

	@Override
	public Producer createProducer() {
		if (rabbitMQStreamsProducer == null) {
			rabbitMQStreamsProducer = new RabbitMQStreamsProducer(
					endpointConfiguration.getName(),
					endpointConfiguration.getStreamName(),
					endpointConfiguration.getEnvironment()
							.producerBuilder()
							.stream(endpointConfiguration.getStreamName())
							.build());
		}
		return rabbitMQStreamsProducer;
	}

	@Override
	public Consumer createConsumer() {
		if (consumer == null) {
			var messagesQueue = new DefaultMessageQueue(endpointConfiguration.getName() + MESSAGES_SUFFIX);
			this.rabbitStreamConsumer = endpointConfiguration.getEnvironment().consumerBuilder()
					.stream(endpointConfiguration.getStreamName())
					.offset(OffsetSpecification.next())
					.noTrackingStrategy()
					.messageHandler((ignoredContext, message) -> {
						var payload = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
						messagesQueue.send(new DefaultMessage(payload));
					})
					.build();
			consumer = new DirectEndpointBuilder()
					.queue(messagesQueue)
					.build()
					.createConsumer();
		}
		return consumer;
	}

	@Override
	public void destroy() {
		if (rabbitMQStreamsProducer != null) {
			rabbitMQStreamsProducer.destroy();
		}
		if (rabbitStreamConsumer != null) {
			rabbitStreamConsumer.close();
		}

	}

}
