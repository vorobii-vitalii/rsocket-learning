package rsocket.learning.serialization.json;

import com.google.gson.Gson;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rsocket.learning.serialization.PayloadDataSerializer;

@RequiredArgsConstructor
@Slf4j
public class JsonPayloadDataSerializer implements PayloadDataSerializer {
	private final Gson gson;

	@Override
	public Payload serializer(Object data) {
		final String json = gson.toJson(data);
		log.info("Object {} to json = {}", data, json);
		return DefaultPayload.create(json);
	}
}
