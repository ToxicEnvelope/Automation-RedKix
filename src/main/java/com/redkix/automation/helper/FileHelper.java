package com.redkix.automation.helper;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class FileHelper {

    public static File getTmpFile() {
        try {
            File tmpFile = File.createTempFile(randomAlphabetic(5).toLowerCase(), ".txt");
            List<String> content = IntStream.range(0, 5).boxed().
                    map(i -> randomAlphabetic(5)).
                    collect(Collectors.toList());
            FileUtils.writeLines(tmpFile, content);
            return tmpFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
