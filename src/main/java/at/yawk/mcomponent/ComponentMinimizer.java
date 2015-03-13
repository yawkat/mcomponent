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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yawkat
 */
public final class ComponentMinimizer {
    private static final Style baseStyle = Style.DEFAULT;
    static final ComponentMinimizer instance = new ComponentMinimizer();

    public static ComponentMinimizer getInstance() {
        return instance;
    }

    public Component minimize(Component component) {
        if (component instanceof BaseComponent) {
            return minimize((BaseComponent) component);
        }
        return component;
    }

    public BaseComponent minimize(BaseComponent component) {
        //noinspection StatementWithEmptyBody
        while (pass(component)) {}
        return component;
    }

    private boolean pass(BaseComponent component) {
        boolean modified = false;
        while (independentPass(component)) {
            modified = true;
        }
        modified |= passEscalateColorDeep(component);
        modified |= passEscalateFirstDeep(component);
        if (modified) {
            passInheritStyleDeep(baseStyle, component);
            return true;
        }
        return false;
    }

    private boolean independentPass(BaseComponent component) {
        AtomicBoolean modifiedAtom = new AtomicBoolean(false);
        component.children.parallelStream()
                .filter(c -> c instanceof BaseComponent)
                .forEach(c -> modifiedAtom.compareAndSet(false, independentPass((BaseComponent) c)));
        boolean modified = modifiedAtom.get();
        for (int i = 0; i < component.children.size(); i++) {
            Component child = component.children.get(i);
            Component min = minifyBasic(child);
            if (child != min) {
                modified = true;
                component.children.set(i, min);
            }
        }
        modified |= passSingleValueFlatten(component);
        modified |= passStringFlatten(component);
        modified |= passRemoveEmpty(component.children);
        modified |= passJoin(component.children);
        modified |= passOptimizeActions(component);
        return modified;
    }

    private boolean passSingleValueFlatten(BaseComponent of) {
        if (of.value.isEmpty() && of.children.size() == 1) {
            Component onlyChild = of.children.get(0);
            if (onlyChild instanceof BaseComponent) {
                of.children = ((BaseComponent) onlyChild).children;
                of.value = ((BaseComponent) onlyChild).value;
                of.events.addAll(((BaseComponent) onlyChild).events);
                return true;
            }
        }
        return false;
    }

    private boolean passInheritStyleDeep(Style parent, BaseComponent component) {
        Style newStyle = StyleOperations.removeMatching(component.style, parent);
        boolean modified = !component.style.equals(newStyle);
        component.style = newStyle;
        Style combined = null;
        for (Component child : component.children) {
            if (child instanceof BaseComponent) {
                if (combined == null) {
                    combined = StyleOperations.inherit(component.style, parent);
                }
                modified |= passInheritStyleDeep(combined, (BaseComponent) child);
            }
        }
        return modified;
    }

    private boolean passStringFlatten(BaseComponent component) {
        while (!component.children.isEmpty()) {
            Component firstChild = component.children.get(0);
            if (firstChild instanceof StringComponent &&
                component.value instanceof StringComponentValue) {
                component.children.remove(0);
                component.value = new StringComponentValue(((StringComponentValue) component.value).getValue() +
                                                           ((StringComponent) firstChild).getValue());
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean passJoin(List<Component> components) {
        boolean modified = false;
        int i = 0;
        while (i < components.size() - 1) {
            Component a = components.get(i);
            Component b = components.get(i + 1);
            Component joined = tryJoin(a, b);
            if (joined != null) {
                components.set(i, joined);
                components.remove(i + 1);
                modified = true;
                if (i > 0) {
                    i--;
                }
            } else {
                i++;
            }
        }
        return modified;
    }

    private boolean passRemoveEmpty(List<? extends Component> components) {
        boolean modified = false;
        for (Iterator<? extends Component> iterator = components.iterator(); iterator.hasNext(); ) {
            Component component = iterator.next();
            if (component.isEmpty()) {
                modified = true;
                iterator.remove();
            }
        }
        return modified;
    }

    private boolean passOptimizeActions(BaseComponent component) {
        boolean modified = false;
        for (Event event : new ArrayList<>(component.events)) {
            if (event instanceof BaseEvent) {
                Action action = ((BaseEvent) event).getAction();
                if (action instanceof BaseAction) {
                    Component val = ((BaseAction) action).getValue();
                    if (val instanceof BaseComponent) {
                        BaseComponent minimized = minimize((BaseComponent) val);
                        if (!minimized.equals(val)) {
                            component.events.remove(event);
                            component.events.add(new BaseEvent(((BaseEvent) event).getType(),
                                                               new BaseAction(((BaseAction) action).getType(),
                                                                              minimized)));
                            modified = true;
                        }
                    }
                }
            }
        }
        return modified;
    }

    private boolean passEscalateFirstDeep(BaseComponent component) {
        component.children.parallelStream()
                .filter(c -> c instanceof BaseComponent)
                .forEach(c -> passEscalateFirstDeep((BaseComponent) c));
        if (!component.children.isEmpty() && component.value.isEmpty()) {
            Component first = component.children.get(0);
            if (first instanceof StringComponent) {
                component.children.remove(0);
                component.value = new StringComponentValue(((StringComponent) first).getValue());
                return true;
            }
        }
        return false;
    }

    private boolean passEscalateColorDeep(BaseComponent component) {
        component.children.parallelStream()
                .filter(c -> c instanceof BaseComponent)
                .forEach(c -> passEscalateColorDeep((BaseComponent) c));
        if (!component.children.isEmpty() && component.value.isEmpty()) {
            Style common = getStyle(component.children.get(0));
            for (int i = 1; i < component.children.size(); i++) {
                common = StyleOperations.overridden(getStyle(component.children.get(i)), common);
            }
            common = StyleOperations.inherit(common, component.style);
            if (!component.style.equals(common)) {
                component.style = common;
                return true;
            }
        }
        return false;
    }

    private Style getStyle(Component component) {
        return component instanceof BaseComponent ? ((BaseComponent) component).style : Style.INHERIT;
    }

    private Component tryJoin(Component a, Component b) {
        if (a instanceof StringComponent) {
            if (b instanceof StringComponent) {
                return new StringComponent(((StringComponent) a).getValue() +
                                           ((StringComponent) b).getValue());
            }
        } else if (a instanceof BaseComponent) {
            if (b instanceof BaseComponent) {
                BaseComponent we = (BaseComponent) a;
                BaseComponent them = (BaseComponent) b;

                if (we.style.equals(them.getStyle()) &&
                    we.children.isEmpty() &&
                    them.getChildren().isEmpty() &&
                    we.events.equals(((BaseComponent) them.events).getEvents())) {
                    ComponentValue value = tryJoin(we.value, them.value);
                    if (value != null) {
                        return new BaseComponent(value, new ArrayList<>(), we.style, we.events);
                    }
                }
            }
        }
        return null;
    }

    private ComponentValue tryJoin(ComponentValue a, ComponentValue b) {
        if (a instanceof StringComponentValue && b instanceof StringComponentValue) {
            return new StringComponentValue(((StringComponentValue) a).getValue() +
                                            ((StringComponentValue) b).getValue());
        }
        return null;
    }

    private Component minifyBasic(Component component) {
        if (component instanceof BaseComponent) {
            BaseComponent bc = (BaseComponent) component;
            if (bc.children.isEmpty()
                && bc.value instanceof StringComponentValue
                && bc.style.equals(Style.INHERIT)
                && bc.events.isEmpty()) {
                return new StringComponent(((StringComponentValue) bc.value).getValue());
            }
        }
        return component;
    }
}
