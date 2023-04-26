package org.broadinstitute.dsp.ontology.services;

import com.google.gson.Gson;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import org.broadinstitute.dsp.ontology.http.configurations.ElasticSearchConfiguration;
import org.broadinstitute.dsp.ontology.http.configurations.OntologyFileConfiguration;
import org.broadinstitute.dsp.ontology.http.enumeration.OntologyTypes;
import org.broadinstitute.dsp.ontology.http.models.StreamRec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

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
        OntologyFileConfiguration fileConfig = context.getBean(OntologyFileConfiguration.class);
        ElasticSearchConfiguration esConfig = context.getBean(ElasticSearchConfiguration.class);
        IndexOntologyService indexOntologyService = new IndexOntologyService(esConfig);
        IndexerServiceImpl service = new IndexerServiceImpl(indexOntologyService);

        System.out.println(new Gson().toJson(esConfig));

        List<StreamRec> ontologyStreams = fileConfig
                .getFiles()
                .stream()
                .map(f -> getStreamRecFromFileName(f.name(), f.type(), f.prefix(), f.fileType()))
                .toList();
        try {
            ontologyStreams.forEach(s -> System.out.println("Indexing stream: " + s.getFileName()));
            service.saveAndIndex(ontologyStreams);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private StreamRec getStreamRecFromFileName(String fileName, String type, String prefix, String fileType) {
        try {
            System.out.println("Processing file " + fileName);
            File inputFile = new File("src/main/resources/ontologies/" + fileName);
            InputStream stream = new FileInputStream(inputFile);
            return new StreamRec(stream,
                    OntologyTypes.getValue(type),
                    prefix,
                    fileType,
                    inputFile.getName());
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found");
            throw new RuntimeException(fnf.getMessage());
        }
    }
}
