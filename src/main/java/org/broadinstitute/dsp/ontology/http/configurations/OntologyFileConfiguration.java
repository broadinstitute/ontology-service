package org.broadinstitute.dsp.ontology.http.configurations;

import io.micronaut.context.annotation.ConfigurationProperties;
import org.broadinstitute.dsp.ontology.http.models.OntologyFile;

import java.util.List;

@ConfigurationProperties("ontologyFiles")
public class OntologyFileConfiguration {
    private List<OntologyFile> files;

    public List<OntologyFile> getFiles() {
        return files;
    }

    public void setFiles(List<OntologyFile> files) {
        this.files = files;
    }
}
