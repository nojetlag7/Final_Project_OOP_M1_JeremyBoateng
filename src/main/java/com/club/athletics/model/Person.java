package com.club.athletics.model;

import java.time.LocalDate;
import java.util.Objects;

/** Identity shared by everyone in the club. */
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

    /** One-line description for listings; athletes and coaches show different details. */
    public abstract String getSummary();

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
