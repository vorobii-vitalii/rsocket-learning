package citrus.rabbitmq.streams;

import org.citrusframework.context.TestContext;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.AbstractMessageConsumer;

public class RabbitMQStreamsConsumer extends AbstractMessageConsumer {

	public RabbitMQStreamsConsumer(String name, RabbitMQStreamsEndpointConfiguration endpointConfiguration) {
		super(name, endpointConfiguration);
	}

	@Override
	public Message receive(TestContext context, long timeout) {
		return null;
	}
}
