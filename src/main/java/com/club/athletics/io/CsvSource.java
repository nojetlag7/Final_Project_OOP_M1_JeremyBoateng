package com.club.athletics.io;

import java.io.IOException;
import java.io.Reader;

//opens one csv file, from the app resources or from a folder
@FunctionalInterface
public interface CsvSource {

    Reader open(String fileName) throws IOException;
}
