package rsocket.learning.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class TestJsonDeserializer {

	JsonDeserializer jsonDeserializer = new JsonDeserializer(new Gson());

	@Test
	void deserialize() {
		var actualUser = jsonDeserializer.deserialize("{\"name\": \"Alex\", \"age\": 24}".getBytes(StandardCharsets.UTF_8), User.class);
		assertThat(actualUser).isEqualTo(new User("Alex", 24));
	}

	record User(String name, int age) {
	}

}
