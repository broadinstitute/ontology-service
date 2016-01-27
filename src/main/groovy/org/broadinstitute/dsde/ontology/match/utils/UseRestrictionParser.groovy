/*
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2015 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
package org.broadinstitute.dsde.ontology.match.utils

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.boon.json.JsonFactory
import org.boon.json.ObjectMapper
import org.broadinstitute.dsde.ontology.match.api.model.And
import org.broadinstitute.dsde.ontology.match.api.model.Everything
import org.broadinstitute.dsde.ontology.match.api.model.Named
import org.broadinstitute.dsde.ontology.match.api.model.Not
import org.broadinstitute.dsde.ontology.match.api.model.Nothing
import org.broadinstitute.dsde.ontology.match.api.model.Only
import org.broadinstitute.dsde.ontology.match.api.model.Or
import org.broadinstitute.dsde.ontology.match.api.model.Some
import org.broadinstitute.dsde.ontology.match.api.model.UseRestriction

/**
 *
 * Created: 12/14/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@Slf4j
class UseRestrictionParser {

    public static UseRestriction parse(Object it) {
        ObjectMapper mapper = JsonFactory.create()
        def objAsString = new JsonBuilder(it).toString()
        log.info("Parsing object: " + it)
        switch (it?.type) {
            case (And.TYPE):
                if (it?.operands) {
                    new And( it?.operands?.collect { parse(it) })
                } else { null }
                break
            case (Everything.TYPE):
                new Everything()
                break
            case (Named.TYPE):
                mapper.readValue(objAsString, Named.class)
                break
            case (Not.TYPE):
                if (it?.operand) {
                    new Not( operand: parse(it?.operand) )
                } else { null }
                break
            case (Nothing.TYPE):
                new Nothing()
                break
            case (Only.TYPE):
                if (it?.target && it?.property) {
                    new Only( property: it?.property, target: parse(it?.target) )
                } else { null }
                break
            case (Or.TYPE):
                if (it?.operands) {
                    new Or( it?.operands?.collect { parse(it) })
                } else { null }
                break
            case (Some.TYPE):
                if (it?.target && it?.property) {
                    new Some( property: it?.property, target: parse(it?.target) )
                } else { null }
                break
            default:
                null
                break
        }
    }

}