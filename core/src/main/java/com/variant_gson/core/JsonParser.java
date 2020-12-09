package com.variant_gson.core;

import com.variant_gson.core.internal.Streams;
import com.variant_gson.core.stream.JsonReader;
import com.variant_gson.core.stream.JsonToken;
import com.variant_gson.core.stream.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A parser to parse Json into a parse tree of {@link JsonElement}s
 */
public final class JsonParser {
    /** @deprecated No need to instantiate this class, use the static methods instead. */
    @Deprecated
    public JsonParser() {}

    /**
     * Parses the specified JSON string into a parse tree
     *
     * @param json JSON text
     * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON
     * @throws JsonParseException if the specified text is not valid JSON
     */
    public static JsonElement parseString(String json) throws JsonSyntaxException {
        return parseReader(new StringReader(json));
    }

    /**
     * Parses the specified JSON string into a parse tree
     *
     * @param reader JSON text
     * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON
     * @throws JsonParseException if the specified text is not valid JSON
     */
    public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            JsonElement element = parseReader(jsonReader);
            if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            }
            return element;
        } catch (MalformedJsonException | NumberFormatException e) {
            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    /**
     * Returns the next value from the JSON stream as a parse tree.
     *
     * @throws JsonParseException if there is an IOException or if the specified
     *     text is not valid JSON
     */
    public static JsonElement parseReader(JsonReader reader)
            throws JsonIOException, JsonSyntaxException {
        boolean lenient = reader.isLenient();
        reader.setLenient(true);
        try {
            return Streams.parse(reader);
        } catch (StackOverflowError | OutOfMemoryError e) {
            throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
        } finally {
            reader.setLenient(lenient);
        }
    }

    /** @deprecated Use {@link JsonParser#parseString} */
    @Deprecated
    public JsonElement parse(String json) throws JsonSyntaxException {
        return parseString(json);
    }

    /** @deprecated Use {@link JsonParser#parseReader(Reader)} */
    @Deprecated
    public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
        return parseReader(json);
    }

    /** @deprecated Use {@link JsonParser#parseReader(JsonReader)} */
    @Deprecated
    public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
        return parseReader(json);
    }
}
