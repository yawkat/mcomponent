package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public class ScoreComponentValue implements ComponentValue {
    private final String objective;
    private final String player;

    public ScoreComponentValue(String objective, String player) {
        this.objective = objective;
        this.player = player;
    }

    public String getObjective() {
        return objective;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void applyToJson(JsonObject target) {
        JsonObject self = new JsonObject();
        self.addProperty("objective", getObjective());
        self.addProperty("name", getPlayer());
        target.add("score", self);
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.name("score");
        writer.beginObject();
        writer.name("objective");
        writer.value(getObjective());
        writer.name("name");
        writer.value(getPlayer());
        writer.endObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScoreComponentValue that = (ScoreComponentValue) o;

        if (!objective.equals(that.objective)) {
            return false;
        }
        if (!player.equals(that.player)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = objective.hashCode();
        result = 31 * result + player.hashCode();
        return result;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
