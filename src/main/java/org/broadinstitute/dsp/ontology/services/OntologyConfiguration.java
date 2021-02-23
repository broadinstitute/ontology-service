package org.broadinstitute.dsp.ontology.services;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("micronaut.application")
public class OntologyConfiguration {

    private String name;
    private String version;

    public OntologyConfiguration() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
