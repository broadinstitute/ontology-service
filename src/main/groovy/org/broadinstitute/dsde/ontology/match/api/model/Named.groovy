/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.broadinstitute.dsde.ontology.match.api.model

import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 *
 * Created: 9/10/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
@EqualsAndHashCode(includes=["type", "name"])
class Named extends UseRestriction {

    public static final String TYPE = "named"
    String type = TYPE
    String name

    @Override
    public OntClass createOntologicalRestriction(OntModel model) {
        return model.createClass(name);
    }

}
