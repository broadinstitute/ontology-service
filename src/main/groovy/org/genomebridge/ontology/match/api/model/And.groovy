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
import com.hp.hpl.jena.rdf.model.RDFList
import com.hp.hpl.jena.rdf.model.RDFNode
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 *
 * Created: 9/10/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
@EqualsAndHashCode(includes=["type", "operands"])
class And extends UseRestriction {

    public static final String TYPE = "and"
    String type = TYPE
    UseRestriction[] operands

    public And(List<UseRestriction> operands) {
        this.operands = operands
        if(operands.size() < 2) {
            throw new IllegalArgumentException("Conjunction must have at least two operands")
        }
    }

    @Override
    public OntClass createOntologicalRestriction(OntModel model) {
        RDFNode[] nodes = new RDFNode[operands.length];
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = operands[i].createOntologicalRestriction(model);
        }
        RDFList list = model.createList(nodes);
        return model.createIntersectionClass(null, list);
    }

}
