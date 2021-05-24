package org.broadinstitute.dsp.ontology.services;

import com.google.gson.Gson;
import org.broadinstitute.dsp.ontology.http.models.IndexConfig;
import org.broadinstitute.dsp.ontology.http.models.IndexFile;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class IndexConfigService {
    private IndexConfig indexConfig;

    public IndexConfigService() throws IOException {
        Gson gson = new Gson();

        try (Reader reader = Files.newBufferedReader(Paths.get("index-config.json"))) {
            indexConfig = gson.fromJson(reader, IndexConfig.class);
        } catch (Exception e) {
            System.out.println("Unable to load index config");
            throw e;
        }
    }

    public String getIndexName() {
        return indexConfig.indexName;
    }

    public List<String> getServers() {
        return indexConfig.servers;
    }

    public List<IndexFile> getOntologies() {
        return indexConfig.ontologies;
    }
}
