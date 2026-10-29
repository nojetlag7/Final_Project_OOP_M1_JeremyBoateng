package com.club.athletics.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

//reading the csv files that are bundled with the app
//so it works no matter which folder you start it from
public class ClasspathCsvSource implements CsvSource {

    private final String folder;

    public ClasspathCsvSource(String folder) {
        this.folder = folder.endsWith("/") ? folder : folder + "/";
    }

    @Override
    public Reader open(String fileName) throws IOException {
        String resource = folder + fileName;
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new FileNotFoundException("Classpath resource not found: " + resource);
        }
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
