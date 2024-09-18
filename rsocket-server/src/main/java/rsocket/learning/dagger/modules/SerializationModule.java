package rsocket.learning.dagger.modules;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import rsocket.learning.serialization.Deserializer;
import rsocket.learning.serialization.PayloadDataDeserializer;
import rsocket.learning.serialization.PayloadDataSerializer;
import rsocket.learning.serialization.json.JsonDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataDeserializer;
import rsocket.learning.serialization.json.JsonPayloadDataSerializer;
import rsocket.learning.utils.LocalDateTimeTypeAdapter;

@Module
public class SerializationModule {

	@Provides
	Gson gson() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
	}

	@Provides
	Deserializer deserializer(Gson gson) {
		return new JsonDeserializer(gson);
	}

	@Provides
	PayloadDataSerializer payloadDataSerializer(Gson gson) {
		return new JsonPayloadDataSerializer(gson);
	}

	@Provides
	PayloadDataDeserializer payloadDataDeserializer(Gson gson) {
		return new JsonPayloadDataDeserializer(gson);
	}

}
