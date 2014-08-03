package at.yawk.mcomponent;

import at.yawk.mcomponent.action.BaseAction;
import at.yawk.mcomponent.action.BaseEvent;
import at.yawk.mcomponent.action.Event;
import at.yawk.mcomponent.style.Color;
import at.yawk.mcomponent.style.FlagKey;
import at.yawk.mcomponent.style.FlagValue;
import at.yawk.mcomponent.style.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yawkat
 */
public class LegacyConverter {
    private static final int COLOR_CACHE_SIZE = 'r' + 1;
    private static final Pattern URL = Pattern.compile( // lol regex
            "(https?://)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|\\w+\\.\\w{2,8})(\\S+[^\\s\\.\"'])?");
    private static final Color[] COLORS = new Color[COLOR_CACHE_SIZE];
    private static final LegacyConverter instance = new LegacyConverter();

    static {
        COLORS['0'] = Color.BLACK;
        COLORS['1'] = Color.DARK_BLUE;
        COLORS['2'] = Color.DARK_GREEN;
        COLORS['3'] = Color.DARK_AQUA;
        COLORS['4'] = Color.DARK_RED;
        COLORS['5'] = Color.DARK_PURPLE;
        COLORS['6'] = Color.GOLD;
        COLORS['7'] = Color.GRAY;
        COLORS['8'] = Color.DARK_GRAY;
        COLORS['9'] = Color.BLUE;
        COLORS['a'] = Color.GREEN;
        COLORS['b'] = Color.AQUA;
        COLORS['c'] = Color.RED;
        COLORS['d'] = Color.LIGHT_PURPLE;
        COLORS['e'] = Color.YELLOW;
        COLORS['f'] = Color.WHITE;
        COLORS['k'] = Color.OBFUSCATED;
        COLORS['l'] = Color.BOLD;
        COLORS['m'] = Color.STRIKETHROUGH;
        COLORS['n'] = Color.UNDERLINE;
        COLORS['o'] = Color.ITALIC;
        COLORS['r'] = Color.RESET;
    }

    private LegacyConverter() {}

    public static BaseComponent convertAndMinimize(CharSequence sequence) {
        return instance.convertMin(sequence);
    }

    public static BaseComponent convert(CharSequence sequence) {
        return instance.convert0(sequence);
    }

    private BaseComponent convertMin(CharSequence sequence) {
        BaseComponent converted = convert0(sequence);
        return ComponentMinimizer.instance.minimize(converted);
    }

    private BaseComponent convert0(CharSequence sequence) {
        List<Component> components = new ArrayList<>();
        StringBuilder part = new StringBuilder();
        Style style = Style.INHERIT;
        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c == '§' && sequence.length() > i + 1) {
                char d = Character.toLowerCase(sequence.charAt(i + 1));
                if (d < COLOR_CACHE_SIZE) {
                    appendStyledPart(components, part, style);
                    part.setLength(0);

                    Color color = COLORS[d];
                    if (color == Color.RESET) {
                        style = Style.DEFAULT;
                    } else {
                        Optional<FlagKey> flag = color.getFlag();
                        if (flag.isPresent()) {
                            style = style.withFlag(flag.get(), FlagValue.TRUE);
                        } else {
                            style = new Style(color);
                        }
                    }
                    i++;
                    continue;
                }
            }
            part.append(c);
        }
        appendStyledPart(components, part, style);
        return new BaseComponent(ComponentValue.EMPTY, components);
    }

    private void appendStyledPart(List<Component> components, StringBuilder part, Style style) {
        Matcher urlMatcher = URL.matcher(part);
        int x = 0;
        while (urlMatcher.find()) {
            String before = part.substring(x, urlMatcher.start());
            if (!before.isEmpty()) { // don't need to leave everything for the minifier
                components.add(new BaseComponent(new StringComponentValue(before), style));
            }
            String in = urlMatcher.group();
            String url = in;
            if (urlMatcher.group(1) == null) {
                url = "http://" + url;
            }
            Event event = new BaseEvent(BaseEvent.Type.CLICK, new BaseAction(BaseAction.Type.OPEN_URL,
                                                                             new StringComponent(url)));
            components.add(new BaseComponent(new StringComponentValue(in),
                                             Collections.emptyList(),
                                             style,
                                             Collections.singleton(event)));
            x = urlMatcher.end();
        }
        String after = part.substring(x);
        if (!after.isEmpty()) { // don't need to leave everything for the minifier
            components.add(new BaseComponent(new StringComponentValue(after), style));
        }
    }
}
