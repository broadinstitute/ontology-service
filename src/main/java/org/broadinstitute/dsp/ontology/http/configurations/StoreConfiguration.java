package org.broadinstitute.dsp.ontology.http.configurations;

import javax.validation.constraints.NotNull;

public class StoreConfiguration {


    public String password;

    @NotNull
    public String endpoint;

    @NotNull
    public String bucket;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getWhitelistBucket() {
        return getBucket() + "/whitelist";
    }

}