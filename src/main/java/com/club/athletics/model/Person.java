package com.club.athletics.model;

import java.time.LocalDate;
import java.util.Objects;

//shared info for athletes and coaches
public abstract class Person {

    private final String id;
    private final String name;
    private final LocalDate dateOfBirth;

    protected Person(String id, String name, LocalDate dateOfBirth) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "dateOfBirth");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    //one line for the lists, each type writes its own
    public abstract String getSummary();

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
