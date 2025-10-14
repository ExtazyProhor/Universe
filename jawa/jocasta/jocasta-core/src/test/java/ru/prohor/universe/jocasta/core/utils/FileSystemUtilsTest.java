package ru.prohor.universe.jocasta.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

public class FileSystemUtilsTest {
    @Test
    void testUserDir() {
        String expectedString = System.getProperty("user.dir");

        FileSystemUtils.UserDir userDir = FileSystemUtils.userDir();
        Assertions.assertNotNull(userDir);

        Assertions.assertEquals(expectedString, userDir.asString());

        Path expectedPath = Path.of(expectedString);
        Assertions.assertEquals(expectedPath, userDir.asPath());

        File expectedFile = new File(expectedString);
        Assertions.assertEquals(expectedFile, userDir.asFile());
    }
}
