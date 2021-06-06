package org.example;

import lombok.Data;

@Data
public class Group {
    private final String name;
    private final String description;

    public Group( final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}
