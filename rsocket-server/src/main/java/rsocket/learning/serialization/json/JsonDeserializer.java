package rsocket.learning.serialization.json;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import rsocket.learning.serialization.Deserializer;

@RequiredArgsConstructor
public class JsonDeserializer implements Deserializer {
	private final Gson gson;

	@Override
	public <T> T deserialize(byte[] payload, Class<T> type) {
		return gson.fromJson(new String(payload, StandardCharsets.UTF_8), type);
	}
}
