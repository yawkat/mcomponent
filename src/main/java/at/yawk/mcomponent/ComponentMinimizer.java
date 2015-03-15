/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import at.yawk.mcomponent.action.Action;
import at.yawk.mcomponent.action.BaseAction;
import at.yawk.mcomponent.action.BaseEvent;
import at.yawk.mcomponent.action.Event;
import at.yawk.mcomponent.style.Style;
import at.yawk.mcomponent.style.StyleOperations;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * @author yawkat
 */
public class ComponentMinimizer {
    private static final boolean debug =
            Boolean.getBoolean(ComponentMinimizer.class.getName() + ".debug");
    private static int depth = 0;

    private static void enter(int d) {
        if (debug) {
            depth += d;
        }
    }

    private static void log(int point, Object state) {
        if (debug) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.printf("%2d %s%n", point, state);
        }
    }

    private ComponentMinimizer() {}

    public static Component minimizeOne(Component component) {
        return minimize0(component);
    }

    public static List<Component> minimize(Component component) {
        Component min = minimize0(component);
        if (min instanceof BaseComponent &&
            ((BaseComponent) min).value.isEmpty() &&
            !hasStyle((BaseComponent) min)) {
            return ((BaseComponent) min).children;
        } else {
            return Collections.singletonList(min);
        }
    }

    private static Component minimize0(Component c) {
        if (c instanceof BaseComponent) {
            enter(1);
            Component then = pass((BaseComponent) c);
            log(11, then);
            enter(-1);
            return then;
        }
        return c;
    }

    private static Component pass(BaseComponent bc) {
        log(0, bc);
        Set<Event> events = new HashSet<>();
        for (Event event : bc.events) {
            if (event instanceof BaseEvent) {
                Action action = ((BaseEvent) event).getAction();
                if (action instanceof BaseAction) {
                    event = new BaseEvent(
                            ((BaseEvent) event).getType(),
                            new BaseAction(
                                    ((BaseAction) action).getType(),
                                    minimize0(((BaseAction) action).getValue())
                            )
                    );
                }
            }
            events.add(event);
        }
        bc.events = events;
        log(1, bc);

        passList(bc.children);
        log(2, bc);

        if (bc.value.isEmpty() && !bc.children.isEmpty()) {
            Component first = bc.children.get(0);
            if (first instanceof StringComponent) {
                bc.value = new StringComponentValue(((StringComponent) first).getValue());
                bc.children.clear();
            }
        }
        log(3, bc);

        if (!bc.children.isEmpty() &&
            bc.value instanceof StringComponentValue) {
            Component first = bc.children.get(0);
            if (first instanceof StringComponent) {
                // we merge into the child here instead of the value so that we can
                // reduce this component to an array if its at the root.
                bc.value = ComponentValue.EMPTY;
                bc.children.set(0, new StringComponent(
                        ((StringComponentValue) bc.value).getValue() +
                        ((StringComponent) first).getValue()
                ));
            }
        }
        log(4, bc);

        if (bc.children.isEmpty() && !hasStyle(bc)) {
            log(6, bc.value);
            return toComponent(bc.value);
        }
        if (bc.value.isEmpty() &&
            bc.children.size() == 1) {
            Component child = bc.children.get(0);
            if (!hasStyle(bc)) {
                return child;
            }
            if (child instanceof BaseComponent) {
                ((BaseComponent) child).style = StyleOperations.inherit(((BaseComponent) child).style, bc.style);
                return child;
            }
            if (child instanceof StringComponent) {
                bc.value = new StringComponentValue(((StringComponent) child).getValue());
                bc.children.clear();
            }
        }
        log(5, bc);
        return bc;
    }

    private static boolean hasStyle(BaseComponent bc) {
        return !bc.style.equals(Style.INHERIT) || !bc.events.isEmpty();
    }

    private static void passList(List<Component> l) {
        enter(1);
        log(7, l);
        for (int i = 0; i < l.size(); i++) {
            Component child = l.get(i);
            Component minimized = minimize0(child);
            if (child != minimized) {
                l.set(i, minimized);
            }
        }
        log(8, l);

        for (int i = 0; i < l.size() - 1; i++) {
            Component a = l.get(i);

            if (a.isEmpty()) {
                l.remove(i);
                i--;
                continue;
            }

            Component b = l.get(i + 1);
            Component merged = merge(a, b);
            if (merged != null) {
                merged = minimize0(merged);

                l.set(i, merged);
                l.remove(i + 1);
                // merge next too
                i--;
            }
        }
        log(9, l);
        // remove empty trailing elements
        if (!l.isEmpty() && l.get(l.size() - 1).isEmpty()) {
            l.remove(l.size() - 1);
        }
        log(10, l);
        enter(-1);
    }

    @Nullable
    private static Component merge(Component a, Component b) {
        if (a instanceof StringComponent &&
            b instanceof StringComponent) {
            return new StringComponent(((StringComponent) a).getValue() + ((StringComponent) b).getValue());
        }
        if (a instanceof BaseComponent &&
            b instanceof BaseComponent &&
            ((BaseComponent) a).style.equals(((BaseComponent) b).style) &&
            ((BaseComponent) a).events.equals(((BaseComponent) b).events)) {

            // merge b into a
            ((BaseComponent) a).children.add(toComponent(((BaseComponent) b).value));
            ((BaseComponent) a).children.addAll(((BaseComponent) b).children);
            return a;
        }
        return null;
    }

    private static Component toComponent(ComponentValue value) {
        if (value instanceof StringComponentValue) {
            return new StringComponent(((StringComponentValue) value).getValue());
        }
        return new BaseComponent(value);
    }
}
