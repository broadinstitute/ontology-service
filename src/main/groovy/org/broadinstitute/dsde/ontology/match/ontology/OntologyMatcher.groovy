/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.broadinstitute.dsde.ontology.match.ontology

import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import groovy.util.logging.Slf4j
import org.broadinstitute.dsde.ontology.match.api.model.UseRestriction
import org.semanticweb.owlapi.model.OWLOntologyCreationException

/**
 *
 * Created: 12/12/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@Slf4j
class OntologyMatcher {

    public Boolean match(UseRestriction purpose, UseRestriction consent) {
        matchPair(new Pair(purpose: purpose, consent: consent))
    }

    class Pair {
        UseRestriction purpose
        UseRestriction consent
    }

    private Boolean matchPair(Pair pair) {
        Boolean match = false
        try {
            def consentId = UUID.randomUUID().toString()
            def model = OntModelProvider.nonBlockingOntModel().toBlocking().first()
            addNamedEquivalentClass(model, consentId, pair.consent)
            OntClass purposeClass = addNamedSubClass(model, UUID.randomUUID().toString(), pair.purpose)
            OntClass consentClass = model.getOntClass(consentId)
            match = purposeClass.hasSuperClass(consentClass)
        } catch (IOException e) {
            log.error("IOException matching purpose", e)
        } catch (OWLOntologyCreationException e) {
            log.error("OWLOntologyCreationException matching purpose", e)
        }
        match
    }

    private static OntClass addNamedEquivalentClass(OntModel model, String name, UseRestriction restriction) {
        OntClass cls = model.createClass(name)
        cls.addEquivalentClass(restriction.createOntologicalRestriction(model))
        cls
    }

    private static OntClass addNamedSubClass(OntModel model, String name, UseRestriction restriction) {
        OntClass cls = model.createClass(name)
        cls.addSuperClass(restriction.createOntologicalRestriction(model))
        cls
    }

}
