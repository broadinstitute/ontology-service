/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.genomebridge.ontology.match.ontology

import com.google.common.io.Resources
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.InfModel
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.reasoner.Reasoner
import com.hp.hpl.jena.reasoner.ReasonerRegistry
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.Actors
import org.genomebridge.ontology.match.api.model.UseRestriction
import org.mindswap.pellet.jena.PelletInfGraph
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*

import java.util.concurrent.TimeUnit

/**
 *
 * Created: 12/12/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@Slf4j
class OntologyManager {

    private final OntModel model

    OntologyManager() {
        this.model = initializeModel()
    }

    public Boolean match(UseRestriction purpose, UseRestriction consent) {
        Boolean reply = matchingActor.sendAndWait(
                new Pair(purpose: purpose, consent: consent),
                30,
                TimeUnit.SECONDS)
        reply
    }

    class Pair {
        UseRestriction purpose
        UseRestriction consent
    }

    private def matchingActor = Actors.actor {
        loop {
            log.debug('Processing restriction matching in actor.loop')
            react {
                Pair pair ->
                    reply matchPair(pair)
            }
        }
    }

    private Boolean matchPair(Pair pair) {
        Boolean match = false
        try {
            def consentId = UUID.randomUUID().toString()
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

    private OntModel initializeModel() {
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC)
        Resources.getResource("ontologies.txt").readLines().each {
            loadOntology(Resources.getResource(it).openStream(), model)
        }
        ((PelletInfGraph) model.getGraph()).classify()
        model
    }

    private void loadOntology(InputStream reader, OntModel model)
            throws OWLOntologyCreationException, IOException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager()
        assert manager != null : "Manager shouldn't be null"

        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(reader)
        assert ontology != null : "Ontology shouldn't be null"

        HashMap<String, OWLAnnotationProperty> annotationProperties = new HashMap<String, OWLAnnotationProperty>()
        ontology.getAnnotationPropertiesInSignature().each {
            OWLAnnotationProperty property ->
                annotationProperties.put(property.getIRI().getFragment(), property)
        }

        OWLAnnotationProperty label = annotationProperties.get("label")
        assert label != null : "Need label annotation property"

        OWLAnnotationProperty deprecated = annotationProperties.get("deprecated")

        ontology.getClassesInSignature().each {
            OWLClass owlClass ->
                // Do not load deprecated classes.
                if (deprecated == null || owlClass.getAnnotations(ontology, deprecated).size() == 0) {
                    String id = owlClass.toStringID()
                    OntClass ontClass = model.createClass(id)
                    owlClass.getSuperClasses(ontology).each {
                        expr ->
                            if(expr instanceof OWLClass) {
                                OWLClass cexpr = (OWLClass)expr
                                // this ignores some obvious restriction / intersection classes.
                                OntClass superClass = model.createClass(cexpr.toStringID())
                                ontClass.addSuperClass(superClass)
                            }
                    }
                }
        }
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
