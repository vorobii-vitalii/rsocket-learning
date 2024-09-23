package citrus.rabbitmq.streams;

import java.nio.charset.StandardCharsets;

import org.citrusframework.common.ShutdownPhase;
import org.citrusframework.context.TestContext;
import org.citrusframework.message.Message;
import org.citrusframework.messaging.Producer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitMQStreamsProducer implements Producer, ShutdownPhase {
	private final String endpointName;
	private final com.rabbitmq.stream.Producer streamProducer;
	private final String streamName;

	public RabbitMQStreamsProducer(String endpointName, String streamName, com.rabbitmq.stream.Producer streamProducer) {
		this.endpointName = endpointName;
		this.streamProducer = streamProducer;
		this.streamName = streamName;
	}

	@Override
	public void send(Message message, TestContext context) {
		log.info("Sending {} to stream {}", message, streamName);
		var rabbitMessage = streamProducer.messageBuilder()
				.addData(message.getPayload().toString().getBytes(StandardCharsets.UTF_8)).build();
		streamProducer.send(rabbitMessage, confirmationStatus -> log.info("Confirmation status = {}", confirmationStatus));
	}

	@Override
	public String getName() {
		return "producer:" + endpointName;
	}

	@Override
	public void destroy() {
		streamProducer.close();
	}

}
