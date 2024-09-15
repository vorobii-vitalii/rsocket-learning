package rsocket.learning.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import io.rsocket.util.DefaultPayload;

class TestJsonPayloadDataDeserializer {

	JsonPayloadDataDeserializer deserializer = new JsonPayloadDataDeserializer(new Gson());

	@Test
	void deserialize() {
		var actualUser = deserializer.deserialize(DefaultPayload.create("{\"name\": \"Alex\", \"age\": 24}"), User.class);
		assertThat(actualUser).isEqualTo(new User("Alex", 24));
	}

	record User(String name, int age) {
	}

}
