package com.github.tscz.spring.platform.gradle;

import org.gradle.api.Project;

public class Versions {

    private Project project;

    public Versions(Project project) {
        this.project = project;
    }

    public String get(String id) {
        try {
            return project.getExtensions().getExtraProperties().get(id).toString();
        }
        catch (Exception cause) {
            var knownProperties = project.getExtensions().getExtraProperties().getProperties();

            throw new IllegalArgumentException("Unknown version for dependency id: " + id + System.lineSeparator() + "Known properties are: " + knownProperties, cause);
        }
    }
}
