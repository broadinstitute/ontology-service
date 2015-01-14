/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.genomebridge.ontology.match.api.model

import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic

/**
 *
 * Created: 9/10/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
abstract class UseRestriction {

    public abstract OntClass createOntologicalRestriction(OntModel model);

    public String toPrettyString() {
        new JsonBuilder(this).toPrettyString()
    }

}
