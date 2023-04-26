package org.broadinstitute.dsp.ontology.http.configurations;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("elasticSearch")
public class ElasticSearchConfiguration {

    private String indexName;

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
