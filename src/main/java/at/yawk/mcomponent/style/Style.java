package at.yawk.mcomponent.style;

import at.yawk.mcomponent.JsonSerializable;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public class Style implements JsonSerializable {
    public static final Style INHERIT = new Style(Color.INHERIT);

    private final FlagValue bold;
    private final FlagValue italic;
    private final FlagValue underline;
    private final FlagValue strikethrough;
    private final FlagValue obfuscated;
    private final Color color;

    public Style(FlagValue bold,
                 FlagValue italic,
                 FlagValue underline,
                 FlagValue strikethrough,
                 FlagValue obfuscated,
                 Color color) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.color = color;
    }

    public Style(Color color) {
        this(FlagValue.INHERIT,
             FlagValue.INHERIT,
             FlagValue.INHERIT,
             FlagValue.INHERIT,
             FlagValue.INHERIT,
             color);
    }

    public void applyToJson(JsonObject object) {
        bold.getValue().ifPresent(fl -> object.addProperty("bold", fl));
        italic.getValue().ifPresent(fl -> object.addProperty("italic", fl));
        underline.getValue().ifPresent(fl -> object.addProperty("underline", fl));
        strikethrough.getValue().ifPresent(fl -> object.addProperty("strikethrough", fl));
        obfuscated.getValue().ifPresent(fl -> object.addProperty("obfuscated", fl));
        color.getName().ifPresent(name -> object.addProperty("color", name));
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writeIfPresent(writer, "bold", bold);
        writeIfPresent(writer, "italic", italic);
        writeIfPresent(writer, "underline", underline);
        writeIfPresent(writer, "strikethrough", strikethrough);
        writeIfPresent(writer, "obfuscated", obfuscated);
        if (color.getName().isPresent()) {
            writer.name("color");
            writer.value(color.getName().get());
        }
    }

    private static void writeIfPresent(JsonWriter writer, String name, FlagValue value) throws IOException {
        if (value.getValue().isPresent()) {
            writer.name(name);
            writer.value(value.getValue().get());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Style style = (Style) o;

        if (bold != style.bold) {
            return false;
        }
        if (color != style.color) {
            return false;
        }
        if (italic != style.italic) {
            return false;
        }
        if (obfuscated != style.obfuscated) {
            return false;
        }
        if (strikethrough != style.strikethrough) {
            return false;
        }
        if (underline != style.underline) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bold.hashCode();
        result = 31 * result + italic.hashCode();
        result = 31 * result + underline.hashCode();
        result = 31 * result + strikethrough.hashCode();
        result = 31 * result + obfuscated.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }
}
