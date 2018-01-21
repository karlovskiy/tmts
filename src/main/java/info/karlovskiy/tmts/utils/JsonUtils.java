package info.karlovskiy.tmts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

    public static final String JSON_CONTENT_TYPE = "application/json";

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> String toJSON(T obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Convert to JSON error", e);
        }
    }

    private JsonUtils() {
    }
}
