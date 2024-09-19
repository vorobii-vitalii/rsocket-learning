package rsocket.learning.serialization.json;

import com.google.gson.Gson;

import io.rsocket.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rsocket.learning.serialization.PayloadDataDeserializer;

@RequiredArgsConstructor
@Slf4j
public class JsonPayloadDataDeserializer implements PayloadDataDeserializer {
	private final Gson gson;

	@Override
	public <T> T deserialize(Payload payload, Class<T> type) {
		final String dataUtf8 = payload.getDataUtf8();
		log.info("Deserialized payload data: {} to type {}", dataUtf8, type);
		return gson.fromJson(dataUtf8, type);
	}
}
