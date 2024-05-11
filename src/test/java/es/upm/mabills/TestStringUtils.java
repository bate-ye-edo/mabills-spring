package es.upm.mabills;

import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStringUtils {
    public static void assertNotBlank(String str) {
        assertNotNull(str);
        assertTrue(StringUtils.isNotBlank(str));
    }
}
