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

@CompileStatic
class Match {

    String content

    Object expression

    String source

    String sourceType

    Map<String, String> annotations

    Collection<String> matches

    Match(String content) {
        this.content = content
    }

}
