package at.yawk.mcomponent;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public interface JsonSerializable {
    void write(JsonWriter writer) throws IOException;
}
