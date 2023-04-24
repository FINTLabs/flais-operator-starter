package no.fintlabs

import no.fintlabs.testmodels.TestCrd
import no.fintlabs.testmodels.TestReconciler
import spock.lang.Specification

class FlaisResourceClassResolverSpec extends Specification {

    def "Should return the generic type for the reconciler"() {
        given:
        def resolver = new FlaisResourceClassResolver()

        when:
        def clazz = resolver.getResourceClass(TestReconciler.class)

        then:
        clazz == TestCrd.class
    }
}
