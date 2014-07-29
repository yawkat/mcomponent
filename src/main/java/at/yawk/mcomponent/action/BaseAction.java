package at.yawk.mcomponent.action;

/**
 * @author yawkat
 */
public class BaseAction implements Action {
    private final Type type;
    private final String value;

    public BaseAction(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseAction that = (BaseAction) o;

        if (type != that.type) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type + ":'" + value + "'";
    }

    public static enum Type {
        OPEN_URL("open_url"),
        OPEN_FILE("open_file"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SHOW_ITEM("show_item"),
        INSERT("insertion"),
        ;

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
