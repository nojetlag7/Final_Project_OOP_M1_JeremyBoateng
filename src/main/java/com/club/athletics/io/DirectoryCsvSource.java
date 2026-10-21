package com.club.athletics.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loads files from a folder given on the command line, so other data sets can be used. */
public class DirectoryCsvSource implements CsvSource {

    private final Path directory;

    public DirectoryCsvSource(Path directory) {
        this.directory = directory;
    }

    @Override
    public Reader open(String fileName) throws IOException {
        return Files.newBufferedReader(directory.resolve(fileName), StandardCharsets.UTF_8);
    }
}
