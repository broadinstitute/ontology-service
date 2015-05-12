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

import com.codahale.metrics.annotation.Timed
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.dropwizard.views.View
import org.genomebridge.ontology.match.api.model.UseRestriction
import org.genomebridge.ontology.match.ontology.OntologyMatcher
import org.genomebridge.ontology.match.utils.UseRestrictionParser
import org.genomebridge.ontology.match.views.IndexView

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@CompileStatic
@Slf4j
@Path('/')
class MatchResource {

    private OntologyMatcher ontologyMatcher

    @Inject
    public MatchResource(@Named("ontologyMatcher") OntologyMatcher ontologyMatcher) {
        this.ontologyMatcher = ontologyMatcher
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public static View index() {
        new IndexView()
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Timed
    public Response match(String postData) {
        def parser = new JsonSlurper()
        List<String> errors = new ArrayList<>()
        if (!postData) {
            return ResourceHelper.badRequest(Collections.singletonList("Please provide two valid owl expressions"))
        }
        try {
            List<UseRestriction> useRestrictions = new ArrayList<>()
            parser.parseText(postData).each {
                def restriction = UseRestrictionParser.parse(it)
                if (restriction) {
                    useRestrictions.add(restriction)
                } else {
                    errors.add("Expression is invalid: " + it)
                }
            }
            if (!useRestrictions || useRestrictions.isEmpty() || useRestrictions.size() != 2) {
                errors.add("Please provide two valid owl expressions")
            }
            if (!errors.isEmpty()) {
                return ResourceHelper.badRequest(errors)
            } else {
                def valid = ontologyMatcher.match(useRestrictions.get(0), useRestrictions.get(1))
                return ResourceHelper.okResponse(valid)
            }
        } catch (Exception e) {
            errors.add("Unable to parse post data: " + postData + "\nError: " + e.getMessage())
        }
        errors.add("Encountered server error. Please raise an issue.")
        ResourceHelper.serverError(errors)
    }

}
