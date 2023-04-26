package org.broadinstitute.dsp.ontology.services;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import org.broadinstitute.dsp.ontology.http.configurations.ElasticSearchConfiguration;
import org.broadinstitute.dsp.ontology.http.configurations.OntologyFileConfiguration;
import org.broadinstitute.dsp.ontology.http.enumeration.OntologyTypes;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Command(name = "ontology", description = "...",
        mixinStandardHelpOptions = true)
public class OntologyCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(OntologyCommand.class);

    public static void main(String[] args) {
        PicocliRunner.execute(OntologyCommand.class, args);
    }

    public void run() {
        // runs the config file
        ApplicationContext context = ApplicationContext.run(ApplicationContext.class);
        OntologyFileConfiguration fileConfig = context.getBean(OntologyFileConfiguration.class);
        ElasticSearchConfiguration esConfig = context.getBean(ElasticSearchConfiguration.class);
        IndexOntologyService indexOntologyService = new IndexOntologyService(esConfig);
        IndexerServiceImpl service = new IndexerServiceImpl(indexOntologyService);

        List<StreamRec> ontologyStreams = fileConfig
                .getFiles()
                .stream()
                .map(f -> getStreamRecFromFileName(f.name(), f.type(), f.prefix(), f.fileType()))
                .toList();
        int exitStatus = 0;
        try {
            ontologyStreams.forEach(s -> logger.info("Indexing stream: " + s.getFileName()));
            service.saveAndIndex(ontologyStreams);
            logger.info("Complete");
        } catch (Exception e) {
            logger.error(e.getMessage());
            exitStatus = -1;
        }
        System.exit(exitStatus);
    }

    private StreamRec getStreamRecFromFileName(String fileName, String type, String prefix, String fileType) {
        try {
            logger.info("Processing file " + fileName);
            File inputFile = new File("src/main/resources/ontologies/" + fileName);
            InputStream stream = new FileInputStream(inputFile);
            return new StreamRec(stream,
                    OntologyTypes.getValue(type),
                    prefix,
                    fileType,
                    inputFile.getName());
        } catch (FileNotFoundException fnf) {
            logger.error("File not found");
            throw new RuntimeException(fnf.getMessage());
        }
    }
}
