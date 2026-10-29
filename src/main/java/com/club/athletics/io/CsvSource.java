package com.club.athletics.io;

import java.io.IOException;
import java.io.Reader;

//opens one csv file from wherever the data is stored
@FunctionalInterface
public interface CsvSource {

    Reader open(String fileName) throws IOException;
}
