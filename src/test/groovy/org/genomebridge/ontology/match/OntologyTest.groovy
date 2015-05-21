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

import groovy.transform.CompileStatic
import org.genomebridge.ontology.match.api.Consent
import org.genomebridge.ontology.match.api.ResearchPurpose
import org.genomebridge.ontology.match.api.model.*
import org.genomebridge.ontology.match.ontology.OntologyMatcher
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 *
 * TODO: Incorporate all of the tests in the original genomebridge-ontology project.
 *
 * Created: 12/14/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class OntologyTest {

    private static final String MOCK_OWNER = "mock owner"
    private static final String MOCK_RESEARCHER = "mock researcher"
    private static final OntologyMatcher ONTOLOGY = new OntologyMatcher()

    @Test
    void testMethodsResearch() {
        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/methods_research")
        )
        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new Everything()
        )
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testMethodsResearchExcluded() {
        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/methods_research")
        )
        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new Not(
                        operand: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/methods_research"))
        )
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertFalse("We should NOT have a match between consent and purpose", match)
    }

    @Test
    void testAggregateResearch() {
        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/aggregate_research")
        )
        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new Everything()
        )
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testAggregateResearchExcluded() {
        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/aggregate_research")
        )
        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new Not(
                        operand: new Named(name: "http://www.genomebridge.org/ontologies/DURPO/aggregate_research"))
        )
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertFalse("We should NOT have a match between consent and purpose", match)
    }

    @Test
    void testMatchRestriction() {
        // Paranoid Schizophrenia
        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new Named(name: "http://purl.obolibrary.org/obo/DOID_1229")
        )

        // Schizophrenia (parent class)
        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new Named(name: "http://purl.obolibrary.org/obo/DOID_5419")
        )

        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)

        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testMisMatchedPurpose() {

        def purpose = new ResearchPurpose(
                id: UUID.randomUUID().toString(),
                researcher: MOCK_RESEARCHER,
                purpose: new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_5041"),
                        new Named(name: "http://www.genomebridge.org/ontologies/DURPO/Non_profit")
                ]))
        )

        def consent = new Consent(
                id: UUID.randomUUID().toString(),
                owner: MOCK_OWNER,
                requiresManualReview: false,
                restriction: new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_162"),
                        new Named(name: "http://www.genomebridge.org/ontologies/DURPO/children")
                ]))
        )

        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)

        assertFalse("We should NOT have a match between consent and purpose", match)

    }

}
