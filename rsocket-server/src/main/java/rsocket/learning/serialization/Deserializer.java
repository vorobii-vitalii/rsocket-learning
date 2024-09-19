package rsocket.learning.serialization;

public interface Deserializer {
	<T> T deserialize(byte[] payload, Class<T> type);
}
