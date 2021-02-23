package org.broadinstitute.dsp.ontology.services;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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

    }
}
