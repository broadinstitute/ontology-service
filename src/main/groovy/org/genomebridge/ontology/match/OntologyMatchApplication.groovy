/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.genomebridge.ontology.match

import groovy.transform.CompileStatic
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import com.hubspot.dropwizard.guice.GuiceBundle
import io.dropwizard.views.ViewBundle
import org.genomebridge.ontology.match.resources.MatchResource

@CompileStatic
class OntologyMatchApplication extends Application<OntologyMatchConfiguration> {

    public static void main(String[] args) {
        new OntologyMatchApplication().run(args);
    }

    @Override
    void initialize(Bootstrap<OntologyMatchConfiguration> bootstrap) {

        GuiceBundle<OntologyMatchConfiguration> guiceBundle =
                GuiceBundle.<OntologyMatchConfiguration>newBuilder().
                        addModule(new OntologyModule()).
                        setConfigClass(OntologyMatchConfiguration.class).
                        enableAutoConfig(getClass().getPackage().getName()).
                        build()

        bootstrap.addBundle(guiceBundle)
        bootstrap.addBundle(new ViewBundle())
    }

    @Override
    void run(OntologyMatchConfiguration configuration, Environment environment) {
        environment.jersey().register(MatchResource.class)
    }

}
