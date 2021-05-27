package org.broadinstitute.dsp.ontology.services;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import org.broadinstitute.dsp.ontology.http.configurations.ElasticSearchConfiguration;
import org.broadinstitute.dsp.ontology.http.enumeration.OntologyTypes;
import org.broadinstitute.dsp.ontology.http.models.IndexFile;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "ontology", description = "...",
        mixinStandardHelpOptions = true)
public class OntologyCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(OntologyCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }

        // runs the config file
        ApplicationContext context = ApplicationContext.run(ApplicationContext.class);
        OntologyConfiguration config = context.getBean(OntologyConfiguration.class);
        // print the version
        System.out.println("ontology-service version: " + config.getVersion());

        try {
            IndexConfigService configService = new IndexConfigService();

            List<StreamRec> streamRecList = configService.getOntologies()
                    .stream()
                    .map(this::getStreamRecFromOntology)
                    .collect(Collectors.toList());

            ElasticSearchConfiguration elasticConfig = new ElasticSearchConfiguration();
            elasticConfig.setIndexName(configService.getIndexName());
            elasticConfig.setServers(configService.getServers());

            try (IndexOntologyService indexOntologyService = new IndexOntologyService(elasticConfig)){
                IndexerServiceImpl service = new IndexerServiceImpl(indexOntologyService);
                service.saveAndIndex(streamRecList);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("File Not Found");
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    private StreamRec getStreamRecFromOntology(IndexFile ontology) {
        try {
            String ontologyType = OntologyTypes.getValue(ontology.ontologyType);
            System.out.println("Processing file " + ontology.name);
            File inputFile = new File("ontologies/" + ontology.name.trim());
            InputStream stream = new FileInputStream(inputFile);

            return new StreamRec(stream,
                    ontologyType,
                    ontology.prefix,
                    ontology.fileType,
                    inputFile.getName());
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found");
            throw new RuntimeException(fnf.getMessage());
        }
    }
}
