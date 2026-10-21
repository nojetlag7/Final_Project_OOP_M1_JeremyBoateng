package com.club.athletics.io;

import java.io.IOException;

/** Source of the club's initial data; the rest of the app does not know the storage format. */
public interface DataLoader {

    ClubData load() throws IOException;
}
