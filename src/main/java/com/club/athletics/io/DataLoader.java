package com.club.athletics.io;

import java.io.IOException;

//loads the club data, the rest of the app doesn't care if it's csv
public interface DataLoader {

    ClubData load() throws IOException;
}
