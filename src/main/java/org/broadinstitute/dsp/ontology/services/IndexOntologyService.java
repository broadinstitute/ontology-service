package org.broadinstitute.dsp.ontology.services;

import org.broadinstitute.dsp.ontology.http.configurations.ElasticSearchConfiguration;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;
import org.broadinstitute.dsp.ontology.http.models.Term;
import org.elasticsearch.client.RestClient;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class IndexOntologyService implements AutoCloseable {

    private final String indexName;
    private IndexerUtils utils = new IndexerUtils();
    private RestClient client;

    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    public IndexOntologyService(ElasticSearchConfiguration config) {
        this.indexName = config.getIndexName();
        this.client = ElasticSearchSupport.createRestClient(config);
    }

    /**
     * For each input stream, parse and upload ontology terms to the configured index.
     *
     * @param streamRecList List of StreamRec objects
     * @throws IOException The exception
     */
    public void indexOntologies(List<StreamRec> streamRecList) throws IOException {
        if (!utils.checkIndexWithRetry(client, indexName, 10)) {
            throw new IOException("Unable to create index");
        }

        try {
            for (StreamRec streamRec : streamRecList) {
                // Deprecate everything that might already exist for this ontology file
                Collection<Term> terms = utils.generateTerms(streamRec);
                Boolean successfulUpload = utils.bulkUploadTerms(client, indexName, terms);
                streamRec.setAtLeastOneOntologyIndexed(successfulUpload);

            }
        } catch (OWLOntologyCreationException | InterruptedException e) {
            throw new BadRequestException("Problem with OWL file.");
        }

    }

    /**
     * Deprecate any indexed terms for the specified type
     *
     * @param ontologyType The ontology type (e.g. "Disease", or "Organization") to mark as deprecated (i.e. usable=false)
     * @return True if no errors, exception otherwise.
     * @throws IOException The exception
     */
    Boolean deprecateOntology(String ontologyType) throws IOException {
        utils.bulkDeprecateTerms(client, indexName, ontologyType);
        return true;
    }
}
