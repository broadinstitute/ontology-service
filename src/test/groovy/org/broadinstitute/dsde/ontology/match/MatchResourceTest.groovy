/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.broadinstitute.dsde.ontology.match

import com.google.common.io.Resources
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.dropwizard.testing.junit.DropwizardAppRule
import org.broadinstitute.dsde.ontology.match.api.model.And
import org.broadinstitute.dsde.ontology.match.api.model.Named
import org.broadinstitute.dsde.ontology.match.api.model.UseRestriction
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.filter.LoggingFilter
import org.junit.ClassRule
import org.junit.Test

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
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
@Slf4j
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
            new DropwizardAppRule<>(OntologyMatchApplication.class, resourceFilePath("test-app-config.yml"))

    private static final String LOCAL_APPLICATION_URL = "http://localhost:%d/"

    private static Response postEntity(UseRestriction purpose, UseRestriction consent) {
        ClientConfig config = new ClientConfig()
        Client client = ClientBuilder.newClient(config)
        client.register(new LoggingFilter())
        client.
                target(String.format(LOCAL_APPLICATION_URL, RULE.getLocalPort())).
                request(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).
                post(Entity.json([purpose, consent]))
    }

    @Test
    public void testMatch() {
        // Paranoid Schizophrenia
        def purpose = new Named(name: "http://purl.obolibrary.org/obo/DOID_1229")
        // Schizophrenia (parent class)
        def consent = new Named(name: "http://purl.obolibrary.org/obo/DOID_5419")
        Response response = postEntity(purpose, consent)
        assertTrue(response.status == Response.Status.OK.statusCode)
        assertTrue(response.readEntity(Boolean))
    }

    @Test
    public void testMisMatch() {
        def purpose = new And(new ArrayList<UseRestriction>([
                new Named(name: "http://purl.obolibrary.org/obo/DOID_5041"),
                new Named(name: "http://www.broadinstitute.org/ontologies/DUOS/Non_profit")
        ]))

        def consent = new And(new ArrayList<UseRestriction>([
                new Named(name: "http://purl.obolibrary.org/obo/DOID_162"),
                new Named(name: "http://www.broadinstitute.org/ontologies/DUOS/children")
        ]))
        Response response = postEntity(purpose, consent)
        assertTrue(response.status == Response.Status.OK.statusCode)
        assertFalse(response.readEntity(Boolean))
    }

}
