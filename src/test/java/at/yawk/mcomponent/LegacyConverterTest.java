package at.yawk.mcomponent;

import at.yawk.mcomponent.action.BaseAction;
import at.yawk.mcomponent.action.BaseEvent;
import at.yawk.mcomponent.style.Color;
import at.yawk.mcomponent.style.FlagKey;
import at.yawk.mcomponent.style.FlagValue;
import at.yawk.mcomponent.style.Style;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LegacyConverterTest {
    @Test
    public void testLegacyConvertBase() {
        assertEquals("{\"text\":\"Hi there \",\"extra\":[{\"text\":\"mate\",\"bold\":true,\"color\":\"blue\"},\"!\"]," +
                     "\"color\":\"red\"}",
                     LegacyConverter.convertAndMinimize("§cHi there §9§lmate§c!").toString());
    }

    @Test
    public void testLegacyConvertLink() {
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Website: \",\"bold\":true},{\"text\":\"yawk.at\"," +
                     "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://yawk.at\"}}],\"color\":\"blue\"}",
                     LegacyConverter.convertAndMinimize("§9§lWebsite: §9yawk.at").toString());
    }

    @Test
    public void testToLegacyText() throws Exception {
        Component component = new BaseComponent(
                new StringComponentValue("Test"),
                Arrays.asList(
                        new StringComponent("String child"),
                        new BaseComponent(
                                new StringComponentValue("Base child"),
                                new Style(Color.BLUE).withFlag(FlagKey.ITALIC, FlagValue.TRUE)
                        )
                ),
                new Style(Color.RED).withFlag(FlagKey.BOLD, FlagValue.TRUE),
                Collections.singleton(
                        new BaseEvent(BaseEvent.Type.CLICK,
                                      new BaseAction(BaseAction.Type.OPEN_URL, new StringComponent("Test")))
                )
        );
        assertEquals("§c§lTestString child§9§l§oBase child", LegacyConverter.toLegacyString(component));
    }
}