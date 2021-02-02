package org.broadinstitute.dsp.ontology.http.configurations;

import java.util.List;
import javax.validation.constraints.NotEmpty;

public class ElasticSearchConfiguration {

    @NotEmpty
    private String indexName;

    @NotEmpty
    private List<String> servers;

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
