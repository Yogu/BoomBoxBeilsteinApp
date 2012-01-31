package de.gaglepinj.boombox.utils;

import java.lang.reflect.Type;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonFactory {
	private GsonFactory() {
	}

	private static GsonBuilder builder;

	private static GsonBuilder getBuilder() {
		if (builder == null) {
			builder = new GsonBuilder();
			builder.registerTypeAdapter(Instant.class, new InstantDeserializer());
			builder.registerTypeAdapter(Instant.class, new InstantSerializer());
			builder.registerTypeAdapter(Duration.class, new DurationDeserializer());
			builder.registerTypeAdapter(Duration.class, new DurationSerializer());
		}
		return builder;
	}

	public static Gson createGson() {
		return getBuilder().setPrettyPrinting().create();
	}

	private static class InstantDeserializer implements JsonDeserializer<Instant> {
		public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			return new Instant(json.getAsJsonPrimitive().getAsLong() * 1000);
		}
	}

	private static class InstantSerializer implements JsonSerializer<Instant> {
		public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive((int)(src.getMillis() / 1000));
		}
	}

	private static class DurationDeserializer implements JsonDeserializer<Duration> {
		public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			return new Duration(json.getAsJsonPrimitive().getAsLong());
		}
	}

	private static class DurationSerializer implements JsonSerializer<Duration> {
		public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getMillis());
		}
	}
}
