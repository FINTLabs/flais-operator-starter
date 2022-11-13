package no.fintlabs

import no.fintlabs.testmodels.TestCrd
import spock.lang.Specification

class LabelValidatorSpec extends Specification {
    def validLabels = [
            "app.kubernetes.io/name"     : "name",
            "app.kubernetes.io/instance" : "instance",
            "app.kubernetes.io/version"  : "version",
            "app.kubernetes.io/component": "component",
            "app.kubernetes.io/part-of"  : "part-of",
            "fintlabs.no/team"           : "team",
            "fintlabs.no/org-id"         : "org-id"
    ]

    def notValidLabels = [
            "app.kubernetes.io/name"     : "name",
            "app.kubernetes.io/instance" : "instance",
            "app.kubernetes.io/component": "component",
            "app.kubernetes.io/part-of"  : "part-of",
    ]

    def "If all mandatory labels exists not exception should be thrown"() {
        given:
        def crd = new TestCrd()
        crd.getMetadata().getLabels().putAll(validLabels)

        when:
        LabelValidator.validate(crd)

        then:
        notThrown(IllegalArgumentException)
    }

    def "If mandatory labels is missing an exception should be thrown"() {
        given:
        def crd = new TestCrd()
        crd.getMetadata().getLabels().putAll(notValidLabels)

        when:
        LabelValidator.validate(crd)

        then:
        thrown(IllegalArgumentException)
    }
}
