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

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import org.genomebridge.ontology.match.api.model.*
import org.genomebridge.ontology.match.utils.UseRestrictionParser
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 *
 * Created: 12/13/14
 *
 * @author <a href="mailto:grushton@broadinstitute.org">grushton</a>
 */
@CompileStatic
class JsonTests {

    private static final Named DOID_1229 = new Named(name: "http://purl.obolibrary.org/obo/DOID_1229")
    private static final Named DOID_5419 = new Named(name: "http://purl.obolibrary.org/obo/DOID_5419")
    private static final Named DOID_150 = new Named(name: "http://purl.obolibrary.org/obo/DOID_150")


    @Test
    public void testSerialize() {
        assertTrue(new JsonBuilder(DOID_1229)
                .toString()
                .equalsIgnoreCase('{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_1229"}'))
        assertTrue(new JsonBuilder(DOID_5419)
                .toString()
                .equalsIgnoreCase('{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_5419"}'))
    }

    @Test
    public void testDeserialize() {

        assertTrue(
                new And(new ArrayList<UseRestriction>([DOID_1229, DOID_5419, DOID_150])).equals(
                        UseRestrictionParser.parse(
                                ["type": "and", "operands": [
                                        ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_1229"],
                                        ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"],
                                        ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_150"]]
                                ]
                        )
                )
        )

        assertTrue(
                new Everything().equals(
                        UseRestrictionParser.
                                parse(["type": "everything"])
                )
        )

        assertTrue(
                DOID_1229.equals(
                        UseRestrictionParser.
                                parse(["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_1229"])
                )
        )

        assertTrue(
                DOID_5419.equals(
                        UseRestrictionParser.
                                parse(["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"])
                )
        )

        assertTrue(
                new Not(operand: DOID_5419).equals(
                        UseRestrictionParser.
                                parse(
                                        ["type"   : "not",
                                         "operand": ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"]]
                                )
                )
        )

        assertTrue(
                new Nothing().equals(
                        UseRestrictionParser.
                                parse(["type": "nothing"])
                )
        )

        assertTrue(
                new Only(property: "property", target: DOID_5419).equals(
                        UseRestrictionParser.
                                parse(
                                        ["type"    : "only",
                                         "property": "property",
                                         "target"  : ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"]]
                                )
                )
        )

        assertTrue(
                new Or(new ArrayList<UseRestriction>([DOID_1229, DOID_5419])).equals(
                        UseRestrictionParser.
                                parse(
                                        ["type": "or", "operands": [
                                                ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_1229"],
                                                ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"]]
                                        ]
                                )
                )
        )

        assertTrue(
                new Some(property: "property", target: DOID_5419).equals(
                        UseRestrictionParser.
                                parse(
                                        ["type"    : "some",
                                         "property": "property",
                                         "target"  : ["type": "named", "name": "http://purl.obolibrary.org/obo/DOID_5419"]]
                                )
                )
        )


    }

}
