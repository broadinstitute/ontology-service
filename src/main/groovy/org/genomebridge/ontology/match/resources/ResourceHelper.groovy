/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.genomebridge.ontology.match.resources

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 *
 * Created: 12/14/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class ResourceHelper {

    static Response okResponse(Object object) {
        Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new JsonBuilder(object).toPrettyString())
                .build()
    }

    static Response badRequest(Collection<String> errors) {
        Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new JsonBuilder({errors: errors}).toPrettyString())
                .build()
    }

    static Response serverError(Collection<String> errors) {
        Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new JsonBuilder({errors: errors}).toPrettyString())
                .build()
    }


}
