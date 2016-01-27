package org.broadinstitute.dsde.ontology.match.ontology

import com.google.common.io.Resources
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.ontology.impl.OntModelImpl
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import groovy.util.logging.Slf4j
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

@Slf4j
class OntModelProvider {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10)

    /**
     * This Observable does not block when subscribed to as it spawns a separate thread.
     * See http://techblog.netflix.com/2013/02/rxjava-netflix-api.html
     *
     * @return Observable <OntModel>
     */
    public static Observable<OntModel> nonBlockingOntModel() {
        if (cachedModel != null) {
            create({
                Observer<OntModel> observer ->
                    observer.onNext(getWorkingModel())
                    observer.onCompleted()
                    Subscriptions.empty()
            })
        } else {
            create({
                Observer<OntModel> observer ->
                    executorService.submit({
                        try {
                            observer.onNext(buildOntModel())
                            observer.onCompleted()
                        } catch (Exception e) {
                            observer.onError(e)
                        }
                    })
                    Subscriptions.empty()
            })
        }
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
        cacheModel(model)
        model
    }

    private static Model cachedModel
    private static OntModelSpec cachedSpec

    private static void cacheModel(OntModel model) {
        log.info("Setting cached model components")
        cachedModel = ModelFactory.createModelForGraph(model.getGraph())
        cachedSpec = model.getSpecification()
    }

    private static OntModel getWorkingModel() {
        log.info("Create new model from cached components")
        new OntModelImpl(cachedSpec, cachedModel)
    }

    private static URL getURL(String str) {
        try {
            new URL(str)
        } catch (MalformedURLException ignore) {
            null
        }
    }

}
