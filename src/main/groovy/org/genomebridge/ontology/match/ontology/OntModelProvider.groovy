package org.genomebridge.ontology.match.ontology

import com.google.common.io.Resources
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.mindswap.pellet.jena.PelletInfGraph
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import rx.Observable
import rx.Observer
import rx.subscriptions.Subscriptions

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static rx.Observable.create

class OntModelProvider {

    /**
     * This Observable does not block when subscribed to as it spawns a separate thread.
     *
     * @return Observable <OntModel>
     */
    public static Observable<OntModel> nonBlockingOntModel() {
        return create({
            Observer<OntModel> observer ->
                ExecutorService executorService = Executors.newSingleThreadExecutor()
                executorService.execute(new Runnable() {
                    public void run() {
                        observer.onNext(buildOntModel())
                        observer.onCompleted()
                    }
                })
                executorService.shutdown()
                return Subscriptions.empty()
        })
    }

    private static OntModel buildOntModel() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager()
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC)
        Resources.getResource("ontologies.txt").readLines().each { line ->
            if (!line.startsWith("#")) {
                OWLOntology ontology
                URL url = getURL(line)
                if (url) {
                    ontology = manager.loadOntology(IRI.create(url))
                } else {
                    ontology = manager.loadOntologyFromOntologyDocument(Resources.getResource(line).openStream())
                }

                HashMap<String, OWLAnnotationProperty> annotationProperties = new HashMap<String, OWLAnnotationProperty>()
                ontology.getAnnotationPropertiesInSignature().each {
                    OWLAnnotationProperty property ->
                        annotationProperties.put(property.getIRI().getRemainder().get(), property)
                }

                OWLAnnotationProperty label = annotationProperties.get("label")
                assert label != null: "Need label annotation property"

                OWLAnnotationProperty deprecated = annotationProperties.get("deprecated")
                ontology.getClassesInSignature().each {
                    OWLClass owlClass ->
                        // Do not load deprecated classes.
                        if (deprecated == null || owlClass.getAnnotations(ontology, deprecated).size() == 0) {
                            String id = owlClass.toStringID()
                            OntClass ontClass = model.createClass(id)
                            owlClass.getSuperClasses(ontology).each {
                                expr ->
                                    if (expr instanceof OWLClass) {
                                        OWLClass cexpr = (OWLClass) expr
                                        // this ignores some obvious restriction / intersection classes.
                                        OntClass superClass = model.createClass(cexpr.toStringID())
                                        ontClass.addSuperClass(superClass)
                                    }
                            }
                        }
                }
            }
        }
        ((PelletInfGraph) model.getGraph()).classify()
        model
    }

    private static URL getURL(String str) {
        try {
            new URL(str)
        } catch (MalformedURLException ignore) {
            null
        }
    }

}
