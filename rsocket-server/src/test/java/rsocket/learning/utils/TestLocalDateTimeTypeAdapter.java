package rsocket.learning.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonPrimitive;

class TestLocalDateTimeTypeAdapter {

	LocalDateTimeTypeAdapter dateTimeTypeAdapter = new LocalDateTimeTypeAdapter();

	@Test
	void serialize() {
		LocalDateTime dateTime = LocalDateTime.of(2020, Month.OCTOBER, 31, 1, 30);
		assertThat(dateTimeTypeAdapter.serialize(dateTime, null, null).getAsString())
				.isEqualTo("2020-10-31T01:30");
	}

	@Test
	void deserialize() {
		assertThat(dateTimeTypeAdapter.deserialize(new JsonPrimitive("2020-10-31T01:30"), null, null))
				.isEqualTo(LocalDateTime.of(2020, Month.OCTOBER, 31, 1, 30));
	}

}
