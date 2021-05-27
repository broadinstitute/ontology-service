package org.broadinstitute.dsp.ontology.http.models;

import com.google.cloud.storage.BlobId;

import java.io.InputStream;

/**
 * Created by SantiagoSaucedo on 3/15/2016.
 */
public class StreamRec {


    private InputStream stream;

    private String prefix;

    private String ontologyType;

    private String fileType;

    private String fileName;

    private String url;

    private Boolean atLeastOneOntologyIndexed;

    private BlobId blobId;

    public StreamRec(InputStream stream, String ontologyType, String prefix, String fileType, String fileName){
        this.stream = stream;
        this.ontologyType = ontologyType;
        this.prefix = prefix;
        this.fileType = fileType;
        this.fileName = fileName;
        this.atLeastOneOntologyIndexed = false;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getOntologyType() {
        return ontologyType;
    }

    public void setOntologyType(String ontologyType) {
        this.ontologyType = ontologyType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BlobId getBlobId() { return blobId; }

    public void setBlobId(BlobId blobId) { this.blobId = blobId; }

    public Boolean getAtLeastOneOntologyIndexed() {
        return atLeastOneOntologyIndexed;
    }

    public void setAtLeastOneOntologyIndexed(Boolean atLeastOneOntologyIndexed) {
        this.atLeastOneOntologyIndexed = atLeastOneOntologyIndexed;
    }
}
