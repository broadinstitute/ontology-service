package org.broadinstitute.dsp.ontology.http.configurations;


import javax.validation.constraints.NotEmpty;

public class StoreOntologyConfiguration {

    @NotEmpty
    public String bucketSubdirectory;

    @NotEmpty
    public String configurationFileName;

    public String getBucketSubdirectory() {
        return bucketSubdirectory;
    }

    public void setBucketSubdirectory(String bucketSubdirectory) {
        this.bucketSubdirectory = bucketSubdirectory;
    }

    public String getConfigurationFileName() {
        return configurationFileName;
    }

    public void setConfigurationFileName(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }
}
