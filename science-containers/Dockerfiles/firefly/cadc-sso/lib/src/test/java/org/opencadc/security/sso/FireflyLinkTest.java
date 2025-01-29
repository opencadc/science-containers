package org.opencadc.security.sso;

import edu.caltech.ipac.firefly.server.security.SsoAdapter; // Import from Firefly JAR
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class FireflyJarTest {

    private static final String FIRELY_JAR_PATH = "build/firefly/jars/build/firefly.jar"; // Expected JAR path

    @Test
    void testFireflyJarExists() {
        File jarFile = new File(FIRELY_JAR_PATH);
        assertTrue(jarFile.exists(), "Firefly JAR was not found at " + FIRELY_JAR_PATH);
    }

    @Test
    void testFireflyJarLinked() {
        try {
            // Attempt to load a class from the Firefly JAR
            Class<?> ssoAdapterClass = Class.forName("edu.caltech.ipac.firefly.server.security.SsoAdapter");

            // If the class loads successfully, the JAR is linked
            assertNotNull(ssoAdapterClass, "Firefly JAR linking failed: SsoAdapter class not found.");
        } catch (ClassNotFoundException e) {
            fail("Firefly JAR linking failed: SsoAdapter class not found.");
        }
    }
}
