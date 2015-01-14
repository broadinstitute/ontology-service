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

import com.google.inject.AbstractModule
import com.google.inject.Provides
import groovy.transform.CompileStatic
import org.genomebridge.ontology.match.ontology.OntologyManager

import javax.inject.Named

/**
 *
 * Created: 12/13/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class OntologyModule extends AbstractModule {

    private final OntologyManager ontologyManager = new OntologyManager()

    @Override
    protected void configure() {}

    @Provides
    @Named("ontologyManager")
    public OntologyManager provideOntologyManager() {
        ontologyManager
    }

}
