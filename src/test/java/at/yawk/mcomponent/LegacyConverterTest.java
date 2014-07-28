package at.yawk.mcomponent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LegacyConverterTest {
    @Test
    public void testLegacyConvertBase() {
        assertEquals("{\"text\":\"Hi there \",\"extra\":[{\"text\":\"mate\",\"bold\":true,\"color\":\"blue\"},\"!\"]," +
                     "\"color\":\"red\"}",
                     LegacyConverter.convertAndMinimize("§cHi there §9§lmate§c!").toString());
    }
}