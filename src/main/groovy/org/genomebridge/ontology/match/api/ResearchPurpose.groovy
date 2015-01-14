/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.genomebridge.ontology.match.api

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.genomebridge.ontology.match.api.model.UseRestriction

/**
 *
 * Created: 12/9/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
@EqualsAndHashCode(includes=["id"])
class ResearchPurpose {

    String id
    String researcher
    UseRestriction purpose
    String[] sensitiveTags

}