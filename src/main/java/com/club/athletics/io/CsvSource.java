package com.club.athletics.io;

import java.io.IOException;
import java.io.Reader;

/** Opens a named CSV file, wherever it lives (classpath, folder, or in-memory in tests). */
@FunctionalInterface
public interface CsvSource {

    Reader open(String fileName) throws IOException;
}
