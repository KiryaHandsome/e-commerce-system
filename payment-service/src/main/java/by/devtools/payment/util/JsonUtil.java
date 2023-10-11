package by.devtools.payment.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static String toJson(Object data) {
        return objectMapper.writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> type) {
        return objectMapper.readValue(json, type);
    }
}
