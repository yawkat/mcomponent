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
