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

import groovy.transform.CompileStatic
import org.broadinstitute.dsde.ontology.match.api.Consent
import org.broadinstitute.dsde.ontology.match.api.ResearchPurpose
import org.broadinstitute.dsde.ontology.match.api.model.*
import org.broadinstitute.dsde.ontology.match.ontology.OntologyMatcher
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 *
 * Created: 1/27/16
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class AdaMTests {

    private static final OntologyMatcher ONTOLOGY = new OntologyMatcher()

    @Test
    void testBroadInstituteIsNonProfit() {
        def consent = new Consent(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#nonProfitOrganization"))
        def purpose = new ResearchPurpose(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#BroadInstitute"))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testBroadInstituteIsInUS() {
        def consent = new Consent(
                new Named(name: "http://www.fao.org/countryprofiles/geoinfo/geopolitical/resource/United_States_of_America"))
        def purpose = new ResearchPurpose(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#BroadInstitute"))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testGenentechIsProfit() {
        def consent = new Consent(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#nonProfitOrganization"))
        def purpose = new ResearchPurpose(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#Genentech"))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertFalse("We should NOT have a match between consent and purpose", match)
    }

    @Test
    void testNonProfitCancerFailure() {
        def consent = new Consent(
                new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_162"), // Cancer
                        new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#nonProfitOrganization")
                ])))
        def purpose = new ResearchPurpose(
                new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#BroadInstitute"))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertFalse("We should NOT have a match between consent and purpose", match)
    }

    @Test
    void testNonProfitCancerSuccess() {
        def consent = new Consent(
                new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_162"), // Cancer
                        new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#nonProfitOrganization")
                ])))
        def purpose = new ResearchPurpose(
                new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_0050686"), // Organ System Cancer
                        new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#BroadInstitute")
                ])))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertTrue("We should have a match between consent and purpose", match)
    }

    @Test
    void testNonProfitCancerFailure2() {
        def consent = new Consent(
                new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_162"), // Cancer
                        new Not(operand: new Named(name: "http://purl.obolibrary.org/obo/DOID_114")), // Not heart disease
                        new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#nonProfitOrganization")
                ])))
        def purpose = new ResearchPurpose(
                new And(new ArrayList<UseRestriction>([
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_114"), // Heart Disease
                        new Named(name: "http://purl.obolibrary.org/obo/DOID_0050686"), // Organ System Cancer
                        new Named(name: "http://www.ga4gh.org/ontologies/ADA-M#p3g")
                ])))
        def match = ONTOLOGY.match(purpose.purpose, consent.restriction)
        assertFalse("We should NOT have a match between consent and purpose", match)
    }

}
