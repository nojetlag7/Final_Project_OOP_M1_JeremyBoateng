package com.club.athletics.io;

import java.io.IOException;

//loads the club data, doesn't care if it's csv or something else
public interface DataLoader {

    ClubData load() throws IOException;
}
