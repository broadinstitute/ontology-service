package org.broadinstitute.dsp.ontology.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;

import javax.ws.rs.InternalServerErrorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by SantiagoSaucedo on 3/11/2016.
 */
public class IndexerServiceImpl implements IndexerService {

    private final IndexOntologyService indexService;
    private static final ObjectMapper mapper = new ObjectMapper();


    public IndexerServiceImpl(IndexOntologyService indexService) {
        this.indexService = indexService;
    }


    @Override
    public List<StreamRec> saveAndIndex(List<StreamRec> streamRecList) throws IOException {
        indexService.indexOntologies(streamRecList);
        return streamRecList;
    }

    /*@Override
    public Response deleteOntologiesByType(String fileURL) throws IOException {
        String configurationFileString = storeService.retrieveConfigurationFile();
        if (StringUtils.isEmpty(configurationFileString)) return Response.status(Response.Status.BAD_REQUEST).build();
        Map<String, HashMap> map = parseAsMap(configurationFileString);

        // Deprecate ontology terms
        Boolean deprecated = indexService.deprecateOntology((String) map.get(fileURL).get("ontologyType"));
        if (deprecated) {
            //Update configuration file
            deleteFileFromMap(map, fileURL);
            storeService.storeOntologyConfigurationFile(mapToStreamParser(map));

            //Delete file from CloudStorage
            storeService.deleteFile(fileURL);
            return Response.ok().build();
        }
        return Response.notModified().build();
    }*/

    private void deleteFileFromMap(Map<String, HashMap> configMap,String fileUrl) {
        configMap.remove(fileUrl);
    }

    private Map<String, HashMap> parseAsMap(String str) throws IOException {
        ObjectReader reader = mapper.readerFor(Map.class);
        return reader.readValue(str);
    }

    private ByteArrayInputStream indexedOntologiesStreamBuilder(List<StreamRec> streamRecList){
        try {
            Map<String, HashMap> json = new HashMap<>();
            for (StreamRec streamRec : streamRecList) {
                if (streamRec.getAtLeastOneOntologyIndexed()) {
                    addFileData(json, streamRec);
                }
            }
            if (Objects.nonNull(json)) {
                return mapToStreamParser(json);
            }
            return null;
        }catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }
    }

    private void addFileData(Map<String, HashMap> json, StreamRec streamRec) {
        HashMap streamRecMap = new HashMap<String, String>();
        streamRecMap.put("fileName", streamRec.getFileName());
        streamRecMap.put("prefix", streamRec.getPrefix());
        streamRecMap.put("ontologyType", streamRec.getOntologyType());
        json.put(streamRec.getUrl(), streamRecMap);
    }

    private ByteArrayInputStream mapToStreamParser(Map<String, HashMap> json) throws JsonProcessingException {
        String content = new ObjectMapper().writeValueAsString(json);
        return new ByteArrayInputStream(content.getBytes());
    }
}

