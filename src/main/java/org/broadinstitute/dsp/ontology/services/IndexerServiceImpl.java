package org.broadinstitute.dsp.ontology.services;

import org.broadinstitute.dsp.ontology.http.models.StreamRec;

import java.io.IOException;
import java.util.List;

public class IndexerServiceImpl implements IndexerService {

    private final IndexOntologyService indexService;

    public IndexerServiceImpl(IndexOntologyService indexService) {
        this.indexService = indexService;
    }


    @Override
    public void saveAndIndex(List<StreamRec> streamRecList) throws IOException {
        indexService.indexOntologies(streamRecList);
    }

}

