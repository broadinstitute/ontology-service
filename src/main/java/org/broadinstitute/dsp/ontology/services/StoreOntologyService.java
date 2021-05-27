package org.broadinstitute.dsp.ontology.services;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.storage.BlobId;
import org.broadinstitute.dsp.ontology.http.cloudstore.CloudStore;
import org.broadinstitute.dsp.ontology.http.cloudstore.GCSService;
import org.broadinstitute.dsp.ontology.http.enumeration.OntologyTypes;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by SantiagoSaucedo on 3/11/2016.
 */
public class StoreOntologyService   {

    private final GCSService gcsService;
    private final String bucketSubdirectory;
    private final String configurationFileName;
    private final String jsonExtension = ".json";

    public StoreOntologyService(GCSService gcsService, String bucketSubdirectory, String configurationFileName) {
        this.gcsService = gcsService;
        this.bucketSubdirectory = bucketSubdirectory;
        this.configurationFileName = configurationFileName;
    }


    public void storeOntologyConfigurationFile(InputStream inputStream)   {
        String type = MediaType.APPLICATION_JSON;
        try {
            String url_suffix =  bucketSubdirectory + configurationFileName + jsonExtension ;
            gcsService.storeDocument(inputStream,
                  type,
                  url_suffix);
        }catch (IOException e) {
            throw new InternalServerErrorException("Problem with storage service. (Error 10)");
        }
    }

    public List<StreamRec> storeOntologies(List<StreamRec> streamRecList){
        for(StreamRec srec : streamRecList) {
            if (srec.getAtLeastOneOntologyIndexed()) {
                try {
                    String url_suffix = bucketSubdirectory + "/" + OntologyTypes.getValue(srec.getOntologyType()) + "/" + srec.getFileName();
                    srec.setBlobId(gcsService.storeOntologyDocument(srec.getStream(),
                          srec.getFileType(),
                          url_suffix));
                }catch (IOException e) {
                    throw new InternalServerErrorException("Problem with storage service. (Error 20)");
                }
            }
        }
        return streamRecList;
    }

    public String retrieveConfigurationFile (){
        try {
            String response = gcsService.getDocument(BlobId.of(this.bucketSubdirectory, configurationFileName).toString()).toString();
            return response;
        } catch (Exception e) {
            if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 404) {
                return null;
            } else {
                throw new InternalError("Problem with storage service. (Error 30)");
            }
        }
    }

    public void  deleteFile(String fileUrl){
        try {
            gcsService.deleteDocument(fileUrl);
        }catch (Exception e) {
            throw new InternalError("Problem with storage service. (Error 50)");
        }
    }
}
