package rsocket.learning.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class TestJsonPayloadDataSerializer {

	JsonPayloadDataSerializer serializer = new JsonPayloadDataSerializer(new Gson());

	@Test
	void serializer() {
		var actualPayload = serializer.serializer(new User("Alex"));
		assertThat(actualPayload.getDataUtf8()).isEqualTo("{\"name\":\"Alex\"}");
	}

	record User(String name) {
	}

}
