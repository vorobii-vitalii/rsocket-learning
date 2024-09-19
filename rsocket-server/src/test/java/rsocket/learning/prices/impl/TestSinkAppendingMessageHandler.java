package rsocket.learning.prices.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;

import reactor.core.publisher.Sinks;
import rsocket.learning.serialization.Deserializer;

@ExtendWith(MockitoExtension.class)
class TestSinkAppendingMessageHandler {
	private static final byte[] MESSAGE_PAYLOAD = {1, 2, 3};
	@Mock
	Deserializer deserializer;

	@Mock
	Sinks.Many<Integer> sink;

	@Mock
	MessageHandler.Context context;

	@Mock
	Message message;

	SinkAppendingMessageHandler<Integer> messageHandler;

	@BeforeEach
	void init() {
		messageHandler = new SinkAppendingMessageHandler<>(deserializer, sink, Integer.class);
	}

	@Test
	void givenDeserializationError() {
		when(message.getBodyAsBinary()).thenReturn(MESSAGE_PAYLOAD);
		when(deserializer.deserialize(MESSAGE_PAYLOAD, Integer.class)).thenThrow(new RuntimeException());
		assertDoesNotThrow(() -> messageHandler.handle(context, message));
	}

	@Test
	void givenMessageSuccessfullyDeserialized() {
		when(message.getBodyAsBinary()).thenReturn(MESSAGE_PAYLOAD);
		when(deserializer.deserialize(MESSAGE_PAYLOAD, Integer.class)).thenReturn(123);
		messageHandler.handle(context, message);
		verify(sink).tryEmitNext(123);
	}

}