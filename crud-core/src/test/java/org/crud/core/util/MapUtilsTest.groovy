package org.crud.core.util

import spock.lang.Specification

import static java.util.Optional.empty
import static java.util.Optional.of

/**
 *
 */
class MapUtilsTest extends Specification {
    def "Create a map"(Object[] v, Map r) {
        expect:
        MapUtils.map(v) == r

        where:
        v                    || r
        null                 || null
        []                   || [:]
        //
        ["a", 1]             || [a: 1]
        ["a", 1, "b", 2]     || [a: 1, b: 2]
        ["a", 1, "a", 2]     || [a: 2]
        ["a.1", 1, "a.2", 2] || ["a.1": 1, "a.2": 2]
    }

    def "Create a map with wrong number of arguments"() {
        when:
        MapUtils.map("a", 1, "b")

        then:
        def e = thrown IllegalArgumentException
        e.message == "params.length"
    }

    def "Create a deep map with regular values"(Object[] v, Map r) {
        expect:
        MapUtils.deepMap(v) == r

        where:
        v                    || r
        null                 || null
        []                   || [:]
        //
        ["a", 1]             || [a: 1]
        ["a", 1, "b", 2]     || [a: 1, b: 2]
        ["a", 1, "a", 2]     || [a: 2]
        ["a.1", 1, "a.2", 2] || [a: ["1": 1, "2": 2]]
        ["a.1", 1, "b.2", 2] || [a: ["1": 1], b: ["2": 2]]
    }

    def "Create a deep map with wrong number of arguments"() {
        when:
        MapUtils.deepMap("a", 1, "b")

        then:
        def e = thrown IllegalArgumentException
        e.message == "params.length"
    }

    def "Get value from a map given a path"(Map map, String path, Object value) {
        expect:
        MapUtils.getValue(map, path as String) == value

        where:
        map                        | path    || value
        null                       | ""      || empty()
        null                       | "a.b.c" || empty()
        //
        [:]                        | ""      || empty()
        [:]                        | "a.b.c" || empty()
        //
        [a: 1, b: 2]               | "z"     || empty()
        [a: 1, b: 2]               | "a"     || of(1)
        [a: 1, b: 2]               | "b"     || of(2)
        [a: ["1": 1, "2": 2]]      | "a.1"    | of(1)
        [a: ["1": 1, "2": 2]]      | "a.2"    | of(2)
        [a: ["1": 1, b: ["2": 2]]] | "a.b.2"  | of(2)
        [a: ["x", "y", "z"]]       | "a.1"    | of("y")
        [a: ["x", [u: "v"], "z"]]  | "a.1.u"  | of("v")
    }

    def "Get value from a map given a path with index out of bounds"() {
        when:
        MapUtils.getValue([a: ["x", "y", "z"]], "a.5")

        then:
        def e = thrown IndexOutOfBoundsException
        e.message == "Index: 5, Size: 3"
    }

    def "Create new sets"(elements, Set expected) {
        expect:
        MapUtils.newHashSet(elements) == expected

        where:
        elements               || expected
        new String[0]          || []
        ["1", "2"] as String[] || ["1", "2"]
    }
}