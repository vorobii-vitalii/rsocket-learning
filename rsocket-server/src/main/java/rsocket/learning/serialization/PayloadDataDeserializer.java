package rsocket.learning.serialization;

import io.rsocket.Payload;

public interface PayloadDataDeserializer {
	<T> T deserialize(Payload payload, Class<T> type);
}
