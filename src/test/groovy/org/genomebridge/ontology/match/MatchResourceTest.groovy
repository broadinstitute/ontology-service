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

import com.google.common.io.Resources
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.filter.LoggingFilter
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import io.dropwizard.testing.junit.DropwizardAppRule
import org.genomebridge.ontology.match.api.model.And
import org.genomebridge.ontology.match.api.model.Named
import org.genomebridge.ontology.match.api.model.UseRestriction
import org.junit.ClassRule
import org.junit.Test

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 *
 * Created: 12/9/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class MatchResourceTest {

    protected static String resourceFilePath(String resourceClassPathLocation) {
        try {
            return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath()
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @ClassRule
    public static final DropwizardAppRule<OntologyMatchConfiguration> RULE =
            new DropwizardAppRule<OntologyMatchConfiguration>(OntologyMatchApplication.class, resourceFilePath("test-app-config.yml"))

    private static final String LOCAL_APPLICATION_URL = "http://localhost:%d/"

    protected static Client getClient() {
        Client client = new Client()
        client.addFilter(new LoggingFilter())
        client
    }

    @Test
    public void testMatch() {
        Client client = getClient()
        // Paranoid Schizophrenia
        def purpose = new Named(name: "http://purl.obolibrary.org/obo/DOID_1229")
        // Schizophrenia (parent class)
        def consent = new Named(name: "http://purl.obolibrary.org/obo/DOID_5419")
        def ClientResponse response = client.resource(
                String.format(LOCAL_APPLICATION_URL, RULE.getLocalPort())).
                accept(MediaType.APPLICATION_JSON).
                type(MediaType.APPLICATION_JSON).
                post(
                        ClientResponse.class,
                        new JsonBuilder([purpose, consent]).toString()
                )
        assertTrue(response.status == Response.Status.OK.statusCode)
        assertTrue(response.getEntity(Boolean.class))
    }

    @Test
    public void testMisMatch() {
        def purpose = new And(new ArrayList<UseRestriction>([
                new Named(name: "http://purl.obolibrary.org/obo/DOID_5041"),
                new Named(name: "http://www.genomebridge.org/ontologies/DURPO/Non_profit")
        ]))

        def consent = new And(new ArrayList<UseRestriction>([
                new Named(name: "http://purl.obolibrary.org/obo/DOID_162"),
                new Named(name: "http://www.genomebridge.org/ontologies/DURPO/children")
        ]))

        def ClientResponse response = client.resource(
                String.format(LOCAL_APPLICATION_URL, RULE.getLocalPort())).
                accept(MediaType.APPLICATION_JSON).
                type(MediaType.APPLICATION_JSON).
                post(
                        ClientResponse.class,
                        new JsonBuilder([purpose, consent]).toString()
                )
        assertTrue(response.status == Response.Status.OK.statusCode)
        assertFalse(response.getEntity(Boolean.class))
    }

}
