package org.broadinstitute.dsp.ontology.services;

import org.broadinstitute.dsp.ontology.http.models.StreamRec;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface IndexerService {

    List<StreamRec> saveAndIndex(List<StreamRec> streamRecList) throws  IOException;

    //Response deleteOntologiesByType(String fileURL) throws IOException;

}
