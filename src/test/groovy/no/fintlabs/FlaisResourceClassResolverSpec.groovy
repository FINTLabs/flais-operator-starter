package no.fintlabs


import no.fintlabs.testmodels.TestCrd
import no.fintlabs.testmodels.TestReconciler
import no.fintlabs.testmodels.TestWorkflow
import spock.lang.Specification

class FlaisResourceClassResolverSpec extends Specification {

    def "Should return the generic type for the reconciler"() {
        given:
        def resolver = new FlaisResourceClassResolver()

        when:
        def clazz = resolver.resolveCustomResourceClass(new TestReconciler(new TestWorkflow(), [], []))

        then:
        clazz == TestCrd.class
    }
}
