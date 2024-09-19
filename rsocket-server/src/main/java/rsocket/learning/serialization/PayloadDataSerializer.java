package rsocket.learning.serialization;

import io.rsocket.Payload;

public interface PayloadDataSerializer {
	Payload serializer(Object data);
}
